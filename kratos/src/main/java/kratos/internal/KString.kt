package kratos.internal

import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import io.nothing.kratos.core.generic.KBase
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by merlin on 15/11/19.
 */
class KString() : KBase(), Parcelable {

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<KString> = object : Parcelable.Creator<KString> {
            override fun createFromParcel(source: Parcel): KString {
                return KString(source)
            }

            override fun newArray(size: Int): Array<KString> {
                return Array(size, { KString() })
            }
        }
    }

    protected constructor(`in`: Parcel) : this() {
        this.data = `in`.readString()
        `in`.readTypedList<KString>(this.kstrings, KString.CREATOR)
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(data)
        dest?.writeTypedList<KString>(kstrings)
    }

    override fun describeContents(): Int {
        return 0
    }

    interface OnUpdateListener {
        fun update(view: View, new: String)
    }

    private var kstrings: MutableList<KString> = ArrayList()
    var updateFn: ((view: View, new: String) -> Unit)? = null
    var onUpdateListener: OnUpdateListener? = null
    var initData: String? = null

    private var data: String by Delegates.observable("") {
        d, old, new ->
        if ((old != new)) {
            updateViews(new)
            updateKStrings(new)
        }
    }

    private fun updateKStrings(new: String) {
            Log.d("KString", "UPDATE BOUND KSTRING TO: $new")
        kstrings.forEach {
            it.set(new)
        }
    }

    private fun updateViews(new: String) {
        for (entry in views.entries) {
            val view = entry.value
                Log.d("KString", "VIEW UPDATE!: ${view.resources.getResourceName(view.id)} TO $new")
            if (updateFn != null)
                updateFn!!(view, new)
            else if (onUpdateListener != null)
                onUpdateListener!!.update(view, new)
            else
                view.updateText(new)
        }
    }

    public fun bind(kstring: KString) {
            Log.d("KString", "BOUND!!: $this TO $kstring")
        kstrings.add(kstring)
        updateKStrings(get())
    }

    public fun bind(view: View) {
            Log.d("KString", "VIEW BOUND!: ${view.resources.getResourceName(view.id)} TO ${this}")
        this.views.put(getId(view.resources.getResourceName(view.id)), view)
        this.views.entries.forEach {
            if (it.value is EditText) {
                (it.value as EditText).addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        data = s.toString()
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
            }
        }
        if (data != "")
            updateViews(data)
    }

    override fun get(): String {
        return data
    }

    fun set(new: String) {
        data = new
    }
}


