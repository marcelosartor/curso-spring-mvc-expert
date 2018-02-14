package com.algaworks.brewer.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;

@Profile("prod")
@Configuration
@PropertySource(value={"file://${HOME}/.brewer-s3.properties"},ignoreResourceNotFound=true)
public class S3Config {
	
	@Autowired
	private Environment env;


	@Bean 
	public AmazonS3 amanzonS3() {

		String accessKey = env.getProperty("AWS_ACCESS_KEY_ID");

		String secretKey = env.getProperty("AWS_SECRET_ACCESS_KEY");
		
		System.out.println("accessKey : "+accessKey);
		System.out.println("secretKey : "+secretKey);
		
		AWSCredentials credenciais = new BasicAWSCredentials(accessKey, secretKey);
		
		AmazonS3 amazonS3 = new AmazonS3Client(credenciais, new ClientConfiguration());
		// S3 reeal
		//Region regiao = Region.getRegion(Regions.US_EAST_1);
		//amazonS3.setRegion(regiao);
		
		// s3ninja
		amazonS3.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());
		amazonS3.setEndpoint("http://localhost:9444/s3");
		return amazonS3;
		 
	}
}
