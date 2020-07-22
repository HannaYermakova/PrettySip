package by.aermakova.prettysip.ui.activity.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.aermakova.prettysip.BuildConfig;
import by.aermakova.prettysip.R;
import by.aermakova.prettysip.ui.activity.NetworkCheckFragment;

/**
 * 1st fragment while authorization. Privacy agreement
 */

public class WelcomeFragment extends NetworkCheckFragment {

    @BindView(R.id.continue_btn)
    Button mContinueBtn;
    @BindView(R.id.agreement_txt)
    TextView mAgreementTxt;

    static final int CAMERA_REQUEST_CODE = 88;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAgreementTxt.setOnClickListener(v -> {
            if (isNetworkConnected(getActivity())) {
                Intent agreementPage = new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.AGREEMENT_URL));
                startActivity(agreementPage);
            }
        });
        mContinueBtn.setOnClickListener(v -> {
            if (isNetworkConnected(getActivity())) {
                if (checkOnePermission(Manifest.permission.CAMERA, CAMERA_REQUEST_CODE)) {
                    Navigation.findNavController(view).navigate(R.id.action_welcomeFragment_to_qrScanFragment);
                }
            }
        });
    }

    //Method can be reused for other permissions
    private boolean checkOnePermission(String permission, int PERMISSION_REQUEST_CODE) {
        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{permission}, PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }
}
