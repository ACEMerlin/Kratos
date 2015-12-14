package me.ele.kratos_sample;

import android.app.Activity;
import android.os.Bundle;

import kratos.BindText;
import kratos.Kratos;
import kratos.internal.KString;

/**
 * Created by merlin on 15/12/10.
 */
public class SimpleActivity extends Activity {

    @BindText({R.id.test_doublebinding_input, R.id.test_doublebinding_presenter})
    KString boundData1 = new KString();
    //following code means the same as upper code;
    //@BindString({"test_doublebinding_input", "test_doublebinding_presenter"})
    //KString boundData2 = new KString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        Kratos.bind(this);
    }
}
