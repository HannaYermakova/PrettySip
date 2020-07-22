package by.aermakova.prettysip.ui.activity.auth;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.zxing.Result;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import by.aermakova.prettysip.R;
import by.aermakova.prettysip.logic.implementation.IAuthCodeLogic;
import by.aermakova.prettysip.logic.util.Constants;
import by.aermakova.prettysip.ui.activity.NetworkCheckFragment;
import by.aermakova.prettysip.ui.custom.CustomScannerView;

/**
 * 2nd fragment while authorization. Scanning of the QR unique code.
 * Logic implementation in {@link EnterAuthCodePresenter} via {@link IAuthCodeLogic}
 */

public class QRScannerFragment extends NetworkCheckFragment implements IAuthCodeLogic.View, ZXingScannerView.ResultHandler {
    private final String TAG = getClass().getSimpleName();

    private IAuthCodeLogic.Presenter mPresenter;
    private View mView;

    @BindView(R.id.camera_preview)
    CustomScannerView mSurfaceView;
    @BindView(R.id.invalid_qr_code)
    TextView mWarning;
    @BindView(R.id.overlay_instruction)
    RelativeLayout mLayoutInstruction;
    @BindView(R.id.btn_cancel_dia)
    ImageButton mCancel;
    @BindView(R.id.enter_manually)
    Button mEnterManually;

    private Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_qr_scanner, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new EnterAuthCodePresenter(this, getLifecycle());
        mEnterManually.setOnClickListener(v -> openEnterAuthCodeManually());
        startCustomAlertDialog();
    }

    private void startCustomAlertDialog() {
        mLayoutInstruction.setVisibility(View.VISIBLE);
        mCancel.setOnClickListener(view -> mLayoutInstruction.setVisibility(View.INVISIBLE));
    }

    @Override
    public void openEnterPhoneNumberActivity() {
        NavController controller = Navigation.findNavController(mView);
        if (controller.getCurrentDestination() != null &&
                controller.getCurrentDestination().getId() == R.id.qrScanFragment)
            controller.navigate(R.id.action_qrScanFragment_to_enterPhoneNumberFragment);
    }

    @Override
    public void showInvalidQRMessage() {
        mHandler.post(() -> {
            if (mWarning.getVisibility() == View.INVISIBLE) {
                mWarning.setVisibility(View.VISIBLE);
            }
        });
        mHandler.postDelayed(() -> {
            if (mWarning.getVisibility() == View.VISIBLE) {
                mWarning.setVisibility(View.INVISIBLE);
            }
        }, Constants.BLOCK_BUTTON);
        startCamera();
    }

    private void startCamera() {
        if (mSurfaceView != null) {
            mSurfaceView.startCamera();
            mSurfaceView.setResultHandler(this);
            mSurfaceView.setFocusable(true);
        }
    }

    @Override
    public void showNoConnectionMessage() {
        showToastMessage(getActivity(), R.string.no_server_connection);
        startCamera();
    }

    private void openEnterAuthCodeManually() {
        NavController controller = Navigation.findNavController(mView);
        if (controller.getCurrentDestination() != null &&
                controller.getCurrentDestination().getId() == R.id.qrScanFragment)
            controller.navigate(R.id.action_qrScanFragment_to_enterCodeFragment);
    }

    @Override
    public void onResume() {
        super.onResume();
        startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSurfaceView != null) {
            mSurfaceView.stopCamera();
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        final String result = rawResult.getText();
        Log.d(TAG, rawResult.getText());
        if (isNetworkConnected(getActivity())) {
            mPresenter.checkAndSendAuthCode(result);
        } else startCamera();
    }
}