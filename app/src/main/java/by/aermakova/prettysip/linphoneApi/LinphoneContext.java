package by.aermakova.prettysip.linphoneApi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.Factory;

import by.aermakova.prettysip.R;
import by.aermakova.prettysip.logic.manager.PreferencesManager;
import by.aermakova.prettysip.logic.util.Constants;
import by.aermakova.prettysip.logic.manager.CustomRingtoneManager;
import by.aermakova.prettysip.logic.util.Variables;
import by.aermakova.prettysip.ui.activity.linphone.CallActivity;

import static android.app.Notification.FLAG_INSISTENT;
import static android.app.Notification.FLAG_ONGOING_EVENT;
import static android.content.Intent.ACTION_MAIN;

/**
 * Context wrapper for Linphone API usage
 */

public class LinphoneContext {
    private final String TAG = getClass().getSimpleName();
    private static LinphoneContext instance = null;

    private Context mContext;
    private CoreListenerStub mListener;
    private LinphoneManager mLinphoneManager;
    private NotificationChannel mChannel;

    public static LinphoneContext instance() {
        if (instance == null) {
            throw new RuntimeException("Linphone Context not available!");
        }
        return instance;
    }

    public static boolean isReady() {
        return instance != null;
    }

    public LinphoneContext(Context context) {
        mContext = context;
        Factory.instance().setLogCollectionPath(context.getFilesDir().getAbsolutePath());
        instance = this;
        Log.i(TAG, "Ready");
        createNotificationChannel();
        CustomRingtoneManager ringtoneManager = new CustomRingtoneManager(mContext, mChannel);
        mListener = new CoreListenerStub() {
            @Override
            public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
                Log.i(TAG, "Call state is " + state);
                if (state == Call.State.IncomingReceived || state == Call.State.IncomingEarlyMedia) {
                    if (conditions(call)) {
                        Log.i(TAG, "On incoming call received");
                        onIncomingReceived();
                        ringtoneManager.playRingtone();
                    }
                    if (!LinphoneService.isReady()) {
                        Log.i(TAG, "Service not running, starting it");
                        Intent intent = new Intent(ACTION_MAIN);
                        intent.setClass(mContext, LinphoneService.class);
                        mContext.startService(intent);
                    }
                } else if (state == Call.State.Connected) {
                    Log.i(TAG, "On incoming call connected");
                    onIncomingReceived();
                    ringtoneManager.stopRingtone();
                    NotificationManagerCompat.from(mContext).cancel(Constants.NOTIFICATION_ID);
                } else if (state == Call.State.End
                        || state == Call.State.Released
                        || state == Call.State.Error) {
                    ringtoneManager.stopRingtone();
                    NotificationManagerCompat.from(mContext).cancel(Constants.NOTIFICATION_ID);
                    Variables.isActiveCall = false;
                    if (LinphoneService.isReady()) {
                        Log.i(TAG, "On incoming call end");
                    }
                }
            }
        };
        mLinphoneManager = new LinphoneManager(context);
    }

    public void start(boolean isPush) {
        Log.i(TAG, "Starting, push status is " + isPush);
        mLinphoneManager.startLibLinphone(isPush, mListener);
    }

    public void destroy() {
        Log.i(TAG, "Destroying");
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.removeListener(mListener);
        }
        if (mLinphoneManager != null) {
            mLinphoneManager.destroy();
        }
        instance = null;
    }

    public void updateContext(Context context) {
        mContext = context;
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(
                    mContext.getResources().getString(R.string.app_name),
                    mContext.getResources().getString(R.string.incoming_call),
                    NotificationManager.IMPORTANCE_HIGH);
            mChannel.setSound(null, null);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{0, 100, 200, 300, 400});
        }
    }

    public LinphoneManager getLinphoneManager() {
        return mLinphoneManager;
    }

    private void onIncomingReceived() {
        Intent intent = new Intent(mContext.getApplicationContext(), CallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = showNotification(mContext,
                mContext.getResources().getString(R.string.app_name),
                mContext.getResources().getString(R.string.incoming_call),
                mContext.getResources().getString(R.string.app_name),
                pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        if(mChannel != null){
            notificationManager.createNotificationChannel(mChannel);
        }
        Notification notification = builder.build();
        notification.flags |= FLAG_ONGOING_EVENT;
        notification.flags |= FLAG_INSISTENT;
        notificationManager.notify(Constants.NOTIFICATION_ID, notification);
    }

    private static NotificationCompat.Builder showNotification(Context context, String chanId, String message, String title, PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(context, chanId)
                .setColor(context.getColor(R.color.background_dark))
                .setContentTitle(title)
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_CALL)
                .setSound(null)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, true);
    }

    private boolean conditions(Call call) {
        boolean cond = PreferencesManager.getInstance().getAccessCall(mContext)
                && PreferencesManager.getInstance().getAuthCode(mContext) != null
                && call.getCallLog().getLocalAddress().getUsername().equals(
                PreferencesManager.getInstance().getSipUsername(mContext));
        if (!cond) {
            call.terminate();
        }
        return cond;
    }
}