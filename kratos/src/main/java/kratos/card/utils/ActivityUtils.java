package kratos.card.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RawRes;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import kratos.card.render.Template;

/**
 * Activity帮助类
 *
 * @author Sanvi E-mail:sanvibyfish@gmail.com
 * @version 创建时间：2010-8-25 上午11:48:00
 */
public class ActivityUtils {
    public static Point displaySize = new Point();

    public static final int REGISTER_ACTIVITY = 10;

    private static final String TAG = "ActivityUtils";

    /**
     * 用于获取来自哪个Activity的标记
     */
    public static final String BUNDLE_ACTIVITY_FROM = "from";
    /**
     * 用户获取是否要刷新页面的标记
     */
    public static final String BUNDLE_ACTIVITY_UPDATE = "update";


    /**
     * 检测是否最顶层的Activity
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isTopActivity(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            //应用程序位于堆栈的顶层
            if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static int randomColor() {
        return Color.argb(127, ((Long) Math.round(Math.random() * 255)).intValue(), ((Long) Math.round(Math.random() * 255)).intValue(), ((Long) Math.round(Math.random() * 255)).intValue());
    }

    /**
     * This method convets dp unit to equivalent device specific value in pixels.
     *
     * @param dp      A value in dp(Device independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent Pixels equivalent to dp according to device
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to device independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent db equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;

    }


    public static int getStatusBarHeight(Activity activity) {
        Rect rect = new Rect();
        Window win = activity.getWindow();
        win.getDecorView().getWindowVisibleDisplayFrame(rect);
        return (int) convertPixelsToDp(rect.top, activity);
    }

    public static String getRealPathFromURI(Uri contentUri, Activity activity) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static String getPathFromUri(Uri contentUri) {
        return contentUri.getPath();
    }

    /**
     * 获取当前Version名称
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 跳转到下一个Acitivty
     *
     * @param old         当前Activity的Context
     * @param cls         下一个Activity的class
     * @param requestCode 返回时带的request code
     * @param mBundle
     */
    public static void jump(Context old, Class<?> cls, int requestCode, Bundle mBundle) {
        jump(old, cls, requestCode, mBundle, false);
    }

    /**
     * 跳转到下一个Acitivty
     *
     * @param old         当前Activity的Context
     * @param cls         下一个Activity的class
     * @param requestCode 返回时带的request code
     * @param mBundle
     * @param clearTop    是否清除堆栈
     */
    public static void jump(Context old, Class<?> cls, int requestCode, Bundle mBundle, boolean clearAll) {
        Intent intent = new Intent(old, cls);
        if (mBundle == null) {
            mBundle = new Bundle();
        }
        Activity oldActivity = (Activity) old;
        mBundle.putString(BUNDLE_ACTIVITY_FROM, oldActivity.getClass().getSimpleName());
        intent.putExtras(mBundle);

        Activity activity = (Activity) old;
        if (clearAll) {
            int flags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;
            intent.setFlags(flags);
            activity.finish();
        }


        activity.startActivityForResult(intent, requestCode);
    }

    public static void jump(Context old, Class<?> cls, int requestCode, @RawRes int id) {
        Bundle bundle = new Bundle();
        bundle.putString(Template.BUNDLE_TEMPLAT, StringUtils.convertStreamToString(old.getResources().openRawResource(id)));
        jump(old, cls, requestCode, bundle);
    }


    /**
     * 跳转到下一个Acitivty
     *
     * @param old         当前Activity的Context
     * @param cls         下一个Activity的class
     * @param requestCode 返回时带的request code
     */
    public static void jump(Context old, Class<?> cls, int requestCode) {
        jump(old, cls, requestCode, null);
    }

    /**
     * 打开一个schema url，如果schema是http那么打开cls的Activity
     *
     * @param old
     * @param cls
     * @param url
     * @param requestCode
     * @param mBundle
     */
    public static void jump(Context old, Class<?> cls, String url, int requestCode, Bundle mBundle) {
        if (url != null && !url.startsWith("http") && !url.startsWith("https")) {
            Intent intent = new Intent();
            intent.setData(Uri.parse(url.trim()));
            if (mBundle != null) {
                intent.putExtras(mBundle);
            }
            old.startActivity(intent);
        } else {
            jump(old, cls, requestCode, mBundle);
        }

    }


    /**
     * 结束工作流
     *
     * @param old
     */
    public static void backAffinity(Context old) {
        back(old, null, true);
    }

    /**
     * 结束工作流
     *
     * @param old
     */
    public static void backAffinity(Context old, Bundle bundle) {
        back(old, bundle, true);
    }

    /**
     * 返回上一个Activity
     *
     * @param old      当前Activity的Context
     * @param isUpdate 带是否刷新标记
     */
    public static void backAffinity(Context old, boolean isUpdated) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(BUNDLE_ACTIVITY_UPDATE, isUpdated);
        back(old, bundle, true);
    }


    /**
     * 返回上一个Activity
     */
    public static void back(Context old, Bundle bundle) {
        back(old, bundle, false);
    }

    /**
     * 返回上一个Activity
     */
    public static void back(Context old) {
        back(old, null, false);
    }


    /**
     * 返回上一个Activity
     *
     * @param old      当前Activity的Context
     * @param isUpdate 带是否刷新标记
     */
    public static void back(Context old, boolean isUpdated) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(BUNDLE_ACTIVITY_UPDATE, isUpdated);
        back(old, bundle, false);
    }

    /**
     * 返回上一个Activity
     *
     * @param old     当前Activity的Context
     * @param mBundle
     */
    public static void back(Context old, Bundle mBundle, boolean isAffinity) {
        Activity activity = (Activity) old;
        Intent intent = activity.getIntent();
        if (mBundle != null) {
            mBundle.putBoolean(BUNDLE_ACTIVITY_UPDATE, true);
            intent.putExtras(mBundle);
        }
        if (isAffinity) {
            activity.setResult(Activity.RESULT_CANCELED);
            ActivityCompat.finishAffinity(activity);
        } else {
            activity.setResult(Activity.RESULT_OK, intent);
            activity.finish();
        }
    }

    public static Display getWindowDisplay(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    public static String getPhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = telephonyManager.getLine1Number();
        return phoneNumber;
    }

    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public static String getString(Context context, final int mStringId) {
        return context.getResources().getString(mStringId);
    }

    public static int getColor(Context context, final int mColorId) {
        return context.getResources().getColor(mColorId);
    }

    public static int getPX(Context context, int dipValue) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.getResources().getDisplayMetrics());
        return (int) px;
    }

    public static void showInput(EditText editText) {
        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

    /**
     * 隐藏软键盘
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager m = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (m != null) {
            m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 隐藏软键盘
     */
    public static void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }


    public static boolean isApkAvailable(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(
                    packageName, 0);

        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    public static void openApk(Context context, String packageName) {
        if (isApkAvailable(context, packageName)) {
            Intent intent = new Intent();
            intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
        } else {//未安装，跳转至market下载该程序
            Uri uri = Uri.parse("market://details?id=" + packageName);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(it);
        }
    }

    public static Drawable getDrawable(Context context, String mDrawableName) {
        Resources res = context.getResources();
        int resID = res.getIdentifier(mDrawableName, "drawable", context.getPackageName());
        Drawable drawable = res.getDrawable(resID);
        return drawable;
    }


    public static View addListViewHeaderView(ListView listview, int layoutId, Context context) {
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(layoutId, null);
        listview.addHeaderView(view);
        return view;
    }


    // =========================================================================
    // app版本信息
    // =========================================================================
    public static int getCurrentVersionCode(Context context) {
        PackageInfo pinfo;
        int versionCode;
        try {
            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = pinfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            versionCode = -1;
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getCurrentVersionName(Context context) {
        PackageInfo pinfo;
        String versionName = null;
        try {
            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = pinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
