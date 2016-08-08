package net.c_kogyo.returnvisitor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;

/**
 * Created by SeijiShii on 2016/08/07.
 */

public class RefreshableCounterView extends FrameLayout {

    public RefreshableCounterView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initCommon(context);
        setAttribute(context, attrs);

    }

    public RefreshableCounterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initCommon(context);
        setAttribute(context, attrs);

    }

    private void setAttribute(Context context, AttributeSet attributeSet) {

        TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.RefreshableCounterView, 0, 0);
        try {
            titleText.setText(array.getString(R.styleable.RefreshableCounterView_Title));
        } finally {
            array.recycle();
        }
    }

    private View v;
    private void initCommon(Context context) {

        v = LayoutInflater.from(context).inflate(R.layout.refreshable_counter_view, this);
        initTitleText();
        initEditText();
        initRefreshButton();
    }

    private TextView titleText;
    private void initTitleText() {

        titleText = (TextView) v.findViewById(R.id.title_text);
    }

    private EditText editText;
    private void initEditText() {

        editText = (EditText) v.findViewById(R.id.edit_text);
    }

    private void initRefreshButton() {

        Button refreshButton = (Button) v.findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mListener != null) {
                    mListener.onRefreshPress(RefreshableCounterView.this);
                }
            }
        });
    }

    public int getCount() {
        return Integer.parseInt(editText.getText().toString());
    }

    public void setCount(int count) {
        editText.setText(String.valueOf(count));
    }

    private OnRefreshPressedListener mListener;
    public void setOnRefreshPressedListener(OnRefreshPressedListener listener) {

        mListener = listener;
    }

    public interface OnRefreshPressedListener{
        void onRefreshPress(RefreshableCounterView refreshableCounterView);
    }


}
