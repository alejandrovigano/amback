# Backend callcenter almundo

- Correr mvn clean install
- Levantar la app con docker-compose up
- Probar la creacion de la llamada con */jmeter/Callcenter Plan.jmx*
- docker-compose.yml tiene incluida la db (mysql)
- la data inicial se encuentra en src/resources/import.sql, se corre automaticamente al iniciar la aplicacion

#### Puntos a resolver

1. *"Existe un call center donde hay 3 tipos de empleados: operador, supervisor
 y director."*
 
Se crea la entidad Empleado, con 3 subclases Operador, Supervisor, Director. Unica tabla, InheritanceType.SINGLE_TABLE, discriminador por defecto **dtype**
____ 

2. *"El proceso de la atención de una llamada telefónica en primera
  instancia debe ser atendida por un operador, si no hay ninguno libre debe
  ser atendida por un supervisor, y de no haber tampoco supervisores libres
  debe ser atendida por un director."*
  
Se crea ***OperadorChainHandlerImpl, SupervisorChainHandlerImpl y DirectorChainHandlerImpl*** como manejadores del patron *Chain of responsibility*. Todos obtienen el correspondiente empleado disponible y lo reservan (ocupado=true)
 
Se usa un lockeo pesimista para obtener atomicidad en la obtencion + actualizacion del empleado. 


*EmpleadoRepository*
```java  
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<E> findFirstByOcupadoIsFalseOrderByLastModifiedDateAsc();

```
*AbstractEmpleadoService*
```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public synchronized Optional<E> findFreeAndLock(){
    Optional<E> empleado = repository.findFirstByOcupadoIsFalseOrderByLastModifiedDateAsc();
    empleado.ifPresent(e -> e.setOcupado(true));
    return empleado;
}
```
____
3. *"Debe existir una clase Dispatcher encargada de manejar las
   llamadas, y debe contener el método dispatchCall para que las
   asigne a los empleados disponibles."*
    
 - Se crea LlamadaDispatcherImpl que encadena a los manejadores anteriores y se encarga del dispatch  
 
```java
private Llamada dispatchCall(Llamada llamada)
```
____
4. *"El método dispatchCall puede invocarse por varios hilos al mismo
     tiempo.""*
     
El bloqueo está controlado a nivel de transaccion con la base de datos, la clase LlamadaDispatcherImpl es thread-safe
____
5. *"La clase Dispatcher debe tener la capacidad de poder procesar 10
   llamadas al mismo tiempo"*
   
Al ser thread-safe puede aceptar 10 llamadas sin problemas, pero para limitar las llamadas a un maximo de 10 simultaneas se delega la ejecucion del dispatch a un ***ExecutorService** (La configuracion del mismo se encuentra en ExecutorServiceConfig.java)
```java
    public Future<Llamada> dispatchQueue(Llamada llamada) {
        Future<Llamada> future = executorService.submit(() -> {
            LOGGER.info(String.format("Ejecutando tarea - Encoladas: %d", executorService.getQueue().size()));
            return this.dispatch(llamada);
        });
        LOGGER.info(String.format("Agregada tarea - Encoladas: %d", executorService.getQueue().size()));
        return future;
    }
```
____
6. *"Cada llamada puede durar un tiempo aleatorio entre 5 y 10
     segundos."*
     
Para simular una espera de 5 y 10 segundos se duerme el thread actual. Ver ***LlamadaServiceImpl#realizarLlamada***
____
7. *"Debe tener un test unitario donde lleguen 10 llamadas.""*

Ver ***LlamadaDispatcherConcurrentTest***. Los test corren sobre una base en memoria
____
8. *"Dar alguna solución sobre qué pasa con una llamada cuando no hay
     ningún empleado libre."*
     
Ver ***CallcenterServiceTest***. Se implementó un reintento de 3 veces con 5 segundos de espera entre reintento (spring-retry)
____
9. *"Dar alguna solución sobre qué pasa con una llamada cuando entran
     más de 10 llamadas concurrentes."*
     
Se encolan hasta que la cola del ExecutorService se libere nuevamente.

#### Endpoints

Se agregaron algunos endpoint de utilidad.

```
Iniciar una nueva llamada - Para estresar con jmeter
GET /llamada/iniciar
```
```
Obtener una llamada en particular
GET /llamada/{idLlamada} 
```
```
Obtener todas las llamadas activas
GET /llamada?[activa=false|true] + Pageable
```
