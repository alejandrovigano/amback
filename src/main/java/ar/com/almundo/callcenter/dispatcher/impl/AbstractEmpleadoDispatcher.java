package ar.com.almundo.callcenter.dispatcher.impl;

import ar.com.almundo.callcenter.dispatcher.Dispatcher;
import ar.com.almundo.callcenter.model.Empleado;
import ar.com.almundo.callcenter.model.Llamada;
import ar.com.almundo.callcenter.service.EmpleadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * Dispatch de empleado, adaptado al patron chain of responsability
 * @param <E>
 */
public abstract class AbstractEmpleadoDispatcher<E extends Empleado> implements Dispatcher<Empleado> {

    Logger LOGGER = LoggerFactory.getLogger(AbstractEmpleadoDispatcher.class);

    @Autowired
    private EmpleadoService<E> service;

    private AbstractEmpleadoDispatcher nextEmpleadoDispatcher;

    /**
     * Obtengo el empleado ya reservado, y lo asigno a la llamada
     * @param llamada
     * @return
     */
    @Override
    public Optional<? extends Empleado> dispatch(Llamada llamada) {
        Optional<? extends Empleado> empleado = service.findFreeAndLock();

        //si no encontre ninguno...
        if(!empleado.isPresent()){
            empleado = getWhenNotFound(llamada);
        }

        return empleado;
    }

    /**
     * Que hacer cuando no se encuentra. Puede devolver optional empty si no hay siguiente cadena.
     * @param llamada
     * @return
     */
    protected Optional<? extends Empleado> getWhenNotFound(Llamada llamada) {
        LOGGER.info("No se encuentra empleado con el dispatcher - " + getClass().getSimpleName());
        return Optional.ofNullable(getNextEmpleadoDispatcher())
                .flatMap(ed -> ed.dispatch(llamada));
    }

    private AbstractEmpleadoDispatcher getNextEmpleadoDispatcher() {
        return nextEmpleadoDispatcher;
    }

    public void setNextEmpleadoDispatcher(AbstractEmpleadoDispatcher nextEmpleadoDispatcher) {
        this.nextEmpleadoDispatcher = nextEmpleadoDispatcher;
    }
}
