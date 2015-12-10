package me.ele.kratos_sample;

import android.app.Activity;
import android.os.Bundle;

import com.kratos.BindText;

import io.nothing.kratos.db.KString;
import me.ele.kratos.Kratos;

/**
 * Created by merlin on 15/12/10.
 */
public class SimpleActivity extends Activity {

    @BindText({R.id.test_doublebinding_input, R.id.test_doublebinding_presenter})
    KString bindedData = new KString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        Kratos.bind(this);
    }
}
