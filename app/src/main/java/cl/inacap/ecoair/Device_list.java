package cl.inacap.ecoair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Device_list extends AppCompatActivity {
    private DeviceAdapter adapter;
    private List<Device> devicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        RecyclerView recyclerView = findViewById(R.id.rvDevices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        devicesList = new ArrayList<>();
        EditText searchEditText = findViewById(R.id.searchEditText);
        Button backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fetchDevicesFromFirebase();

        adapter = new DeviceAdapter(devicesList, isAdmin);
        recyclerView.setAdapter(adapter);
    }

    private void fetchDevicesFromFirebase() {
        DatabaseReference devicesRef = FirebaseDatabase.getInstance().getReference("devices");
        devicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                devicesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Device device = snapshot.getValue(Device.class);
                    if (device != null) {
                        device.setFirebaseKey(snapshot.getKey());
                        devicesList.add(device);
                    }
                }
                if (adapter != null) {
                    adapter.updateDevicesListFull(new ArrayList<>(devicesList));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Device_list.this, "Error al obtener dispositivos: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}