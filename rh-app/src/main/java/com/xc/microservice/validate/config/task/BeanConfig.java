package com.xc.microservice.validate.config.task;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class BeanConfig{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Bean
	public ThreadPoolExecutor getBean(){
		return new ThreadPoolExecutor(4,
						5, 10, TimeUnit.SECONDS,
						new LinkedBlockingQueue<Runnable>(1),
						new ThreadFactory() {
							final AtomicInteger threadNumber = new AtomicInteger(1);
							public Thread newThread(Runnable r) {
								Thread t = new Thread(Thread.currentThread()
										.getThreadGroup(), r, "ThreadPoolExecutor中线程"
										+ (threadNumber.getAndIncrement()));
								t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
											public void uncaughtException(Thread t,
													Throwable e) {
												logger.error("ThreadPoolExecutor异常:"+e.getMessage()+ "\r\n");
																
											}
										});
								return t;
							}
						}, new ThreadPoolExecutor.CallerRunsPolicy());
	}
}
