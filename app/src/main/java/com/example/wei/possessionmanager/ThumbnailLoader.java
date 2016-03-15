package com.example.wei.possessionmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.util.Pair;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by wei on 2016/2/28 0028.
 */
public class ThumbnailLoader<T> extends HandlerThread {

    private static final String TAG = "ThumbnailLoader";
    private static final int MESSAGE_DOWNLOAD = 0;

    private Handler mResponder;
    private Handler mRequestHandler;
    private Context mContext;
    private OnRequestDoneListener<T> mOnRequestDoneListener;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private LinkedBlockingQueue<Pair<T, String>> mQueue = new LinkedBlockingQueue<>();
    private LruCache<String, Bitmap> mCache = new LruCache<>(100);

    public ThumbnailLoader(Context context, Handler responder,
                           OnRequestDoneListener<T> onRequestDoneListener) {
        super(TAG);
        mResponder = responder;
        mOnRequestDoneListener = onRequestDoneListener;
        mContext = context;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                   handleRequest((T) msg.obj);
                }
            }
        };

        Pair<T, String> pair;
        while ((pair = mQueue.poll()) != null) {
            queueThumbnail(pair.first, pair.second);
        }
        mQueue = null;
    }

    public boolean isCached(String path) {
        return mCache.get(path) != null;
    }

    public void queueThumbnail(T target, String path) {
        if (mRequestHandler == null) {
            try {
                mQueue.put(new Pair<>(target, path));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }

        Log.d(TAG, "queueThumbnail, target=" + target + ", path=" + path);
        mRequestMap.remove(target);
        mResponder.removeMessages(MESSAGE_DOWNLOAD, target);
        if (!TextUtils.isEmpty(path)) {
            Bitmap cache = mCache.get(path);
            if (cache == null) {
                mRequestMap.put(target, path);
                mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
            } else {
                if (mOnRequestDoneListener != null) {
                    mOnRequestDoneListener.onRequestDone(target, cache);
                }
            }
        }
    }

    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

    private void handleRequest(final T target) {
        Log.d(TAG, "handleRequest, target=" + target);
        final String path = mRequestMap.get(target);

        final Bitmap bitmap = Utils.getThumbnail(mContext.getContentResolver(), path);

        if (bitmap != null) {
            mCache.put(path, bitmap);
        }

        mResponder.post(new Runnable() {
            @Override
            public void run() {
                if (!mRequestMap.get(target).equals(path)) {
                    return;
                }
                mRequestMap.remove(target);
                mOnRequestDoneListener.onRequestDone(target, bitmap);
            }
        });

    }

    public interface OnRequestDoneListener<T> {
        void onRequestDone(T target, Bitmap bitmap);
    }
}
