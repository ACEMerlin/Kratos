package kratos.internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.nothing.kratos.core.generic.KBase;
import kratos.card.KCard;
import kratos.card.entity.KData;

/**
 * Created by sanvi on 1/20/16.
 */
public class Binding<T extends KBase> {
    public String key;
    public T value;
    public String valueStr;

    public static List<Binding> parse(KCard kcard) {
        List<Binding> bindingList = new ArrayList<>();
        for (Field field : kcard.getData().getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                //如果是个列表
                if (field.getType().isAssignableFrom(List.class) || field.getType().isAssignableFrom(ArrayList.class)) {
                    List list = ((List) field.get(kcard.getData()));
                    for (Object obj : list) {
                        for (Field objField : obj.getClass().getDeclaredFields()) {
                            objField.setAccessible(true);
                            Binding binding = getBinding(objField, (KData) obj);
                            if (binding != null) {
                                bindingList.add(binding);
                            }
                        }
                    }
                    //如果不是列表
                } else {
                    Binding binding = getBinding(field, kcard.getData());
                    if (binding != null) {
                        bindingList.add(binding);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return bindingList;
    }

    private static Binding getBinding(Field field, KData data) throws IllegalAccessException {
        if (field.getType().isAssignableFrom(KString.class)) {
            Binding binding = new Binding();
            binding.value = ((KString) field.get(data));
            String temp = (String) binding.value.get();
            if (temp.contains("{") && temp.contains("}")) {
                binding.key = field.getName();
                binding.valueStr = temp;
                return binding;
            }
        }
        return null;
    }
}
