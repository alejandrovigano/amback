package ar.com.almundo.callcenter.repository;

import ar.com.almundo.callcenter.model.Llamada;

public interface LlamadaCustomRepository {

    /**
     * Guarda si las llamadas no superan los
     * @param llamada
     * @param maxConcurrent
     * @return
     */
    Llamada saveLimit(Llamada llamada, int maxConcurrent);

}
