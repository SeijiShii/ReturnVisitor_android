package net.c_kogyo.returnvisitor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;

/**
 * Created by SeijiShii on 2016/07/20.
 */

public class TitleFrameView extends RelativeLayout {

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

    private View v;
    private FrameLayout frame;
    private void initCommon(Context context) {

        v = LayoutInflater.from(context).inflate(R.layout.title_fame_view, this);

        titleText = (TextView) v.findViewById(R.id.title_text);
        frame = (FrameLayout) v.findViewById(R.id.child_frame);
    }

    private void applyAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TitleFrameView,
                0, 0);

        try {

            titleText.setText(a.getString(R.styleable.TitleFrameView_Title));

            TextView plchldr = (TextView) v.findViewById(R.id.placeholder_text);
            plchldr.setText(a.getString(R.styleable.TitleFrameView_PlaceholderText));

            showPlaceholder(a.getBoolean(R.styleable.TitleFrameView_ShowPlaceholder, true));

        } finally {
            a.recycle();
        }
    }

    public void addView(View child) {

        frame.addView(child);
    }

    public void showPlaceholder(boolean show) {

        if (show) {
            frame.setBackgroundColor(0);
        } else {
            frame.setBackgroundColor(Color.WHITE);
        }

    }
}
