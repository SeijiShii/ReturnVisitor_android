package net.c_kogyo.returnvisitor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;

/**
 * Created by SeijiShii on 2016/07/20.
 */

public class TitleFrameView extends FrameLayout {

    public TitleFrameView(Context context) {
        super(context);

        initCommon(context);
    }

    public TitleFrameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initCommon(context);
        applyAttrs(context, attrs);

    }

    public TitleFrameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initCommon(context);
        applyAttrs(context, attrs);

    }

    private TextView titleText;

    private void initCommon(Context context) {

        View v = LayoutInflater.from(context).inflate(R.layout.title_fame_view, null);
        this.addView(v);

        titleText = (TextView) v.findViewById(R.id.title_text);
    }

    private void applyAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TitleFrameView,
                0, 0);

        try {

            titleText.setText(a.getString(R.styleable.TitleFrameView_Title));
        } finally {
            a.recycle();
        }
    }

}
