package by.aermakova.prettysip.ui.activity.main;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import by.aermakova.prettysip.R;
import by.aermakova.prettysip.ui.activity.BaseActivity;

public class MainActivity extends BaseActivity {
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Navigation.findNavController(this, R.id.nav_host_fragment_main);
    }
}