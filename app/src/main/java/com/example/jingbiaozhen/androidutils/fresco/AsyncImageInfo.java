package com.example.jingbiaozhen.androidutils.fresco;

import android.text.TextUtils;

import com.facebook.imagepipeline.request.Postprocessor;

/**
 * Created by jingbiaozhen on 2018/5/22.
 * 1. url
 * 2. defaultImage
 * 3. loadingImage
 * 4. 圆角：topLeftRadius、topRightRadius、bottomLeftRadius、bottomRightRadius
 * 5. 圆形
 * 6. fadeInMillis 渐显
 * 7. gif 自动播放
 * 8. listener
 * 9. Postprocessor 后处理
 */
public class AsyncImageInfo
{
    
    private final static int DEFAULT_FADE = 150;
    
    private final String url;

    private final int defaultImage;

    private final int loadingImage;

    private final int topLeftRadius;

    private final int topRightRadius;

    private final int bottomLeftRadius;

    private final int bottomRightRadius;

    private final boolean autoPlayAnim;

    private final boolean circle;
    
    private final int fadeInMillis;

    private final Postprocessor postprocessor;

    private final OnLoadingCompleteListener listener;

    public AsyncImageInfo(String url, int defaultImage, int loadingImage, int topLeftRadius, int topRightRadius,
                          int bottomLeftRadius, int bottomRightRadius, int fadeInMillis, boolean autoPlayAnim, boolean circle,
                          Postprocessor postprocessor, OnLoadingCompleteListener listener)
    {
        this.url = url;
        this.defaultImage = defaultImage;
        this.loadingImage = loadingImage;
        this.topLeftRadius = topLeftRadius;
        this.topRightRadius = topRightRadius;
        this.bottomLeftRadius = bottomLeftRadius;
        this.bottomRightRadius = bottomRightRadius;
        this.fadeInMillis = fadeInMillis;
        this.autoPlayAnim = autoPlayAnim;
        this.circle = circle;
        this.postprocessor = postprocessor;
        this.listener = listener;
    }

    public String getUrl()
    {
        return url;
    }

    public int getDefaultImage()
    {
        return defaultImage;
    }

    public int getTopLeftRadius()
    {
        return topLeftRadius;
    }

    public int getTopRightRadius()
    {
        return topRightRadius;
    }

    public int getBottomLeftRadius()
    {
        return bottomLeftRadius;
    }

    public int getBottomRightRadius()
    {
        return bottomRightRadius;
    }

    public OnLoadingCompleteListener getListener()
    {
        return listener;
    }

    public int getLoadingImage()
    {
        return loadingImage;
    }

    public int getFadeInMillis()
    {
        return fadeInMillis;
    }

    public boolean isAutoPlayAnim()
    {
        return autoPlayAnim;
    }

    public boolean isCircle()
    {
        return circle;
    }

    public Postprocessor getPostprocessor()
    {
        return postprocessor;
    }

    public static class Builder
    {
        private String url;

        private int defaultImage;

        private int loadingImage;

        private int fadeInMillis;

        private int topLeftRadius;

        private int topRightRadius;

        private int bottomLeftRadius;

        private int bottomRightRadius;

        private boolean autoPlayAnim;

        private boolean circle;

        private Postprocessor postprocessor;

        private OnLoadingCompleteListener listener;

        public Builder()
        {
            url = null;
            defaultImage = -1;
            loadingImage = -1;
            fadeInMillis = DEFAULT_FADE;
            topLeftRadius = 0;
            topRightRadius = 0;
            bottomLeftRadius = 0;
            bottomRightRadius = 0;
            autoPlayAnim = true;
            circle = false;
            postprocessor = null;
            listener = null;
        }

        /**
         * 本地路径：可以图片、视频文件等等
         */
        public Builder setLocalUrl(String url)
        {
            if (!TextUtils.isEmpty(this.url))
            {
                throw new IllegalArgumentException("has set url");
            }
            this.url = "file:///" + url;
            return this;
        }

        public Builder setUrl(String url)
        {
            if (!TextUtils.isEmpty(this.url))
            {
                throw new IllegalArgumentException("has set url");
            }
            this.url = url;
            return this;
        }

        public Builder setDefaultImage(int defaultImage)
        {
            this.defaultImage = defaultImage;
            return this;
        }

        public Builder setLoadingImage(int loadingImage)
        {
            this.loadingImage = loadingImage;
            return this;
        }

        public Builder radius(int radius)
        {
            this.topLeftRadius = radius;
            this.topRightRadius = radius;
            this.bottomLeftRadius = radius;
            this.bottomRightRadius = radius;
            return this;
        }
        
        public Builder setTopLeftRadius(int topLeftRadius)
        {
            this.topLeftRadius = topLeftRadius;
            return this;
        }

        public Builder setTopRightRadius(int topRightRadius)
        {
            this.topRightRadius = topRightRadius;
            return this;
        }

        public Builder setBottomLeftRadius(int bottomLeftRadius)
        {
            this.bottomLeftRadius = bottomLeftRadius;
            return this;
        }

        public Builder setBottomRightRadius(int bottomRightRadius)
        {
            this.bottomRightRadius = bottomRightRadius;
            return this;
        }

        public Builder setAutoPlayAnim(boolean autoPlayAnim)
        {
            this.autoPlayAnim = autoPlayAnim;
            return this;
        }

        public Builder setCircle(boolean circle)
        {
            this.circle = circle;
            return this;
        }

        public Builder setPostprocessor(Postprocessor postprocessor)
        {
            this.postprocessor = postprocessor;
            return this;
        }

        public Builder setListener(OnLoadingCompleteListener listener)
        {
            this.listener = listener;
            return this;
        }

        public Builder setFadeInMillis(int fadeInMillis)
        {
            this.fadeInMillis = fadeInMillis;
            return this;
        }

        public AsyncImageInfo build()
        {
            return new AsyncImageInfo(url, defaultImage, loadingImage, topLeftRadius, topRightRadius, bottomLeftRadius,
                    bottomRightRadius, fadeInMillis, autoPlayAnim, circle, postprocessor, listener);
        }
    }
}
