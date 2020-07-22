package by.aermakova.prettysip.logic.util;
/**
 * Helper for application constants
 */
public class Constants {
    /**
     * Incoming call time limit. Auto hang up call after this time
     */
    public static final CharSequence TALK_LIMIT = "02:00";

    public static final long FREEZE_TIME = 60 * 1000;

    /**
     * Buttons unavailability time
     */
    public static final long BLOCK_BUTTON = 5 *1000;

    /**
     * Server's response code: limit of entering codes from SMS is reached
     */
    public static final int LIMIT_SMS_CODE = 406;

    /**
     * Server's response code: error connecting to server
     */
    public static final int NO_SERVER_CONNECTION_CODE = 500;

    /**
     * ID of incoming call notification
     */
    public static final int NOTIFICATION_ID = 42;

    /**
     * Prefix before digital code
     */
    public static final String CODE_PREFIX = "#";
}
