package cl.inacap.ecoair;

public class Plaza extends Comuna{

    private int id;
    private String nombre;

    public Plaza(int id, String nombre) {
        super(id, nombre);
        this.id = id;
        this.nombre = nombre;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
