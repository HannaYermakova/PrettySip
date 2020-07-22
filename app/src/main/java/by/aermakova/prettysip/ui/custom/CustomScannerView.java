package by.aermakova.prettysip.ui.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * QR-scanner view realization. Usage: {@link by.aermakova.prettysip.ui.activity.auth.QRScannerFragment}
 */

public class CustomScannerView extends ZXingScannerView {
    public CustomScannerView(Context context) {
        super(context);
    }

    public CustomScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected IViewFinder createViewFinderView(Context context) {
        return new CustomViewFinderView(context);
    }

    static class CustomViewFinderView extends ViewFinderView {
        public CustomViewFinderView(Context context) {
            super(context);
            setSquareViewFinder(true);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }

        @Override
        public void drawLaser(Canvas canvas) {
            //Remove super call to do not draw laser
        }
    }
}