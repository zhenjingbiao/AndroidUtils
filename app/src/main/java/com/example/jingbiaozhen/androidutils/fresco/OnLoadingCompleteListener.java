package com.example.jingbiaozhen.androidutils.fresco;

import android.view.View;

/**
 * <p>
 * 图片加载完后回调, onResult() 和 onGetImageInfo() 两者都可以作为加载完成的回调，只是返回的信息不同，使用其中一个即可
 * </p>
 * <p>
 * 当AsyncImageView的尺寸要根据图片的实际大小改变时，可以在回调方法onGetImageInfo()里设置View的大小，并通过invalidate()使设置生效
 * </p>
 */
public interface OnLoadingCompleteListener
{
    /**
     * @param ok 加载是否成功
     * @param view 当前view
     * @param animPeriod 动画时长，非动图默认为0
     */
    void onResult(boolean ok, View view, int animPeriod);

    /**
     * @param ok 加载是否成功
     * @param width 图片宽度
     * @param height 图片高度
     */
    void onGetImageInfo(boolean ok, int width, int height);
}
