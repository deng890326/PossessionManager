package com.example.wei.possessionmanager;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.provider.MediaStore;
import android.text.format.DateFormat;

import java.util.Date;

/**
 * Created by wei on 2016/2/29 0029.
 */
public class Utils {

    public static String getDateString(Date date) {
        return DateFormat.format("yyyy/MM/dd hh:mm:ss", date).toString();
    }

    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }

    public static Bitmap getScaledBitmap(String path, int width, int height) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);

        int srcWidth = opts.outWidth;
        int srcHeight = opts.outHeight;
        int inSampleSize = 1;
        if (srcWidth > width || srcHeight > height) {
            int widthRate = srcWidth / width;
            int heightRate = srcHeight / height;
            inSampleSize = Math.max(widthRate, heightRate);
        }

        opts = new BitmapFactory.Options();
        opts.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, opts);
    }

    public static Bitmap getThumbnail(ContentResolver cr, String path) {
        long id = -1;
        Cursor c = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Images.Media._ID},
                MediaStore.Images.ImageColumns.DATA + " = ?",
                new String[] {path}, null);
        if (c != null) {
            if (c.getCount() != 0) {
                c.moveToFirst();
                id = c.getLong(0);
            }
            c.close();
        }

        if (id != -1) {
            return MediaStore.Images.Thumbnails
                    .getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        } else {
            return null;
        }
    }
}
