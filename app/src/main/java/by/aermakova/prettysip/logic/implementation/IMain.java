package by.aermakova.prettysip.logic.implementation;

import by.aermakova.prettysip.logic.model.requestMessages.DeviceIdRequestMessage;
import by.aermakova.prettysip.logic.model.UserSipInfo;
import by.aermakova.prettysip.ui.activity.main.MainFragment;
import by.aermakova.prettysip.ui.activity.main.MainPresenter;

/**
 * Connection between {@link MainFragment} and {@link MainPresenter}
 */
public interface IMain {
    interface View {

        /**
         * Start service to handle change registration state and calls incoming
         *
         */
        void saveUserInfoInCache(UserSipInfo userSipInfo);

        /**
         * Led-mark changes its color depending on registration on sip-server
         * @param value means are user registered.
         */
        void showSipConnection(boolean value);
    }

    interface Presenter {
        /**
         * Send request to server to get the user-info for registration on sip-server
         */
        void sendGetSipInfoMessage(DeviceIdRequestMessage userSipInfo);
    }
}
