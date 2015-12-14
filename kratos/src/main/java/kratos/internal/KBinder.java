package kratos.internal;

/**
 * Created by merlin on 15/12/7.
 */
public interface KBinder<T> {
    void bind(T target, KFinder finder);
}
