package kratos.internal;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import kratos.card.KCard;

/**
 * Created by merlin on 15/12/7.
 */
public enum KFinder {
    ACTIVITY {
        @Override
        protected View findView(Object source, int id) {
            return ((Activity) source).findViewById(id);
        }

        @Override
        public Context getContext(Object source) {
            return (Activity) source;
        }
    },
    KCARD {
        @Override
        protected View findView(Object source, int id) {
            KCard card = (KCard) source;
            if (card.getRootView() == null) {
                LayoutInflater inflater = (LayoutInflater) getContext(source).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (card.getLayoutId() != 0) {
                    card.setRootView(inflater.inflate(card.getLayoutId(), null));
                }
            }
            return (card.getRootView()).findViewById(id);
        }

        @Override
        public Context getContext(Object source) {
            return ((KCard) source).getContext();
        }
    };

    @SuppressWarnings("unchecked") // That's the point.
    public <T> T castView(View view) {
        try {
            return (T) view;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T findRequiredView(Object source, int id) {
        T view = findOptionalView(source, id);
        if (view == null) {
            throw new IllegalStateException("Required view"
                    + "with ID "
                    + id);
        }
        return view;
    }

    public <T> T findOptionalView(Object source, int id) {
        View view = findView(source, id);
        return castView(view);
    }

    protected abstract View findView(Object source, int id);

    public abstract Context getContext(Object source);
}
