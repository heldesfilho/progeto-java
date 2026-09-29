package model;

public class Usuario {

    private int id;
    private String usuario;
    private String senhaHash;
    private String senhaSalt;
    private Cargo cargo;

    public Usuario() {
    }

    public Usuario(String usuario, String senhaHash, String senhaSalt, Cargo cargo) {
        this.usuario = usuario;
        this.senhaHash = senhaHash;
        this.senhaSalt = senhaSalt;
        this.cargo = cargo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public String getSenhaSalt() { return senhaSalt; }
    public void setSenhaSalt(String senhaSalt) { this.senhaSalt = senhaSalt; }

    public Cargo getCargo() { return cargo; }
    public void setCargo(Cargo cargo) { this.cargo = cargo; }
}