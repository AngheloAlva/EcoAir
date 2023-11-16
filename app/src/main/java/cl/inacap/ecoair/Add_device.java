package cl.inacap.ecoair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Add_device extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LatLng selectedLocation;
    private Uri imageUri;
    private static final int GALLERY_REQUEST_CODE = 1;
    private ImageView ivDeviceImage;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        Button saveDeviceButton = findViewById(R.id.saveDeviceButton);
        saveDeviceButton.setOnClickListener(v -> saveDevice());

        ivDeviceImage = findViewById(R.id.ivDeviceImage);
        Button uploadImageButton = findViewById(R.id.btnUploadImage);

        uploadImageButton.setOnClickListener(v -> openGallery());
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

        if (deviceName.isEmpty() || plazaName.isEmpty() || co2.isEmpty() || nox.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int co2Value, noxValue;

        try {
            co2Value = Integer.parseInt(co2); // Integer.parseInt("123") -> 123 (int)
            noxValue = Integer.parseInt(nox);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Los valores de CO2 y NOx deben ser números", Toast.LENGTH_SHORT).show();
            return;
        }


        if (selectedLocation != null && imageUrl != null) {
            double latitude = selectedLocation.latitude;
            double longitude = selectedLocation.longitude;

            Device device = new Device(deviceName, plazaName, latitude, longitude, co2Value, noxValue, imageUrl);

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

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ivDeviceImage.setImageURI(imageUri);
            uploadImageToFirebaseStorage();
        }
    }

    private void uploadImageToFirebaseStorage() {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("device_images");
            StorageReference imageRef = storageRef.child(System.currentTimeMillis() + ".jpg");

            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUrl = uri.toString();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(Add_device.this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        }
    }
}