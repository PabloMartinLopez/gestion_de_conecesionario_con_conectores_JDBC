package model;

import java.util.List;

public class Car {
    private String matricula;
    private String marca;
    private String modelo;
    private List<String> extras;
    private Float precio;
    private Propietario propietario;

    //*    Constructor con propietario
    public Car(String matricula, String marca, String modelo, List<String> extras, Float precio, Propietario propietario) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.extras = extras;
        this.precio = precio;
        this.propietario = propietario;
    }

    //*    Constructor sin propietario
    public Car(String matricula, String marca, String modelo, List<String> extras, Float precio) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.extras = extras;
        this.precio = precio;
        this.propietario = new Propietario();
    }


    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public List<String> getExtras() {
        return extras;
    }

    public String getExtrasString(){
        return extras.toString();
    }

    public void setExtras(List<String> extras) {
        this.extras = extras;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }
}
