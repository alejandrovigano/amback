package ar.com.almundo.callcenter.dispatcher.impl;

import ar.com.almundo.callcenter.dispatcher.LlamadaDispatcher;
import ar.com.almundo.callcenter.exception.EmpleadoNoDisponibleException;
import ar.com.almundo.callcenter.model.*;
import ar.com.almundo.callcenter.service.LlamadaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Dispatcher de las llamadas
 * Manejo de concurrencia a nivel de DB
 */
@Component
public class LlamadaDispatcherImpl implements LlamadaDispatcher {

    private static Logger LOGGER = LoggerFactory.getLogger(LlamadaDispatcherImpl.class);

    @Autowired
    @Qualifier("customExecutorService")
    private ThreadPoolExecutor executorService;

    @Autowired
    private AbstractEmpleadoDispatcher<Operador> operadorDispatcher;
    @Autowired
    private AbstractEmpleadoDispatcher<Supervisor> supervisorDispatcher;
    @Autowired
    private AbstractEmpleadoDispatcher<Director> directorDispatcher;

    @Autowired
    private LlamadaService llamadaService;

    @PostConstruct
    public void postConstruct() {
        operadorDispatcher.setNextEmpleadoDispatcher(supervisorDispatcher);
        supervisorDispatcher.setNextEmpleadoDispatcher(directorDispatcher);
    }

    /**
     * Se la envio al operadorDispatcher
     *
     * @return Optional.llamada generada- empty si no se pudo asignar
     */
    private Llamada dispatch(Llamada llamada) {
        Optional<? extends Empleado> empleadoOpt = operadorDispatcher.dispatch(llamada);
        //guardo la llamada
        llamadaService.save(llamada);
        LOGGER.info("inicio llamada: " + llamada.getId());

        Empleado empleado = empleadoOpt.orElseThrow(EmpleadoNoDisponibleException::new);
        //asigno
        llamadaService.asignarEmpleado(llamada, empleado);
        //llamo
        llamadaService.realizarLlamada(llamada);
        //termino
        llamadaService.finalizarLlamada(llamada);

        LOGGER.info("fin llamada: " + llamada.getId());
        return llamada;
    }

    /**
     * Envia el dispatch de llamada a una cola de threads, si excede el limite disponible de hilos se encola
     *
     */
    public Future<Llamada> dispatchQueue(Llamada llamada) {
        Future<Llamada> future = executorService.submit(() -> {
            LOGGER.info(String.format("Tareas encoladas x: %d", executorService.getQueue().size()));
            return this.dispatch(llamada);
        });
        LOGGER.info(String.format("Tareas encoladas: %d", executorService.getQueue().size()));
        return future;
    }

}
