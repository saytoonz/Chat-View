package com.sayt.chatview.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.sayt.chatview.ui.views.CollageView.ImageForm;

/**
 * * Created by say on 16/01/20.
 */

public class RectangleImageView extends ImageView {
    private ImageForm imageForm = ImageForm.IMAGE_FORM_SQUARE;

    public RectangleImageView(Context context, ImageForm imageForm) {
        super(context);
        this.imageForm = imageForm;
    }

    public RectangleImageView(Context context, AttributeSet attrs, ImageForm imageForm) {
        super(context, attrs);
        this.imageForm = imageForm;
    }

    public RectangleImageView(Context context, AttributeSet attrs, ImageForm imageForm, int defStyle) {
        super(context, attrs, defStyle);
        this.imageForm = imageForm;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getParent() != null) {
            getLayoutParams().height = widthMeasureSpec / imageForm.getDivider();
            setMeasuredDimension(widthMeasureSpec, widthMeasureSpec / imageForm.getDivider());
        }
    }
}