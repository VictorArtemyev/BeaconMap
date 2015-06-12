package com.vitman.rxRealm.altbeacon_map.app.entity;

/**
 * Created by Victor Artemjev on 05.06.2015.
 */
public class Customer {

    private String avatarUrl;
    private String userId;
    private int beaconId;

    public Customer(String avatarUrl, int beaconId, String userId) {
        this.avatarUrl = avatarUrl;
        this.beaconId = beaconId;
        this.userId = userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(int beaconId) {
        this.beaconId = beaconId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ClubGuest{" +
                "avatarUrl='" + avatarUrl + '\'' +
                ", userId='" + userId + '\'' +
                ", beaconId=" + beaconId +
                '}';
    }
}
