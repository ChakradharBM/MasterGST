/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.core.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

@Service
public class MasterGstExecutorService {
	
	private ExecutorService  executorService;
	
	public MasterGstExecutorService(){
		executorService = Executors.newFixedThreadPool(25);
	}
	
	public void execute(Runnable job){
		executorService.execute(job);
	}

}
