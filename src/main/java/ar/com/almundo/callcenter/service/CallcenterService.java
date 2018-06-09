package ar.com.almundo.callcenter.service;

import ar.com.almundo.callcenter.model.Llamada;

public interface CallcenterService {
    Llamada iniciarLlamada() throws Throwable;
}
