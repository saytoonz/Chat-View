package com.sayt.chatview.utils;


import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * * Created by say on 16/01/20.
 */

public class FontChanger {
    private Typeface typeface;

    public FontChanger(Typeface typeface){
        this.typeface = typeface;
    }

    public void replaceFonts(ViewGroup viewTree){
        View child;
        for(int i=0;i<viewTree.getChildCount();++i){
            child = viewTree.getChildAt(i);
            if(child instanceof ViewGroup){
                replaceFonts((ViewGroup) child);
            }
            else if(child instanceof TextView){
                ((TextView) child).setTypeface(typeface);
            }
        }
    }
}
