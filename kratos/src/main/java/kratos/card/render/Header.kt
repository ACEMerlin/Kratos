package kratos.card.render

import java.io.Serializable

class Header : Serializable {
    var title: String? = null
    var arrow: Boolean = false
    var icon: String? = null
    var menus: MutableList<Menu<out Style>> = arrayListOf()
}
