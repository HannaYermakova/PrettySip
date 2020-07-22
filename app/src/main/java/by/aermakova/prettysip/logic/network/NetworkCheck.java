package by.aermakova.prettysip.logic.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import by.aermakova.prettysip.logic.util.Variables;

/**
 * Utilitarian class to checking network connection
 */

public class NetworkCheck {
    private Context context;
    private ConnectivityManager connectivityManager;
    private NetworkCallback networkCallback;

    public NetworkCheck(Context context) {
        this.context = context;
    }

    public void registerNetworkCallback() {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                networkCallback = new NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        Variables.isNetworkConnected = true;
                    }

                    @Override
                    public void onLost(Network network) {
                        Variables.isNetworkConnected = false;
                    }
                };
                if (connectivityManager != null) {
                    connectivityManager.registerDefaultNetworkCallback(networkCallback);
                }
                Variables.isNetworkConnected = false;
            } catch (Exception e) {
                e.printStackTrace();
                Variables.isNetworkConnected = false;
            }
        }
    }

    public void isNetworkAvailableForMinAPI() {
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        Variables.isNetworkConnected = (networkInfo != null);
    }

    public void unregisterNetworkCallback() {
        if (networkCallback != null)
            connectivityManager.unregisterNetworkCallback(networkCallback);
    }
}