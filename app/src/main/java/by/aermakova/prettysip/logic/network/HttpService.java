package by.aermakova.prettysip.logic.network;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import by.aermakova.prettysip.logic.model.AuthInformation;
import by.aermakova.prettysip.logic.model.requestMessages.DeviceIdRequestMessage;
import by.aermakova.prettysip.logic.model.requestMessages.KeyRequestMassage;
import by.aermakova.prettysip.logic.model.requestMessages.PhoneRequestMessage;
import by.aermakova.prettysip.logic.model.requestMessages.SMSCodeRequestMessage;
import by.aermakova.prettysip.logic.model.UserSipInfo;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * REST-API requests
 */

public interface HttpService {

    /**
     * Request to start authorization process
     * @param key - message with QR or manually entered unique code
     */
    @POST("verifyqr")
    Single<ResponseBody> verifyKey(@Body KeyRequestMassage key);

    /**
     * Sending key and phone number as next step of authorization
     * @param message - message with QR or manually entered unique code and phone number
     */
    @POST("getcode")
    Single<AuthInformation> sendAuthCodeAndPhoneNumber(@Body PhoneRequestMessage message);

    /**
     * Requesting of information to register on sip-server
     * @param message - message with QR or manually entered unique code, phone number and device ID
     */
    @POST("getsip")
    Single<UserSipInfo> getSipUserInfo(@Body DeviceIdRequestMessage message);

    /**
     * Final step of authorization
     * @param message - message with QR or manually entered unique code, phone number, device ID
     * and code from SMS-message
     */
    @POST("authorize")
    Single<ResponseBody> authorize(@Body SMSCodeRequestMessage message);
}