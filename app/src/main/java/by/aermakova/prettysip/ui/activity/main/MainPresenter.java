package by.aermakova.prettysip.ui.activity.main;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import org.linphone.core.AccountCreator;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.ProxyConfig;
import org.linphone.core.RegistrationState;
import org.linphone.core.TransportType;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import by.aermakova.prettysip.linphoneApi.LinphoneManager;
import by.aermakova.prettysip.logic.implementation.IMain;
import by.aermakova.prettysip.logic.model.UserSipInfo;
import by.aermakova.prettysip.logic.model.requestMessages.DeviceIdRequestMessage;
import by.aermakova.prettysip.logic.network.HttpClient;
import by.aermakova.prettysip.logic.network.HttpService;

/**
 * Logic implementation of {@link MainFragment} via {@link IMain}
 */

public class MainPresenter implements IMain.Presenter, LifecycleObserver {
    private final String TAG = getClass().getSimpleName();
    private IMain.View mView;
    private Core mCore;
    private CoreListenerStub mCoreListener;
    private AccountCreator mAccountCreator;

    private HttpService mHttpService;
    private CompositeDisposable mDisposable = new CompositeDisposable();

    public MainPresenter(IMain.View view, Lifecycle lifecycle) {
        this.mView = view;
        lifecycle.addObserver(this);
        mCore = LinphoneManager.getCore();
        mCoreListener = new CoreListenerStub() {
            @Override
            public void onRegistrationStateChanged(Core core, ProxyConfig cfg, RegistrationState state, String message) {
                Log.i(TAG, "Registration " + state.name());
                view.showSipConnection(state.equals(RegistrationState.Ok));
            }
        };
        if (mCore != null)
            mAccountCreator = mCore.createAccountCreator(null);
        mHttpService = HttpClient.getClient().create(HttpService.class);
    }


    @Override
    public void sendGetSipInfoMessage(DeviceIdRequestMessage message) {
        Log.i(TAG, "Send message to get sip-info");
        mDisposable.add(mHttpService.getSipUserInfo(message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<UserSipInfo>() {
                    @Override
                    public void onSuccess(UserSipInfo userSipInfo) {
                        mView.saveUserInfoInCache(userSipInfo);
                        configureSipAccount(userSipInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }

    private void configureSipAccount(UserSipInfo userSipInfo) {
        mAccountCreator.setUsername(userSipInfo.getUsername());
        mAccountCreator.setDomain(userSipInfo.getHostname());
        mAccountCreator.setPassword(userSipInfo.getPassword());
        mAccountCreator.setTransport(TransportType.Udp);
        ProxyConfig cfg = mAccountCreator.createProxyConfig();
        mCore.setDefaultProxyConfig(cfg);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void resumeFragment() {
        mCore.addListener(mCoreListener);
        ProxyConfig config = mCore.getDefaultProxyConfig();
        if (config != null) {
            mView.showSipConnection(config.getState().equals(RegistrationState.Ok));
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void pauseFragment() {
        mCore.removeListener(mCoreListener);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destroyFragment() {
        mDisposable.dispose();
    }
}