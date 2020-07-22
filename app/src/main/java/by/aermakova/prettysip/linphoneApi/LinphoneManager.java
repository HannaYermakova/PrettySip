package by.aermakova.prettysip.linphoneApi;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.linphone.core.BuildConfig;
import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListener;
import org.linphone.core.Factory;
import org.linphone.core.PresenceBasicStatus;
import org.linphone.core.PresenceModel;
import org.linphone.core.tools.H264Helper;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import by.aermakova.prettysip.R;

/**
 * Creator and owner of Linphone core object, manager of its usage
 */
public class LinphoneManager {
    private String TAG = getClass().getSimpleName();

    private final String mBasePath;
    private final Context mContext;
    private TelephonyManager mTelephonyManager;
    private PhoneStateListener mPhoneStateListener;
    private Timer mTimer;
    private Core mCore;
    private boolean mExited;
    private boolean mCallGsmON;
    private Runnable mIterateRunnable;

    public LinphoneManager(Context c) {
        mExited = false;
        mContext = c;
        mBasePath = c.getFilesDir().getAbsolutePath();
        String mUserCertsPath = mBasePath + "/user-certs";
        mTelephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);

        mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                Log.i(TAG, "Phone state is " + state);
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        setCallGsmON(true);
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        setCallGsmON(false);
                        break;
                }
            }
        };

        Log.i(TAG, "Registering phone state listener");
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        File f = new File(mUserCertsPath);
        if (!f.exists()) {
            if (!f.mkdir()) {
                Log.e(TAG, mUserCertsPath + " can't be created.");
            }
        }
    }

    public static synchronized Core getCore() {
        if (!LinphoneContext.isReady()) return null;
        if (getInstance().mExited) {
            return null;
        }
        return getInstance().mCore;
    }

    public static Call getCall() {
        Core core = getCore();
        Call call = null;
        if (core != null && core.getCallsNb() > 0) {
            call = core.getCurrentCall();
            if (call == null) {
                call = core.getCalls()[0];
            }
        }
        return call;
    }

    public static synchronized LinphoneManager getInstance() {
        LinphoneManager manager = LinphoneContext.instance().getLinphoneManager();
        if (manager == null) {
            throw new RuntimeException("Linphone Manager should be created before accessed");
        }
        if (manager.mExited) {
            throw new RuntimeException("Linphone Manager was already destroyed. "
                    + "Better use getCore and check returned value");
        }
        return manager;
    }

    public boolean getCallGsmON() {
        return mCallGsmON;
    }

    private void setCallGsmON(boolean on) {
        mCallGsmON = on;
        if (on && mCore != null) {
            mCore.pauseAllCalls();
        }
    }

    private String getString(int key) {
        return mContext.getString(key);
    }

    private void changeStatusToOffline() {
        if (mCore != null) {
            PresenceModel model = mCore.getPresenceModel();
            if (model != null) {
                model.setBasicStatus(PresenceBasicStatus.Closed);
                mCore.setPresenceModel(model);
            }
        }
    }

    private void destroyCore() {
        Log.w(TAG, "Destroying Core");
        mCore.stop();
    }

    private synchronized void destroyManager() {
        Log.w(TAG, "Destroying Manager");
        changeStatusToOffline();
        if (mTelephonyManager != null) {
            Log.i(TAG, "Unregistering phone state listener");
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if (mTimer != null) mTimer.cancel();
        Log.w(TAG, "core is null " + (mCore == null));
        if (mCore != null) {
            destroyCore();
            mCore = null;
        }
    }

    synchronized void startLibLinphone(boolean isPush, CoreListener listener) {
        String basePath = mContext.getFilesDir().getAbsolutePath();
        Log.d(TAG + " Current thread trace", Thread.currentThread().getName());
        try {
            mCore = Factory.instance().createCore(basePath + "/.linphonerc", basePath + "/linphonerc", mContext);
            mCore.addListener(listener);
            if (isPush) {
                mCore.enterBackground();
            }
            mCore.start();
            mIterateRunnable = () -> {
                if (mCore != null) {
                    mCore.iterate();
                }
            };
            TimerTask lTask = new TimerTask() {
                @Override
                public void run() {
                    sHandler.post(mIterateRunnable);
                }
            };
            mTimer = new Timer("Linphone scheduler");
            mTimer.schedule(lTask, 0, 20);
            configureCore();
        } catch (Exception e) {
            Log.e(TAG, "Cannot start linphone");
        }
        H264Helper.setH264Mode(H264Helper.MODE_AUTO, mCore);
    }
    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private synchronized void configureCore() {
        Log.i(TAG, "Configuring Core");
        mCore.setZrtpSecretsFile(mBasePath + "/zrtp_secrets");
        String appName = mContext.getResources().getString(R.string.user_agent);
        String androidVersion = BuildConfig.VERSION_NAME;
        String userAgent = appName + "/" + androidVersion + "LinphoneSDK";
        mCore.setUserAgent(userAgent,
                getString(R.string.linphone_sdk_version)
                        + " (" + getString(R.string.linphone_sdk_branch)
                        + ")");
        int availableCores = Runtime.getRuntime().availableProcessors();
        Log.w(TAG, "MediaStreamer : " + availableCores + " cores detected and configured");
        mCallGsmON = false;
        Log.i(TAG, "Core configured");
    }

    public synchronized void destroy() {
        destroyManager();
        mExited = true;
    }
}