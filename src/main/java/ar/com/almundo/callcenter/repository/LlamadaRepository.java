package ar.com.almundo.callcenter.repository;

import ar.com.almundo.callcenter.model.Llamada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LlamadaRepository extends JpaRepository<Llamada,Long> {

    Page<Llamada> findByActiva(Boolean active, Pageable pageable);
}
