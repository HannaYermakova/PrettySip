package by.aermakova.prettysip.logic.util;

import by.aermakova.prettysip.logic.implementation.ITimer;
import by.aermakova.prettysip.logic.manager.AudioManagerSettings;
import by.aermakova.prettysip.logic.model.AuthInformation;

/**
 * Helper for application variables
 */
public class Variables {

    /**
     * Variable shows is network connection active
     */
    public static boolean isNetworkConnected;

    /**
     * Variable shows is there active unmuted call
     */
    public static boolean isActiveCall;

    /**
     * Variable for timer of active unmuted call
     */
    public static ITimer iTimer;

    /**
     * Authorization information saved here while authorization process
     */
    private static AuthInformation authInformation = new AuthInformation();

    public static AuthInformation getAuthInformation() {
        return authInformation;
    }

    public static AudioManagerSettings audioManagerSettings = null;

    public static boolean isVideoCallAccepted = false;
}
