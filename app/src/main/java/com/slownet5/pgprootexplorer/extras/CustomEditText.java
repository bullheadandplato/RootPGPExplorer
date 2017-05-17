/*
 * Copyright (c) 2017. slownet5
 *  This file is part of RootPGPExplorer also known as CryptoFM
 *
 *       RootPGPExplorer a is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       RootPGPExplorer is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with RootPGPExplorer.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.slownet5.pgprootexplorer.extras;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * Created by asad on 5/14/17.
 */

public class CustomEditText extends android.support.v7.widget.AppCompatEditText {
    private Rect rect;
    private Paint paint;

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        rect = new Rect();
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#BDBDBD"));
        paint.setTextSize(22);
    }

    public CustomEditText(Context context, Rect rect, Paint paint) {
        super(context);
        this.rect = rect;
        this.paint = paint;
    }

    public CustomEditText(Context context, AttributeSet attrs, Rect rect, Paint paint) {
        super(context, attrs);
        this.rect = rect;
        this.paint = paint;
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr, Rect rect, Paint paint) {
        super(context, attrs, defStyleAttr);
        this.rect = rect;
        this.paint = paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int baseline = getBaseline();
        for (int i = 0; i < getLineCount(); i++) {
            canvas.drawText("  " + (i+1),rect.left,baseline, paint);
            baseline += getLineHeight();
        }
        super.onDraw(canvas);
    }
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        this.setSelection(this.getText().length());
    }
}
