package by.aermakova.prettysip.ui.activity.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.aermakova.prettysip.R;
import by.aermakova.prettysip.logic.implementation.IAuthCodeLogic;
import by.aermakova.prettysip.logic.util.Util;
import by.aermakova.prettysip.ui.activity.NetworkCheckFragment;

/**
 * 3rd fragment while authorization. Manually entering of the unique code. Logic implementation in {@link EnterAuthCodePresenter} via {@link IAuthCodeLogic}
 */

public class EnterCodeFragment extends NetworkCheckFragment implements IAuthCodeLogic.View {

    private IAuthCodeLogic.Presenter mPresenter;
    private View mView;
    private String mAuthCode;

    @BindView(R.id.enter_code)
    EditText mAuthCodeEditText;
    @BindView(R.id.enter_code_btn)
    Button mContinueBtn;
    @BindView(R.id.enter_code_manually_warning)
    TextView mWarning;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_enter_code_manually, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new EnterAuthCodePresenter(this, getLifecycle());
        mContinueBtn.setEnabled(false);
        mAuthCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAuthCode = mAuthCodeEditText.getText().toString().trim();
                boolean check = Util.checkValidAuth(mAuthCode);
                mContinueBtn.setEnabled(check);
                mWarning.setVisibility(View.INVISIBLE);
                if (check) {
                    mAuthCodeEditText.setBackgroundResource(R.drawable.ic_text_input_pressed);
                    mAuthCodeEditText.setTextColor(getResources().getColor(R.color.white));
                } else {
                    mAuthCodeEditText.setBackgroundResource(R.drawable.ic_text_input);
                    mAuthCodeEditText.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mContinueBtn.setOnClickListener(v -> {
            if (Util.checkValidAuth(mAuthCode)) {
                if (isNetworkConnected(getActivity())) {
                    mPresenter.checkAndSendAuthCode(mAuthCode);
                }
            } else {
                showInvalidQRMessage();
            }
        });
    }

    @Override
    public void openEnterPhoneNumberActivity() {
        NavController controller = Navigation.findNavController(mView);
        if (controller.getCurrentDestination() != null &&
                controller.getCurrentDestination().getId() == R.id.enterCodeFragment)
            controller.navigate(R.id.action_enterCodeFragment_to_enterPhoneNumberFragment);
    }

    @Override
    public void showInvalidQRMessage() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                mAuthCodeEditText.setBackgroundResource(R.drawable.ic_text_input_wrong);
                mAuthCodeEditText.setTextColor(getResources().getColor(R.color.red_light));
                mWarning.setVisibility(View.VISIBLE);
            });
        }
    }

    @Override
    public void showNoConnectionMessage() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                mAuthCodeEditText.setBackgroundResource(R.drawable.ic_text_input_wrong);
                mAuthCodeEditText.setTextColor(getResources().getColor(R.color.red_light));
            });
            showToastMessage(getActivity(), R.string.no_server_connection);
        }
    }
}