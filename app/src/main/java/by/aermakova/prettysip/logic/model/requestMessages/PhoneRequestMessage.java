package by.aermakova.prettysip.logic.model.requestMessages;

/**
 * Message model
 */

public class PhoneRequestMessage extends KeyRequestMassage {
    private String phone;

    public PhoneRequestMessage(String key, String phone) {
        super(key);
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}
