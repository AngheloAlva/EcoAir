package cl.inacap.ecoair;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

public class Main_user extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        LinearLayout btnDeviceList = findViewById(R.id.listButtonContainer);
        LinearLayout btnMap = findViewById(R.id.mapButtonContainer);
        Button logoutBtn = findViewById(R.id.logoutButton);

        btnDeviceList.setOnClickListener(v -> {
            Intent intent = new Intent(Main_user.this, Device_list.class);
            startActivity(intent);
        });

        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(Main_user.this, All_devices_map.class);
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Main_user.this, Login.class);
            startActivity(intent);
            finish();
        });
    }
}