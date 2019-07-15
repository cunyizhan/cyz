package com.ronghe.core.datasource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
/**
 * 数据库动态切换
 * @author zk
 *
 */
@Aspect
@Component
public class DynamicDataSourceAspect{
	private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);
	private final String[] QUERY_PREFIX = {"server","client"};
	/**
     * dao 切面
     */
    @Pointcut("execution( * com.ronghe.core.dao.*.*(..))")
    public void daoAspect() {
    }

    /**
     * 切换数据源
     * @param point the point
     */
    @Before("daoAspect()")
    public void switchDataSource(JoinPoint point) {
        String isQueryMethod = isQueryMethod(point.getSignature().getName());
        if (isQueryMethod.equals("server")) {
            DynamicDataSourceContextHolder.useServerDataSource();
        }else{
        	DynamicDataSourceContextHolder.useClientDataSource();
        }
    }

	private String isQueryMethod(String name) {
		 for (String prefix : QUERY_PREFIX) {
	           if (name.startsWith(prefix)) {
	                return prefix;
	           }
	        }
	     return "base";
	}
}
