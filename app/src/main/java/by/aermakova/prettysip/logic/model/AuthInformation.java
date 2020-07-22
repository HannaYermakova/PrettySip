package by.aermakova.prettysip.logic.model;

/**
 * Information for authorization
 */

public class AuthInformation {

    /** QR or manually entered unique code */
    private String key;

    /** User's phone number*/
    private String phone;

    /** Verification SMS-code */
    private String code;

    /** Unique device ID*/
    private String deviceId;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "AuthInformation{" +
                "key='" + key + '\'' +
                ", phone='" + phone + '\'' +
                ", code='" + code + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}
