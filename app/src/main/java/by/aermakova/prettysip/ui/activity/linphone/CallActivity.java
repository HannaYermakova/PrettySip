package by.aermakova.prettysip.ui.activity.linphone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.linphone.core.Call;
import org.linphone.core.Core;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.aermakova.prettysip.R;
import by.aermakova.prettysip.linphoneApi.LinphoneManager;
import by.aermakova.prettysip.logic.implementation.ITimer;
import by.aermakova.prettysip.logic.manager.AudioManagerSettings;
import by.aermakova.prettysip.logic.util.Constants;
import by.aermakova.prettysip.logic.util.Variables;
import by.aermakova.prettysip.ui.activity.BaseActivity;
import by.aermakova.prettysip.ui.activity.main.MainActivity;

/**
 * Base activity for incoming call
 */
public class CallActivity extends BaseActivity implements ITimer {

    private NavController mNavController;
    private Handler mHandler;

    @BindView(R.id.incoming_call_timer)
    LinearLayout mCallStart;
    @BindView(R.id.call_timer)
    Chronometer mCallTimer;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        ButterKnife.bind(this);
        mHandler = new Handler();
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment_call);
        Variables.iTimer = this;
        Variables.audioManagerSettings = new AudioManagerSettings(this, Variables.isActiveCall, getLifecycle());

        NotificationManagerCompat.from(this).cancel(Constants.NOTIFICATION_ID);
    }

    @Override
    public void startTalk() {
        if (!Variables.isActiveCall)
            mHandler.post(() -> {
                mCallStart.setVisibility(View.VISIBLE);
                mCallTimer.setVisibility(View.VISIBLE);
                startTimer();
            });
    }

    @Override
    public void finishTalk() {
        mHandler.post(() -> {
            mCallStart.setVisibility(View.GONE);
            mCallTimer.setVisibility(View.GONE);
        });
    }

    private void startTimer() {
        mCallTimer.setBase(SystemClock.elapsedRealtime());
        mCallTimer.start();
        mCallTimer.setOnChronometerTickListener(chronometer -> {
            if (mCallTimer.getText().equals(Constants.TALK_LIMIT)) {
                mNavController.navigate(R.id.callEndFragment);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Core core = LinphoneManager.getCore();
        if (core == null || core.getCurrentCall() == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        mNavController.navigate(R.id.callEndFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Call call;
        Core core = LinphoneManager.getCore();
        if (core != null && core.getCallsNb() > 0) {
            call = core.getCurrentCall();
            if (call != null) {
                call.terminate();
            }
        }
        Variables.iTimer = null;
        Variables.audioManagerSettings = null;
        Variables.isVideoCallAccepted = false;
    }
}