package by.aermakova.prettysip.logic.model.requestMessages;

/**
 * Message model
 */

public class SMSCodeRequestMessage extends DeviceIdRequestMessage {
    private String code;

    public SMSCodeRequestMessage(String key, String phone, String deviceId, String code) {
        super(key, phone, deviceId);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
