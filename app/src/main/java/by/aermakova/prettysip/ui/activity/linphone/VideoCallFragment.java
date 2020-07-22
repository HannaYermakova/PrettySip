package by.aermakova.prettysip.ui.activity.linphone;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.VideoDefinition;

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
 * Fragment for accepting incoming call with video. Logic implementation in {@link IncomingCallPresenter} via {@link ICallLogic}
 */
public class VideoCallFragment extends NetworkCheckFragment implements ICallLogic.View {
    private final String TAG = getClass().getSimpleName();

    private ICallLogic.Presenter mPresenter;
    private View mView;
    private Core mCore;

    @BindView(R.id.video_hang)
    NamedButton mHangUp;
    @BindView(R.id.video_hang_mute)
    NamedButton mAcceptUnmuteCall;

    @BindView(R.id.videoSurface)
    TextureView mVideoView;
    @BindView(R.id.videoCaptureSurface)
    TextureView mCaptureView;

    private AudioManagerSettings mAudioManagerSettings;
    private CoreListenerStub mCoreListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_video_call, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new IncomingCallPresenter(this, getLifecycle());
        initView();
        mCore = LinphoneManager.getCore();
        if (mCore != null)
            mCore.setNativeVideoWindowId(mVideoView);
        mCoreListener = new CoreListenerStub() {
            @Override
            public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
                Log.i(TAG, "Incoming call " + state.name());
                if (state == Call.State.End || state == Call.State.Released) {
                    NavController controller = Navigation.findNavController(mView);
                    if (controller.getCurrentDestination() != null &&
                            controller.getCurrentDestination().getId() == R.id.videoCallFragment)
                        controller.navigate(R.id.action_videoCallFragment_to_callEndFragment);
                }
            }
        };

        mHangUp.setOnClickListener(v -> {
            if (mCore.getCallsNb() > 0) {
                Call call = mCore.getCurrentCall();
                if (call == null) {
                    call = mCore.getCalls()[0];
                }
                call.terminate();
            }
        });

        mAudioManagerSettings = Variables.audioManagerSettings;
        Variables.isVideoCallAccepted = true;
        mAudioManagerSettings.setAudioSetting();
    }

    @Override
    public void unmuteMic() {
        if (mAudioManagerSettings != null) {
            mAudioManagerSettings.unmuteMic();
        }
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> mAcceptUnmuteCall.setVisibility(View.GONE));
        }
        Variables.isActiveCall = true;
    }

    private void initView() {
        mAcceptUnmuteCall.setBackgroundImage(getResources().getDrawable(R.drawable.slide_unmute_video_call), R.string.call_unmute);
        if (Variables.isActiveCall) {
            mAcceptUnmuteCall.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCore.addListener(mCoreListener);
        resizePreview();
    }

    @Override
    public void onPause() {
        mCore.removeListener(mCoreListener);
        super.onPause();
    }

    private void resizePreview() {
        if (mCore.getCallsNb() > 0) {
            Call call = mCore.getCurrentCall();
            if (call == null) {
                call = mCore.getCalls()[0];
            }
            if (call == null) return;

            DisplayMetrics metrics = new DisplayMetrics();
            if (getActivity() != null)
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int screenHeight = metrics.heightPixels;
            int maxHeight = screenHeight / 4;

            VideoDefinition videoSize = call.getCurrentParams().getSentVideoDefinition();
            if (videoSize.getWidth() == 0 || videoSize.getHeight() == 0) {
                Log.w(TAG, "Couldn't get sent video definition, using default video definition");
                videoSize = mCore.getPreferredVideoDefinition();
            }
            int width = videoSize.getWidth();
            int height = videoSize.getHeight();

            Log.d(TAG, "Video height is " + height + ", width is " + width);
            width = width * maxHeight / height;
            height = maxHeight;

            if (mCaptureView == null) {
                Log.e(TAG, "mCaptureView is null!");
                return;
            }

            RelativeLayout.LayoutParams newLp = new RelativeLayout.LayoutParams(width, height);
            newLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
            newLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
            mCaptureView.setLayoutParams(newLp);
            Log.d(TAG, "Video preview size set to " + width + "x" + height);
        }
    }
}