package com.example.wei.possessionmanager.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;

import com.example.wei.possessionmanager.bean.Item;
import com.example.wei.possessionmanager.database.ItemCursorWrapper;
import com.example.wei.possessionmanager.database.PossessionDatabase;
import com.example.wei.possessionmanager.database.PossessionDatabaseHelper;
import com.example.wei.possessionmanager.utils.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by wei on 2016/2/28 0028.
 */
public class ItemManager {

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private static ItemManager sItemManager;
    private static final String FLODER = "ItemPossession";
    private File mFloder;

    private ItemManager(Context context) {
        mContext = context;
        mDatabase = new PossessionDatabaseHelper(mContext).getWritableDatabase();

        File external = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (external != null) {
            mFloder = new File(external, FLODER);
            if (!mFloder.exists()) {
                if (!mFloder.mkdir()) {
                    Toast.makeText(mContext, mFloder + "creation failed.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public static ItemManager getInstance(Context context) {
        if (sItemManager == null) {
            sItemManager = new ItemManager(context);
        }
        return sItemManager;
    }

    public Item getItemWithUuid(String uuid) {
        ItemCursorWrapper cursorWrapper = queryDatabase(PossessionDatabase.ItemTable.Cols.UUID + " = ?",
                new String[]{uuid});

        cursorWrapper.moveToFirst();
        Item item = cursorWrapper.getItem();

        cursorWrapper.close();
        return item;
    }

    public ArrayList<Item> getAllItems() {
        ItemCursorWrapper cursorWrapper = queryDatabase(null, null);

        ArrayList<Item> allItems = new ArrayList<>();
        cursorWrapper.moveToFirst();
        while (!cursorWrapper.isAfterLast()) {
            allItems.add(cursorWrapper.getItem());
            cursorWrapper.moveToNext();
        }

        cursorWrapper.close();
        return allItems;
    }

    public void addItem(Item item) {
        ContentValues values = getContentValues(item);
        mDatabase.insert(PossessionDatabase.ItemTable.NAME, null, values);
    }

    public void deleteItem(Item item) {
        deleteItemWithUuid(item.getUUID().toString());
    }

    public void deleteItemWithUuid(String uuid) {
        mDatabase.delete(PossessionDatabase.ItemTable.NAME,
                PossessionDatabase.ItemTable.Cols.UUID + " = ?",
                new String[]{uuid});
    }

    public void updateItem(Item item) {
        ContentValues values = getContentValues(item);
        mDatabase.update(PossessionDatabase.ItemTable.NAME, values,
                PossessionDatabase.ItemTable.Cols.UUID + " = ?",
                new String[]{item.getUUID().toString()});
    }

    public File getItemPhotoFile(Item item) {
        return getItemPhotoFileWithUuid(item.getUUID().toString());
    }

    public File getItemPhotoFileWithUuid(String uuid) {
        if (mFloder == null) {
            return null;
        }
        String fileName = Item.getPhotoNameWithUuid(uuid);
        return new File(mFloder, fileName);
    }

    private ContentValues getContentValues(Item item) {
        ContentValues values = new ContentValues();

        values.put(PossessionDatabase.ItemTable.Cols.UUID, item.getUUID().toString());
        values.put(PossessionDatabase.ItemTable.Cols.NAME, item.getName());
        values.put(PossessionDatabase.ItemTable.Cols.DATE, item.getDate().getTime());
        values.put(PossessionDatabase.ItemTable.Cols.OWNER, item.getOwner());

        return values;
    }

    private ItemCursorWrapper queryDatabase(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(PossessionDatabase.ItemTable.NAME,
                null, whereClause, whereArgs, null, null, null);

        return new ItemCursorWrapper(cursor);
    }
}
