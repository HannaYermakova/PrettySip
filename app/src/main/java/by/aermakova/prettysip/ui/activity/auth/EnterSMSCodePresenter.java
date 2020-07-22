package by.aermakova.prettysip.ui.activity.auth;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import by.aermakova.prettysip.R;
import by.aermakova.prettysip.logic.implementation.IEnterSMSCode;
import by.aermakova.prettysip.logic.model.AuthInformation;
import by.aermakova.prettysip.logic.model.requestMessages.DeviceIdRequestMessage;
import by.aermakova.prettysip.logic.model.requestMessages.PhoneRequestMessage;
import by.aermakova.prettysip.logic.model.requestMessages.SMSCodeRequestMessage;
import by.aermakova.prettysip.logic.network.HttpClient;
import by.aermakova.prettysip.logic.network.HttpService;
import by.aermakova.prettysip.logic.util.Constants;
import by.aermakova.prettysip.logic.util.Variables;

/**
 * Logic implementation of {@link EnterSMSCodeFragment} via {@link IEnterSMSCode}
 */

public class EnterSMSCodePresenter implements IEnterSMSCode.Presenter, LifecycleObserver {
    private final String TAG = getClass().getSimpleName();

    private IEnterSMSCode.View mView;
    private AuthInformation mAuthInformation;
    private HttpService mHttpService;
    private CompositeDisposable mDisposable = new CompositeDisposable();

    public EnterSMSCodePresenter(IEnterSMSCode.View view, Lifecycle lifecycle) {
        this.mView = view;
        lifecycle.addObserver(this);
        mHttpService = HttpClient.getClient().create(HttpService.class);
        mAuthInformation = Variables.getAuthInformation();
    }

    @Override
    public void sendSMSCode(String code) {
        Log.i(TAG, "Send authorize message");
        SMSCodeRequestMessage message = new SMSCodeRequestMessage(mAuthInformation.getKey(),
                mAuthInformation.getPhone(),
                mAuthInformation.getDeviceId(),
                code);

        mDisposable.add(mHttpService.authorize(message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        sendGetInfoMessage();
                        mView.setInfoToPreferences(mAuthInformation.getKey(),
                                mAuthInformation.getPhone(), null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showInvalidCredentialsMessage(R.string.invalid_phone_number);
                    }
                }));
    }

    private void sendGetInfoMessage() {
        Log.i(TAG, "Send get info message");
        DeviceIdRequestMessage message = new DeviceIdRequestMessage(mAuthInformation.getKey(),
                mAuthInformation.getPhone(),
                mAuthInformation.getDeviceId());
        mDisposable.add(mHttpService.getAccountInfo(message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<AccountInfo>() {
                    @Override
                    public void onSuccess(AccountInfo accountInfo) {
                        mView.setInfoToPreferences(null, null, accountInfo);
                        mView.openMainActivity();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                }));

    }

    @Override
    public void repeatGetCode() {
        if (mAuthInformation.getKey() != null && mAuthInformation.getPhone() != null) {
            Log.i(TAG, "Send repeat SMS message");
            PhoneRequestMessage message = new PhoneRequestMessage(
                    mAuthInformation.getKey(),
                    mAuthInformation.getPhone());
            mDisposable.add(mHttpService.sendAuthCodeAndPhoneNumber(message)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<AuthInformation>() {
                        @Override
                        public void onSuccess(AuthInformation information) {
                            mAuthInformation.setCode(information.getCode());
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            if (e instanceof HttpException) {
                                HttpException exception = (HttpException) e;
                                int code = exception.code();
                                int warningMessage = R.string.invalid_phone_number;
                                if (code == Constants.LIMIT_SMS_CODE) {
                                    warningMessage = R.string.limit_phone_number;
                                }
                                mView.showInvalidCredentialsMessage(warningMessage);
                            } else mView.showInvalidCredentialsMessage(R.string.no_server_connection);
                        }
                    }));
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destroyFragment() {
        mDisposable.dispose();
    }
}