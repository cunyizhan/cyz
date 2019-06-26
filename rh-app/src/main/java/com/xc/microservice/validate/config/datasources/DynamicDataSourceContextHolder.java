package com.xc.microservice.validate.config.datasources;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicDataSourceContextHolder {

		private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceContextHolder.class);
		   
		private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<String>();
	
		/**
		 * 数据源集合
		 */
		public static List<Object> dataSourceKeys = new ArrayList<Object>();
		/**
		 * 配置数据源方法
		 * @param key
		 */
		public static void setDataSourceKey(String key) {
		        CONTEXT_HOLDER.set(key);
		}
	
		/**
		 * server数据库
		 */
		public static void useServerDataSource() {
		        CONTEXT_HOLDER.set(DataSourceKey.serverDataSource.name());
		}
		/**
		 * client 数据源
		 */
		 public static void useClientDataSource() {
		        CONTEXT_HOLDER.set(DataSourceKey.clientDataSource.name());
		 }
		
	    /**
	     * 获取数据源
	     *
	     * @return data source key
	     */
	    public static String getDataSourceKey() {
	        return CONTEXT_HOLDER.get();
	    }
	    /**
	     * To set DataSource as default
	     */
	    public static void clearDataSourceKey() {
	        CONTEXT_HOLDER.remove();
	    }
	    /**
	     * Check if give DataSource is in current DataSource list
	     *
	     * @param key the key
	     * @return boolean boolean
	     */
	    public static boolean containDataSourceKey(String key) {
	        return dataSourceKeys.contains(key);
	    }
}
