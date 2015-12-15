package kratos.card.render

import java.io.Serializable

open class Style : Serializable {

    var margin_top: Int = 0
    var margin_bottom: Int = 0
    var margin_left: Int = 0
    var margin_right: Int = 0
    var background_color: String? = null
    var text_color: String? = null
    var normal_color: String? = null
    var pressed_color: String? = null
    var href_color: String? = null
    var text_type: String? = null
    var text_length: Int ? = null
}
