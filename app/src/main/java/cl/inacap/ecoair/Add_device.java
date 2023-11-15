package cl.inacap.ecoair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Add_device extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LatLng selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync((OnMapReadyCallback) this);
    }

    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng initialLocation = new LatLng(-33.4372, -70.6506); // Santiago
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 10));

        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            selectedLocation = latLng;
            mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación seleccionada"));
        });
    }

    private void saveDevice() {
        String deviceName = ((EditText) findViewById(R.id.deviceNameEditText)).getText().toString().trim();
        String plazaName = ((EditText) findViewById(R.id.plazaNameEditText)).getText().toString().trim();
        String co2 = ((EditText) findViewById(R.id.co2EditText)).getText().toString().trim();
        String nox = ((EditText) findViewById(R.id.noxEditText)).getText().toString().trim();

        int co2Value = Integer.parseInt(co2); // Integer.parseInt("123") -> 123 (int)
        int noxValue = Integer.parseInt(nox);

        if (selectedLocation != null) {
            double latitude = selectedLocation.latitude;
            double longitude = selectedLocation.longitude;

            Device device = new Device(deviceName, plazaName, latitude, longitude, co2Value, noxValue);

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            String firebaseDeviceID = mDatabase.child("devices").push().getKey();

            if (firebaseDeviceID != null) {
                mDatabase.child("devices").child(firebaseDeviceID).setValue(device)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(Add_device.this, "Dispositivo agregado", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Add_device.this, "Error al agregar dispositivo", Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }
}