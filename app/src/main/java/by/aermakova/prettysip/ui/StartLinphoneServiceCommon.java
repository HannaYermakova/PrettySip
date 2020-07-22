package by.aermakova.prettysip.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import by.aermakova.prettysip.linphoneApi.LinphoneService;
import by.aermakova.prettysip.ui.activity.BaseActivity;
import by.aermakova.prettysip.ui.activity.main.MainActivity;

/**
 * Decorator for {@link BaseActivity} helps to start {@link LinphoneService}
 * on {@link by.aermakova.prettysip.ui.activity.auth.EnterSMSCodeFragment}
 * or {@link by.aermakova.prettysip.ui.activity.splash.SplashActivity}
 */

public abstract class StartLinphoneServiceCommon extends BaseActivity {
    private final String TAG = getClass().getSimpleName();
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    public void onServiceReady() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public void checkService() {
        if (LinphoneService.isReady()) {
            onServiceReady();
        } else {
            Log.i(TAG, "Start linphone service");
            startService(new Intent().setClass(this, LinphoneService.class));
            new ServiceWaitThread().start();
        }
    }

    private class ServiceWaitThread extends Thread {
        public void run() {
            while (!LinphoneService.isReady()) {
                try {
                    sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException("waiting thread sleep() has been interrupted");
                }
            }
            mHandler.post(StartLinphoneServiceCommon.this::onServiceReady);
        }
    }
}