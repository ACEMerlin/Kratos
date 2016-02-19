package kratos.card.render

import kratos.card.KCard
import kratos.card.entity.KData
import java.io.Serializable


/**
 * Created by sanvi on 11/4/15.
 */
class Template : Serializable {
    var header: Header? = null
    val body: MutableList<KCard<KData>> = arrayListOf()
    val footer: MutableList<KCard<KData>> = arrayListOf()

    companion object {
        const val BUNDLE_TEMPLAT: String = "template"
        const val BUNDLE_URL: String = "url"
    }

}
