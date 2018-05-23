package com.example.jingbiaozhen.androidutils.fresco;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

/** Uri相关工具类 */
public class ImgUriUtils
{

    static final String LOG_TAG = "ZYM ImageLoader";

    /** 3g不加载图片 */
    public static final String SETTINGS_3G_NO_IMAGE = "SETTINGS_3G_NO_IMAGE";
    public static final String SHARED_PREFERENCE_NAME = "webp";

    /** 手机系统是否支持webp格式 **/
    public static final String WEBP_SUPPORT = "webp_support";

    /** globalconfig返回的webp总开关 **/
    public static final String WEBP_CONFIG = "webp_config";

    /**
     * 支持webp的图片域名
     */
    public static String imgDomains = "^((img([1-3]|[5-9]|1[0-9]|2[0-8]|3[0-9]|4[0-5])|m\\.imgx|v\\.img|webpic)\\.pplive\\.cn|(img(1|[5-9]|1[0-9]|2[0-8])|res[1-4]?|sr[1-9]|img\\.bkm)\\.pplive\\.com|(m\\.imgx|focus)\\.pptv\\.com)$";

    // 缓存已经做个正则匹配的host，避免每次都去做正则匹配，导致卡慢
    private static ArrayList<String> matcheredDomains = new ArrayList<String>();

    private static Pattern pattern = Pattern.compile(imgDomains);

    public static String getHostPort(String url)
    {
        String hostPort = "";
        try
        {
            int start = url.indexOf("://");

            if (start > 0)
            {
                int end = url.indexOf("/", start + 3);

                hostPort = url.substring(start + 3, end > 0 ? end : url.length());
            }
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, "getHostPort--> " + e);
        }
        // Log.d(LOG_TAG, "getHostPort--> " + hostPort + "|" + url);
        return hostPort;
    }

    public static String getPath(String url)
    {
        String path = "";
        try
        {
            int start = url.indexOf("://");

            if (start > 0)
            {
                start = url.indexOf("/", start + 3);

                if (start > 0)
                {
                    int end = url.indexOf("?", start + 1);

                    path = url.substring(start, end > 0 ? end : url.length());
                }
            }
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, "getPath--> " + e);
        }

        // Log.d(LOG_TAG, "getHostPort--> " + path + "|" + url);
        return path;
    }

    public static String getQuery(String url)
    {
        String query = "";
        try
        {
            int start = url.indexOf("?");

            if (start > 0)
            {

                int end = url.indexOf("#", start + 1);

                query = url.substring(start + 1, end > 0 ? end : url.length());
            }
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, "getQuery--> " + e);
        }

        // Log.d(LOG_TAG, "getHostPort--> " + query + "|" + url);

        return query;
    }

    /**
     * 判断图片域名是否支持webp
     * 
     * @param url
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isImageSupportWebP(String url)
    {
        if (TextUtils.isEmpty(url))
        {
            return false;
        }
        String host = getHostPort(url);
        if (TextUtils.isEmpty(host))
        {
            return false;
        }
        if (pattern != null)
        {
            if (matcheredDomains != null && matcheredDomains.size() > 0)
            {
                int size = matcheredDomains.size();
                for (int i = 0; i < size; i++)
                {
                    if (matcheredDomains.get(i).equals(host))
                    {
                        return true;
                    }
                }
            }

            Matcher matcher = pattern.matcher(host);
            if (matcher != null && matcher.matches())
            {
                if (matcheredDomains != null)
                {
                    matcheredDomains.add(host);
                }

                return true;
            }
        }
        return false;
    }

    /**
     * 手机系统是否支持webp格式的图片
     * 
     * @param context
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isSupportWebP(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(WEBP_SUPPORT, false);
    }

    /**
     * 保存globalconfig返回的webp开关
     * 
     * @param context
     * @param open
     * @see [类、类#方法、类#成员]
     */
    public static void saveWebpConfig(Context context, boolean open)
    {
        SharedPreferences sp = context.getSharedPreferences(ImgUriUtils.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(ImgUriUtils.WEBP_CONFIG, open);
        editor.commit();
    }

    /**
     * 判断globalconfig返回的webp开关是否打开，默认是false
     *
     * @param context
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isWebpOpen(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(WEBP_CONFIG, false);
    }

    /**
     * 根据图片url，获取webp格式的url
     *
     * @param context
     * @param url 原图片格式的url
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getWebpUrl(Context context, String url)
    {

        if (TextUtils.isEmpty(url))
        {
            return url;
        }
        boolean webpOpen = isWebpOpen(context);
        boolean webpSupport = isSupportWebP(context);
        boolean isImageSurr = isImageSupportWebP(url);
        boolean checkImage = checkImageSuffix(url);
        // 先判断globalconfig开关是否允许webp
        if (webpOpen && webpSupport && isImageSurr && checkImage)
        {
            return url + ".webp";
        }
        return url;
    }

    /**
     * 目前仅对jpg,jpeg格式的图片做webp支持
     * 
     * @param url
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean checkImageSuffix(String url)
    {
        if (TextUtils.isEmpty(url))
        {
            return false;
        }
        if (url.toLowerCase().endsWith(".jpg") || url.toLowerCase().endsWith(".jpeg"))
        {
            return true;
        }
        return false;
    }

    static boolean isMobileNetwork(Context context)
    {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null)
        {
            NetworkInfo netInfo = manager.getActiveNetworkInfo();
            if (netInfo != null)
            {
                return netInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }

    public static boolean is3GNoImage(Context context)
    {
        String configPrefsName = "config";
        try
        {
            // 默认关闭
            return context.getSharedPreferences(configPrefsName, Context.MODE_PRIVATE).getBoolean(SETTINGS_3G_NO_IMAGE,
                    false);
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, e + "");
            remove(context, configPrefsName, SETTINGS_3G_NO_IMAGE);
        }
        return false;
    }

    private static void remove(Context context, String prfsName, String key)
    {
        try
        {
            SharedPreferences.Editor editor = context.getSharedPreferences(prfsName, Context.MODE_PRIVATE).edit();
            editor.remove(key);
            editor.apply();
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, "" + e);
        }
    }
}
