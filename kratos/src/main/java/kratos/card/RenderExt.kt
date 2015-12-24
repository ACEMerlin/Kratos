package kratos.card

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import kratos.Kratos
import kratos.R
import kratos.card.entity.KData
import kratos.card.render.*
import kratos.card.utils.DelegateExt
import kratos.card.utils.DrawableUtils
import kratos.card.utils.GsonUtils
import kratos.card.utils.OnCardRenderListener
import org.json.JSONArray
import org.json.JSONObject

fun KCardActivity.render(viewGroup: ViewGroup, footerViewGroup: ViewGroup, toolbar: Toolbar, cb: (template: Template) -> Unit) {
    onRender(getIntent().extras.getString(Template.BUNDLE_TEMPLAT), { json ->
        cb(renderTemplate(viewGroup, footerViewGroup, toolbar, json, this))
    })
}

public var Context.onCardRenderListener: OnCardRenderListener by DelegateExt.notNullCardRenderListener<OnCardRenderListener>()
public var cards: Map<String, KCard<KData>> = emptyMap()

fun Context.renderTemplate(viewGroup: ViewGroup, footerViewGroup: ViewGroup, toolbar: Toolbar, templateString: String, context: Context): Template {
    Log.d("RenderExt", "$templateString")
    viewGroup.removeAllViews()
    cards = emptyMap()
    var template: Template = toTemplate(templateString) as Template
    template.header?.title?.let {
        toolbar.title = template.header?.title
    }
    template.header?.icon?.let {
        toolbar.navigationIcon = DrawableUtils.getMipmap(context, template.header?.icon)
    }
    template.header?.background?.let {
        toolbar.setBackgroundColor(Color.parseColor(template.header?.background))
    }
    template.header?.bodyBackgroud?.let {
        viewGroup.setBackgroundColor(Color.parseColor(template.header?.bodyBackgroud))
    }

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

fun Context.resetMenu(template: Template?, toolbar: Toolbar?, cb: (id: Int, data: String) -> Unit): android.view.Menu {
    toolbar?.inflateMenu(R.menu.empty)
    var menu = toolbar?.menu
    template?.header?.menus?.let {
        menu?.clear()
        for (i in 0..template?.header!!.menus.size - 1) {
            var m = template?.header!!.menus[i]
            if (m.type.equals(kratos.card.render.Menu.SEARCH)) {
                var item: MenuItem = menu!!.add(0, m.id, 0, "")
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM or MenuItem.SHOW_AS_ACTION_WITH_TEXT)
                var searchView: SearchView = SearchView(this)
                m.style?.let {
                    if ((m.style as SearchStyle).expand) {
                        val searchEditView = searchView.findViewById(R.id.search_src_text) as EditText
                        try {
                            val mCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                            mCursorDrawableRes.isAccessible = true
                            mCursorDrawableRes.set(searchEditView, 0) //This sets the cursor resource ID to 0 or @null which will make it visible on white background
                        } catch (e: Exception) {
                        }
                        searchView.isSubmitButtonEnabled = false
                        searchView.isIconified = false
                        searchView.queryHint = m.hint ?: ""
                    }
                }
                item.setActionView(searchView)


                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        cb(m.id, newText)
                        return true
                    }
                })
            } else if (m.type.equals(kratos.card.render.Menu.TEXT)) {
                var item: MenuItem = menu!!.add(0, m.id, 0, "")
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM or MenuItem.SHOW_AS_ACTION_WITH_TEXT)
                item.setOnMenuItemClickListener {
                    cb(m.id, "")
                    true
                }
            }
        }
    }
    return menu as android.view.Menu
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
                var clazz = Class.forName(jsonObject.get("type").toString())
                val card = GsonUtils.getGson(this, clazz).fromJson<KCard<KData>>(jsonString, clazz)
                t.body.add(card)
                Kratos.bind(card)
            }
        }
        if (`object`.has("footer")) {
            val jsonObjectParentList = JSONArray(`object`.getString("footer"))
            for (i in 0..jsonObjectParentList.length() - 1) {
                val jsonObject = jsonObjectParentList.getJSONObject(i)
                Log.d("KCARD.jsonobject", jsonObjectParentList.toString())
                var jsonString = jsonObject.toString()
                jsonString = onCardRenderListener.onRender(jsonString)
                var clazz = Class.forName(jsonObject.get("type").toString())
                t.footer.add(GsonUtils.getGson(this, clazz).fromJson<KCard<KData>>(jsonString, clazz))
            }
        }
        return t
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}
