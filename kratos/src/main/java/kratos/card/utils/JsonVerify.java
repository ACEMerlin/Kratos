package kratos.card.utils;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * To verify whether a json is legal
 * Created by Wayne on 15/12/25.
 */
public class JsonVerify {

    public static boolean isIllegalJson(String json) {
        return !isLegalJson(json);
    }

    public static boolean isLegalJson(String json) {
        if ((json).isEmpty()) {
            return false;
        }
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
