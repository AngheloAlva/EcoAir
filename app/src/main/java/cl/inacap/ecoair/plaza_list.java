package cl.inacap.ecoair;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class plaza_list extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plaza_list);

        LinearLayout listContainer = findViewById(R.id.plazaListContainer);

        String[] plazaNames = new String[]{"Plaza 1", "Plaza 2", "Plaza 3"};
        String[] plazaStates = new String[]{"Malo", "Bueno", "Normal"};

        for (int i = 0; i < plazaNames.length; i++) {
            View plazaItem = LayoutInflater.from(this).inflate(R.layout.plaza_item, listContainer, false);

            TextView textViewPlazaName = plazaItem.findViewById(R.id.textViewPlazaName);
            TextView textViewPlazaState = plazaItem.findViewById(R.id.textViewPlazaState);

            textViewPlazaName.setText(plazaNames[i]);
            textViewPlazaState.setText("Estado: " +plazaStates[i]);

            listContainer.addView(plazaItem);
        }
    }
}