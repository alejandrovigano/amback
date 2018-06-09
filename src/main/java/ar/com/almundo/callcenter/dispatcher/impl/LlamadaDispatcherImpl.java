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
 * ChainHandler de las llamadas
 * Manejo de concurrencia a nivel de DB
 */
@Component
public class LlamadaDispatcherImpl implements LlamadaDispatcher {

    private static Logger LOGGER = LoggerFactory.getLogger(LlamadaDispatcherImpl.class);

    @Autowired
    @Qualifier("customExecutorService")
    private ThreadPoolExecutor executorService;

    @Autowired
    private AbstractEmpleadoChainHandler<Operador> operadorHandler;
    @Autowired
    private AbstractEmpleadoChainHandler<Supervisor> supervisorHandler;
    @Autowired
    private AbstractEmpleadoChainHandler<Director> directorHandler;

    @Autowired
    private LlamadaService llamadaService;

    @PostConstruct
    public void postConstruct() {
        operadorHandler.setNextEmpleadoHandler(supervisorHandler);
        supervisorHandler.setNextEmpleadoHandler(directorHandler);
    }

    /**
     * Se la envio al operadorHandler
     *
     * @return Optional.llamada generada- empty si no se pudo asignar
     */
    private Llamada dispatchCall(Llamada llamada) {
        Optional<? extends Empleado> empleadoOpt = operadorHandler.dispatch(llamada);
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
            LOGGER.info(String.format("Ejecutando tarea - Encoladas: %d", executorService.getQueue().size()));
            return this.dispatchCall(llamada);
        });
        LOGGER.info(String.format("Agregada tarea - Encoladas: %d", executorService.getQueue().size()));
        return future;
    }

}
