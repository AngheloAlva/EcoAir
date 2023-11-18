package cl.inacap.ecoair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class Device_list extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DeviceAdapter adapter;
    private List<Device> devicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        recyclerView = findViewById(R.id.rvDevices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        devicesList = new ArrayList<>();

        fetchDevicesFromFirebase();

        adapter = new DeviceAdapter(devicesList);
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
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Device_list.this, "Error al obtener dispositivos: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}