package com.vitman.rxRealm.altbeacon_map.app.entity;

/**
 * Created by Victor Artemjev on 23.04.2015.
 */
public class PandaBeacon {
    private int mIdent;
    private String mBeaconId;
    private String mName;
    private int mMajor;
    private int mMinor;
    private double mStrenght;
    private double mX;
    private double mY;
    private double mZ;
    private Club mClub;
    private ClubRoom mClubRoom;
//    private List<User> mUsers;

    public int getIdent() {
        return mIdent;
    }

    public void setIdent(int ident) {
        mIdent = ident;
    }

    public String getBeaconId() {
        return mBeaconId;
    }

    public void setBeaconId(String beaconId) {
        mBeaconId = beaconId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getMajor() {
        return mMajor;
    }

    public void setMajor(int major) {
        mMajor = major;
    }

    public int getMinor() {
        return mMinor;
    }

    public void setMinor(int minor) {
        mMinor = minor;
    }

    public double getStrenght() {
        return mStrenght;
    }

    public void setStrenght(double strenght) {
        mStrenght = strenght;
    }

    public double getX() {
        return mX;
    }

    public void setX(double x) {
        mX = x;
    }

    public double getY() {
        return mY;
    }

    public void setY(double y) {
        mY = y;
    }

    public double getZ() {
        return mZ;
    }

    public void setZ(double z) {
        mZ = z;
    }

    public Club getClub() {
        return mClub;
    }

    public void setClub(Club club) {
        mClub = club;
    }

    public ClubRoom getClubRoom() {
        return mClubRoom;
    }

    public void setClubRoom(ClubRoom clubRoom) {
        mClubRoom = clubRoom;
    }

//    public List<User> getUsers() {
//        return mUsers;
//    }
//
//    public void setUsers(List<User> users) {
//        mUsers = users;
//    }
}
