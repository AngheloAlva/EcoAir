package cl.inacap.ecoair;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditRealDeviceActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private DatabaseReference deviceRef;
    private ImageView ivDeviceImage;
    private TextInputLayout editTextDeviceName;
    private LatLng selectedLocation;
    private Uri imageUri;
    private static final int GALLERY_REQUEST_CODE = 1;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_real_device);

        ivDeviceImage = findViewById(R.id.ivDeviceImage);
        editTextDeviceName = findViewById(R.id.editTextDeviceName);
        Button saveDeviceButton = findViewById(R.id.saveDeviceButton);
        Button uploadImageButton = findViewById(R.id.btnUploadImage);
        Button backButton = findViewById(R.id.backButton);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        uploadImageButton.setOnClickListener(v -> openGallery());
        saveDeviceButton.setOnClickListener(v -> saveDevice());
        backButton.setOnClickListener(v -> finish());

        deviceRef = FirebaseDatabase.getInstance().getReference("sensorData");
        loadDeviceDetails();
    }


    private void loadDeviceDetails() {
        deviceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                Double longitude = dataSnapshot.child("longitude").getValue(Double.class);

                if (name != null) {
                    editTextDeviceName.getEditText().setText(name);
                }
                if (!isFinishing() && !isDestroyed()) {
                    Glide.with(EditRealDeviceActivity.this).load(imageUrl).into(ivDeviceImage);
                }

                if (latitude != null && longitude != null) {
                    selectedLocation = new LatLng(latitude, longitude);
                    updateMapLocation(selectedLocation);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditRealDeviceActivity.this, "Error al cargar datos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMapLocation(LatLng location) {
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(location).title("Ubicación del dispositivo"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        }
    }


    private void saveDevice() {
        String newName = editTextDeviceName.getEditText().getText().toString().trim();
        if (newName.isEmpty()) {
            editTextDeviceName.setError("El nombre es obligatorio");
            return;
        }

        if (selectedLocation == null) {
            Toast.makeText(this, "Seleccione una ubicación en el mapa", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            // Si hay una nueva imagen, primero la subimos a Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("device_images");
            StorageReference imageRef = storageRef.child(System.currentTimeMillis() + ".jpg");

            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Una vez que tenemos la URL, actualizamos Firebase Database
                    updateDeviceInFirebase(newName, selectedLocation, uri.toString());
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(EditRealDeviceActivity.this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        } else {
            // Si no hay una nueva imagen, solo actualizamos el nombre y la ubicación
            updateDeviceInFirebase(newName, selectedLocation, null);
        }
    }

    private void updateDeviceInFirebase(String name, LatLng location, String imageUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("latitude", location.latitude);
        updates.put("longitude", location.longitude);

        if (imageUrl != null) {
            updates.put("imageUrl", imageUrl);
        }

        deviceRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditRealDeviceActivity.this, "Dispositivo actualizado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditRealDeviceActivity.this, RealDeviceActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(EditRealDeviceActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show());
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                selectedLocation = latLng;
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación del dispositivo"));
            }
        });

        loadDeviceDetails();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ivDeviceImage.setImageURI(imageUri);
        }
    }

}
