package by.aermakova.prettysip.logic.model.requestMessages;

/**
 * Message model
 */

public class DeviceIdRequestMessage extends PhoneRequestMessage {
    private String deviceId;

    public DeviceIdRequestMessage(String key, String phone, String deviceId) {
        super(key, phone);
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
