package by.aermakova.prettysip.ui.activity.linphone;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import by.aermakova.prettysip.R;
import by.aermakova.prettysip.logic.util.Variables;

/**
 * Fragment for call ending animation
 */

public class CallEndFragment extends Fragment {
    private Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call_end, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Variables.iTimer != null)
            Variables.iTimer.finishTalk();
        final int FINISH_DELAY = 2 * 1000;
        mHandler.postDelayed(() -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }, FINISH_DELAY);
    }
}