package kratos.compiler.binding;

import com.squareup.javapoet.MethodSpec;

/**
 * Created by sanvi on 1/8/16.
 */
public interface KBindingGeneric {
    public void addGenericCode(MethodSpec.Builder result, String key);

    public String getMethodName();

    public String[] getParameterTypes();
}