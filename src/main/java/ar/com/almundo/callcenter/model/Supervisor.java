package ar.com.almundo.callcenter.model;

import javax.persistence.Entity;

@Entity
public class Supervisor extends Empleado {

    public Supervisor(String nombre) {
        super(nombre);
    }

    public Supervisor() {
    }
}
