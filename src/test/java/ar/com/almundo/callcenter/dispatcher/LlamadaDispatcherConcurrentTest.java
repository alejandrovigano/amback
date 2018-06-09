package ar.com.almundo.callcenter.dispatcher;

import ar.com.almundo.callcenter.exception.EmpleadoNoDisponibleException;
import ar.com.almundo.callcenter.model.*;
import ar.com.almundo.callcenter.repository.DirectorRepository;
import ar.com.almundo.callcenter.repository.LlamadaRepository;
import ar.com.almundo.callcenter.repository.OperadorRepository;
import ar.com.almundo.callcenter.repository.SupervisorRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

/**
 * Test aislado de llamada dispatcher, usado en el desarrollo
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class LlamadaDispatcherConcurrentTest {

    Logger LOGGER = LoggerFactory.getLogger(LlamadaDispatcherConcurrentTest.class);


    @Autowired
    private LlamadaDispatcher llamadaDispatcher;

    @Autowired
    private OperadorRepository operadorRepository;

    @Autowired
    private SupervisorRepository supervisorRepository;

    @Autowired
    private DirectorRepository directorRepository;

    @Autowired
    private LlamadaRepository llamadaRepository;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    @Qualifier("customExecutorService")
    private ThreadPoolExecutor executorService; //para ver las tareas pendientes


    @Before
    public void before(){
        llamadaRepository.deleteAll();

        operadorRepository.deleteAll();
        supervisorRepository.deleteAll();
        directorRepository.deleteAll();
    }

    @Test
    public void testOne() throws Exception {
        Operador empleado = operadorRepository.save(new Operador("operador1"));
        Llamada llamada = new Llamada();

        Future<Llamada> future = llamadaDispatcher.dispatchQueue(llamada);
        Optional<Llamada> updatedLlamada = Optional.ofNullable(future.get());

        assertEquals(updatedLlamada.map(Llamada::getEmpleado).map(Empleado::getNombre).get(),"operador1");
        assertFalse(updatedLlamada.map(Llamada::getEmpleado).map(Empleado::getOcupado).get());
    }



    @Test
    public void test2() throws Exception{
        //inserto 2 operadores
        insertNOperador(2);
        //hago dos llamadas
        List<Optional<Llamada>> list = this.llamar(2);

        //compruebo que las 2 llamadas se asignaron a los operadores
        assertTrue(nombreStream(list).anyMatch(x-> x.equals("operador0")));
        assertTrue(nombreStream(list).anyMatch(x-> x.equals("operador1")));
    }

    @Test
    public void test10() throws Exception{
        //inserto 2 operadores
        insertNOperador(6);
        insertNSupervisor(3);
        insertNDirector(1);
        this.llamar(10);
    }

    @Test
    public void test30() throws Exception{
        insertNOperador(10);
        insertNSupervisor(10);
        insertNDirector(10);
        this.llamar(30);
    }

    @Test
    public void test11() throws Exception{
        insertNOperador(7);
        insertNSupervisor(3);
        insertNDirector(1);
        this.llamar(11);
    }

    @Test(expected = ExecutionException.class)
    public void testEmpleadoNoDisponible() throws Exception{
        this.llamar(100);
    }

    //-----HELPER METHODS

    private Stream<String> nombreStream(List<Optional<Llamada>> list) {
        return list.stream().map(Optional::get).map(Llamada::getEmpleado).map(Empleado::getNombre);
    }

    public <E extends Empleado> void insertNOperador(int n) {
        insertNEmpleado(n,(i) -> operadorRepository.save(new Operador("operador" + i)));
    }

    public <E extends Empleado> void insertNSupervisor(int n) {
        insertNEmpleado(n,(i) -> supervisorRepository.save(new Supervisor("supervisor" + i)));
    }

    public <E extends Empleado> void insertNDirector(int n) {
        insertNEmpleado(n,(i) -> directorRepository.save(new Director("operador" + i)));
    }

    public <E extends Empleado> void insertNEmpleado(int n, Consumer<Integer> consumer){
        IntStream.range(0, n).forEach(nbr -> consumer.accept(nbr));
    }

    public List<Optional<Llamada>> llamar(int n) throws Exception {
        List<Future<Llamada>> futures = new ArrayList<>();
        IntStream.range(0, n).forEach(nbr -> {
            Llamada llamada = new Llamada();
            futures.add(llamadaDispatcher.dispatchQueue(llamada));
        });

        List<Optional<Llamada>> llamadas = new ArrayList<>();
        //espero todos
        for(Future<Llamada> future: futures){
            llamadas.add(Optional.ofNullable(future.get()));
            LOGGER.info(String.format("Tareas pendientes: %d", executorService.getQueue().size()));
        }

        //por cada llamada se creó un operador, tienn que existir todos

        return llamadas;
    }
}
