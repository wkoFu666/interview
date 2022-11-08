package com.wko.dothings.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;


/**
 * 数据源配置类
 */
@Configuration
@MapperScan(basePackages = "com.wko.dothings.wallet.dao.local",sqlSessionFactoryRef = "localSqlSessionRef")
@MapperScan(basePackages = "com.wko.dothings.wallet.dao.remote",sqlSessionFactoryRef = "remoteSqlSessionRef")
public class DataSourceConfig {

    @Autowired
    private Environment environment;

    @Bean
    public DataSource localDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(environment.getProperty("spring.datasource.db1.url"));
        druidDataSource.setUsername(environment.getProperty("spring.datasource.db1.username"));
        druidDataSource.setPassword(environment.getProperty("spring.datasource.db1.password"));
        druidDataSource.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));
        return druidDataSource;
    }

    @Bean
    public DataSource remoteDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(environment.getProperty("spring.datasource.db2.url"));
        druidDataSource.setUsername(environment.getProperty("spring.datasource.db2.username"));
        druidDataSource.setPassword(environment.getProperty("spring.datasource.db2.password"));
        druidDataSource.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));
        return druidDataSource;
    }
    @Bean(name = "localSqlSessionRef")
    public SqlSessionFactory localSqlSessionRef() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/local/*.xml"));
        sqlSessionFactoryBean.setDataSource(localDataSource());
        return sqlSessionFactoryBean.getObject();
    }
    @Bean(name = "remoteSqlSessionRef")
    public SqlSessionFactory remoteSqlSessionRef() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/remote/*.xml"));
        sqlSessionFactoryBean.setDataSource(remoteDataSource());
        return sqlSessionFactoryBean.getObject();
    }
}
