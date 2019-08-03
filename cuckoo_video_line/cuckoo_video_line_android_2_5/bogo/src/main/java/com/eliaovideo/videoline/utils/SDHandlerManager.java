package com.eliaovideo.videoline.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class SDHandlerManager
{
    private final static Handler mainHandler = new Handler(Looper.getMainLooper());
    private static Handler backgroundHandler;

    private final static HandlerThread handlerThread = new HandlerThread("handlehread");
    private static boolean isHandlerThreadStarted = false;

    /**
     * 获得主线程Handler
     *
     * @return
     */
    public final static Handler getMainHandler()
    {
        return mainHandler;
    }

    /**
     * 获得后台线程Handler
     *
     * @return
     */
    public final static Handler getBackgroundHandler()
    {
        startBackgroundHandler();
        return backgroundHandler;
    }

    /**
     * 开始后台线程
     */
    public final static void startBackgroundHandler()
    {
        synchronized (SDHandlerManager.class)
        {
            if (!isHandlerThreadStarted)
            {
                handlerThread.start();
                backgroundHandler = new Handler(handlerThread.getLooper());
                isHandlerThreadStarted = true;
            }
        }
    }

    /**
     * 结束后台线程
     */
    public final static void stopBackgroundHandler()
    {
        synchronized (SDHandlerManager.class)
        {
            if (isHandlerThreadStarted)
            {
                handlerThread.quit();
                isHandlerThreadStarted = false;
            }
        }
    }

    /**
     * 主线程执行
     *
     * @param r
     */
    public final static void post(Runnable r)
    {
        if (r == null)
        {
            return;
        }
        if (Looper.getMainLooper() == Looper.myLooper())
        {
            r.run();
        } else
        {
            getMainHandler().post(r);
        }
    }

    /**
     * 主线程执行
     *
     * @param r
     * @param delayMillis 延迟毫秒
     */
    public final static void postDelayed(Runnable r, long delayMillis)
    {
        if (r == null)
        {
            return;
        }
        getMainHandler().postDelayed(r, delayMillis);
    }

    /**
     * 后台线程执行
     *
     * @param r
     */
    public final static void postBack(Runnable r)
    {
        if (r == null)
        {
            return;
        }
        getBackgroundHandler().post(r);
    }

    /**
     * 后台线程执行
     *
     * @param r
     * @param delayMillis 延迟毫秒
     */
    public final static void postDelayedBack(Runnable r, long delayMillis)
    {
        if (r == null)
        {
            return;
        }
        getBackgroundHandler().postDelayed(r, delayMillis);
    }

    /**
     * 移除任务
     *
     * @param r
     */
    public final static void remove(Runnable r)
    {
        if (r == null)
        {
            return;
        }
        getMainHandler().removeCallbacks(r);
        getBackgroundHandler().removeCallbacks(r);
    }

}
