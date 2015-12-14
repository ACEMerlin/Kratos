package kratos.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by merlin on 15/12/7.
 */
public class KBindings {
    private final Set<FieldViewBinding> fieldBindings = new LinkedHashSet<>();
    private final String id;

    KBindings(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void addFieldBinding(FieldViewBinding fieldBinding) {
        fieldBindings.add(fieldBinding);
    }

    public Collection<FieldViewBinding> getFieldBindings() {
        return fieldBindings;
    }

    public List<KBinding> getRequiredBindings() {
        List<KBinding> requiredViewBindings = new ArrayList<>();
        for (FieldViewBinding fieldBinding : fieldBindings) {
            if (fieldBinding.isRequired()) {
                requiredViewBindings.add(fieldBinding);
            }
        }
        return requiredViewBindings;
    }

}
