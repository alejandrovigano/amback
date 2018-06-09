package ar.com.almundo.callcenter.dispatcher;

import ar.com.almundo.callcenter.model.AbstractEntity;
import ar.com.almundo.callcenter.model.Llamada;

import java.util.Optional;

public interface Dispatcher<E extends AbstractEntity> {
    Optional<? extends E> dispatch(Llamada llamada);
}
