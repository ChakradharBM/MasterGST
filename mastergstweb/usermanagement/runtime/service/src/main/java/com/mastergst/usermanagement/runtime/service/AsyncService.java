package com.mastergst.usermanagement.runtime.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

@Service
public class AsyncService {

	private ExecutorService executorService;
	
	public AsyncService(){
		executorService = Executors.newFixedThreadPool(10);
	}
	
	
	public void execute(Runnable runnable){
		executorService.execute(runnable);
	}
	
	
}
