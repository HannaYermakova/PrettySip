package by.aermakova.prettysip.logic.manager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import by.aermakova.prettysip.logic.util.Variables;

/**
 * Audio settings controller for conversation
 */

public class AudioManagerSettings implements LifecycleObserver {
    private final String TAG = getClass().getSimpleName();
    private final AudioManager mAudioManager;
    private final Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothHeadset mBluetoothHeadset;
    private BluetoothReceiver mBluetoothReceiver;
    private HeadsetReceiver mHeadsetReceiver;
    private boolean mHeadsetConnect;
    private boolean mBluetoothConnect;

    public AudioManagerSettings(Context context, boolean unmute, Lifecycle lifecycle) {
        mContext = context;
        lifecycle.addObserver(this);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Log.i(TAG, "Mute microphone: " + !unmute);
        if (mAudioManager != null) {
            mAudioManager.setMicrophoneMute(!unmute);
        }
        setAudioSetting();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void turnOn() {
        mHeadsetReceiver = new HeadsetReceiver();
        mBluetoothReceiver = new BluetoothReceiver();
        mContext.registerReceiver(mHeadsetReceiver, new IntentFilter(AudioManager.ACTION_HEADSET_PLUG));
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
        filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mBluetoothReceiver, filter);
        bluetoothAdapterStateChanged();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void turnOff() {
        mContext.unregisterReceiver(mHeadsetReceiver);
        mContext.unregisterReceiver(mBluetoothReceiver);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void destroyAudioManager() {
        if (mAudioManager != null) {
            mAudioManager.setSpeakerphoneOn(false);
            Log.d(TAG, "Set mic mute false");
            mAudioManager.setMicrophoneMute(false);
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
        }
        if (mBluetoothAdapter != null && mBluetoothHeadset != null) {
            mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, mBluetoothHeadset);
        }
    }

    @SuppressLint("NewApi")
    public void setAudioSetting() {
        if (mHeadsetConnect || mBluetoothConnect || mAudioManager.isBluetoothScoOn()) {
            Log.d(TAG, "Set mode in call");
            mAudioManager.setMode(AudioManager.MODE_IN_CALL);
            mAudioManager.setSpeakerphoneOn(false);
            if (!mHeadsetConnect) {
                if (!mAudioManager.isBluetoothScoOn()) {
                    mAudioManager.setBluetoothScoOn(true);
                    mAudioManager.startBluetoothSco();
                }
            } else {
                mAudioManager.setBluetoothScoOn(false);
                mAudioManager.stopBluetoothSco();
            }
        } else if (Variables.isVideoCallAccepted) {
            Log.d(TAG, "Set mode in communication");
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            mAudioManager.setSpeakerphoneOn(true);
            mAudioManager.setBluetoothScoOn(false);
            mAudioManager.stopBluetoothSco();
        } else {
            Log.d(TAG, "Set mode normal");
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
            mAudioManager.setSpeakerphoneOn(false);
        }
    }

    public void unmuteMic() {
        Log.i(TAG, "Unmute microphone");
        if (mAudioManager != null)
            mAudioManager.setMicrophoneMute(false);
    }

    private void bluetoothAdapterStateChanged() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            BluetoothProfile.ServiceListener bluetoothServiceListener = new BluetoothProfile.ServiceListener() {
                public void onServiceConnected(int profile, BluetoothProfile proxy) {
                    if (profile == BluetoothProfile.HEADSET) {
                        Log.d(TAG, "Bluetooth headset profile connected");
                        mBluetoothHeadset = (BluetoothHeadset) proxy;
                        mBluetoothConnect = true;
                        setAudioSetting();
                    }
                }
                public void onServiceDisconnected(int profile) {
                    if (profile == BluetoothProfile.HEADSET) {
                        Log.d(TAG, "Bluetooth headset profile disconnected");
                        mBluetoothConnect = false;
                        setAudioSetting();
                    }
                }
            };

            mBluetoothAdapter.getProfileProxy(mContext, bluetoothServiceListener, BluetoothProfile.HEADSET);
        } else {
            Log.d(TAG, "Bluetooth adapter disabled");
        }
        setAudioSetting();
    }

    private class HeadsetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Log.d(TAG, "Headset unplugged");
                        mHeadsetConnect = false;
                        break;
                    case 1:
                        Log.d(TAG, "Headset plugged");
                        mHeadsetConnect = true;
                        break;
                }
                setAudioSetting();
            }
        }
    }

    private class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                Log.d(TAG, "Bluetooth Adapter state " + state);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                    case BluetoothAdapter.STATE_TURNING_OFF:
                    case BluetoothAdapter.ERROR:
                        mBluetoothConnect = false;
                        break;
                    case BluetoothAdapter.STATE_ON:
                    case BluetoothAdapter.STATE_TURNING_ON:
                        mBluetoothConnect = true;
                        break;
                    default:
                        break;
                }
            } else if (action != null && action.equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_DISCONNECTED);
                if (state == BluetoothHeadset.STATE_CONNECTED) {
                    Log.d(TAG, "Bluetooth headset connected");
                    mBluetoothConnect = true;
                } else if (state == BluetoothHeadset.STATE_DISCONNECTED) {
                    Log.d(TAG, "Bluetooth headset disconnected");
                    mBluetoothConnect = false;
                } else {
                    Log.d(TAG, "Bluetooth headset unknown state changed: " + state);
                }
            } else if (action != null && action.equals(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_AUDIO_DISCONNECTED);
                if (state == BluetoothHeadset.STATE_AUDIO_CONNECTED || state == BluetoothHeadset.STATE_AUDIO_CONNECTING) {
                    Log.d(TAG, "Bluetooth headset connected");
                    mBluetoothConnect = true;
                } else if (state == BluetoothHeadset.STATE_AUDIO_DISCONNECTED) {
                    Log.d(TAG, "Bluetooth headset disconnected");
                    mBluetoothConnect = false;
                }
            }
            setAudioSetting();
        }
    }
}