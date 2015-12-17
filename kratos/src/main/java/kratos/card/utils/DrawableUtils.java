package kratos.card.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

public class DrawableUtils {

    public static Drawable changeWhiteDrawable(Context context, Drawable drawable) {
        return changeDrawableColor(drawable, Color.parseColor("#FFFFFF"));
    }

    public static Drawable changeDrawableColor(Drawable drawable, int color) {
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }


    public static Drawable getMipmap(Context context, String name) {
        int resourceId = context.getResources().getIdentifier(name, "mipmap", context.getPackageName());
        return ContextCompat.getDrawable(context, resourceId);
    }

    public static int getMipmapId(Context context, String name) {
        return context.getResources().getIdentifier(name, "mipmap", context.getPackageName());
    }


}