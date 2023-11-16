package cl.inacap.ecoair;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
    private List<Device> devicesList; // Lista de dispositivos para mostrar

    // Constructor del adaptador
    public DeviceAdapter(List<Device> devicesList) {
        this.devicesList = devicesList;
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

        if ("Bueno".equals(airQualityState)) {
            holder.tvAirQuality.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.green));
        } else if ("Regular".equals(airQualityState)) {
            holder.tvAirQuality.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.yellow));
        } else {
            holder.tvAirQuality.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), Device_detail.class);
            intent.putExtra("DEVICE_ID", device.getDeviceID());
            holder.itemView.getContext().startActivity(intent);
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

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tvDeviceName);
            ivDeviceImage = itemView.findViewById(R.id.ivDeviceImage);
            tvAirQuality = itemView.findViewById(R.id.tvAirQuality);
        }
    }
}
