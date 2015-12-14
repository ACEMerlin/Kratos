package kratos.card

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import kratos.R
import kratos.card.entity.KData
import kratos.card.event.KMenuClickEvent
import kratos.card.event.KOnClickEvent
import kratos.card.render.Template
import kratos.card.utils.GsonUtils
import kratos.card.utils.OnCardRenderListener
import org.json.JSONObject

open class KCardActivity : AppCompatActivity() {
    open fun onRender(json: String,filter:(json: String) -> Unit){
        filter(json)
    }

    var mainLayout: LinearLayout? = null
    var footerLayout: RelativeLayout? = null
    var template: Template? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kcard_main)
        mainLayout = findViewById(R.id.kcard_main_content) as LinearLayout
        footerLayout = findViewById(R.id.kcard_main_footer) as RelativeLayout
        onCreated()
        render()
    }

    fun render(){
        render(mainLayout as LinearLayout, footerLayout as RelativeLayout, { template ->
            this.template = template
            invalidateOptionsMenu()
            onFinishRender()
            refresh()
        })
    }

    open public fun refresh() {
        cards.entries.map { it.value.refresh() }
    }

    open public fun onCreated(){}
    open public fun onFinishRender(){}

    public fun getCard(id: String): KCard<KData>? {
        return cards[id]
    }

    public fun getCardJSON(id: String): String? {
        return GsonUtils.getInstance().gson.toJson(cards[id])
    }

    public fun replaceCard(id: String, json: String) {
        val jsonObject = JSONObject(json)
        var clazz = Class.forName("kratos.card." + jsonObject.get("type"))
        cards += mapOf<String, KCard<KData>>(id to GsonUtils.getGson(this,clazz).fromJson<KCard<KData>>(json, clazz))
        getCard(id)?.refresh()
    }

    public fun replaceData(id:String, json: String) {
        val jsonObject = JSONObject(json)
        var clazz = Class.forName("kratos.card." + jsonObject.get("type"))
        var card = GsonUtils.getGson(this,clazz).fromJson<KCard<KData>>(json, clazz)
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
}
