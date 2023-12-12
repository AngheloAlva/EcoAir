package cl.inacap.ecoair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

public class RealDeviceActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private DatabaseReference deviceRef;
    private ImageView ivDeviceImage;
    private TextView tvCo2, tvAirQuality, tvDeviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_device);

        ivDeviceImage = findViewById(R.id.ivDeviceImage);
        tvCo2 = findViewById(R.id.tvCO2);
        tvAirQuality = findViewById(R.id.tvAirQuality);
        tvDeviceName = findViewById(R.id.tvDeviceName);
        Button backButton = findViewById(R.id.backButton);
        Button buttonEditDevice = findViewById(R.id.buttonEditDevice);

        buttonEditDevice.setOnClickListener(v -> {
            Intent intent = new Intent(RealDeviceActivity.this, EditRealDeviceActivity.class);
            startActivity(intent);
        });
        backButton.setOnClickListener(v -> onBackPressed());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null){
            mapFragment.getMapAsync(this);
        }

        deviceRef = FirebaseDatabase.getInstance().getReference("sensorData");
        loadDeviceDetails();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RealDeviceActivity.this, Main_admin.class);
        startActivity(intent);
        finish();
    }

    private void loadDeviceDetails() {
        deviceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                String deviceName = dataSnapshot.child("name").getValue(String.class);
                String co2 = dataSnapshot.child("CO2").getValue(String.class);
                Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                Double longitude = dataSnapshot.child("longitude").getValue(Double.class);

                Glide.with(RealDeviceActivity.this).load(imageUrl).into(ivDeviceImage);
                tvCo2.setText("Nivel de Gas: " + co2 + " ppm");
                tvDeviceName.setText(deviceName);

                double co2Value = Double.parseDouble(co2);
                if (co2Value >= 125.00 && co2Value <= 135.00) {
                    tvAirQuality.setText("Calidad del aire: Normal");
                    tvAirQuality.setTextColor(getResources().getColor(R.color.colorGreenLight));
                } else {
                    tvAirQuality.setText("Calidad del aire: Peligroso");
                    tvAirQuality.setTextColor(getResources().getColor(R.color.colorRed));
                }

                if (latitude != null && longitude != null && mMap != null) {
                    updateMapLocation(latitude, longitude);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RealDeviceActivity.this, "Error al cargar datos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loadDeviceDetails();
    }

    private void updateMapLocation(double lat, double lon) {
        Log.d("RealDeviceActivity", "Updating map location to: lat=" + lat + ", lon=" + lon);
        LatLng deviceLocation = new LatLng(lat, lon);
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(deviceLocation).title("Ubicación del Dispositivo"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deviceLocation, 15));
        }
    }
}
