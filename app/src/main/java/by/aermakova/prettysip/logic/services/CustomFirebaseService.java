package by.aermakova.prettysip.logic.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.linphone.core.Call;
import org.linphone.core.Core;

import java.util.Map;
import java.util.Objects;

import by.aermakova.prettysip.linphoneApi.LinphoneContext;
import by.aermakova.prettysip.linphoneApi.LinphoneManager;
import by.aermakova.prettysip.logic.manager.PreferencesManager;

/**
 * Service for handling message from Firebase
 */

public class CustomFirebaseService extends FirebaseMessagingService {
    private static final String TAG = CustomFirebaseService.class.getSimpleName();
    private static final Handler handler = new Handler(Looper.getMainLooper());

    private Runnable mPushReceivedRunnable = () -> {
        if (!LinphoneContext.isReady()) {
            Log.i(TAG, "Starting context");
            new LinphoneContext(getApplicationContext());
            LinphoneContext.instance().start(true);
        } else {
            Log.i(TAG, "Notifying core");
            Core core = LinphoneManager.getCore();
            if (core != null) {
                core.ensureRegistered();
            }
        }
    };

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }

    @Override

    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> map = remoteMessage.getData();
        if (map.containsKey("type")) {
            String type = map.get("type");
            Log.i(TAG, "Get message from Firebase. Type: " + type);
            if (Objects.equals(type, "call")) {
                if (PreferencesManager.getInstance().getAccessCall(this))
                    wakeUpDevice();
                dispatchOnMainThread(mPushReceivedRunnable);
            } else if (Objects.equals(type, "endcall")) {
                Call call = LinphoneManager.getCall();
                if (call != null) {
                    call.terminate();
                }
            }
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    private void wakeUpDevice() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }
        Log.d(TAG, "PowerManager check. Is screen on: " + isScreenOn);
        if (!isScreenOn) {
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK |
                            PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.ON_AFTER_RELEASE, "MyLock");
            wl.acquire(50);
            wl.release();
        }
    }

    private static void dispatchOnMainThread(Runnable r) {
        handler.post(r);
    }
}