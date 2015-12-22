package me.ele.kratos_sample.entity;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import java.io.IOException;
import java.lang.reflect.Type;

import kratos.internal.KString;

/**
 * Created by sanvi on 12/22/15.
 */
public class KStringDeserializer implements JsonDeserializer<KString> {

    private Class<?> mTargetClass;

    @Override
    public KString deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        KString field = new KString();
        field.setData(json.getAsString());
        return null;
    }
}
