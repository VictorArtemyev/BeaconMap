package com.vitman.rxRealm.altbeacon_map.app.entity;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victor Artemjev on 23.04.2015.
 */
public class ClubRoom {
    private int mIdent;
//    private List<Point> mPoints = Collections.emptyList();
    private List<PointF> mPoints = new ArrayList<>();

    public int getIdent() {
        return mIdent;
    }

    public void setIdent(int ident) {
        mIdent = ident;
    }

    public List<PointF> getPoints() {
        return mPoints;
    }

    public void setPoints(List<PointF> points) {
        mPoints = points;
    }
}
