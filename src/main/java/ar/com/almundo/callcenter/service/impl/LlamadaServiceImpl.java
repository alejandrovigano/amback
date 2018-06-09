package ar.com.almundo.callcenter.service.impl;

import ar.com.almundo.callcenter.dispatcher.impl.LlamadaDispatcherImpl;
import ar.com.almundo.callcenter.model.Empleado;
import ar.com.almundo.callcenter.model.Llamada;
import ar.com.almundo.callcenter.repository.LlamadaRepository;
import ar.com.almundo.callcenter.service.LlamadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class LlamadaServiceImpl implements LlamadaService {

    @Autowired
    private LlamadaRepository llamadaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Llamada save(Llamada llamada) {
        return llamadaRepository.save(llamada);
    }

    @Override
    public Llamada asignarEmpleado(Llamada llamada, Empleado empleado) {
        llamada.setEmpleado(empleado);
        llamada.setActiva(true);

        //NOTIFICAR A ALGUIEN QUE LA LLAMADA SE ASIGNO?

        return save(llamada);
    }

    @Override
    public void realizarLlamada(Llamada llamada) {
        try {
            //ACA ESPERA HASTA QUE TERMINE LA LLAMADA.....
            int seconds = 5+ new Random().nextInt(6);
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Llamada> getOne(Long idLlamada) {
        return llamadaRepository.findById(idLlamada);
    }

    @Override
    public Page<Llamada> findAll(Boolean active, Pageable pageable) {
        return llamadaRepository.findByActiva(active,pageable);
    }

    /**
     * Fin de la llamada, libera al empleado, doy de baja la llamada
     *
     * @param llamada
     * @return
     */
    @Override
    public Llamada finalizarLlamada(Llamada llamada) {
        llamada.setActiva(false);
        llamada.getEmpleado().setOcupado(false);
        return save(llamada);
    }
}
