package cl.inacap.ecoair;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

public class Main_user extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        LinearLayout btnDeviceList = findViewById(R.id.listButtonContainer);
        LinearLayout btnMap = findViewById(R.id.mapButtonContainer);

        btnDeviceList.setOnClickListener(v -> {
            Intent intent = new Intent(Main_user.this, Device_list.class);
            startActivity(intent);
        });

        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(Main_user.this, All_devices_map.class);
            startActivity(intent);
        });
    }
}