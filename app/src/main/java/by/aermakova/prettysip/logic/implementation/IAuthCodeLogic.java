package by.aermakova.prettysip.logic.implementation;

import by.aermakova.prettysip.ui.activity.auth.EnterAuthCodePresenter;

/**
 * Connection between {@link by.aermakova.prettysip.ui.activity.auth.EnterCodeFragment}
 * and {@link by.aermakova.prettysip.ui.activity.auth.QRScannerFragment}
 * and {@link EnterAuthCodePresenter}
 */

public interface IAuthCodeLogic {
    interface View {
        /**
         * Open next fragment for the phone number entering
         */
        void openEnterPhoneNumberActivity();

        /**
         * Show error-message "Invalid QR-code" on the UI
         */
        void showInvalidQRMessage();

        /**
         * Show error-message "No connection to server" on the UI
         */
        void showNoConnectionMessage();
    }

    interface Presenter {
        /**
         * Check and send key to server for verification
         * @param key - QR or manually entered unique code
         * @see by.aermakova.prettysip.logic.model.AuthInformation
         */
        void checkAndSendAuthCode(String key);
    }
}
