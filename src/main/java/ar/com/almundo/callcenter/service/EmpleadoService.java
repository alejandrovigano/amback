package ar.com.almundo.callcenter.service;

import ar.com.almundo.callcenter.model.Empleado;

import java.util.Optional;

public interface EmpleadoService<E extends Empleado> {

    /**
     * Busca empleado libre y lo pone ocupado
     * @return
     */
    Optional<E> findFreeAndLock();

}
