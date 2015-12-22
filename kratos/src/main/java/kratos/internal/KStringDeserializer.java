package kratos.internal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by sanvi on 12/22/15.
 */
public class KStringDeserializer implements JsonDeserializer<KString> {

    private Class<?> mTargetClass;

    @Override
    public KString deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        KString field = new KString();
        field.setInitData(json.getAsString());
        return field;
    }
}
