package cl.inacap.ecoair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class All_devices_map extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_devices_map);

        Button backButton = findViewById(R.id.backButton);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.allDevicesMapFragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("devices");
        backButton.setOnClickListener(v -> finish());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loadDeviceMarkers();
    }

    private void loadDeviceMarkers() {
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mMap.clear();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for (DataSnapshot deviceSnapshot : snapshot.getChildren()) {
                    Device device = deviceSnapshot.getValue(Device.class);
                    if (device != null && device.getLatitude() != 0 && device.getLongitude() != 0) {
                        LatLng deviceLocation = new LatLng(device.getLatitude(), device.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(deviceLocation).title(device.getDeviceName()));
                        builder.include(deviceLocation);
                    }
                }

                if(snapshot.getChildrenCount() > 0) {
                    LatLngBounds bounds = builder.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(All_devices_map.this, "Error al cargar los dispositivos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}