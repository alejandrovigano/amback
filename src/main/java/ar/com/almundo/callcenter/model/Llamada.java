package ar.com.almundo.callcenter.model;

import javax.persistence.*;

@Entity
public class Llamada extends AbstractEntity{

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

    @Column(nullable = false)
    private boolean activa = false;

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    @Override
    public String toString() {
        return "Llamada{" +
                "empleado=" + empleado +
                '}';
    }
}
