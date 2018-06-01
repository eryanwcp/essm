package com.eryansky.core.quartz;

import org.quartz.Job;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

public class SpringQuartzJobFactory extends SpringBeanJobFactory {
    Logger logger = LoggerFactory.getLogger(SpringQuartzJobFactory.class);

    @Autowired
    private ApplicationContext ctx;

    @Override
    @SuppressWarnings("unchecked")
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Job job = null;
        try {
            job = ctx.getBean(bundle.getJobDetail().getJobClass());
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(job);
            MutablePropertyValues pvs = new MutablePropertyValues();
            pvs.addPropertyValues(bundle.getJobDetail().getJobDataMap());
            pvs.addPropertyValues(bundle.getTrigger().getJobDataMap());
            bw.setPropertyValues(pvs, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new Exception(e);
        }
        return job;
    }
}
