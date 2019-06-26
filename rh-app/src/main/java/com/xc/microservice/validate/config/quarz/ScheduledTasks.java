package com.xc.microservice.validate.config.quarz;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@Configurable
@EnableScheduling
public class ScheduledTasks{
	
    //每小时执行一次
    @Scheduled(cron = "0 */60 *  * * * ")
    public void reportCurrentByCron(){
    	System.out.println("定时任务~");
    }

}
