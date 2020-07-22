package by.aermakova.prettysip.logic.implementation;

import by.aermakova.prettysip.ui.activity.auth.EnterPhoneNumberPresenter;

/**
 * Connection between {@link by.aermakova.prettysip.ui.activity.auth.EnterCodeFragment}
 * and {@link EnterPhoneNumberPresenter}
 */

public interface IEnterPhoneNumber {
    interface View {
        /**
         * Open next fragment for the verify code entering
         */
        void openEnterCodeActivity();

        /**
         * Show error-message "Invalid phone number format" on the UI
         */
        void showInvalidPhoneNumberMessage(int messageText);

        /**
         * @param value - block and unblock button for the phone number entering
         */
        void enabledEnterButton(boolean value);
    }

    interface Presenter {
        /**
         * Send QR or manually entered unique code and phone number to server for verification
         * @param phoneNumber - entered phone number
         * @see by.aermakova.prettysip.logic.model.AuthInformation
         */
        void sendAuthCodeAndPhoneNumber(String phoneNumber);
    }
}
