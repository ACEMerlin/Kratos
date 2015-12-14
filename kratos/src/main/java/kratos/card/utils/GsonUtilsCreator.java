package kratos.card.utils;

import android.content.Context;

import com.google.gson.InstanceCreator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import kratos.card.KCard;


/**
 * Created by sanvi on 11/26/15.
 */
public class GsonUtilsCreator implements InstanceCreator<KCard>
{
    private Context context;

    public GsonUtilsCreator(Context context)
    {
        this.context = context;
    }

    @Override
    public KCard createInstance(Type type)
    {
        KCard np = null;
        try {
            np = (KCard) Class.forName(type.toString().replace("class ","")).getConstructor(Context.class).newInstance(context);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        KCard np = new KCard(context);
        return np;
    }

}
