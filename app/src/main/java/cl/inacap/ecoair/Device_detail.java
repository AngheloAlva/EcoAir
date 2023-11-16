package cl.inacap.ecoair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Device_detail extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        deviceID = getIntent().getStringExtra("DEVICE_ID");
        if (deviceID == null) {
            Toast.makeText(this, "No se encontro el ID del dispositivo", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadDeviceDetails();
    }

    private void loadDeviceDetails() {
        DatabaseReference deviceRef = FirebaseDatabase.getInstance().getReference("devices").child(deviceID);

        deviceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Device device = snapshot.getValue(Device.class);
                if (device != null) {
                    updateUI(device);
                } else {
                    Toast.makeText(Device_detail.this, "No se encontro el dispositivo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Device_detail.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady (@NonNull GoogleMap googleMap) {
        map = googleMap;
    }

    private void updateUI(Device device) {
        ImageView ivDeviceImage = findViewById(R.id.ivDeviceImage);
        TextView tvDeviceName = findViewById(R.id.tvDeviceName);
        TextView tvPlazaName = findViewById(R.id.tvPlazaName);
        TextView tvCo2 = findViewById(R.id.tvCO2Level);
        TextView tvNox = findViewById(R.id.tvNOxLevel);
        TextView tvAirQuality = findViewById(R.id.tvAirQuality);
        String airQualityState = calculateAirQualityState(device.getCo2(), device.getNox());
        tvAirQuality.setText("Calidad del aire: " + airQualityState);

        if ("Bueno".equals(airQualityState)) {
            tvAirQuality.setTextColor(getResources().getColor(R.color.green));
        } else if ("Regular".equals(airQualityState)) {
            tvAirQuality.setTextColor(getResources().getColor(R.color.yellow));
        } else {
            tvAirQuality.setTextColor(getResources().getColor(R.color.red));
        }

        Glide.with(this).load(device.getImageUrl()).into(ivDeviceImage);
        tvDeviceName.setText(device.getDeviceName());
        tvPlazaName.setText(device.getPlazaName());
        tvCo2.setText("Nivel de CO2: " + device.getCo2() + " ppm");
        tvNox.setText("Nivel de NOx: " + device.getNox() + " ppm");

        if (map != null) {
            LatLng deviceLocation = new LatLng(device.getLatitude(), device.getLongitude());
            map.addMarker(new MarkerOptions().position(deviceLocation).title("Ubicación del dispositivo"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(deviceLocation, 15));
        }
    }

    private String calculateAirQualityState(int co2, int nox) {
        if (co2 > 2000 || nox > 100) {
            return "Mala";
        } else if (co2 > 1000 || nox > 50) {
            return "Regular";
        } else {
            return "Buena";
        }
    }
}