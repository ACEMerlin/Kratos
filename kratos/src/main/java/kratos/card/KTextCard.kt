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
    @BindString("kcard_text_text")
    public var _text = KString {
        it, new ->
        it as TextView
        it.text = new
    }

    constructor(context: Context) : super(context) {
        Kratos.bind(this)
        setOnLinkListener()
    }

    override fun refresh() {
        _text.data = data?.text!!
    }

    override fun getResourceLayoutId(): Int {
        return R.layout.kcard_avatar
    }
}