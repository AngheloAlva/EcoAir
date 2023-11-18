package cl.inacap.ecoair;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> implements Filterable {
    private final List<Device> devicesList; // Lista de dispositivos para mostrar
    private final List<Device> devicesListFull; // Lista de dispositivos completa para el filtro
    private final boolean isAdmin;

    // Constructor del adaptador
    public DeviceAdapter(List<Device> devicesList, boolean isAdmin) {
        this.devicesList = devicesList;
        this.isAdmin = isAdmin;
        devicesListFull = new ArrayList<>(devicesList);
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device device = devicesList.get(position);
        holder.tvDeviceName.setText(device.getDeviceName());
        Glide.with(holder.itemView.getContext())
                .load(device.getImageUrl())
                .into(holder.ivDeviceImage);

        String airQualityState = calculateAirQualityState(device.getCo2(), device.getNox());
        holder.tvAirQuality.setText("Calidad del aire: " + airQualityState);

        if (isAdmin) {
            holder.editImageView.setVisibility(View.VISIBLE);
            holder.deleteImageView.setVisibility(View.VISIBLE);
        }

        if ("Bueno".equals(airQualityState)) {
            holder.tvAirQuality.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.green));
        } else if ("Regular".equals(airQualityState)) {
            holder.tvAirQuality.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.yellow));
        } else {
            holder.tvAirQuality.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), Device_detail.class);
            intent.putExtra("DEVICE_ID", device.getFirebaseKey());
            holder.itemView.getContext().startActivity(intent);
        });

        holder.editImageView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), Edit_device.class);
            intent.putExtra("DEVICE_ID", device.getFirebaseKey());
            holder.itemView.getContext().startActivity(intent);
        });

        holder.deleteImageView.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Eliminar dispositivo")
                    .setMessage("¿Está seguro que desea eliminar el dispositivo " + device.getDeviceName() + "?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        DatabaseReference devicesRef = FirebaseDatabase.getInstance().getReference("devices");
                        devicesRef.child(device.getFirebaseKey()).removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(holder.itemView.getContext(), "Dispositivo eliminado", Toast.LENGTH_LONG).show();
                                    devicesList.remove(position); // Actualiza tu lista local
                                    notifyItemRemoved(position); // Notifica al adaptador del cambio
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(holder.itemView.getContext(), "Fallo al eliminar el dispositivo", Toast.LENGTH_LONG).show();
                                });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return devicesList.size();
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

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeviceName;
        ImageView ivDeviceImage;
        TextView tvAirQuality;
        ImageView editImageView;
        ImageView deleteImageView;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tvDeviceName);
            ivDeviceImage = itemView.findViewById(R.id.ivDeviceImage);
            tvAirQuality = itemView.findViewById(R.id.tvAirQuality);
            editImageView = itemView.findViewById(R.id.editImageView);
            deleteImageView = itemView.findViewById(R.id.deleteImageView);
        }
    }

    @Override
    public Filter getFilter() {
        return deviceFilter;
    }

    private final Filter deviceFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Device> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(devicesListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Device device : devicesListFull) {
                    if (device.getDeviceName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(device);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            devicesList.clear();
            devicesList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public void updateDevicesListFull(List<Device> newDevicesListFull) {
        devicesListFull.clear();
        devicesListFull.addAll(newDevicesListFull);
    }
}
