package ar.com.almundo.callcenter.service.impl;

import ar.com.almundo.callcenter.dispatcher.LlamadaDispatcher;
import ar.com.almundo.callcenter.dispatcher.impl.AbstractEmpleadoDispatcher;
import ar.com.almundo.callcenter.exception.EmpleadoNoDisponibleException;
import ar.com.almundo.callcenter.model.Llamada;
import ar.com.almundo.callcenter.service.CallcenterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class CallcenterServiceImpl implements CallcenterService {

    Logger LOGGER = LoggerFactory.getLogger(CallcenterServiceImpl.class);


    @Autowired
    private LlamadaDispatcher llamadaDispatcher;

    /**
     * Inicia la llamada - Reintenta 2 veces con un delay de 5 segundos
     *
     * @return
     */
    @Override
    @Retryable(
            value = {EmpleadoNoDisponibleException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 5000))
    public Llamada iniciarLlamada() throws Throwable {
        Llamada llamada = new Llamada();
        try {
            return llamadaDispatcher.dispatchQueue(llamada).get();
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Recover
    public Llamada iniciarLlamadaRecover(EmpleadoNoDisponibleException ex) throws Throwable{
        LOGGER.info("Fallaron todos los intentos, se cancela la llamada");
        throw ex;
    }

}
