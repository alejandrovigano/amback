package ar.com.almundo.callcenter.dispatcher;

import ar.com.almundo.callcenter.dispatcher.impl.AbstractEmpleadoDispatcher;
import ar.com.almundo.callcenter.dispatcher.impl.LlamadaDispatcherImpl;
import ar.com.almundo.callcenter.model.Director;
import ar.com.almundo.callcenter.model.Llamada;
import ar.com.almundo.callcenter.model.Operador;
import ar.com.almundo.callcenter.model.Supervisor;
import ar.com.almundo.callcenter.repository.LlamadaRepository;
import ar.com.almundo.callcenter.service.EmpleadoService;
import ar.com.almundo.callcenter.service.LlamadaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test aislado de llamada dispatcher, usado en el desarrollo
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class LlamadaDispatcherChainTest {

    @MockBean
    private LlamadaRepository llamadaRepository;

    @MockBean
    private EmpleadoService<Operador> operadorEmpleadoService;
    @MockBean
    private EmpleadoService<Supervisor> supervisorEmpleadoService;
    @MockBean
    private EmpleadoService<Director> directorEmpleadoService;

    @Autowired
    private LlamadaDispatcherImpl llamadaDispatcher;

    @Test
    public void testOperadorLibre() throws ExecutionException, InterruptedException {

        Optional empleado = Optional.of(new Operador("Ale"));
        when(operadorEmpleadoService.findFreeAndLock()).thenReturn(empleado);

        Llamada llamada = new Llamada();
        when(llamadaRepository.save(any(Llamada.class))).thenReturn(llamada);

        Optional<Llamada> response = Optional.ofNullable(llamadaDispatcher.dispatchQueue(llamada).get());

        assertTrue(response
                .map(x -> x.getEmpleado())
                .filter( x -> x instanceof Operador) //hay operador
                .isPresent());
    }

    @Test
    public void testOperadorOcupadoSupervisorLibre() throws ExecutionException, InterruptedException {

        Optional empleado = Optional.of(new Supervisor("Ale"));
        when(operadorEmpleadoService.findFreeAndLock()).thenReturn(Optional.empty());
        when(supervisorEmpleadoService.findFreeAndLock()).thenReturn(empleado);

        Llamada llamada = new Llamada();
        when(llamadaRepository.save(any(Llamada.class))).thenReturn(llamada);

        Optional<Llamada> response = Optional.ofNullable(llamadaDispatcher.dispatchQueue(llamada).get());

        assertTrue(response
                .map(x -> x.getEmpleado())
                .filter( x -> x instanceof Supervisor) //hay operador
                .isPresent());
    }

    @Test
    public void testOperadorOcupadoSupervisorOcupadoDirectorLibre() throws ExecutionException, InterruptedException {

        Optional empleado = Optional.of(new Director("Ale"));
        when(operadorEmpleadoService.findFreeAndLock()).thenReturn(Optional.empty());
        when(supervisorEmpleadoService.findFreeAndLock()).thenReturn(Optional.empty());
        when(directorEmpleadoService.findFreeAndLock()).thenReturn(empleado);

        Llamada llamada = new Llamada();
        when(llamadaRepository.save(any(Llamada.class))).thenReturn(llamada);

        Optional<Llamada> response = Optional.ofNullable(llamadaDispatcher.dispatchQueue(llamada).get());

        assertTrue(response
                .map(x -> x.getEmpleado())
                .filter( x -> x instanceof Director) //hay operador
                .isPresent());
    }

    @Test(expected = ExecutionException.class)
    public void testTodosOcupados() throws ExecutionException, InterruptedException {

        when(operadorEmpleadoService.findFreeAndLock()).thenReturn(Optional.empty());
        when(supervisorEmpleadoService.findFreeAndLock()).thenReturn(Optional.empty());
        when(directorEmpleadoService.findFreeAndLock()).thenReturn(Optional.empty());

        Llamada llamada = new Llamada();
        when(llamadaRepository.save(any(Llamada.class))).thenReturn(llamada);

        Llamada response = llamadaDispatcher.dispatchQueue(llamada).get();
    }
}
