package by.aermakova.prettysip.ui.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.aermakova.prettysip.R;

/**
 * Custom alert dialog realization
 */
public class CustomAlertDialog extends Dialog {
    private ICustomAlert iCustomAlert;
    @BindView(R.id.btn_ok_dia)
    Button mOkBtn;
    @BindView(R.id.txt_dia)
    TextView mMessageTxt;
    @BindView(R.id.btn_cancel_dia)
    ImageButton mCancel;

    public CustomAlertDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_alert_dialog);
        ButterKnife.bind(this);
        mOkBtn.setOnClickListener(v -> {
            iCustomAlert.okLogic();
            dismiss();
        });
        mCancel.setOnClickListener(v -> {
            iCustomAlert.cancelLogic();
            dismiss();
        });
    }

    public void createAlertContent(int messageText, boolean value) {
        mMessageTxt.setText(messageText);
        int vis = value ? View.GONE : View.VISIBLE;
        mOkBtn.setVisibility(vis);
    }

    public void setICustomAlert(ICustomAlert iCustomAlert) {
        this.iCustomAlert = iCustomAlert;
    }

    /**
     * Interface for using {@link by.aermakova.prettysip.ui.custom.CustomAlertDialog}
     */
    public interface ICustomAlert {
        void okLogic();
        void cancelLogic();
    }
}