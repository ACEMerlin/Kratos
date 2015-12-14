package kratos.card

import android.content.Context
import android.widget.TextView
import kratos.BindString
import kratos.Kratos
import kratos.R
import kratos.card.entity.KText
import kratos.card.utils.Skip
import kratos.internal.KString

/**
 * Created by merlin on 15/11/23.
 */
class KTextCard : KCard<KText> {

    @Skip
    var text: TextView? = null
    @Skip
    @BindString("kcard_avatar_text")
    public var _text = KString()

    constructor(context: Context): super(context){
        text = findViewById(R.id.kcard_avatar_text) as TextView
        rootView?.setOnClickListener { onLink() }
        Kratos.bind(this)
    }

    override fun refresh() {
       _text.data = data?.text!!
    }

    override fun getResourceLayoutId(): Int {
        return R.layout.kcard_avatar
    }
}