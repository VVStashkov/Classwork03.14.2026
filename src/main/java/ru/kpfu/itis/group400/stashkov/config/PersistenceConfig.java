//package ru.kpfu.itis.group400.stashkov.config;
//
//import com.zaxxer.hikari.HikariDataSource;
//import jakarta.persistence.EntityManagerFactory;
//import org.springframework.context.EnvironmentAware;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.core.env.Environment;
//import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.hibernate5.HibernateTransactionManager;
//import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
//import org.springframework.orm.jpa.vendor.Database;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//import java.util.Properties;
//@Configuration
//@EnableTransactionManagement
//@PropertySource("classpath:persistence.properties")
//@EnableJpaRepositories("ru.kpfu.itis.group400.stashkov.repository")
//public class PersistenceConfig implements EnvironmentAware {
//
//    private Environment environment;
//
//    @Override
//    public void setEnvironment(Environment environment) {
//        this.environment = environment;
//    }
//
//    @Bean
//    public DataSource dataSource() {
//        HikariDataSource dataSource = new HikariDataSource();
//        dataSource.setJdbcUrl(environment.getProperty("spring.datasource.url"));
//        dataSource.setUsername(environment.getProperty("spring.datasource.username"));
//        dataSource.setPassword(environment.getProperty("spring.datasource.password"));
//        dataSource.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));
//        return dataSource;
//    }
//
//    @Bean
//    public HibernateJpaVendorAdapter jpaVendorAdapter() {
//        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
//        adapter.setDatabase(Database.valueOf(environment.getProperty("spring.database")));
//        adapter.setShowSql(true);
//        adapter.setGenerateDdl(true); // JPA генерирует схему
//        return adapter;
//    }
//
//    @Bean
//    public EntityManagerFactory entityManagerFactory() {
//        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//        factory.setJpaVendorAdapter(jpaVendorAdapter());
//        factory.setPackagesToScan("ru.kpfu.itis.group400.stashkov.model");
//        factory.setDataSource(dataSource());
//        factory.afterPropertiesSet();
//        return factory.getObject();
//    }
//
//    @Bean
//    @Primary   // основной менеджер транзакций – для JPA
//    public PlatformTransactionManager transactionManager() {
//        JpaTransactionManager tx = new JpaTransactionManager();
//        tx.setEntityManagerFactory(entityManagerFactory());
//        return tx;
//    }
//
//    // Hibernate native конфигурация
//    @Bean
//    public LocalSessionFactoryBean localSessionFactoryBean() {
//        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
//        sessionFactory.setDataSource(dataSource());
//        sessionFactory.setPackagesToScan("ru.kpfu.itis.group400.stashkov.model");
//
//        Properties hibernateProperties = new Properties();
//        // Используем свойства из persistence.properties
//        hibernateProperties.setProperty("hibernate.hbm2ddl.auto",
//                environment.getProperty("hibernate.hbm2ddl.auto", "none")); // none, чтобы не конфликтовать с JPA
//        // Можно добавить и другие свойства, например, диалект
//        hibernateProperties.setProperty("hibernate.dialect",
//                environment.getProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"));
//        sessionFactory.setHibernateProperties(hibernateProperties);
//
//        return sessionFactory;
//    }
//
//    // Менеджер транзакций для Hibernate
//    @Bean
//    public PlatformTransactionManager hibernateTransactionManager() {
//        HibernateTransactionManager tx = new HibernateTransactionManager();
//        // Используем уже созданный бин sessionFactory, а не вызываем метод заново
//        tx.setSessionFactory(localSessionFactoryBean().getObject());
//        return tx;
//    }
//}