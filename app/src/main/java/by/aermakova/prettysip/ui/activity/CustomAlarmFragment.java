package by.aermakova.prettysip.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import by.aermakova.prettysip.ui.custom.CustomAlertDialog;

public class CustomAlarmFragment extends NetworkCheckFragment {

    protected void createCustomAlarm(Context context, int message, CustomAlertDialog.ICustomAlert customAlert, boolean showOk) {
        if (context != null) {
            CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
            if (customAlertDialog.getWindow() != null) {
                customAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            customAlertDialog.setCancelable(false);
            customAlertDialog.setICustomAlert(customAlert);
            customAlertDialog.show();
            customAlertDialog.createAlertContent(message, showOk);
        }
    }
}
