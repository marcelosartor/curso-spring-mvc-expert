package com.algaworks.brewer.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.Cervejas;

@Configuration
@EnableJpaRepositories(basePackageClasses = Cervejas.class, enableDefaultTransactions = false)
@ComponentScan(basePackageClasses = Cervejas.class)
@EnableTransactionManagement
public class JPAConfig {
		
	@Profile("local")
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl( "jdbc:postgresql://localhost:5432/brewer?useSSL=false");
		dataSource.setUsername( "usuario" );
		dataSource.setPassword( "senha" );
		return dataSource;	
	}

	
	@Profile("prod")
	@Bean
	public DataSource dataSourceProd() throws URISyntaxException {
		
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

	    String username = dbUri.getUserInfo().split(":")[0];
	    String password = dbUri.getUserInfo().split(":")[1];
	    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath()
	    +"?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

	    BasicDataSource dataSource = new BasicDataSource();
	    dataSource.setUrl(dbUrl);
	    dataSource.setUsername(username);
	    dataSource.setPassword(password);
	    dataSource.setInitialSize(5);
	    
	    return dataSource;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter(){
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.POSTGRESQL);
		adapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
		adapter.setGenerateDdl(false);
		adapter.setShowSql(false);
		
				
		adapter.getJpaPropertyMap().put("factory", "org.apache.naming.factory.BeanFactory");
		adapter.getJpaPropertyMap().put("type", "com.mchange.v2.c3p0.ComboPooledDataSource");
		adapter.getJpaPropertyMap().put("minPoolSize", "5");
		adapter.getJpaPropertyMap().put("maxPoolSize", "5");
		
		
		return adapter;
	}
	
	@Bean
	public EntityManagerFactory entityManagerFactory(DataSource dataSource,JpaVendorAdapter jpaVendorAdapter){

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		
		factory.setDataSource(dataSource);
		factory.setJpaVendorAdapter(jpaVendorAdapter);
		factory.setPackagesToScan(Cerveja.class.getPackage().getName());
		factory.setMappingResources("sql/consultas-nativas.xml");
		factory.afterPropertiesSet();
		
		return factory.getObject();
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory){
		
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
		
	}
			
}


