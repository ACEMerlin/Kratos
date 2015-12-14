package kratos.card.render

import java.io.Serializable

open class Menu<T : Style>: Serializable {
    var type: String? = null
    var style: T? = null
    var id: Int = 0
    var hint: String? = null

    companion object {
        val SEARCH = "search"
        val TEXT = "text"
    }
}