package com.example.hf_rfid_test;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hf_rfid_test.tools.StringUtils;
import com.rscja.barcode.BarcodeDecoder;
import com.rscja.barcode.BarcodeFactory;
import com.rscja.deviceapi.RFIDWithISO15693;
import com.rscja.deviceapi.entity.ISO15693Entity;
import com.rscja.deviceapi.exception.ConfigurationException;

import es.dmoral.toasty.Toasty;


public class MainActivity extends Activity {

    private final static String TAG = "MainActivity";
    RFIDWithISO15693 mRFID;
    BarcodeDecoder barcodeDecoder = BarcodeFactory.getInstance().getBarcodeDecoder();
    private EditText tv_boxid;
    private EditText tv_boxid_hex;
    private TextView tv_barcode;
    private EditText et_BarCode;
    private Button btn_clear;
    private Button btn_restart;
    Button btn_readBarcode;
    Button btn_readRFID;
    Button btn_writeRFID;
    Button btn_writeBarcodeRFID;
    EditText et_writeContent;
    EditText et_taglength;
    EditText et_tagStartAddr;
    Spinner memBank;
    int tagLen = 0;
    String seldata = "ASCII";
    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RFIDRECORD.txt";
    String StrArray = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            mRFID = RFIDWithISO15693.getInstance();

        } catch (ConfigurationException e) {

            Toast.makeText(MainActivity.this, "Device configuration error",
                    Toast.LENGTH_SHORT).show();
            return;

        }
        //StrArray=new String[10000];
        tv_boxid = this.<EditText>findViewById(R.id.boxid);
        tv_boxid_hex = this.<EditText>findViewById(R.id.boxid_hex);
        et_taglength = this.findViewById(R.id.TagLength);
        et_taglength.setText("10");
        et_tagStartAddr = this.findViewById(R.id.TagStartAddr);
        et_tagStartAddr.setText("0");
        tagLen = 10;

        et_taglength.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (et_taglength.getText().length() > 0) {
                    tagLen = Integer.valueOf(et_taglength.getText().toString());
                    if (tagLen > 12) {
                        et_taglength.setText("12");
                    }
                }
            }
        });
        et_BarCode = this.<EditText>findViewById(R.id.barcode);
        et_BarCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                int len = et_BarCode.getText().length();
                if (len > tagLen) {
                    et_BarCode.setText(et_BarCode.getText().toString().substring(0, tagLen));
                }

            }
        });

        barcodeDecoder.setDecodeCallback((BarcodeDecoder.DecodeCallback) barcodeEntity -> {
            if (barcodeEntity.getResultCode() == BarcodeDecoder.DECODE_SUCCESS) {
                et_BarCode.setText(barcodeEntity.getBarcodeData());
                barcodeDecoder.stopScan();
            } else {
                Toasty.error(MainActivity.this, "读码失败", 500);
            }
        });

        //读RFID
        btn_readRFID = this.<Button>findViewById(R.id.btn_readRFID);
        btn_readRFID.setOnClickListener(v -> {
            tv_boxid.setText("");
            tv_boxid_hex.setText("");
            ISO15693Entity result;
            try {
                int tagBlocks = tagLen % 4 == 0 ? tagLen / 4 : tagLen / 4 + 1;
                Log.d(String.valueOf(MainActivity.this), String.valueOf(et_tagStartAddr.getText()));
                Log.d(String.valueOf(MainActivity.this), String.valueOf(tagBlocks));
                result = mRFID.read(Integer.parseInt(et_tagStartAddr.getText().toString()), tagBlocks);

                if (result == null) {
                    Toasty.error(MainActivity.this, "读取失败", 1000).show();
                    return;
                } else {
                    String mySubStr = result.getData().toUpperCase();
                    String Str = StringUtils.convertHexToString(mySubStr);
                    tv_boxid.setText(Str.substring(0, tagLen));
                    tv_boxid_hex.setText(mySubStr.substring(0, tagLen * 2));
                    Toasty.success(MainActivity.this, "读取成功", 1000).show();
                }

            } catch (Exception e) {
                Toasty.error(MainActivity.this, "标签读取失败：" + e.getMessage(), 500).show();
            }
            return;
        });

        //写RFID
        et_writeContent = this.<EditText>findViewById(R.id.writeContent);
        btn_writeRFID = this.<Button>findViewById(R.id.btn_writeRFID);
        btn_writeRFID.setOnClickListener(v -> {
            String strData = et_writeContent.getText().toString().trim();
            if (strData.length() != tagLen) {
                et_writeContent.setText("写入内容长度与设定标签长度不符");
                return;
            }
            if (strData.length() % 4 != 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(strData);
                for (int i = 4 - strData.length() % 4; i > 0; i--) sb.append("0");
                strData = sb.toString();
            }

            String writeData = StringUtils.convertStringToHex(strData);

            Toasty.info(MainActivity.this, "写卡中...", 500).show();
            try {
                mRFID.write(0, tagLen == 1 ? 1 : tagLen * 2 / 4, writeData);
                Toasty.success(MainActivity.this, "写入成功", 500).show();
            } catch (Exception e) {
                Toasty.error(MainActivity.this, "写入失败", 500).show();
            }
        });

        //写二维码入标签

        btn_writeBarcodeRFID = this.<Button>findViewById(R.id.btn_writeBarcodeToRFID);
        btn_writeBarcodeRFID.setOnClickListener(v -> {
            String strData = et_BarCode.getText().toString().trim().toUpperCase();
            if (strData.length() != 10) {
                et_writeContent.setText("写入内容长度需要为10");
                return;
            }

            if (StrArray.contains(strData)) {
                Toasty.error(MainActivity.this, "标签已写入过，无需重复写入", 500).show();
                return;
            }
        });

        //清屏
        btn_clear = this.findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(v -> {
            et_writeContent.setText("");
            tv_boxid_hex.setText("");
            tv_boxid.setText("");
            et_BarCode.setText("");
        });

        tv_barcode = this.findViewById(R.id.barcode);
        btn_readBarcode = this.findViewById(R.id.btn_readBarcode);
        btn_readBarcode.setOnClickListener(v -> barcodeDecoder.startScan());

        btn_restart = this.findViewById(R.id.btn_restart);
        btn_restart.setOnClickListener(v->{
            Intent intent = getIntent();
            finish(); // 结束当前 Activity
            startActivity(intent); // 重新启动应用程序
        });
    }

    @Override
    public boolean onKeyDown(int KeyCode, KeyEvent event) {
        if (KeyCode == 293) {
            //按下手柄扳机按钮执行rfid读
            btn_readRFID.callOnClick();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        new InitTask().execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;

        @Override
        protected Boolean doInBackground(String... params) {
            Log.d(TAG, "RFID init result: " + mRFID.init());
            barcodeDecoder.open(MainActivity.this);
            mRFID.init();
            return mRFID.isPowerOn();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            mypDialog.cancel();

            if (!result) {
                Toasty.error(MainActivity.this, "init fail", 500).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(MainActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("模块正在初始化");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }
    }
}