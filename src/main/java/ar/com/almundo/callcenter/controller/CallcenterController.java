package ar.com.almundo.callcenter.controller;

import ar.com.almundo.callcenter.model.Llamada;
import ar.com.almundo.callcenter.service.CallcenterService;
import ar.com.almundo.callcenter.service.LlamadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class CallcenterController {

    @Autowired
    private LlamadaService llamadaService;

    @Autowired
    private CallcenterService callcenterService;

    @GetMapping("/llamada/{idLlamada}")
    public Optional<Llamada> getLlamada(@PathVariable("idLlamada") Long idLlamada) {
        return llamadaService.getOne(idLlamada);
    }

    @GetMapping("/llamada")
    public Page<Llamada> findAll(@PageableDefault(size = 20) Pageable pageable,
                                 @RequestParam(name = "activa", required = false, defaultValue = "true") Boolean active) {
        return llamadaService.findAll(active, pageable);
    }

    @GetMapping("/llamada/iniciar")
    public Llamada iniciarLlamada() throws Throwable {
        return callcenterService.iniciarLlamada();
    }
}
