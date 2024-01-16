package com.example.hf_rfid_test;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.exception.ConfigurationException;

public class SettingActivity extends AppCompatActivity {

    private RFIDWithUHFUART mRFID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("UHF模块设置");

        try {
            Log.d(TAG, "onCreate: " + mRFID.getInstance().getPower());
            Log.d(TAG, "onCreate: " + mRFID.getInstance().getFrequencyMode());

        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}