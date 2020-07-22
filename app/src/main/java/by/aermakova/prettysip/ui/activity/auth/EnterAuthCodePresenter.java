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
import by.aermakova.prettysip.logic.implementation.IAuthCodeLogic;
import by.aermakova.prettysip.logic.model.requestMessages.KeyRequestMassage;
import by.aermakova.prettysip.logic.network.HttpClient;
import by.aermakova.prettysip.logic.network.HttpService;
import by.aermakova.prettysip.logic.util.Constants;
import by.aermakova.prettysip.logic.util.Util;
import by.aermakova.prettysip.logic.util.Variables;

/**
 * Logic implementation of {@link QRScannerFragment} and {@link EnterCodeFragment} via {@link IAuthCodeLogic}
 */
public class EnterAuthCodePresenter implements IAuthCodeLogic.Presenter, LifecycleObserver {
    private final String TAG = getClass().getName();

    private IAuthCodeLogic.View mView;
    private String mAuthCode;

    private HttpService mHttpService;
    private CompositeDisposable mDisposable = new CompositeDisposable();

    public EnterAuthCodePresenter(IAuthCodeLogic.View view, Lifecycle lifecycle) {
        this.mView = view;
        lifecycle.addObserver(this);
        mHttpService = HttpClient.getClient().create(HttpService.class);
    }

    @Override
    public void checkAndSendAuthCode(String key) {
        if (Util.checkValidAuth(key)) {
            mAuthCode = key;
            Log.i(TAG, "Has been scanned the valid QR-code: " + key);
            sendAuthCode(key);
        } else {
            Log.i(TAG, "Has been scanned the invalid QR-code: " + key);
            mView.showInvalidQRMessage();
        }
    }

    private void sendAuthCode(String key) {
        mDisposable.add(mHttpService.verifyKey(new KeyRequestMassage(key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        Variables.getAuthInformation().setKey(mAuthCode);
                        mView.openEnterPhoneNumberActivity();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (e instanceof HttpException) {
                            HttpException exception = (HttpException) e;
                            int code = exception.code();
                            if (code < Constants.NO_SERVER_CONNECTION_CODE) {
                                mView.showInvalidQRMessage();
                            } else {
                                mView.showNoConnectionMessage();
                            }
                        } else mView.showNoConnectionMessage();
                    }
                }));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void unregisterCallbackHandler() {
        Variables.getAuthInformation().setKey(null);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destroyFragment() {
        mDisposable.dispose();
    }
}
