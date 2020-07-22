package by.aermakova.prettysip.ui.activity.auth;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import by.aermakova.prettysip.R;
import by.aermakova.prettysip.logic.util.Variables;
import by.aermakova.prettysip.ui.StartLinphoneServiceCommon;

/**
 * Base activity for all authorization flow
 */

public class AuthorizationActivity extends StartLinphoneServiceCommon {
    private final String TAG = getClass().getSimpleName();
    private NavController mNavController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment_auth);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WelcomeFragment.CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mNavController.navigate(R.id.action_welcomeFragment_to_qrScanFragment);
            } else {
                mNavController.navigate(R.id.action_welcomeFragment_to_enterCodeFragment);
            }
        }
    }

    @Override
    public void onServiceReady() {
        subscribeToFirebase();
        super.onServiceReady();
    }

    private void subscribeToFirebase() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "getInstanceId failed", task.getException());
            }
        });

        String key = Variables.getAuthInformation().getKey();
        Log.i(TAG, "Firebase key = " + key);
        FirebaseMessaging.getInstance().subscribeToTopic(key)
                .addOnCompleteListener(task -> Log.i(TAG, "Fiberbase task = " + task.getResult()));
    }
}