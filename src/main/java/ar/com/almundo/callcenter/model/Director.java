package ar.com.almundo.callcenter.model;

import javax.persistence.Entity;

@Entity
public class Director extends Empleado{

    public Director(String nombre) {
        super(nombre);
    }

    public Director() {
    }

}
