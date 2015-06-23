package com.vitman.rxRealm.altbeacon_map.app.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.vitman.rxRealm.altbeacon_map.app.entity.ClubRoom;
import com.vitman.rxRealm.altbeacon_map.app.entity.PandaBeacon;

import static android.widget.GridLayout.spec;

/**
 * Created by Victor Artemjev on 15.06.2015.
 */

//TODO: refactor
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

    public GridLayout getGridLayoutWithRelativeLayoutParams(PandaBeacon beacon, Point startPoint) {
        GridLayout gridLayout = new GridLayout(mContext);
        ClubRoom room = beacon.getClubRoom();
        float greatestXValue = room.getPoints().get(0).x;
        float greatestYValue = room.getPoints().get(0).y;
        float smallestXValue = greatestXValue;
        float smallestYValue = greatestYValue;

        for(PointF point : room.getPoints())
        {
            greatestXValue = Math.max(greatestXValue, point.x);
            greatestYValue = Math.max(greatestYValue, point.y);
            smallestXValue = Math.min(smallestXValue, point.x);
            smallestYValue = Math.min(smallestYValue, point.y);
        }

        Rect roomRect = new Rect();
        roomRect.left = (int) convertValueToView(smallestXValue);
        roomRect.top = (int) convertValueToView(smallestYValue);
        roomRect.right = (int) convertValueToView(greatestXValue);
        roomRect.bottom = (int) convertValueToView(greatestYValue);

        Rect beaconRect = new Rect();
        beaconRect.left = Math.max((int) convertValueToView(beacon.getX() - beacon.getStrenght()), roomRect.left);
        beaconRect.top = Math.max((int) convertValueToView(beacon.getY() - beacon.getStrenght()), roomRect.top);
        beaconRect.right = Math.min((int) convertValueToView(beacon.getX() + beacon.getStrenght()), roomRect.right);
        beaconRect.bottom = Math.min((int) convertValueToView(beacon.getY() + beacon.getStrenght()), roomRect.bottom);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(beaconRect.right - beaconRect.left,
                beaconRect.bottom - beaconRect.top);

        params.leftMargin = beaconRect.left;
        params.topMargin = beaconRect.top + startPoint.y;
        gridLayout.setLayoutParams(params);
        gridLayout.setBackgroundColor(Color.argb(100, 0, 0, 255));
        return gridLayout;
    }

    public GridLayout getGridLayoutWithLinearLayoutParams() {
        GridLayout gridLayout = new GridLayout(mContext);
        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        gridLayout.setLayoutParams(params);

        gridLayout.setBackgroundColor(Color.argb(100, 0, 0, 255));
        return gridLayout;
    }

    public LinearLayout getLinearLayoutWithRelativeLayoutParams(PandaBeacon beacon, Point startPoint) {
        LinearLayout layout = new LinearLayout(mContext);

        ClubRoom room = beacon.getClubRoom();

        float greatestXValue = room.getPoints().get(0).x;
        float greatestYValue = room.getPoints().get(0).y;
        float smallestXValue = greatestXValue;
        float smallestYValue = greatestYValue;

        for(PointF point : room.getPoints())
        {
            greatestXValue = Math.max(greatestXValue, point.x);
            greatestYValue = Math.max(greatestYValue, point.y);
            smallestXValue = Math.min(smallestXValue, point.x);
            smallestYValue = Math.min(smallestYValue, point.y);
        }

        Rect roomRect = new Rect();
        roomRect.left = (int) convertValueToView(smallestXValue);
        roomRect.top = (int) convertValueToView(smallestYValue);
        roomRect.right = (int) convertValueToView(greatestXValue);
        roomRect.bottom = (int) convertValueToView(greatestYValue);

        Rect beaconRect = new Rect();
        beaconRect.left = Math.max((int) convertValueToView(beacon.getX() - beacon.getStrenght()), roomRect.left);
        beaconRect.top = Math.max((int) convertValueToView(beacon.getY() - beacon.getStrenght()), roomRect.top);
        beaconRect.right = Math.min((int) convertValueToView(beacon.getX() + beacon.getStrenght()), roomRect.right);
        beaconRect.bottom = Math.min((int) convertValueToView(beacon.getY() + beacon.getStrenght()), roomRect.bottom);

        int width = beaconRect.right - beaconRect.left;
        int height = beaconRect.bottom - beaconRect.top;
        RelativeLayout.LayoutParams params = new RelativeLayout
                .LayoutParams(width, height);
        params.leftMargin = beaconRect.left;
        params.topMargin = beaconRect.top + startPoint.y;
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    public static GridLayout.LayoutParams getGridLayoutParamForMarker(int width, int height,
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
