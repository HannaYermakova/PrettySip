package by.aermakova.prettysip.logic.implementation;

import by.aermakova.prettysip.ui.activity.splash.SplashPresenter;

/**
 * Connection between {@link by.aermakova.prettysip.ui.activity.splash.SplashActivity} and {@link SplashPresenter}
 */
public interface ISplashLogic {
    interface View {
        /**
         * Open {@link by.aermakova.prettysip.ui.activity.main.MainActivity}
         * if user has been authorized previously
         * or {@link by.aermakova.prettysip.ui.activity.auth.AuthorizationActivity} if he has been not
         * @param isAuthorized - has user been authorized or not
         */
        void openMainActivity(boolean isAuthorized);
    }

    interface Presenter {
        /**
         * Check is the key saved in shared preferences
         * @param key - QR or manually entered unique code
         */
        void checkAuthCode(String key);
    }
}