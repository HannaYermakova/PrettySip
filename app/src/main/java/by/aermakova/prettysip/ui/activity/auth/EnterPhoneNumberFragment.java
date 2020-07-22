package by.aermakova.prettysip.ui.activity.auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.aermakova.prettysip.R;
import by.aermakova.prettysip.logic.enums.CountryCode;
import by.aermakova.prettysip.logic.implementation.IEnterPhoneNumber;
import by.aermakova.prettysip.logic.util.Util;
import by.aermakova.prettysip.ui.activity.CustomAlarmFragment;
import by.aermakova.prettysip.ui.custom.CustomAlertDialog;

/**
 * 4th fragment while authorization. Entering the phone number. Logic implementation in {@link EnterPhoneNumberPresenter} via {@link IEnterPhoneNumber}
 */
public class EnterPhoneNumberFragment extends CustomAlarmFragment implements IEnterPhoneNumber.View {
    private final String TAG = getClass().getSimpleName();

    private IEnterPhoneNumber.Presenter mPresenter;
    private View mView;

    @BindView(R.id.enter_phone_btn)
    Button mEnterPhoneBtn;
    @BindView(R.id.enter_phonenumber)
    EditText mEnterPhoneNumber;
    @BindView(R.id.invalid_number)
    TextView mWarningText;
    @BindView(R.id.progress_circular)
    ProgressBar mProgressBar;
    @BindView(R.id.enter_prefix_phonenumber)
    Spinner mSpinner;

    private String mPrefixChoose;
    private String mInputPhoneNumber;
    private List<CountryPrefix> mCountries;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_enter_phone_number, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new EnterPhoneNumberPresenter(this, getLifecycle());
        createCountryPicker();
        mEnterPhoneBtn.setEnabled(false);
        mEnterPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mWarningText.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mInputPhoneNumber = mEnterPhoneNumber.getText().toString().trim();
                boolean check = Util.checkValidInputPhone(mInputPhoneNumber);
                mEnterPhoneBtn.setEnabled(check);
                if (check) {
                    mEnterPhoneNumber.setBackgroundResource(R.drawable.btn_long_pressed);
                    mEnterPhoneNumber.setTextColor(getResources().getColor(R.color.white));
                } else {
                    mEnterPhoneNumber.setBackgroundResource(R.drawable.btn_long_disable);
                    mEnterPhoneNumber.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mEnterPhoneBtn.setOnClickListener(v -> enterPhoneNumber());
    }

    private void enterPhoneNumber() {
        String finalPhoneNumber = mPrefixChoose + mInputPhoneNumber;
        if (isNetworkConnected(getActivity())) {
            if (Util.checkFinalPhoneNumber(finalPhoneNumber)) {
                mWarningText.setVisibility(View.INVISIBLE);
                mPresenter.sendAuthCodeAndPhoneNumber(finalPhoneNumber);
                enabledEnterButton(false);
            } else {
                mWarningText.setVisibility(View.VISIBLE);
                mEnterPhoneNumber.setBackgroundResource(R.drawable.btn_long_wrong);
                mEnterPhoneNumber.setTextColor(getResources().getColor(R.color.red_light));
            }
        }
    }

    private void createCountryPicker() {
        mCountries = createArrayCountryPrefix();
        List<String> countryViews = new ArrayList<>();
        for (CountryPrefix country : mCountries) {
            countryViews.add(country.getCountryView());
        }
        SpinnerAdapter adapter = new SpinnerAdapter(getContext(), R.layout.prefix_custom_spinner, countryViews);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPrefixChoose = mCountries.get(position).getCountryPrefix();
                if (view != null)
                    view.findViewById(R.id.arrow).setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void openEnterCodeActivity() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> createCustomAlarm(getContext(),
                    R.string.sms_instruction,
                    new CustomAlertDialog.ICustomAlert() {
                        @Override
                        public void okLogic() {
                            navigate();
                        }

                        @Override
                        public void cancelLogic() {
                            navigate();
                        }
                    }, true));
        }
    }

    private void navigate() {
        NavController controller = Navigation.findNavController(mView);
        if (controller.getCurrentDestination() != null &&
                controller.getCurrentDestination().getId() == R.id.enterPhoneNumberFragment)
            controller.navigate(R.id.action_enterPhoneNumberFragment_to_enterSMSCodeFragment);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void showInvalidPhoneNumberMessage(int messageText) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                mEnterPhoneNumber.setBackgroundResource(R.drawable.btn_long_wrong);
                mEnterPhoneNumber.setTextColor(getResources().getColor(R.color.red_light));
                mEnterPhoneNumber.setText(messageText);
                mWarningText.setVisibility(View.VISIBLE);
            });
        }
    }

    @Override
    public void enabledEnterButton(boolean value) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                mEnterPhoneBtn.setEnabled(value);
                if (value) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private static class CountryPrefix {
        String countryView;
        String countryPrefix;

        CountryPrefix(String countryView, String countryPrefix) {
            this.countryView = countryView;
            this.countryPrefix = countryPrefix;
        }

        String getCountryPrefix() {
            return countryPrefix;
        }

        String getCountryView() {
            return countryView;
        }
    }

    private List<CountryPrefix> createArrayCountryPrefix() {
        List<CountryPrefix> countryPrefixes = new ArrayList<>();
        for (CountryCode value : CountryCode.values()) {
            countryPrefixes.add(new CountryPrefix(value.getCountryView(), value.getCountryPrefix()));
        }
        return countryPrefixes;
    }

    private static class SpinnerAdapter extends ArrayAdapter<String> {
        private List<String> objects;

        SpinnerAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            this.objects = objects;
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        private View getCustomView(final int position, View convertView, ViewGroup parent) {
            View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.prefix_custom_spinner, parent, false);
            final TextView label = row.findViewById(R.id.tv_spinnervalue);
            label.setText(objects.get(position));
            return row;
        }
    }
}