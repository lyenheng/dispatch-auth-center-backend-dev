package com.kedacom.dispatch.ac.web;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.*;

@Configuration
@EnableFeignClients
@EnableScheduling
public class DpAuthCenterWebConfiguration {
    @Value("${auth.center.pool.corePoolSize:4}")
    private int corePoolSize;
    @Value("${auth.center.pool.maximumPoolSize:6}")
    private int maximumPoolSize;
    @Value("${auth.center.pool.queueSize:1000}")
    private int queueSize;
    @Bean
    public ExecutorService authCenterThreadPool() {
        ThreadFactory nameFactory = new ThreadFactoryBuilder().setNameFormat("authCenter-pool-%d").build();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue(queueSize), nameFactory, new ThreadPoolExecutor.DiscardPolicy());
        return threadPoolExecutor;
    }

    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setTargetClass(SecurityContextHolder.class);
        methodInvokingFactoryBean.setTargetMethod("setStrategyName");
        methodInvokingFactoryBean.setArguments(new String[]{SecurityContextHolder.MODE_INHERITABLETHREADLOCAL});
        return methodInvokingFactoryBean;
    }

}
