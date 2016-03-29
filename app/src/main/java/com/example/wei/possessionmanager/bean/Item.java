package com.example.wei.possessionmanager.bean;

import java.util.Date;
import java.util.UUID;

/**
 * Created by wei on 2016/2/28 0028.
 */
public class Item {

    private String mName;
    private UUID mUUID;
    private String mOwner;
    private Date mDate;

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public Item(UUID uuid) {
        mUUID = uuid;
        mDate = new Date();
        mName = "";
        mOwner = "";
    }
    public Item() {
        this(UUID.randomUUID());
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public static String getPhotoNameWithUuid(String uuid) {
        return "IMG_" + uuid + ".jpg";
    }
}
