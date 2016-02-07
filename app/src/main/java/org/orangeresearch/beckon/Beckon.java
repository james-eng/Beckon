package org.orangeresearch.beckon;

import java.util.UUID;

/**
 * Created by james on 2/7/2016.
 */
public class Beckon {
    private UUID mId;
    private String mTitle;
    private String mSender;
    private String mLat;
    private String mLon;

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getSender() {
        return mSender;
    }

    public void setSender(String sender) {
        mSender = sender;
    }

    public String getLat() {
        return mLat;
    }

    public void setLat(String lat) {
        mLat = lat;
    }

    public String getLon() {
        return mLon;
    }

    public void setLon(String lon) {
        mLon = lon;
    }

    public Beckon() {
        mId = UUID.randomUUID();
        mTitle = "Random Beacon 1";
        mLat = "25.777754";
        mLon = "-80.191793";
    }
}
