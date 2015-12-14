package kratos.card.utils

import kotlin.properties.ReadWriteProperty

object  DelegateExt {
    fun <T> notNullCardRenderListener():
            ReadWriteProperty<Any?, T> = NotNullCardRenderListener()
}