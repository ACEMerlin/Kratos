package me.ele.kratos_sample;

import android.content.Context;

import kratos.BindLayout;
import kratos.BindText;
import kratos.Kratos;
import kratos.card.KCard;
import kratos.card.utils.Skip;
import kratos.internal.KString;
import me.ele.kratos_sample.entity.KText;

/**
 * Created by merlin on 15/12/17.
 */
@BindLayout(R.layout.kcard_text)  //@LBindLayout("kcard_text")
public class TextCard extends KCard<KText> {
    @Skip
    @BindText(R.id.kcard_text_text)  //@LBindText("kcard_text_text")
    public KString _text = new KString();

    public TextCard(Context context) {
        super(context);
        Kratos.bind(this);
        setOnLinkListener();
    }

    @Override
    public void refresh() {
        if (getData() != null)
            _text.setData(getData().text);
    }
}
