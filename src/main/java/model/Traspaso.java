package model;

public class Traspaso {
    String id;
    String matricula_coche;
    int id_vendedor;
    int id_comprador;
    int monto_economico;

    public Traspaso(String matricula_coche, int id_vendedor, int id_comprador, int monto_economico) {
        this.matricula_coche = matricula_coche;
        this.id_vendedor = id_vendedor;
        this.id_comprador = id_comprador;
        this.monto_economico = monto_economico;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatricula_coche() {
        return matricula_coche;
    }

    public void setMatricula_coche(String matricula_coche) {
        this.matricula_coche = matricula_coche;
    }

    public int getId_vendedor() {
        return id_vendedor;
    }

    public void setId_vendedor(int id_vendedor) {
        this.id_vendedor = id_vendedor;
    }

    public int getId_comprador() {
        return id_comprador;
    }

    public void setId_comprador(int id_comprador) {
        this.id_comprador = id_comprador;
    }

    public int getMonto_economico() {
        return monto_economico;
    }

    public void setMonto_economico(int monto_economico) {
        this.monto_economico = monto_economico;
    }
}
