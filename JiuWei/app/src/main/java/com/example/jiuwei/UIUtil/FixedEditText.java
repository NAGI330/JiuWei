package com.example.jiuwei.UIUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class FixedEditText extends android.support.v7.widget.AppCompatAutoCompleteTextView{
    private String fixedText;
    private OnClickListener mListener;

    public FixedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFixedText(String text) {
        fixedText = text;
        int left = (int) getPaint().measureText(fixedText)+ getPaddingLeft();
        setPadding(left, getPaddingTop(), getPaddingBottom(), getPaddingRight());
        invalidate();
    }




    public void setDrawableClk(OnClickListener listener) {
        mListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!TextUtils.isEmpty(fixedText)) {
            canvas.drawText(fixedText, 0, (getMeasuredHeight() - getTextSize()) / 2 + getTextSize(), getPaint());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mListener != null && getCompoundDrawables()[2] != null) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    int i = getMeasuredWidth() - getCompoundDrawables()[2].getIntrinsicWidth();
                    if (event.getX() > i) {
                        mListener.onClick(this);
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    break;
                default:
                    break;
            }

        }

        return super.onTouchEvent(event);
    }

}
