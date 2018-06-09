package ar.com.almundo.callcenter.repository;

import ar.com.almundo.callcenter.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.LockModeType;
import java.util.Optional;

@NoRepositoryBean
public interface EmpleadoRepository <E extends Empleado> extends JpaRepository<E, Long> {

    /**
     * Obtengo el primero no ocupado, ordenado por fecha de modificacion
     * @return
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<E> findFirstByOcupadoIsFalseOrderByLastModifiedDateAsc();

}
