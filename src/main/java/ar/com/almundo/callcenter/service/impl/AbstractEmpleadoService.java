package ar.com.almundo.callcenter.service.impl;

import ar.com.almundo.callcenter.model.Empleado;
import ar.com.almundo.callcenter.repository.EmpleadoRepository;
import ar.com.almundo.callcenter.service.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public abstract class AbstractEmpleadoService<E extends Empleado> implements EmpleadoService<E> {

    @Autowired
    private EmpleadoRepository<E> repository;

    /**
     * Commit al final de la transaccion, rollback es automatico para todas las excepciones
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<E> findFreeAndLock(){
        Optional<E> empleado = repository.findFirstByOcupadoIsFalseOrderByLastModifiedDateAsc();
        empleado.ifPresent(e -> e.setOcupado(true));
        return empleado;
    }

}
