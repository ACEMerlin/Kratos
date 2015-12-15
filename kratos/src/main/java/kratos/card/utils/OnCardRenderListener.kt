package kratos.card.utils

/**
 * Created by sanvi on 11/16/15.
 */
interface OnCardRenderListener {
    open fun onRender(json: String): String
}