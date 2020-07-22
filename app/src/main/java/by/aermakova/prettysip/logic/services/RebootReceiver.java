package by.aermakova.prettysip.logic.services;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import by.aermakova.prettysip.linphoneApi.LinphoneService;

/**
 * Handler of reboot action
 */
public class RebootReceiver extends BroadcastReceiver {
    private final String TAG = getClass().getSimpleName();

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SHUTDOWN)) {
                Log.d(TAG, "Device is shutting down, destroying Core to unregister");
                context.stopService(new Intent(Intent.ACTION_MAIN).setClass(context, LinphoneService.class));
            } else if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                if (!LinphoneService.isReady()) {
                    Log.d(TAG, "Device is starting, action Boot completed");
                    startService(context);
                }
            }
        }
    }

    private void startService(Context context) {
        Intent serviceIntent = new Intent(Intent.ACTION_MAIN);
        serviceIntent.setClass(context, LinphoneService.class);
        serviceIntent.putExtra("ForceStartForeground", true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}