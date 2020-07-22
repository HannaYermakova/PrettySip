package by.aermakova.prettysip.logic.util;

import android.os.Build;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper for validation fields
 */

public class Util {

    /**
     * Check is that auth code valid, should contains only numbers and Latin letters,
     * 1 to 20 characters.
     *
     * @param authCode checking QR or manually entered code
     */
    public static boolean checkValidAuth(String authCode) {
        if (authCode == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("^([0-9A-Za-z]){1,20}$");
        Matcher matcher = pattern.matcher(authCode);
        return matcher.matches();
    }

    /**
     * Check is that phone number valid, should contains only numbers,
     * 9 to 10 characters.
     *
     * @param inputPhoneNumber entered phone number
     */
    public static boolean checkValidInputPhone(String inputPhoneNumber) {
        if (inputPhoneNumber == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("^([0-9]){9,10}$");
        Matcher matcher = pattern.matcher(inputPhoneNumber);
        return matcher.matches();
    }

    /**
     * Check is full phone number valid, should match pattern.
     *
     * @param finalPhoneNumber full phone number
     */
    public static boolean checkFinalPhoneNumber(String finalPhoneNumber) {
        Pattern pattern = Pattern.compile("^((\\+375)+([0-9]){9})|((\\+7)+([0-9]){10})|((\\+380)+([0-9]){9})$");
        Matcher matcher = pattern.matcher(finalPhoneNumber);
        return matcher.matches();
    }

    /**
     * Check is verify code match pattern.
     *
     * @param smsCode verify code from SMS
     */
    public static boolean checkValidInputSMSCode(String smsCode) {
        return smsCode.length() == 6;
    }

    /**
     * Check is the response code means ok
     *
     * @param code - response code from server
     */
    public static boolean validResponse(int code) {
        return code >= 200 && code < 300;
    }

    /**
     * Generate pseudo device unique ID
     */
    public static String getPseudoDeviceID() {
        final String ID = "42";
        return ID +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10;
    }

    public static boolean isMiUi() {
        String line;
        BufferedReader input = null;
        try {
            java.lang.Process p = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name");
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return false;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return !TextUtils.isEmpty(line);
    }
}