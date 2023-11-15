package cl.inacap.ecoair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        // Configura otros TextViews con información del dispositivo
    }

    @Override
    public int getItemCount() {
        return devicesList.size();
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeviceName;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tvDeviceName);
            // Inicializa otros TextViews aquí
        }
    }
}
