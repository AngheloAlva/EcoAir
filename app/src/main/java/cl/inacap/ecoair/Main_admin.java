package cl.inacap.ecoair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

public class Main_admin extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        CardView btnAddDevice = findViewById(R.id.addDeviceButtonCard);
        CardView btnMap = findViewById(R.id.mapButtonCard);
        CardView btnEditDevice = findViewById(R.id.deviceListButtonCard);
        CardView realDevice = findViewById(R.id.realDeviceButtonCard);
        Button logoutBtn = findViewById(R.id.logoutButton);

        boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);

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
                intent.putExtra("isAdmin", isAdmin);
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

        realDevice.setOnClickListener(v -> {
            Intent intent = new Intent(Main_admin.this, RealDeviceActivity.class);
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Main_admin.this, Login.class);
            startActivity(intent);
            finish();
        });
    }
}