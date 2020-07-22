package by.aermakova.prettysip.logic.implementation;

import by.aermakova.prettysip.ui.activity.linphone.IncomingCallPresenter;

/**
 * Connection between {@link by.aermakova.prettysip.ui.activity.linphone.IncomingCallFragment}
 * and {@link by.aermakova.prettysip.ui.activity.linphone.VideoCallFragment}
 * and {@link IncomingCallPresenter}
 */

public interface ICallLogic {
    interface View {
        /**
         * Unmute device's microphone to start conversation
         */
        void unmuteMic();
    }

    interface Presenter {
    }
}
