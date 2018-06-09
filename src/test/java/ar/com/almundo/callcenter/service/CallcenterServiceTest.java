package ar.com.almundo.callcenter.service;

import ar.com.almundo.callcenter.dispatcher.LlamadaDispatcher;
import ar.com.almundo.callcenter.dispatcher.impl.LlamadaDispatcherImpl;
import ar.com.almundo.callcenter.exception.EmpleadoNoDisponibleException;
import ar.com.almundo.callcenter.model.Llamada;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CallcenterServiceTest {

    @Autowired
    private CallcenterService callcenterService;

    @MockBean
    private LlamadaDispatcher llamadaDispatcher;

    @Test
    public void testRetry() throws Throwable {
        when(llamadaDispatcher.dispatchQueue(any(Llamada.class))).thenThrow(new RuntimeException(new EmpleadoNoDisponibleException()));

        try{
            callcenterService.iniciarLlamada();
        }catch (Exception e){
            //es esperado
        }

        verify(llamadaDispatcher, times(3)).dispatchQueue(any(Llamada.class));
    }

}
