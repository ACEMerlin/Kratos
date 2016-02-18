package io.nothing.kratos.core.generic

import android.view.View
import java.io.Serializable
import java.util.*

/**
 * Created by sanvi on 1/8/16.
 */
abstract class KBase() : Serializable {
    protected var views = LinkedHashMap<String, View>()

    inline fun <reified T> getView(str: String): T {
        return views.get(str)!! as T
    }

    abstract fun get(): Any


    /**
     * 通过resource name获取ID
     */
    fun getId(str: String): String {
        return str.split("/")[1]
    }
}