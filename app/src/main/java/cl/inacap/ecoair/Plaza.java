package cl.inacap.ecoair;

public class Plaza {
    private int id;
    private String nombre;
    private String comuna;

    public Plaza(int id, String nombre, String comuna) {
        this.comuna = comuna;
        this.id = id;
        this.nombre = nombre;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
