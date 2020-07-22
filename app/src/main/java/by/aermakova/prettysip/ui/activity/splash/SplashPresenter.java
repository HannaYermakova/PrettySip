package by.aermakova.prettysip.ui.activity.splash;

import android.text.TextUtils;
import android.util.Log;

import by.aermakova.prettysip.logic.implementation.ISplashLogic;
import by.aermakova.prettysip.logic.util.Variables;

/**
 * Logic implementation of {@link SplashActivity} via {@link SplashPresenter}
 */
public class SplashPresenter implements ISplashLogic.Presenter {
    private final String TAG = getClass().getSimpleName();
    private ISplashLogic.View mView;

    public SplashPresenter(ISplashLogic.View view) {
        this.mView = view;
    }

    @Override
    public void checkAuthCode(String authCode) {
        boolean check = !TextUtils.isEmpty(authCode);
        if (check) {
            Log.d(TAG, "Auth code: " + authCode);
            Variables.getAuthInformation().setKey(authCode);
        }
        mView.openMainActivity(check);
    }
}