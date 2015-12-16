package kratos.card

import android.content.Context
import android.util.Log
import android.view.Menu
import android.view.ViewGroup
import android.widget.RelativeLayout
import kratos.card.entity.KData
import kratos.card.render.*
import kratos.card.utils.DelegateExt
import kratos.card.utils.GsonUtils
import kratos.card.utils.OnCardRenderListener
import org.json.JSONArray
import org.json.JSONObject

fun KCardActivity.render(viewGroup: ViewGroup, footerViewGroup: ViewGroup, cb: (template: Template) -> Unit) {
    onRender(getIntent().extras.getString(Template.BUNDLE_TEMPLAT), { json ->
        cb(renderTemplate(viewGroup, footerViewGroup, json, this))
    })
}

public var Context.onCardRenderListener: OnCardRenderListener by DelegateExt.notNullCardRenderListener<OnCardRenderListener>()
public var cards: Map<String, KCard<KData>> = emptyMap()

fun Context.renderTemplate(viewGroup: ViewGroup, footerViewGroup: ViewGroup, templateString: String, context: Context): Template {
    Log.d("RenderExt", "$templateString")
    viewGroup.removeAllViews()
    cards = emptyMap()
    var template: Template = toTemplate(templateString) as Template

    template.body.let {
        for (card in template.body) {
            viewGroup.addView(card.rootView, card.resetLayoutParams())
            cards += mapOf<String, KCard<KData>>(card.id as String to card)
        }
    }

    template.footer.let {
        for (card in template.footer) {
            var params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            card.rootView?.layoutParams = params
            footerViewGroup.addView(card.rootView)
            cards += mapOf(card.id as String to card)
        }
    }
    return template
}

fun Context.toTemplate(template: String): Template? {
    val t = Template()
    try {
        val `object` = JSONObject(template)
        var gson = GsonUtils.getGson(null, null)
        t.header = gson.fromJson(`object`.getString("header"), Header::class.java)

        if (`object`.has("header")) {
            t.header = gson.fromJson(`object`.getString("header"), Header::class.java)
            if (t.header!!.menus.size > 0) {
                t.header!!.menus = arrayListOf()
                var list = JSONArray(`object`.getJSONObject("header").getString("menus"))
                for (i in 0..list.length() - 1) {
                    var jsonObject = list.getJSONObject(i)
                    if (kratos.card.render.Menu.SEARCH.equals(jsonObject.getString("type"))) {
                        t.header!!.menus.add(gson.fromJson<SearchMenu<SearchStyle>>(jsonObject.toString(), SearchMenu::class.java))
                    } else {
                        t.header!!.menus.add(gson.fromJson<kratos.card.render.Menu<Style>>(jsonObject.toString(), Menu::class.java))
                    }
                }
            }
        }

        if (`object`.has("body")) {
            val jsonObjectList = JSONArray(`object`.getString("body"))
            for (i in 0..jsonObjectList.length() - 1) {
                val jsonObject = jsonObjectList.getJSONObject(i)
                Log.d("KCARD.jsonobject", jsonObject.toString())
                var jsonString = jsonObject.toString()
                jsonString = onCardRenderListener.onRender(jsonString)
                var clazz = Class.forName("kratos.card." + jsonObject.get("type"))
                t.body.add(GsonUtils.getGson(this, clazz).fromJson<KCard<KData>>(jsonString, clazz))
            }
        }
        if (`object`.has("footer")) {
            val jsonObjectParentList = JSONArray(`object`.getString("footer"))
            for (i in 0..jsonObjectParentList.length() - 1) {
                val jsonObject = jsonObjectParentList.getJSONObject(i)
                Log.d("KCARD.jsonobject", jsonObjectParentList.toString())
                var jsonString = jsonObject.toString()
                jsonString = onCardRenderListener.onRender(jsonString)
                var clazz = Class.forName("kratos.card." + jsonObject.get("type"))
                t.footer.add(GsonUtils.getGson(this, clazz).fromJson<KCard<KData>>(jsonString, clazz))
            }
        }
        return t
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}
