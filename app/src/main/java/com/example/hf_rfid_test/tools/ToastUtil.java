package com.example.hf_rfid_test.tools;

import android.content.Context;

import es.dmoral.toasty.Toasty;

public class ToastUtil {
    public static void ShortToast_Success(Context context, String msg) {
        Toasty.success(context, msg, 500);
    }
}
