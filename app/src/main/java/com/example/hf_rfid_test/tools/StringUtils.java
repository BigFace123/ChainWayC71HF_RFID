package com.example.hf_rfid_test.tools;

public class StringUtils {

    public static String convertStringToHex(String str) {

        char[] chars = str.toCharArray();

        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    public static String convertHexToString(String HexStr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < HexStr.length() - 1; i += 2) {
            String str = HexStr.substring(i, (i + 2));
            int dec = Integer.parseInt(str, 16);
            sb.append((char) dec);
        }
        return sb.toString();
    }


    public static String ConvertSmallEndToBigEnd(String regionStr) {
        String targetData = "";
        char[] chars = regionStr.toCharArray();
        for (int i = 3; i >= 0; i--) {
            targetData += chars[i];
        }
        for (int i = 7; i >= 4; i--) {
            targetData += chars[i];
        }
        for (int i = 11; i >= 8; i--) {
            targetData += chars[i];
        }

        return targetData;
    }
}
