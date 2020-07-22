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
import by.aermakova.prettysip.R;
import by.aermakova.prettysip.logic.implementation.IEnterPhoneNumber;
import by.aermakova.prettysip.logic.model.AuthInformation;
import by.aermakova.prettysip.logic.model.requestMessages.PhoneRequestMessage;
import by.aermakova.prettysip.logic.network.HttpClient;
import by.aermakova.prettysip.logic.network.HttpService;
import by.aermakova.prettysip.logic.util.Constants;
import by.aermakova.prettysip.logic.util.Variables;

/**
 * Logic implementation of {@link EnterPhoneNumberFragment} via {@link IEnterPhoneNumber}
 */
public class EnterPhoneNumberPresenter implements IEnterPhoneNumber.Presenter, LifecycleObserver {
    private final String TAG = getClass().getSimpleName();
    private IEnterPhoneNumber.View mView;
    private HttpService mHttpService;
    private CompositeDisposable mDisposable = new CompositeDisposable();

    EnterPhoneNumberPresenter(IEnterPhoneNumber.View view, Lifecycle lifecycle) {
        this.mView = view;
        lifecycle.addObserver(this);
        mHttpService = HttpClient.getClient().create(HttpService.class);
    }

    @Override
    public void sendAuthCodeAndPhoneNumber(String phoneNumber) {
        AuthInformation authInformation = Variables.getAuthInformation();
        if (authInformation.getKey() != null) {
            Log.i(TAG, "Send auth code and phone number");
            PhoneRequestMessage message = new PhoneRequestMessage(
                    authInformation.getKey(),
                    phoneNumber);
            mDisposable.add(mHttpService.sendAuthCodeAndPhoneNumber(message)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<AuthInformation>() {
                        @Override
                        public void onSuccess(AuthInformation information) {
                            mView.openEnterCodeActivity();
                            authInformation.setPhone(phoneNumber);
                            authInformation.setCode(information.getCode());
                            mView.enabledEnterButton(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            if (e instanceof HttpException) {
                                HttpException exception = (HttpException) e;
                                int code = exception.code();
                                int textMessage = R.string.invalid_phone_number;
                                if (code == Constants.LIMIT_SMS_CODE) {
                                    textMessage = R.string.limit_phone_number;
                                }
                                mView.showInvalidPhoneNumberMessage(textMessage);
                                mView.enabledEnterButton(true);
                            } else
                                mView.showInvalidPhoneNumberMessage(R.string.no_server_connection);
                        }
                    }));
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destroyFragment() {
        mDisposable.dispose();
    }
}