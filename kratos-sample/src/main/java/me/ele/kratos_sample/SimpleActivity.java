package me.ele.kratos_sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import kratos.BindText;
import kratos.Kratos;
import kratos.card.utils.ActivityUtils;
import kratos.internal.KString;

/**
 * Created by merlin on 15/12/10.
 */
public class SimpleActivity extends Activity {

    private final int CODE_CARD_SAMPLE = 123;

    @BindText({R.id.test_doublebinding_input, R.id.test_doublebinding_presenter})
    KString boundData1 = new KString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        Kratos.bind(this);
        findViewById(R.id.card_sample).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.jump(SimpleActivity.this, CardSampleActivity.class, CODE_CARD_SAMPLE, R.raw.sample);
            }
        });
    }
}
