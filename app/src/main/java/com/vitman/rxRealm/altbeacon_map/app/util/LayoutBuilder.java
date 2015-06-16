package com.vitman.rxRealm.altbeacon_map.app.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import static android.widget.GridLayout.spec;

/**
 * Created by Victor Artemjev on 15.06.2015.
 */
public class LayoutBuilder {
    private Context mContext;
    private double mScale;

    public LayoutBuilder(Context context) {
        this(context, 0.0);
        mContext = context;

    }

    public LayoutBuilder(Context context, double scale) {
        mContext = context;
        mScale = scale;
    }

    public GridLayout getGridLayoutWithRelativeLayoutParams(Point point, double strength) {
        GridLayout gridLayout = new GridLayout(mContext);

        int width = (int) convertValueToView(strength * 2);
        int height = (int) convertValueToView(strength * 2);
        RelativeLayout.LayoutParams params = new RelativeLayout
                .LayoutParams(width, height);
        params.leftMargin = point.x - width / 2;
        params.topMargin = point.y - height / 2;
        gridLayout.setLayoutParams(params);

        gridLayout.setBackgroundColor(Color.argb(100, 0, 0, 255));
        return gridLayout;
    }

    public GridLayout getGridLayoutWithLinearLayoutParams(double strength) {
        GridLayout gridLayout = new GridLayout(mContext);
        int width = (int) convertValueToView(strength * 2);
        int height = (int) convertValueToView(strength * 2);
        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(width, height);
        gridLayout.setLayoutParams(params);

        gridLayout.setBackgroundColor(Color.argb(100, 0, 0, 255));
        return gridLayout;
    }

    public LinearLayout getLinearLayout() {
        LinearLayout layout = new LinearLayout(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    public GridLayout.LayoutParams getGridLayoutParamForMarker(int width, int height,
                                                                int columnSpec, int rowSpec) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = width;
        params.height = height;
        params.setGravity(Gravity.CENTER);
        params.columnSpec = spec(columnSpec);
        params.rowSpec = spec(rowSpec);
        return params;
    }

    private double convertValueToView(double value) {
        return (value * mScale * 100); //Floor scale
    }


}
