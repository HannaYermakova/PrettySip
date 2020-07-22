package by.aermakova.prettysip.linphoneApi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.linphone.core.Core;
import org.linphone.core.Factory;
import org.linphone.core.LogCollectionState;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import by.aermakova.prettysip.R;

/**
 * Service of Linphone API usage
 */

public class LinphoneService extends Service {
    private final String TAG = getClass().getSimpleName();

    private static LinphoneService instance;
    private boolean misLinphoneContextOwned;

    public static boolean isReady() {
        return instance != null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        misLinphoneContextOwned = false;
        if (!LinphoneContext.isReady()) {
            new LinphoneContext(getApplicationContext());
            misLinphoneContextOwned = true;
        }
        String basePath = getFilesDir().getAbsolutePath();
        Factory.instance().setLogCollectionPath(basePath);
        Factory.instance().enableLogCollection(LogCollectionState.Enabled);
        Factory.instance().setDebugMode(true, getString(R.string.app_name));
        try {
            copyIfNotExist(R.raw.linphonerc_default, basePath + "/.linphonerc");
            copyFromPackage(R.raw.linphonerc_factory, "linphonerc");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (instance != null) {
            return START_STICKY;
        }
        instance = this;
        if (misLinphoneContextOwned) {
            LinphoneContext.instance().start(false);
        } else {
            LinphoneContext.instance().updateContext(getApplicationContext());
        }
        Log.i(TAG, "Started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LinphoneContext.instance().destroy();
        instance = null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.terminateAllCalls();
        }
        stopSelf();
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "Task removed, stop service");
    }

    private void copyIfNotExist(int resourceId, String target) throws IOException {
        File lFileToCopy = new File(target);
        if (!lFileToCopy.exists()) {
            copyFromPackage(resourceId, lFileToCopy.getName());
        }
    }

    private void copyFromPackage(int resourceId, String target) throws IOException {
        FileOutputStream lOutputStream = openFileOutput(target, 0);
        InputStream lInputStream = getResources().openRawResource(resourceId);
        int readByte;
        byte[] buff = new byte[8048];
        while ((readByte = lInputStream.read(buff)) != -1) {
            lOutputStream.write(buff, 0, readByte);
        }
        lOutputStream.flush();
        lOutputStream.close();
        lInputStream.close();
    }
}