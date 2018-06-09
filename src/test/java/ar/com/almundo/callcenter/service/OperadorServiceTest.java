package ar.com.almundo.callcenter.service;

import ar.com.almundo.callcenter.model.Operador;
import ar.com.almundo.callcenter.repository.OperadorRepository;
import ar.com.almundo.callcenter.service.impl.OperadorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OperadorServiceTest {

    @Autowired
    private OperadorService operadorService;

    @MockBean
    private OperadorRepository operadorRepository;

    @Test
    public void testFindFreeAndLock(){
        Operador empleado = new Operador("Ale");
        empleado.setOcupado(false);

        Optional<Operador> empleadoOpt = Optional.of(empleado);
        when(operadorRepository.findFirstByOcupadoIsFalseOrderByLastModifiedDateAsc())
                .thenReturn(empleadoOpt);

        Optional<Operador> nowLocked = operadorService.findFreeAndLock();
        assertTrue(nowLocked.filter(x -> x.getOcupado()).isPresent());
    }

    @Test
    public void testFindFreeAndLockEmpty(){

        Optional<Operador> empleadoOpt = Optional.empty();
        when(operadorRepository.findFirstByOcupadoIsFalseOrderByLastModifiedDateAsc())
                .thenReturn(empleadoOpt);

        Optional<Operador> nowLocked = operadorService.findFreeAndLock();
        assertTrue(!nowLocked.isPresent());
    }
}
