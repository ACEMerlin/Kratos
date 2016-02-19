package kratos.internal

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.View
import io.nothing.kratos.core.generic.KBase
import kotlin.properties.Delegates

/**
 * Created by merlin on 15/11/19.
 */
class KBoolean() : KBase() {

    interface OnUpdateListener {
        fun update(view: View, new: Boolean)
    }

    var updateFn: ((view: View, new: Boolean) -> Unit)? = null
    var onUpdateListener: OnUpdateListener? = null

    private var data: Boolean by Delegates.observable(true) {
        d, old, new ->
        if ((old != new)) {
            updateViews(new)
        }
    }

    private fun updateViews(new: Boolean) {
        for (entry in views.entries) {
            val view = entry.value
            Log.d("KBoolean", "VIEW UPDATE!: ${view.resources.getResourceName(view.id)} TO $new")
            if (updateFn != null)
                updateFn!!(view, new)
            else if (onUpdateListener != null)
                onUpdateListener!!.update(view, new)
            else
                view.updateVisibility(new)
        }
    }

    public fun bind(view: View) {
        Log.d("KBoolean", "VIEW BOUND!: ${view.resources.getResourceName(view.id)} TO ${this}")
        this.views.put(getId(view.resources.getResourceName(view.id)), view)
        this.views.entries.forEach {
            it.value.setOnSystemUiVisibilityChangeListener {
                if (it == 0) {
                    data = true
                } else {
                    data = false
                }
            }

        }
        updateViews(data)
    }

    override fun get(): Boolean {
        return data
    }

    fun set(new: Boolean) {
        data = new
    }
}
