package com.example.wei.possessionmanager.view;

import java.util.Date;

/**
 * Created by admin on 2016/3/29.
 */
public interface IDetailView {

    void setName(String name);

    String getName();

    void setDate(Date date);

    Date getDate();

    void setOwner(String owner);

    String getOwner();

    void setPhotoPath(String photoPath);

}
