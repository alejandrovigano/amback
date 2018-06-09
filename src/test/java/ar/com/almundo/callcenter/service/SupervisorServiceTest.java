package ar.com.almundo.callcenter.service;

import ar.com.almundo.callcenter.model.Operador;
import ar.com.almundo.callcenter.model.Supervisor;
import ar.com.almundo.callcenter.repository.OperadorRepository;
import ar.com.almundo.callcenter.repository.SupervisorRepository;
import ar.com.almundo.callcenter.service.impl.OperadorService;
import ar.com.almundo.callcenter.service.impl.SupervisorService;
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
public class SupervisorServiceTest {

    @Autowired
    private SupervisorService supervisorService;

    @MockBean
    private SupervisorRepository supervisorRepository;

    @Test
    public void testFindFreeAndLock(){
        Supervisor empleado = new Supervisor("Ale");
        empleado.setOcupado(false);

        Optional<Supervisor> empleadoOpt = Optional.of(empleado);
        when(supervisorRepository.findFirstByOcupadoIsFalseOrderByLastModifiedDateAsc())
                .thenReturn(empleadoOpt);

        Optional<Supervisor> nowLocked = supervisorService.findFreeAndLock();
        assertTrue(nowLocked.filter(x -> x.getOcupado()).isPresent());
    }

    @Test
    public void testFindFreeAndLockEmpty(){

        Optional<Supervisor> empleadoOpt = Optional.empty();
        when(supervisorRepository.findFirstByOcupadoIsFalseOrderByLastModifiedDateAsc())
                .thenReturn(empleadoOpt);

        Optional<Supervisor> nowLocked = supervisorService.findFreeAndLock();
        assertTrue(!nowLocked.isPresent());
    }
}
