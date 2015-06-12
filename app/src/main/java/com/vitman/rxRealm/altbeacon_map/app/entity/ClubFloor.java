package com.vitman.rxRealm.altbeacon_map.app.entity;

import java.util.List;

/**
 * Created by Victor Artemjev on 23.04.2015.
 */
public class ClubFloor {

    private int mIdent;
    private List<ClubRoom> mClubRooms;
    private int mMapScale;
    private String mName;
    private int mWidth;
    private int mHeight;

    public int getIdent() {
        return mIdent;
    }

    public void setIdent(int ident) {
        mIdent = ident;
    }

    public List<ClubRoom> getClubRooms() {
        return mClubRooms;
    }

    public void setClubRooms(List<ClubRoom> clubRooms) {
        mClubRooms = clubRooms;
    }

    public int getMapScale() {
        return mMapScale;
    }

    public void setMapScale(int mapScale) {
        mMapScale = mapScale;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }
}
