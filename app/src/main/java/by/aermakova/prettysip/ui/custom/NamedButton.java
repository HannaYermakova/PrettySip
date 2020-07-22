package by.aermakova.prettysip.ui.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.aermakova.prettysip.R;

/**
 * Custom button's animation realization. Usage: {@link by.aermakova.prettysip.ui.activity.linphone.CallActivity}
 */
public class NamedButton extends RelativeLayout {
    @BindView(R.id.button_thumb)
    ImageView mButtonThumb;
    @BindView(R.id.btn_instruction)
    TextView mInstruction;

    public NamedButton(Context context) {
        super(context);
        init(context, null);
    }

    public NamedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NamedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.swipe_up_button_layout, this, true);
        }
        ButterKnife.bind(this);
    }

    public void setBackgroundImage(Drawable drawable, int text) {
        if (mButtonThumb != null)
            mButtonThumb.setImageDrawable(drawable);
        if (mInstruction != null) {
            mInstruction.setText(text);
        }
    }
}