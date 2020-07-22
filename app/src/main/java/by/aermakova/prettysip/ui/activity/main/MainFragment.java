package by.aermakova.prettysip.ui.activity.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.aermakova.prettysip.R;
import by.aermakova.prettysip.linphoneApi.LinphoneContext;
import by.aermakova.prettysip.linphoneApi.LinphoneService;
import by.aermakova.prettysip.logic.implementation.IMain;
import by.aermakova.prettysip.logic.manager.PreferencesManager;
import by.aermakova.prettysip.logic.model.UserSipInfo;
import by.aermakova.prettysip.logic.model.requestMessages.DeviceIdRequestMessage;
import by.aermakova.prettysip.logic.util.Util;
import by.aermakova.prettysip.ui.activity.CustomAlarmFragment;
import by.aermakova.prettysip.ui.custom.CustomAlertDialog;

/**
 * Main activity of the application. Logic implementation in {@link MainPresenter} via {@link IMain}
 */
public class MainFragment extends CustomAlarmFragment implements IMain.View {
    private final String TAG = getClass().getSimpleName();
    private IMain.Presenter mPresenter;
    private View mView;

    @BindView(R.id.menu_settings)
    ImageButton mSettingsBtn;

    @BindView(R.id.sip_check)
    ImageButton mSipLed;

    @BindView(R.id.overlay_error)
    RelativeLayout mErrorLayout;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private String[] mPermissions = {
            Manifest.permission.RECORD_AUDIO
    };

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, mView);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new MainPresenter(this, getLifecycle());
        ensureServiceIsRunning();
        showSipConnection(false);
        mErrorLayout.setOnClickListener(v -> {
            //remove click listener
        });

        mSettingsBtn.setOnClickListener(v -> openSettingsActivity());
        checkPermissions();
        getSipInfo();
    }

    private void getSipInfo() {
        DeviceIdRequestMessage message = new DeviceIdRequestMessage(
                PreferencesManager.getInstance().getAuthCode(getActivity()),
                PreferencesManager.getInstance().getRegistrationPhone(getActivity()),
                Util.getPseudoDeviceID());
        mPresenter.sendGetSipInfoMessage(message);
    }

    private void openSettingsActivity() {
        NavController controller = Navigation.findNavController(mView);
        if (controller.getCurrentDestination() != null &&
                controller.getCurrentDestination().getId() == R.id.mainFragment)
            controller.navigate(R.id.action_mainFragment_to_settingsFragment);
    }

    @Override
    public void saveUserInfoInCache(UserSipInfo userSipInfo) {
        PreferencesManager.getInstance().setSipUsername(
                requireActivity().getApplicationContext(),
                userSipInfo.getUsername());
    }

    @Override
    public void showSipConnection(boolean value) {
        mSipLed.setEnabled(value);
    }

    @Override
    public void onResume() {
        super.onResume();
        ensureServiceIsRunning();
    }

    private void ensureServiceIsRunning() {
        if (!LinphoneService.isReady()) {
            if (!LinphoneContext.isReady() && getActivity() != null) {
                new LinphoneContext(getActivity().getApplicationContext());
                LinphoneContext.instance().start(false);
                Log.d(TAG, "Context created & started");
            }
            Log.d(TAG, "Starting Service");
            try {
                if (getActivity() != null)
                    getActivity().startService(new Intent().setClass(getActivity(), LinphoneService.class));
            } catch (IllegalStateException ise) {
                Log.e(TAG, "Couldn't start service, exception: " + ise);
            }
        }
    }

    private void checkAllPermissions(Activity activity) {
        List<String> listNeedsPermissions = new LinkedList<>();
        for (String permission : mPermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                listNeedsPermissions.add(permission);
            }
        }
        if (!listNeedsPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listNeedsPermissions.toArray(new String[0]), 88);
        }
    }

    private void checkPermissions() {
        Activity activity = getActivity();
        if (!PreferencesManager.getInstance().getAdditionalPermission(activity)) {
            if ("xiaomi".equalsIgnoreCase(android.os.Build.MANUFACTURER) && Util.isMiUi()) {
                checkXiaomiAutoStartPermission();
                checkXiaomiBatterySaver();
            }
            if ("huawei".equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
                checkHuaweiPermission();
            }
        }
        checkAllPermissions(activity);
        PreferencesManager.getInstance().setAdditionalPermission(activity, true);
    }

    private void checkXiaomiBatterySaver() {
        createCustomAlarm(getContext(), R.string.turn_off_battery_saver,
                new CustomAlertDialog.ICustomAlert() {
                    @Override
                    public void okLogic() {
                        try {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"));
                            intent.putExtra("package_name", requireActivity().getPackageName());
                            intent.putExtra("package_label", getText(R.string.app_name));
                            startActivity(intent);

                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void cancelLogic() {
                    }
                }, false);
    }

    /**
     * Additional permissions for Xiaomi devices
     */

    private void checkXiaomiAutoStartPermission() {
        createCustomAlarm(getContext(), R.string.give_reboot_perm,
                new CustomAlertDialog.ICustomAlert() {
                    @Override
                    public void okLogic() {
                        try {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                            startActivity(intent);
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void cancelLogic() {
                    }
                }, false);
    }

    /**
     * Additional permission for Huawei devices
     */

    private void checkHuaweiPermission() {
        createCustomAlarm(getContext(), R.string.give_reboot_perm,
                new CustomAlertDialog.ICustomAlert() {
                    @Override
                    public void okLogic() {
                        try {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                            startActivity(intent);
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void cancelLogic() {
                    }
                }, false);
    }
}