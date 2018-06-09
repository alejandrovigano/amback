package ar.com.almundo.callcenter.dispatcher;

import ar.com.almundo.callcenter.model.Llamada;

import java.util.concurrent.Future;

public interface LlamadaDispatcher {
    Future<Llamada> dispatchQueue(Llamada llamada);
}
