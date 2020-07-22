package by.aermakova.prettysip.logic.manager;

import android.app.NotificationChannel;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import by.aermakova.prettysip.linphoneApi.LinphoneManager;

/**
 * Settings of ringtone and vibration while incoming call
 */

public class CustomRingtoneManager {
    private Context mContext;

    private MediaPlayer mMediaPlayer;
    private Ringtone mRingtone;
    private Vibrator mVibrator;
    private NotificationChannel mChannel;

    public CustomRingtoneManager(Context mContext, NotificationChannel mChannel) {
        this.mContext = mContext;
        this.mChannel = mChannel;
    }

    public void playRingtone() {
        Uri uri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_RINGTONE);
        if (!LinphoneManager.getInstance().getCallGsmON()) {
            mRingtone = android.media.RingtoneManager.getRingtone(mContext, uri);
            mRingtone.play();
        } else {
            mMediaPlayer = MediaPlayer.create(mContext, uri);
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
            }
        }
        if (statusRingVibrate() && !LinphoneManager.getInstance().getCallGsmON()) {
            long[] pattern = {0, 100, 200, 300, 400};
            mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            if (mVibrator != null && mVibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build();
                    mVibrator.vibrate(VibrationEffect.createWaveform(pattern, 0), audioAttributes);
                } else {
                    mVibrator.vibrate(pattern, 0);
                }
            }
        }
    }

    public void stopRingtone() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
        if (mRingtone != null) {
            mRingtone.stop();
        }
        if (mVibrator != null) {
            mVibrator.cancel();
        }
    }

    private boolean statusRingVibrate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && mChannel != null) {
            return mChannel.shouldVibrate();
        }
        AudioManager manager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        return (manager != null && manager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) ||
                (manager != null && manager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL);
    }
}