package com.example.wei.possessionmanager.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.wei.possessionmanager.bean.Item;

import java.util.Date;
import java.util.UUID;

/**
 * Created by wei on 2016/2/28 0028.
 */
public class ItemCursorWrapper extends CursorWrapper {

    public ItemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Item getItem() {
        String uuid = getString(getColumnIndex(PossessionDatabase.ItemTable.Cols.UUID));
        String name = getString(getColumnIndex(PossessionDatabase.ItemTable.Cols.NAME));
        long date = getLong(getColumnIndex(PossessionDatabase.ItemTable.Cols.DATE));
        String owner = getString(getColumnIndex(PossessionDatabase.ItemTable.Cols.OWNER));

        Item item = new Item(UUID.fromString(uuid));
        item.setName(name);
        item.setDate(new Date(date));
        item.setOwner(owner);

        return item;
    }
}
