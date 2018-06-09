package ar.com.almundo.callcenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ExecutorServiceConfig {

    //TODO: SE PUEDE SACAR A UNA PROPERTIE CON @VALUE
    private static final int MAX_CONCURRENT = 10;

    @Bean("customExecutorService")
    public ThreadPoolExecutor customExecutorService(){
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_CONCURRENT);
    }

}
