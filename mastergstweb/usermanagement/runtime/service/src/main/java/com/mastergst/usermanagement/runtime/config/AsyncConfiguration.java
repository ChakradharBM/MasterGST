package com.mastergst.usermanagement.runtime.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
@Configuration
public class AsyncConfiguration {
	
	@Bean(name = "reconcileTaskExecutor")
	public Executor executor1() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(4);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("MasterGSTReconcileExecutor::");
		executor.initialize();
		return executor;
	 }

}
