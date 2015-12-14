package kratos.card.utils

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by sanvi on 11/16/15.
 */
internal class NotNullCardRenderListener<T>() : ReadWriteProperty<Any?, T> {
    private var value: T? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: object : OnCardRenderListener {
            override fun onRender(json: String): String {
                return json
            }
        } as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value

    }
}