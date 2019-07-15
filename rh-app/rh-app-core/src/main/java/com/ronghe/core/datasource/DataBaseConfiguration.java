package com.ronghe.core.datasource;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
/**
 * 数据源注解类
 * @author zk
 *
 */
@Configuration
public class DataBaseConfiguration {
	/**
     * server
     * @return
     */
    @Bean(name = "serverDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.druid.master.server")
    public DataSource serverDataSource(){
        return DataSourceBuilder.create().build();
    }
    /**
     * client
     * @return
     */
    @Bean(name="clientDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.master.client")
    public DataSource clientDataSource() {
        return DataSourceBuilder.create().build();
    }
}
