package com.eryansky.core.quartz;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QuartJobSchedulingListener implements ApplicationListener<ContextRefreshedEvent> {
    Logger logger = LoggerFactory.getLogger(QuartJobSchedulingListener.class);

    @Autowired
    private Scheduler scheduler;

    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            ApplicationContext applicationContext = event.getApplicationContext();
            this.loadCronTriggers(applicationContext, scheduler);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void loadCronTriggers(ApplicationContext applicationContext, Scheduler scheduler) {
        Map<String, Object> quartzJobBeans = applicationContext.getBeansWithAnnotation(QuartzJob.class);
        Set<String> beanNames = quartzJobBeans.keySet();
        List<CronTrigger> cronTriggerBeans = new ArrayList<CronTrigger>();
        for (String beanName : beanNames) {
            Object object = quartzJobBeans.get(beanName);
            try {
                if (Job.class.isAssignableFrom(object.getClass())) {
                    QuartzJob quartzJobAnnotation = AnnotationUtils.findAnnotation(object.getClass(), QuartzJob.class);
                    JobKey jobKey = new JobKey(quartzJobAnnotation.name(), quartzJobAnnotation.group());
                    JobDetail job = JobBuilder
                            .newJob((Class<? extends Job>) object.getClass())
                            .withIdentity(jobKey)
                            .build();
                    CronTrigger cronTrigger = TriggerBuilder
                            .newTrigger()
                            .withIdentity(quartzJobAnnotation.name() + "_trigger", quartzJobAnnotation.group())
                            .withSchedule(CronScheduleBuilder.cronSchedule(quartzJobAnnotation.cronExp()))
                            .build();
                    boolean exist = scheduler.checkExists(jobKey);
                    if(exist){
                        scheduler.deleteJob(jobKey);
                    }
                    scheduler.scheduleJob(job, cronTrigger);
                } else {
                    String errorMsg = object.getClass() + " doesn't implemented " + Job.class.getName();
                    logger.error(errorMsg);
                    throw new RuntimeException(errorMsg);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
