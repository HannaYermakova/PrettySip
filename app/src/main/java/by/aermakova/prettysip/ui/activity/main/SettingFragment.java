package by.aermakova.prettysip.ui.activity.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.aermakova.prettysip.R;
import by.aermakova.prettysip.logic.manager.PreferencesManager;

/**
 * Settings and account information fragment
 */
public class SettingFragment extends Fragment {
    private View mView;

    @BindView(R.id.back_settings)
    ImageButton mBtnBackSettings;
    @BindView(R.id.on_off_accept_calls)
    Switch mSwitchAcceptCalls;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        mBtnBackSettings.setOnClickListener(v -> Navigation.findNavController(mView).popBackStack());
        mSwitchAcceptCalls.setChecked(PreferencesManager.getInstance().getAccessCall(getActivity()));
        mSwitchAcceptCalls.setOnCheckedChangeListener((buttonView, isChecked) -> PreferencesManager.getInstance().setAccessCall(getActivity(), isChecked));
    }
}