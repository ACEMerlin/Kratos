package kratos.internal

import android.view.View
import android.widget.EditText
import android.widget.TextView

/**
 * Created by sanvi on 1/8/16.
 */
public fun TextView.updateText(new: String) {
    this.text = new
}

public fun EditText.updateText(new: String) {
    val position = this.selectionStart
    this.setText(new)
    this.setSelection(position)
}

public fun View.updateText(new: String): Unit = when (this) {
    is EditText -> this.updateText(new)
    is TextView -> this.updateText(new)
    else -> throw NoSuchMethodException("no update method on $this")
}

public fun View.updateVisibility(new: Boolean) {
    if (new) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}



