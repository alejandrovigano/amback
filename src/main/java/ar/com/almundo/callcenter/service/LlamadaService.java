package ar.com.almundo.callcenter.service;

import ar.com.almundo.callcenter.model.Empleado;
import ar.com.almundo.callcenter.model.Llamada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface LlamadaService {

    /**
     * Persiste la llamada
     * @param llamada
     * @return
     */
    Llamada save(Llamada llamada);

    /**
     * OBtiene todas las llamadas paginadas
     *
     * @param active
     * @param pageable
     * @return
     */
    Page<Llamada> findAll(Boolean active, Pageable pageable);

    /**
     * Finaliza la llamada
     * @param llamada
     * @return
     */
    Llamada finalizarLlamada(Llamada llamada);

    /**
     * Asigna un empleado ya reservado a la llamada, y cambia la llamada a activa=true
     * @param llamada
     * @param empleado
     * @return
     */
    Llamada asignarEmpleado(Llamada llamada, Empleado empleado);

    /**
     * Procesa una llamada ya asignada a un empleado, tiempo de espera de 0 a 6 segundos
     * @param llamada
     */
    void realizarLlamada(Llamada llamada);

    /**
     * Obtiene una llamada
     * @param idLlamada
     * @return
     */
    Optional<Llamada> getOne(Long idLlamada);
}
