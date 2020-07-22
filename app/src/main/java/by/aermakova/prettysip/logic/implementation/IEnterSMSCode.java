package by.aermakova.prettysip.logic.implementation;

import by.aermakova.prettysip.ui.activity.auth.EnterSMSCodePresenter;

/**
 * Connection between {@link by.aermakova.prettysip.ui.activity.auth.EnterSMSCodeFragment}
 * and {@link EnterSMSCodePresenter}
 */

public interface IEnterSMSCode {
    interface View {

        /**
         * Open the main activity of the application
         */
        void openMainActivity();

        /**
         * Show error-message "Invalid credentials" on the UI
         */
        void showInvalidCredentialsMessage(int warningMessage);

        /**
         * Hide button for the repeating verify-code sending from the UI
         */
        void hideRepeatButton();

        /**
         * Change the appearance of the button for the repeating verify-code sending
         */
        void changeRepeatButton(boolean value);

        /**
         * Save information in shared preferences
         */
        void setInfoToPreferences(String key, String phone);

    }

    interface Presenter {
        /**
         * Send code from SMS to server for verification
         * @param code - verify code from sms
         * @see by.aermakova.prettysip.logic.model.AuthInformation
         */
        void sendSMSCode(String code);

        /**
         * Send request to server to get SMS with verify-code repeatedly
         */
        void repeatGetCode();

    }
}