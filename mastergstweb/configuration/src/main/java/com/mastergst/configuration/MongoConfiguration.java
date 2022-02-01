/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

@Configuration
@EnableMongoRepositories(basePackages = "com.mastergst")
@PropertySource("classpath:mongodb.properties")
@EnableMongoAuditing
public class MongoConfiguration extends AbstractMongoConfiguration {

	@Resource
	private Environment env;
	
	@Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
	    return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
	}

	@Override
	protected String getDatabaseName() {
		return env.getRequiredProperty("mongo.db.name");
	}

	@Override
	public Mongo mongo() throws Exception {
		final MongoClientOptions options = MongoClientOptions.builder()
				.connectionsPerHost(100)
		        .threadsAllowedToBlockForConnectionMultiplier(1)
		        .connectTimeout(5000)
		        .maxWaitTime(5000)
		        .maxConnectionLifeTime(3600000)
		        .socketKeepAlive(true)
		        .socketTimeout(5000)
		        .heartbeatFrequency(600000)
		        .minHeartbeatFrequency(5000)
		        .build();
		ServerAddress serverAddress = new ServerAddress(env.getRequiredProperty("mongo.host.name"),
				Integer.parseInt(env.getRequiredProperty("mongo.host.port")));
		MongoClient mongoClient = new MongoClient(serverAddress, options);
		return mongoClient;
	}

	@Override
	protected String getMappingBasePackage() {
		return "com.mastergst";
	}

}
