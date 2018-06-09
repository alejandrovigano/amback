package ar.com.almundo.callcenter.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Empleado extends AbstractEntity {

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "ocupado", nullable = false)
    private boolean ocupado;

    public Empleado(String nombre) {
        this.nombre = nombre;
    }

    public Empleado() {

    }

    public Boolean getOcupado() {
        return ocupado;
    }

    public void setOcupado(Boolean ocupado) {
        this.ocupado = ocupado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "nombre='" + nombre + '\'' +
                ", ocupado=" + ocupado +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Empleado empleado = (Empleado) o;
        return ocupado == empleado.ocupado &&
                Objects.equals(nombre, empleado.nombre);
    }

    @Override
    public int hashCode() {

        return Objects.hash(nombre, ocupado);
    }
}
