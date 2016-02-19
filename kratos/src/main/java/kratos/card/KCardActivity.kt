package kratos.card

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.widget.LinearLayout
import android.widget.RelativeLayout
import de.greenrobot.event.EventBus
import io.nothing.kratos.core.generic.KBase
import kratos.R
import kratos.card.entity.KData
import kratos.card.event.KMenuClickEvent
import kratos.card.event.KOnClickEvent
import kratos.card.render.Template
import kratos.card.utils.FixSwipeRefreshLayout
import kratos.card.utils.GsonUtils
import kratos.card.utils.OnCardRenderListener
import kratos.internal.Binding
import kratos.internal.KString
import org.json.JSONObject
import java.util.*

open class KCardActivity : AppCompatActivity() {
    open fun onRender(json: String, filter: (json: String) -> Unit) {
        filter(json)
    }

    var mainLayout: LinearLayout? = null
    var footerLayout: RelativeLayout? = null
    var toolbar: Toolbar? = null
    var template: Template? = null
    var swipeLayout: FixSwipeRefreshLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kcard_main)
        mainLayout = findViewById(R.id.kcard_main_content) as LinearLayout
        footerLayout = findViewById(R.id.kcard_main_footer) as RelativeLayout
        toolbar = findViewById(R.id.a_toolbar) as Toolbar
        swipeLayout = findViewById(R.id.kcard_main_swipe_refresh_layout) as FixSwipeRefreshLayout
        swipeLayout!!.setOnRefreshListener {
            refresh()
            swipeLayout!!.isRefreshing = false
        }
        EventBus.getDefault().register(this)
        onCreated()
        render()
        setSupportActionBar(toolbar)
    }

    fun render() {
        render(mainLayout as LinearLayout, footerLayout as RelativeLayout, toolbar as Toolbar, { template ->
            this.template = template
            invalidateOptionsMenu()
            onFinishRender()
            refresh()
        })
    }

    public fun disableRefresh() {
        swipeLayout!!.isEnabled = false
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        resetMenu(template, toolbar, { id, text ->
            EventBus.getDefault().post(KMenuClickEvent(id, text, ""))
        })
        return super.onPrepareOptionsMenu(menu)
    }

    open public fun refresh() {
        cards.entries.map { it.value.refresh() }
    }

    open public fun onCreated() {
    }

    open public fun onFinishRender() {
    }

    public fun getCard(id: String): KCard<KData>? {
        return cards[id]
    }

    public fun getCardJSON(id: String): String? {
        return GsonUtils.getInstance().gson.toJson(cards[id])
    }

    public fun replaceCard(id: String, json: String) {
        val jsonObject = JSONObject(json)
        var clazz = Class.forName("kratos.card." + jsonObject.get("type"))
        cards += mapOf<String, KCard<KData>>(id to GsonUtils.getGson(this, clazz).fromJson<KCard<KData>>(json, clazz))
        getCard(id)?.refresh()
    }

    public fun replaceData(id: String, json: String) {
        val jsonObject = JSONObject(json)
        var clazz = Class.forName("kratos.card." + jsonObject.get("type"))
        var card = GsonUtils.getGson(this, clazz).fromJson<KCard<KData>>(json, clazz)
        getCard(id)?.data = card.data
        getCard(id)?.refresh()
    }

    public fun setOnCardRenderListener(listener: OnCardRenderListener) {
        this.onCardRenderListener = listener
    }

    open fun onEventMainThread(event: KOnClickEvent<KData>) {
    }

    open fun onEventMainThread(event: KMenuClickEvent) {
    }

    fun getCards(): List<KCard<KData>> {
        return ArrayList(cards.values)
    }

    fun getBinding(kcard: KCard<KData>): List<Binding<KBase>> {
        return Binding.parse(kcard)
    }

    fun getBindings(): List<Binding<KBase>> {
        return getCards().flatMap {
            getBinding(it)
        }
    }

    fun bind(activity: KCardActivity, obj: Any) {
        for (binding in activity.getBindings()) {
            try {
                if (binding.value.javaClass.isAssignableFrom(KString::class.java)) {
                    val shit = binding.value.get() as String
                    val temp = shit.replace("{", "").replace("}", "").split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val clazzName = temp[0]
                    val fieldName = temp[1]
                    val `object` = obj.javaClass.getField(fieldName).get(obj)
                    val field = `object` as KString
                    field.bind(binding.value as KString)
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            }
        }
    }
}
