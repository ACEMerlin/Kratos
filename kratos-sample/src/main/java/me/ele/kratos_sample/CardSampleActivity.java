package me.ele.kratos_sample;

import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import kratos.card.KCardActivity;
import kratos.card.entity.KData;
import kratos.card.event.KOnClickEvent;
import me.ele.kratos_sample.entity.Customer;

/**
 * Created by merlin on 15/12/14.
 */
public class CardSampleActivity extends KCardActivity {

    Customer customer = new Customer();

    private void showToast(String text) {
        Toast.makeText(CardSampleActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventMainThread(@NotNull KOnClickEvent<KData> event) {
        super.onEventMainThread(event);
        switch (event.id) {
            case "textCard1":
                showToast("Handle click on textCard1!");
                break;
        }
    }

    @Override
    public void onFinishRender() {
        bind(this, customer);
        customer.name.set("Merlin");
    }
}
