package cl.inacap.ecoair;

public class User {

    private String name;
    private String email;
    private String role;

    public User(String nombre, String email, String role) {
        this.name = nombre;
        this.email = email;
        this.role = role;
    }

    public User() {
    }

    public String getNombre() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setNombre(String nombre) {
        this.name = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
