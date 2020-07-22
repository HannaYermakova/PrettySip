package by.aermakova.prettysip.ui.activity.splash;

import android.content.Intent;
import android.os.Bundle;

import by.aermakova.prettysip.R;
import by.aermakova.prettysip.logic.implementation.ISplashLogic;
import by.aermakova.prettysip.logic.manager.PreferencesManager;
import by.aermakova.prettysip.ui.StartLinphoneServiceCommon;
import by.aermakova.prettysip.ui.activity.auth.AuthorizationActivity;

/**
 * Splash activity, check, has user been authorized? Logic implementation in {@link SplashPresenter} via {@link ISplashLogic}
 */
public class SplashActivity extends StartLinphoneServiceCommon implements ISplashLogic.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ISplashLogic.Presenter presenter = new SplashPresenter(this);
        String authCode = PreferencesManager.getInstance().getAuthCode(this);
        presenter.checkAuthCode(authCode);
    }

    @Override
    public void openMainActivity(boolean value) {
        if (value) {
            checkService();
        } else {
            startActivity(new Intent(getApplicationContext(), AuthorizationActivity.class));
            finish();
        }
    }
}