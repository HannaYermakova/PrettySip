package by.aermakova.prettysip.logic.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Shared preferences manager: storing of account's information
 */
public class PreferencesManager {

    private static PreferencesManager mInstance = null;
    private static final String SETTING_PREFS = "SETTING_PREFS";

    /**
     * Accepting or not incoming calls
     */
    private static final String AVAILABLE_CALLS = "AVAILABLE_CALLS";

    /**
     * QR or manually entered unique code for user's authentication
     */
    private static final String AUTH_CODE = "AUTH_CODE";

    /**
     * Phone number entered while authorization
     */
    private static final String REGISTRATION_PHONE = "REGISTRATION_PHONE";

    /**
     * Information about username of current sip-account required for accepting only current user's calls
     */
    private static final String SIP_USERNAME = "SIP_USERNAME";

    /**
     * Has been additional permission of device done
     */
    private static final String ADDITIONAL_PERMISSION = "ADDITIONAL_PERMISSION";

    private PreferencesManager() {
    }

    public static PreferencesManager getInstance() {
        if (mInstance == null) {
            synchronized (PreferencesManager.class) {
                if (mInstance == null) {
                    mInstance = new PreferencesManager();
                }
            }
        }
        return mInstance;
    }

    private SharedPreferences.Editor getPreferencesEditor(Context context) {
        return getPreferences(context).edit();
    }

    private SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(SETTING_PREFS, Context.MODE_PRIVATE);
    }


    public String getRegistrationPhone(Context context) {
        return getPreferences(context).getString(REGISTRATION_PHONE, null);
    }

    public void setRegistrationPhone(Context context, String phone) {
        getPreferencesEditor(context).putString(REGISTRATION_PHONE, phone).apply();
    }

    public String getAuthCode(Context context) {
        return getPreferences(context).getString(AUTH_CODE, null);
    }

    public void setAuthCode(Context context, String token) {
        getPreferencesEditor(context).putString(AUTH_CODE, token).apply();
    }

    public boolean getAccessCall(Context context) {
        return getPreferences(context).getBoolean(AVAILABLE_CALLS, true);
    }

    public void setAccessCall(Context context, boolean value) {
        getPreferencesEditor(context).putBoolean(AVAILABLE_CALLS, value).apply();
    }

    public String getSipUsername(Context context) {
        return getPreferences(context).getString(SIP_USERNAME, null);
    }

    public void setSipUsername(Context context, String value) {
        getPreferencesEditor(context).putString(SIP_USERNAME, value).apply();
    }

    public boolean getAdditionalPermission(Context context) {
        return getPreferences(context).getBoolean(ADDITIONAL_PERMISSION, false);
    }

    public void setAdditionalPermission(Context context, boolean value) {
        getPreferencesEditor(context).putBoolean(ADDITIONAL_PERMISSION, value).apply();
    }
}