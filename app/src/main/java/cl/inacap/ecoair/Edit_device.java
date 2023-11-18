package cl.inacap.ecoair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Edit_device extends AppCompatActivity {
    private EditText editTextDeviceName, editTextPlazaName, editTextCo2, editTextNox;
    private DatabaseReference deviceRef;
    private Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_device);

        editTextDeviceName = findViewById(R.id.editTextDeviceName);
        editTextPlazaName = findViewById(R.id.editTextPlazaName);
        editTextCo2 = findViewById(R.id.editTextCo2);
        editTextNox = findViewById(R.id.editTextNox);

        String deviceId = getIntent().getStringExtra("DEVICE_ID");
        if (deviceId == null) {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        deviceRef = FirebaseDatabase.getInstance().getReference("devices").child(deviceId);
        loadDeviceData();

        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> saveDevice());
    }

    private void loadDeviceData() {
        deviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                device = dataSnapshot.getValue(Device.class);
                if (device != null) {
                    editTextDeviceName.setText(device.getDeviceName());
                    editTextPlazaName.setText(device.getPlazaName());
                    editTextCo2.setText(String.valueOf(device.getCo2()));
                    editTextNox.setText(String.valueOf(device.getNox()));
                } else {
                    Toast.makeText(Edit_device.this, "Dispositivo no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Edit_device.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDevice() {
        String deviceName = editTextDeviceName.getText().toString().trim();
        String plazaName = editTextPlazaName.getText().toString().trim();
        int co2 = Integer.parseInt(editTextCo2.getText().toString().trim());
        int nox = Integer.parseInt(editTextNox.getText().toString().trim());

        if (deviceName.isEmpty()) {
            editTextDeviceName.setError("Debe ingresar un nombre");
            editTextDeviceName.requestFocus();
            return;
        }

        if (plazaName.isEmpty()) {
            editTextPlazaName.setError("Debe ingresar un nombre");
            editTextPlazaName.requestFocus();
            return;
        }

        double latitude = device.getLatitude();
        double longitude = device.getLongitude();
        String imageUrl = device.getImageUrl();

        Device updateDevice = new Device(deviceName, plazaName, latitude, longitude, co2, nox, imageUrl);

        deviceRef.setValue(updateDevice)
                .addOnSuccessListener(aVoid -> Toast.makeText(Edit_device.this, "Dispositivo actualizado con éxito.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(Edit_device.this, "Error al actualizar el dispositivo.", Toast.LENGTH_SHORT).show());
    }
}