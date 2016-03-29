package com.example.wei.possessionmanager.presenter;

import android.text.TextUtils;

import com.example.wei.possessionmanager.bean.Item;
import com.example.wei.possessionmanager.model.ItemManager;
import com.example.wei.possessionmanager.view.IDetailView;

import java.util.UUID;

/**
 * Created by admin on 2016/3/29.
 */
public class DetailPresenter {

    IDetailView mDetailView;
    ItemManager mItemManager;
    String mUuid;


    public DetailPresenter(IDetailView detailView, ItemManager itemManager) {
        mDetailView = detailView;
        mItemManager = itemManager;
    }

    public void loadItem(String uuid) {
        mUuid = uuid;
        Item item = mItemManager.getItemWithUuid(uuid);
        mDetailView.setDate(item.getDate());
        mDetailView.setName(item.getName());
        mDetailView.setOwner(item.getOwner());
        mDetailView.setPhotoPath(mItemManager.getItemPhotoFile(item).getAbsolutePath());
    }

    public void saveItem() {
        if (!TextUtils.isEmpty(mUuid)) {
            Item item = new Item(UUID.fromString(mUuid));
            item.setName(mDetailView.getName());
            item.setOwner(mDetailView.getOwner());
            item.setDate(mDetailView.getDate());
            mItemManager.updateItem(item);
        }
    }

    public void deleteItem() {
        if (!TextUtils.isEmpty(mUuid)) {
            mItemManager.deleteItemWithUuid(mUuid);
        }
    }

}
