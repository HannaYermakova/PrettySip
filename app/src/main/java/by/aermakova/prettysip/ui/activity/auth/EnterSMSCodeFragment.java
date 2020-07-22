package by.aermakova.prettysip.ui.activity.auth;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.aermakova.prettysip.R;
import by.aermakova.prettysip.logic.implementation.IEnterSMSCode;
import by.aermakova.prettysip.logic.manager.PreferencesManager;
import by.aermakova.prettysip.logic.model.AuthInformation;
import by.aermakova.prettysip.logic.util.Constants;
import by.aermakova.prettysip.logic.util.Util;
import by.aermakova.prettysip.logic.util.Variables;
import by.aermakova.prettysip.ui.activity.NetworkCheckFragment;

/**
 * 5th fragment while authorization. Entering verify code from SMS. Logic implementation in {@link EnterSMSCodePresenter} via {@link IEnterSMSCode}
 */

public class EnterSMSCodeFragment extends NetworkCheckFragment implements IEnterSMSCode.View {
    private final String TAG = getClass().getSimpleName();

    private IEnterSMSCode.Presenter mPresenter;
    private String mSMSCode;

    @BindView(R.id.enter_sms_code)
    EditText mEnterSMSCode;
    @BindView(R.id.enter_sms_code_btn)
    Button mEnterSMSCodeBtn;
    @BindView(R.id.chronometer)
    TextView mChronometer;
    @BindView(R.id.invalid_code)
    TextView mWarning;
    @BindView(R.id.repeat_code_btn)
    Button mRepeatCodeBtn;
    @BindView(R.id.progress_circular_sms)
    ProgressBar mProgressBar;

    private Handler mHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enter_sms_code, container, false);
        ButterKnife.bind(this, view);
        startChronometer();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new EnterSMSCodePresenter(this, getLifecycle());
        mEnterSMSCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSMSCode = mEnterSMSCode.getText().toString().trim();
                boolean check = Util.checkValidInputSMSCode(mSMSCode);
                mEnterSMSCodeBtn.setEnabled(check);
                mWarning.setVisibility(View.INVISIBLE);
                if (check) {
                    mEnterSMSCode.setBackgroundResource(R.drawable.ic_text_input_pressed);
                    mEnterSMSCode.setTextColor(getResources().getColor(R.color.white));
                } else {
                    mEnterSMSCode.setBackgroundResource(R.drawable.ic_text_input);
                    mEnterSMSCode.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        AuthInformation information = Variables.getAuthInformation();
        String code = information.getCode();
        if (code != null) {
            mEnterSMSCode.setText(code);
        }

        mEnterSMSCodeBtn.setOnClickListener(v -> {
            if (information.getDeviceId() == null) {
                information.setDeviceId(Util.getPseudoDeviceID());
            }
            if (isNetworkConnected(getActivity())) {
                mPresenter.sendSMSCode(mSMSCode);
                mEnterSMSCodeBtn.setEnabled(false);
                changeRepeatButton(false);
            }
        });

        mRepeatCodeBtn.setOnClickListener(v -> {
            if (isNetworkConnected(getActivity())) {
                mPresenter.repeatGetCode();
                hideRepeatButton();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void startChronometer() {
        mChronometer.setVisibility(View.VISIBLE);
        new CountDownTimer(Constants.FREEZE_TIME, 1000) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                @SuppressLint("DefaultLocale") String v = String.format("%02d", millisUntilFinished / 60000);
                int va = (int) ((millisUntilFinished % 60000) / 1000);
                mChronometer.setText(v + ":" + String.format("%02d", va));
            }

            @Override
            public void onFinish() {
                mRepeatCodeBtn.setEnabled(true);
                mChronometer.setVisibility(View.INVISIBLE);
            }
        }.start();
    }

    @Override
    public void hideRepeatButton() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                mRepeatCodeBtn.setEnabled(false);
                mEnterSMSCodeBtn.setEnabled(false);
                startChronometer();
            });
        }
    }

    @Override
    public void changeRepeatButton(boolean value) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                mEnterSMSCodeBtn.setEnabled(false);
                mRepeatCodeBtn.setVisibility(View.INVISIBLE);
                mChronometer.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
            });
        }
    }

    @Override
    public void setInfoToPreferences(String key, String phone, AccountInfo accountInfo) {
        if (getActivity() != null) {
            PreferencesManager preferencesManager = PreferencesManager.getInstance();
            if (key != null)
                preferencesManager.setAuthCode(getActivity().getApplicationContext(), key);
            if (phone != null)
                preferencesManager.setRegistrationPhone(getActivity().getApplicationContext(), phone);
            if (accountInfo != null)
                preferencesManager.setAccountInfo(getActivity().getApplicationContext(), accountInfo);
        }
    }

    @Override
    public void openMainActivity() {
        if (getActivity() != null) {
            ((AuthorizationActivity) getActivity()).checkService();
        }
    }

    @Override
    public void showInvalidCredentialsMessage(int warningMessage) {
        mHandler.post(() -> {
            mEnterSMSCode.setBackgroundResource(R.drawable.ic_text_input_wrong);
            mEnterSMSCode.setTextColor(getResources().getColor(R.color.red_light));
            mEnterSMSCodeBtn.setEnabled(false);
            mWarning.setText(warningMessage);
            mWarning.setVisibility(View.VISIBLE);
        });
        mHandler.postDelayed(() -> {
            if (getContext() != null) {
                mEnterSMSCode.setText("");
                mEnterSMSCode.setBackgroundResource(R.drawable.ic_text_input);
                mEnterSMSCode.setTextColor(getResources().getColor(R.color.colorAccent));
                mWarning.setVisibility(View.INVISIBLE);
            }
        }, Constants.BLOCK_BUTTON);
    }
}