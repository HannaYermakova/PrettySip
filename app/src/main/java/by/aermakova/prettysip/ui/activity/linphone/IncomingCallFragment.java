package by.aermakova.prettysip.ui.activity.linphone;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.linphone.core.Call;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.aermakova.prettysip.R;
import by.aermakova.prettysip.linphoneApi.LinphoneManager;
import by.aermakova.prettysip.logic.implementation.ICallLogic;
import by.aermakova.prettysip.logic.manager.AudioManagerSettings;
import by.aermakova.prettysip.logic.util.Variables;
import by.aermakova.prettysip.ui.activity.NetworkCheckFragment;
import by.aermakova.prettysip.ui.custom.NamedButton;

/**
 * Fragment for accepting incoming call without video. Logic implementation in {@link IncomingCallPresenter} via {@link ICallLogic}
 */
public class IncomingCallFragment extends NetworkCheckFragment implements ICallLogic.View {
    private static final String TAG = IncomingCallFragment.class.getSimpleName();

    private ICallLogic.Presenter mPresenter;
    private View mView;
    private Core mCore;
    @BindView(R.id.accept_call)
    NamedButton mAcceptBtn;
    @BindView(R.id.hang_up_call)
    NamedButton mHangUpBtn;
    @BindView(R.id.accept_video_call)
    NamedButton mAcceptVidBtn;
    @BindView(R.id.incoming_call_txt)
    LinearLayout mCallText;

    private AudioManagerSettings mAudioManagerSettings;
    private CoreListenerStub mCoreListener;
    private Handler mHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_audio_call, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new IncomingCallPresenter(this, getLifecycle());
        initView();
        mAudioManagerSettings = Variables.audioManagerSettings;
        mCore = LinphoneManager.getCore();
        mCoreListener = new CoreListenerStub() {
            @Override
            public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
                Log.i(TAG, "Incoming call " + state.name());
                if (state == Call.State.End || state == Call.State.Released) {
                    NavController controller = Navigation.findNavController(mView);
                    if (controller.getCurrentDestination() != null &&
                            controller.getCurrentDestination().getId() == R.id.incomingCallFragment)
                        controller.navigate(R.id.action_incomingCallFragment_to_callEndFragment);
                }
            }
        };

        mAcceptBtn.setOnClickListener(view1 -> {
            startTalkView();
            Call call = getCall();
            if (call != null) {
                CallParams params = mCore.createCallParams(call);
                mCore.enableEchoCancellation(true);
                params.enableVideo(true);
                call.acceptWithParams(params);
            }
            mAcceptBtn.setVisibility(View.INVISIBLE);
            mHangUpBtn.setBackgroundImage(getResources().getDrawable(R.drawable.slide_cancel_call), R.string.call_cancel);
        });

        mHangUpBtn.setOnClickListener(view1 -> {
            Call call = getCall();
            if (call != null) {
                call.terminate();
            } else {
                NavController controller = Navigation.findNavController(mView);
                if (controller.getCurrentDestination() != null &&
                        controller.getCurrentDestination().getId() == R.id.incomingCallFragment)
                    controller.navigate(R.id.action_incomingCallFragment_to_callEndFragment);
            }
        });

        mAcceptVidBtn.setOnClickListener(view1 -> {
            startTalkView();
            Call call = getCall();
            if (call != null) {
                CallParams params = mCore.createCallParams(call);
                mCore.enableEchoCancellation(true);
                params.enableVideo(true);
                call.acceptWithParams(params);
                startVideoFragment();
            }
        });
    }

    private Call getCall() {
        Call call = null;
        if (mCore.getCallsNb() > 0) {
            call = mCore.getCurrentCall();
            if (call == null) {
                call = mCore.getCalls()[0];
            }
        }
        return call;
    }

    private void startVideoFragment() {
        NavController controller = Navigation.findNavController(mView);
        if (controller.getCurrentDestination() != null &&
                controller.getCurrentDestination().getId() == R.id.incomingCallFragment)
            controller.navigate(R.id.action_incomingCallFragment_to_videoCallFragment);
    }

    private void initView() {
        mAcceptBtn.setBackgroundImage(getResources().getDrawable(R.drawable.slide_accept_call), R.string.call_accept);
        mHangUpBtn.setBackgroundImage(getResources().getDrawable(R.drawable.slide_hung_up), R.string.call_hung_up);
        mAcceptVidBtn.setBackgroundImage(getResources().getDrawable(R.drawable.slide_accept_video_call), R.string.call_video);
    }

    private void startTalkView() {
       requireActivity().runOnUiThread(() -> mCallText.setVisibility(View.INVISIBLE));
        if (Variables.iTimer != null) Variables.iTimer.startTalk();
    }


    @Override
    public void unmuteMic() {
        Variables.isActiveCall = true;
        if(mAudioManagerSettings != null){
            mAudioManagerSettings.unmuteMic();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCore.addListener(mCoreListener);
    }

    @Override
    public void onPause() {
        mCore.removeListener(mCoreListener);
        super.onPause();
    }
}