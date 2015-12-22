package me.ele.kratos_sample;

import android.content.Context;

import kratos.Bind;
import kratos.BindLayout;
import kratos.Binds;
import kratos.card.KCard;
import me.ele.kratos_sample.entity.KText;

/**
 * Created by merlin on 15/12/17.
 */
@BindLayout(R.layout.kcard_text)  //@LBindLayout("kcard_text")
@Binds({@Bind(id = R.id.kcard_text_text1, data = "text1"),
        @Bind(id = R.id.kcard_text_text2, data = "text2")})
public class TextCard extends KCard<KText> {
    public TextCard(Context context) {
        super(context);
    }

    @Override
    public void onRender() {
        setOnLinkListener();
    }
}
