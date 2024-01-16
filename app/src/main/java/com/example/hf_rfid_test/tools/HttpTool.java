package com.example.hf_rfid_test.tools;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpTool {
    private OkHttpClient client = new OkHttpClient.Builder().build();
    private Request.Builder request;

    public String SendScanLog(String data) throws IOException {
        String url = "http://localhost:8080/GetScanLog?data=" + data;
        request.url(url);
        Response response = client.newCall(request.build()).execute();
        assert response.body() != null;
        return response.body().string();
    }

    public String SendWriteLog(String beforeData, String afterData) throws IOException {
        String url = "http://localhost:8080/GetScanLog?beforeData=" + beforeData + "&afterData=" + afterData;
        request.url(url);
        Response response = client.newCall(request.build()).execute();
        assert response.body() != null;
        return response.body().string();
    }
}
