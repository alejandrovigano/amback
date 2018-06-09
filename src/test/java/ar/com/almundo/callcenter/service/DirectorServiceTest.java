package ar.com.almundo.callcenter.service;

import ar.com.almundo.callcenter.model.Director;
import ar.com.almundo.callcenter.model.Operador;
import ar.com.almundo.callcenter.repository.DirectorRepository;
import ar.com.almundo.callcenter.repository.OperadorRepository;
import ar.com.almundo.callcenter.service.impl.DirectorService;
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
public class DirectorServiceTest {

    @Autowired
    private DirectorService directorService;

    @MockBean
    private DirectorRepository directorRepository;

    @Test
    public void testFindFreeAndLock(){
        Director empleado = new Director("Ale");
        empleado.setOcupado(false);

        Optional<Director> empleadoOpt = Optional.of(empleado);
        when(directorRepository.findFirstByOcupadoIsFalseOrderByLastModifiedDateAsc())
                .thenReturn(empleadoOpt);

        Optional<Director> nowLocked = directorService.findFreeAndLock();
        assertTrue(nowLocked.filter(x -> x.getOcupado()).isPresent());
    }

    @Test
    public void testFindFreeAndLockEmpty(){

        Optional<Director> empleadoOpt = Optional.empty();
        when(directorRepository.findFirstByOcupadoIsFalseOrderByLastModifiedDateAsc())
                .thenReturn(empleadoOpt);

        Optional<Director> nowLocked = directorService.findFreeAndLock();
        assertTrue(!nowLocked.isPresent());
    }
}
