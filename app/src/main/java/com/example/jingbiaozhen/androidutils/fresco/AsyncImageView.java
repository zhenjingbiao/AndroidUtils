package com.example.jingbiaozhen.androidutils.fresco;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.example.jingbiaozhen.androidutils.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.animated.base.AbstractAnimatedDrawable;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class AsyncImageView extends SimpleDraweeView implements ControllerListener<ImageInfo>
{
    private String mUrl = null;

    /** 宽高比 */
    private float widthHeightRatio;

    /** 比例参照，0-参照width（默认），1-参照height */
    private int ratioReference;

    /**
     * 是否移动网络 监听太多网络对部分手机会有影响，所以在receiver里面修改这个字段。
     */
    public static boolean mobileNetwork;

    /**
     * 是否允许通过网络监测判断是否加载图片，设置一次即可
     */
    public static boolean enableNetworkDetect = true;

    protected OnLoadingCompleteListener onLoadingCompleteListener;

    public static void setMobileNetwork(boolean isMobile)
    {
        mobileNetwork = isMobile;
    }

    public static void enableNetWorkDetect(boolean enable)
    {
        enableNetworkDetect = enable;
    }

    public AsyncImageView(Context context)
    {
        this(context, null);
    }

    public AsyncImageView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public AsyncImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AsyncImageView,
                defStyleAttr, 0);
        if (attributes != null)
        {
            // 宽高比
            String value = attributes.getString(R.styleable.AsyncImageView_width_height_ratio);

            if (value != null)
            {
                try
                {
                    this.widthHeightRatio = Float.parseFloat(value);
                }
                catch (NumberFormatException e)
                {
                    widthHeightRatio = 0;
                    Log.e(ImgUriUtils.LOG_TAG, "width_height_ratio must be float:" + e.toString());
                }
            }

            if (this.widthHeightRatio != 0)
            {
                // 指定参照
                value = attributes.getString(R.styleable.AsyncImageView_ratio_reference);

                if ("width".equalsIgnoreCase(value))
                {
                    ratioReference = 0;
                }
                else if ("height".equalsIgnoreCase(value))
                {
                    ratioReference = 1;
                }
                else
                {
                    // 只接受width和height
                    ratioReference = 0;

                    Log.e(ImgUriUtils.LOG_TAG,
                            "ratio_reference must be width or heigth:ratio_reference=\"" + value + "\"");
                }
            }
            attributes.recycle();
        }
        initScaleType();
        mobileNetwork = ImgUriUtils.isMobileNetwork(getContext());
    }

    private void initScaleType()
    {
        if (getHierarchy() == null)
        {
            return;
        }
        // SimpleDraweeView的自定义ScaleType默认是CENTER_CROP，这里这样做是为了让android：scaleType属性生效,但不支持原生的MATRIX
        if (ScalingUtils.ScaleType.CENTER_CROP.equals(getHierarchy().getActualImageScaleType()))
        {
            getHierarchy().setActualImageScaleType(getDraweeScaleType(getScaleType()));
        }
    }

    public void setAsyncScaleType(ScaleType scaleType)
    {
        if (getHierarchy() == null)
        {
            GenericDraweeHierarchyBuilder hierarchyBuilder = new GenericDraweeHierarchyBuilder(getResources());
            hierarchyBuilder.setActualImageScaleType(getDraweeScaleType(scaleType));
            GenericDraweeHierarchy hierarchy = hierarchyBuilder.build();
            setHierarchy(hierarchy);
        }
        else
        {
            getHierarchy().setActualImageScaleType(getDraweeScaleType(scaleType));
        }
    }

    private ScalingUtils.ScaleType getDraweeScaleType(ScaleType scaleType)
    {
        switch (scaleType.ordinal())
        {
            case 1:
                return ScalingUtils.ScaleType.FIT_XY;
            case 2:
                return ScalingUtils.ScaleType.FIT_START;
            case 3:
                return ScalingUtils.ScaleType.FIT_CENTER;
            case 4:
                return ScalingUtils.ScaleType.FIT_END;
            case 5:
                return ScalingUtils.ScaleType.CENTER;
            case 6:
                return ScalingUtils.ScaleType.CENTER_CROP;
            case 7:
                return ScalingUtils.ScaleType.CENTER_INSIDE;
            default:
                return ScalingUtils.ScaleType.FIT_XY;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (widthHeightRatio != 0)
        {
            int width = getDefaultSize(-1, widthMeasureSpec);
            int height = getDefaultSize(-1, heightMeasureSpec);
            int pleft = getPaddingLeft();
            int pright = getPaddingRight();
            int ptop = getPaddingTop();
            int pbottom = getPaddingBottom();

            int imageWidth;
            int imageHeigth;

            // 判断参照物
            if (ratioReference == 0 && width > 0)
            {
                // width固定
                // 图片实际大小
                imageWidth = width - pleft - pright;
                imageHeigth = (int) (imageWidth / widthHeightRatio);
                height = imageHeigth + ptop + pbottom;
            }
            else if (height > 0)
            {
                // height固定
                imageHeigth = height - ptop - pbottom;
                imageWidth = (int) (imageHeigth * widthHeightRatio);
                width = imageWidth + pleft + pright;
            }
            setMeasuredDimension(width, height);
        }
        else
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    protected String getUrl()
    {
        return mUrl;
    }

    public void setGifResource(int resId, OnLoadingCompleteListener listener)
    {
        setImageUrl("res:///" + resId, -1, listener);
    }

    public void setImageUrl(String url)
    {
        setImageUrl(url, -1);
    }

    public void setImageUrl(String url, boolean autoPlayAnim)
    {
        setImageUrl(url, -1, -1, 0, 150, null, autoPlayAnim);
    }

    public void setImageUrl(String url, int defaultImage)
    {
        setImageUrl(url, defaultImage, null);
    }

    public void setImageUrl(String url, int defaultImage, int loadingImage)
    {
        setImageUrl(url, defaultImage, loadingImage, 0, 150, null, true);
    }

    public void setImageUrl(String url, int defaultImage, OnLoadingCompleteListener listener)
    {

        setImageUrl(url, defaultImage, defaultImage, 0, 150, listener, true);
    }

    public void setCircleImageUrl(String url)
    {
        setCircleImageUrl(url, -1);
    }

    public void setCircleImageUrl(String url, int defaultImage)
    {
        setRoundCornerImageUrl(url, defaultImage, -1);
    }

    /**
     * 设置渐显加载图片
     */
    public void setFadeInImageUrl(String url, int defaultImage)
    {
        setFadeInImageUrl(url, 300, defaultImage);
    }

    public void setFadeInImageUrl(String url, int fadeInMillis, int defaultImage)
    {
        setImageUrl(url, defaultImage, defaultImage, 0, fadeInMillis, null, true);
    }

    /**
     * 设置圆角图片
     */
    public void setRoundCornerImageUrl(String url, int defaultImage, int cornerRadiusPixels)
    {
        setRoundCornerImageUrl(url, defaultImage, cornerRadiusPixels, null);
    }

    public void setRoundCornerImageUrl(String url, int defaultImage, int cornerRadiusPixels,
            OnLoadingCompleteListener listener)
    {
        setImageUrl(url, defaultImage, defaultImage, cornerRadiusPixels, 150, listener, true);
    }

    public void setRoundCornerImageUrl(String url, int defaultImage, int topLeftRadius, int topRightRadius,
            int bottomLeftRadius, int bottomRightRadius, OnLoadingCompleteListener listener)
    {
        setImageUrl(url, defaultImage, defaultImage, topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius,
                150, listener, true);
    }

    public void setImageUrl(String url, int failImage, int loadingImage, int cornerRadius, int fadeInMillis,
            OnLoadingCompleteListener listener, boolean autoPlayAnim)
    {
        setImageUrl(url, failImage, loadingImage, cornerRadius, cornerRadius, cornerRadius, cornerRadius, fadeInMillis,
                listener, autoPlayAnim);
    }

    /***
     * 加载图片
     *
     * @param url 图片url
     * @param failImage 加载失败显示图
     * @param loadingImage 加载中显示图
     * @param fadeInMillis 图片渐进显示的时间
     * @param listener 加载图片回调
     */
    public void setImageUrl(String url, int failImage, int loadingImage, int topLeftRadius, int topRightRadius,
            int bottomLeftRadius, int bottomRightRadius, int fadeInMillis, OnLoadingCompleteListener listener,
            boolean autoPlayAnim)
    {
        if (url == null)
        {
            url = "";
        }
        onLoadingCompleteListener = listener;
        url = ImgUriUtils.getWebpUrl(getContext(), url);
        mUrl = url;
        GenericDraweeHierarchy hierarchy = getHierarchy();
        if (hierarchy != null)
        {
            hierarchy.setFadeDuration(fadeInMillis);
            try
            {
                if (failImage != -1)
                {
                    hierarchy.setFailureImage(failImage); // 加载失败图片
                }
                if (loadingImage != -1)
                {
                    hierarchy.setPlaceholderImage(loadingImage); // 加载中图片
                }
            }
            catch (Resources.NotFoundException e)
            {
                Log.e(ImgUriUtils.LOG_TAG, e + "");
            }

            RoundingParams roundingParams = new RoundingParams();
            if (topLeftRadius < 0 && topRightRadius < 0 && bottomLeftRadius < 0 && bottomRightRadius < 0)
            {
                roundingParams.setRoundAsCircle(true); // 圆形图片
            }
            else
            {
                roundingParams.setCornersRadii(topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius);// 圆角
            }
            hierarchy.setRoundingParams(roundingParams);
            ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(
                    Uri.parse(url)).setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH);
            if (!TextUtils.isEmpty(url)
                    && (url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://")))
            {
                if (enableNetworkDetect && mobileNetwork && ImgUriUtils.is3GNoImage(getContext())) // 移动网络加载网络图片开关
                {
                    // url = null;
                    imageRequestBuilder.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.DISK_CACHE);
                }
            }
            ImageRequest imageRequest = imageRequestBuilder.build();
            DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(
                    imageRequest).setAutoPlayAnimations(autoPlayAnim).setOldController(
                            getController()).setControllerListener(this).build();
            setController(controller);
        }
    }

    /**
     * {@link SimpleDraweeView} 展示出来主要有两个方面。 1.
     * {@link com.facebook.drawee.interfaces.DraweeHierarchy} 包含了展示信息：圆角、默认图等等 2.
     * {@link DraweeController} 包含了控制的逻辑：请求的链接、是否允许直接播放gif等等
     */
    public void setImageInfo(AsyncImageInfo imageInfo)
    {
        if (imageInfo == null)
        {
            throw new IllegalArgumentException("imageInfo can not be null");
        }
        String url = imageInfo.getUrl();
        if (url == null)
        {
            url = "";
        }
        onLoadingCompleteListener = imageInfo.getListener();
        url = ImgUriUtils.getWebpUrl(getContext(), url);
        mUrl = url;

        // 1. build hierarchy
        GenericDraweeHierarchy hierarchy = getHierarchy();
        if (hierarchy != null)
        {
            hierarchy.setFadeDuration(imageInfo.getFadeInMillis());
            try
            {
                if (imageInfo.getDefaultImage() != -1)
                {
                    hierarchy.setFailureImage(imageInfo.getDefaultImage());
                }
                if (imageInfo.getLoadingImage() != -1)
                {
                    hierarchy.setPlaceholderImage(imageInfo.getLoadingImage());
                }
            }
            catch (Resources.NotFoundException e)
            {
                Log.e(ImgUriUtils.LOG_TAG, e + "");
            }

            RoundingParams roundingParams = new RoundingParams();
            if (imageInfo.isCircle())
            {
                roundingParams.setRoundAsCircle(true);
            }
            else if ((imageInfo.getTopLeftRadius() | imageInfo.getTopRightRadius() | imageInfo.getBottomLeftRadius()
                    | imageInfo.getTopLeftRadius()) > 0)
            {
                roundingParams.setCornersRadii(imageInfo.getTopLeftRadius(), imageInfo.getTopRightRadius(),
                        imageInfo.getBottomLeftRadius(), imageInfo.getTopLeftRadius());
            }
            hierarchy.setRoundingParams(roundingParams);

            // 2. build ImageRequest for DraweeController
            ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(
                    Uri.parse(url)).setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH);
            if (!TextUtils.isEmpty(url)
                    && (url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://")))
            {
                if (enableNetworkDetect && mobileNetwork && ImgUriUtils.is3GNoImage(getContext()))
                {
                    imageRequestBuilder.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.DISK_CACHE);
                }
            }
            if (imageInfo.getPostprocessor() != null)
            {
                imageRequestBuilder.setPostprocessor(imageInfo.getPostprocessor());
            }
            ImageRequest imageRequest = imageRequestBuilder.build();
            // 3. build DraweeController
            DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(
                    imageRequest).setAutoPlayAnimations(imageInfo.isAutoPlayAnim()).setOldController(
                            getController()).setControllerListener(this).build();
            setController(controller);
        }
    }

    @Override
    public void onSubmit(String id, Object callerContext)
    {

    }

    @Override
    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable)
    {
        if (onLoadingCompleteListener != null)
        {
            int duration = 0;
            if (animatable != null && animatable instanceof AbstractAnimatedDrawable)
            {
                duration = ((AbstractAnimatedDrawable) animatable).getDuration();
            }
            onLoadingCompleteListener.onResult(true, this, duration);
            if (imageInfo != null)
            {
                onLoadingCompleteListener.onGetImageInfo(true, imageInfo.getWidth(), imageInfo.getHeight());
            }
            else
            {
                onLoadingCompleteListener.onGetImageInfo(false, 0, 0);
            }
        }
    }

    @Override
    public void onIntermediateImageSet(String id, ImageInfo imageInfo)
    {

    }

    @Override
    public void onIntermediateImageFailed(String id, Throwable throwable)
    {

    }

    @Override
    public void onFailure(String id, Throwable throwable)
    {
        if (onLoadingCompleteListener != null)
        {
            onLoadingCompleteListener.onResult(false, this, 0);
            onLoadingCompleteListener.onGetImageInfo(false, 0, 0);
            Log.e(ImgUriUtils.LOG_TAG, "load img err ---> " + throwable.toString());
        }
    }

    @Override
    public void onRelease(String id)
    {

    }
}
