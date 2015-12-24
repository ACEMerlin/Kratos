package kratos.compiler;

/**
 * Created by merlin on 15/12/24.
 */
public class UpdateKStringBinding {

    private String methodName;
    private String[] parameterTypes;

    public UpdateKStringBinding(String methodName, String[] parameterTypes) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }
}
