package kratos.card.utils;

import android.content.Context;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kratos.internal.KString;
import kratos.internal.KStringDeserializer;

/**
 * Created by sanvi on 9/28/15.
 */
public class GsonUtils {
    public static Gson gson;
    private static GsonUtils instance;

    public static GsonUtils getInstance() {
        if (instance == null) {
            instance = new GsonUtils();
        }
        return instance;
    }

    public Gson getGson() {
        return getGson(null, null);
    }

    public static Gson getGson(Context context, Class clazz) {
        GsonBuilder gsonBuilder = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return fieldAttributes.getDeclaredClass().getName().startsWith("android.")
                        || fieldAttributes.getName().endsWith("$delegate");
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        }).addDeserializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return fieldAttributes.getDeclaredClass().getName().startsWith("android.")
                        || fieldAttributes.getName().endsWith("$delegate");
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        });

        if (clazz != null && context != null) {
            gsonBuilder.registerTypeAdapter(clazz, new GsonUtilsCreator(context));
        }
        gsonBuilder.registerTypeAdapter(KString.class, new KStringDeserializer());
        return gsonBuilder.setPrettyPrinting()
                .disableHtmlEscaping().create();

    }
}
