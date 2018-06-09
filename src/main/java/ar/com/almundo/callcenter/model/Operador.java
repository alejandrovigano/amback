package ar.com.almundo.callcenter.model;

import javax.persistence.Entity;

@Entity
public class Operador extends Empleado {

    public Operador(String nombre) {
        super(nombre);
    }

    public Operador() {
    }

}
