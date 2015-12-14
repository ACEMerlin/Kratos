package kratos.card

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import de.greenrobot.event.EventBus
import kratos.Kratos
import kratos.card.event.KOnClickEvent
import kratos.card.entity.KData
import kratos.card.render.Style
import kratos.card.utils.Skip

open class KCard<T : KData>() {

    var view: String? = null
    var id: String? = null
    var data: T? = null
    var url: String? = null
    var style: Style? = null
    @Skip
    var rootView: View? = null
    @Skip
    var context: Context? = null

    constructor(context: Context): this(){
        this.context = context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if ( getResourceLayoutId() != 0) {
            rootView = inflater.inflate(getResourceLayoutId(), null)
        }
    }

    public fun show(){
        rootView?.visibility = View.VISIBLE
    }

    public fun hide(){
        rootView?.visibility = View.GONE
    }

    open public fun resetLayoutParams(): LinearLayout.LayoutParams {
        var params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )

        style?.let {
            var style: Style = style as Style
            params.setMargins(style.margin_left, style.margin_top, style.margin_right, style.margin_bottom)
        }

        return params
    }


    public open fun refresh() {
    }

    protected fun onLink() {
        EventBus.getDefault().post(KOnClickEvent(id, data, url))
    }

    protected fun onLink(position: Int) {
        EventBus.getDefault().post(KOnClickEvent(id, data, url, position))
    }

    fun findViewById(id: Int): View? {
        return rootView?.findViewById(id)
    }

    open fun getResourceLayoutId(): Int {
        return 0
    }

    open public fun getValue():String{
        return ""
    }

}
