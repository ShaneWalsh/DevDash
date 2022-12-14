package dev.dash.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
 
@Configuration
@EnableTransactionManagement
public class HibernateConfiguration {
	@Value("${db.driver}")
	private String DRIVER;
 
	@Value("${db.password}")
	private String PASSWORD;
 
	@Value("${db.url}")
	private String URL;
 
	@Value("${db.username}")
	private String USERNAME;
 
	@Value("${hibernate.dialect}")
	private String DIALECT;
 
	@Value("${hibernate.show_sql}")
	private String SHOW_SQL;
 
	@Value("${hibernate.hbm2ddl.auto}")
	private String HBM2DDL_AUTO;
 
	@Value("${entitymanager.packagesToScan}")
	private String PACKAGES_TO_SCAN;
 
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(DRIVER);
		dataSource.setUrl(URL);
		dataSource.setUsername(USERNAME);
		dataSource.setPassword(PASSWORD);
		return dataSource;
    }
    
    @Bean
     public JpaTransactionManager transactionManager() {
         JpaTransactionManager transactionManager = new JpaTransactionManager();
         transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
         return transactionManager;
     }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

         LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
         entityManagerFactoryBean.setJpaVendorAdapter(vendorAdaptor());
         entityManagerFactoryBean.setDataSource(dataSource());
         entityManagerFactoryBean.setPackagesToScan(PACKAGES_TO_SCAN);
         //entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);          
         entityManagerFactoryBean.setJpaProperties(jpaHibernateProperties());

         return entityManagerFactoryBean;
     }

     private HibernateJpaVendorAdapter vendorAdaptor() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        return vendorAdapter;
    }

     private Properties jpaHibernateProperties() {
        Properties hibernateProperties = new Properties();
		hibernateProperties.put("hibernate.dialect", DIALECT);
		hibernateProperties.put("hibernate.show_sql", SHOW_SQL);
		hibernateProperties.put("hibernate.hbm2ddl.auto", HBM2DDL_AUTO);
        return hibernateProperties;       
    }

 
    // @Bean
    // public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    //    LocalContainerEntityManagerFactoryBean em 
    //      = new LocalContainerEntityManagerFactoryBean();
    //    em.setDataSource(dataSource());
    //    em.setPackagesToScan(new String[] { "dev.dash.*" });
  
    //    JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    //    em.setJpaVendorAdapter(vendorAdapter);
    //    em.setJpaProperties(additionalProperties());
  
    //    return em;
    // }
 
	// @Bean
	// public HibernateTransactionManager transactionManager() {
	// 	HibernateTransactionManager transactionManager = new HibernateTransactionManager();
	// 	transactionManager.setSessionFactory(sessionFactory().getObject());
	// 	return transactionManager;
	// }	
}
 

/*
@Configuration
@PropertySource(value = { "classpath:database/jdbc.properties" })
@EnableTransactionManagement
public class PersistenceConfig {

    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_MAX_FETCH_DEPTH = "hibernate.max_fetch_depth";
    private static final String PROPERTY_NAME_HIBERNATE_JDBC_FETCH_SIZE = "hibernate.jdbc.fetch_size";
    private static final String PROPERTY_NAME_HIBERNATE_JDBC_BATCH_SIZE = "hibernate.jdbc.batch_size";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    private static final String[] ENTITYMANAGER_PACKAGES_TO_SCAN = {"a.b.c.entities", "a.b.c.converters"};

    @Autowired
    private Environment env;

     @Bean(destroyMethod = "close")
     public DataSource dataSource() {
         BasicDataSource dataSource = new BasicDataSource();
         dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
         dataSource.setUrl(env.getProperty("jdbc.url"));
         dataSource.setUsername(env.getProperty("jdbc.username"));
         dataSource.setPassword(env.getProperty("jdbc.password"));
         return dataSource;
     }

     @Bean
     public JpaTransactionManager jpaTransactionManager() {
         JpaTransactionManager transactionManager = new JpaTransactionManager();
         transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
         return transactionManager;
     }

    private HibernateJpaVendorAdapter vendorAdaptor() {
         HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
         vendorAdapter.setShowSql(true);
         return vendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {

         LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
         entityManagerFactoryBean.setJpaVendorAdapter(vendorAdaptor());
         entityManagerFactoryBean.setDataSource(dataSource());
         entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
         entityManagerFactoryBean.setPackagesToScan(ENTITYMANAGER_PACKAGES_TO_SCAN);             
         entityManagerFactoryBean.setJpaProperties(jpaHibernateProperties());

         return entityManagerFactoryBean;
     }

     private Properties jpaHibernateProperties() {

         Properties properties = new Properties();

         properties.put(PROPERTY_NAME_HIBERNATE_MAX_FETCH_DEPTH, env.getProperty(PROPERTY_NAME_HIBERNATE_MAX_FETCH_DEPTH));
         properties.put(PROPERTY_NAME_HIBERNATE_JDBC_FETCH_SIZE, env.getProperty(PROPERTY_NAME_HIBERNATE_JDBC_FETCH_SIZE));
         properties.put(PROPERTY_NAME_HIBERNATE_JDBC_BATCH_SIZE, env.getProperty(PROPERTY_NAME_HIBERNATE_JDBC_BATCH_SIZE));
         properties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, env.getProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));

         properties.put(AvailableSettings.SCHEMA_GEN_DATABASE_ACTION, "none");
         properties.put(AvailableSettings.USE_CLASS_ENHANCER, "false");      
         return properties;       
     }

}
*/