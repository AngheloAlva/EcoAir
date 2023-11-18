package cl.inacap.ecoair;

public class Device {
    private String firebaseKey;
    private String deviceName;
    private String plazaName;
    private double latitude;
    private double longitude;
    private int co2;
    private int nox;
    private String imageUrl;

    public Device(String deviceName, String plazaName, double latitude, double longitude, int co2, int nox, String imageUrl) {
        this.deviceName = deviceName;
        this.plazaName = plazaName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.co2 = co2;
        this.nox = nox;
        this.imageUrl = imageUrl;
    }

    public Device() {}

    public String getDeviceID() {
        return deviceName + plazaName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getPlazaName() {
        return plazaName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getCo2() {
        return co2;
    }

    public int getNox() {
        return nox;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }
}
