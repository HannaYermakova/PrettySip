package by.aermakova.prettysip.ui.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import by.aermakova.prettysip.R;
import by.aermakova.prettysip.logic.network.NetworkCheck;
import by.aermakova.prettysip.logic.util.Variables;

/**
 * Decorator helps to check network connection on fragments
 * and show error-message
 */

public class NetworkCheckFragment extends Fragment {
    private NetworkCheck mNetwork;
    private Toast mToast;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null)
            mNetwork = new NetworkCheck(getContext().getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        mNetwork.registerNetworkCallback();
    }

    @Override
    public void onPause() {
        super.onPause();
        mNetwork.unregisterNetworkCallback();
    }

    protected boolean isNetworkConnected(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mNetwork.isNetworkAvailableForMinAPI();
        }
        if (!Variables.isNetworkConnected) {
            showToastMessage(activity, R.string.network_is_not_available);
        }
        return Variables.isNetworkConnected;
    }

    protected void showToastMessage(Activity activity, int message) {
        if (activity != null)
            activity.runOnUiThread(() -> {
                if (mToast != null && mToast.getView().isShown()) {
                    mToast.setText(message);
                } else {
                    mToast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
                    mToast.show();
                }
            });
    }
}
