package com.jinshu.weixinbook.widget;

import android.graphics.Color;

public abstract class AbstractAction implements ActionBar.Action {
    private int mDrawable;
    private String text;

    public AbstractAction(String text) {
        this.text = text;
    }

    public AbstractAction(int drawable) {
        mDrawable = drawable;
    }

    @Override
    public int getDrawable() {
        return mDrawable;
    }

    @Override
    public String getText() {return text;}

    public void setText(String text) {
        this.text = text;
    }
    
    public int getTextColor() {
    	return Color.WHITE;
    }
}