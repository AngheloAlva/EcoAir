package cl.inacap.ecoair;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Main_admin extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        ImageButton btnAddDevice = findViewById(R.id.addButton);
        ImageButton btnMap = findViewById(R.id.mapButton);
        ImageButton btnEditDevice = findViewById(R.id.listButton);

        btnAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main_admin.this, Add_device.class);
                startActivity(intent);
            }
        });

        btnEditDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main_admin.this, Device_list.class);
                startActivity(intent);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main_admin.this, All_devices_map.class);
                startActivity(intent);
            }
        });
    }
}