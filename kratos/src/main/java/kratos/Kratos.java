package kratos;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

import kratos.internal.KBinder;
import kratos.internal.KFinder;

/**
 * Created by merlin on 15/12/7.
 */
public final class Kratos {
    private Kratos() {}
    private static final String TAG = "Kratos";

    static final Map<Class<?>, KBinder<Object>> BINDERS = new LinkedHashMap<>();
    static final KBinder<Object> NOP_VIEW_BINDER = new KBinder<Object>() {
        @Override public void bind(Object target, KFinder finder) { }
    };

    public static void bind(@NonNull Activity target) {
        bind(target, KFinder.ACTIVITY);
    }

    static void bind(@NonNull Object target, @NonNull KFinder finder) {
        Class<?> targetClass = target.getClass();
        try {
            Log.d(TAG, "Looking up view binder for " + targetClass.getName());
            KBinder<Object> viewBinder = findKBinderForClass(targetClass);
            viewBinder.bind(target, finder);
        } catch (Exception e) {
            throw new RuntimeException("Unable to bind views for " + targetClass.getName(), e);
        }
    }

    @NonNull
    private static KBinder<Object> findKBinderForClass(Class<?> cls)
            throws IllegalAccessException, InstantiationException {
        KBinder<Object> kBinder = BINDERS.get(cls);
        if (kBinder != null) {
            Log.d(TAG, "HIT: Cached in view binder map.");
            return kBinder;
        }
        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
            return NOP_VIEW_BINDER;
        }
        try {
            Class<?> kBindingClass = Class.forName(clsName + "$$KBinder");
            //noinspection unchecked
            kBinder = (KBinder<Object>) kBindingClass.newInstance();
            Log.d(TAG, "HIT: Loaded view binder class.");
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
            kBinder = findKBinderForClass(cls.getSuperclass());
        }
        BINDERS.put(cls, kBinder);
        return kBinder;
    }

}

