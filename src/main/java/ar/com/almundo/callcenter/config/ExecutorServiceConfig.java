package ar.com.almundo.callcenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ExecutorServiceConfig {

    private int maxConcurrent = 10;

    @Bean("customExecutorService")
    public ThreadPoolExecutor customExecutorService(){
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(maxConcurrent);
    }

}
