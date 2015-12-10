package com.kratos;

import com.squareup.javapoet.TypeName;

import static com.kratos.KratosProcessor.VIEW_TYPE;
/**
 * Created by merlin on 15/12/7.
 */
public final class FieldViewBinding implements KBinding {
    private final String name;
    private final TypeName type;
    private final boolean required;

    FieldViewBinding(String name, TypeName type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public TypeName getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean requiresCast() {
        return !VIEW_TYPE.equals(type.toString());
    }


}
