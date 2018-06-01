package com.eryansky.core.quartz;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
//@Scope("prototype")
public @interface QuartzJob
{

	String name();

	String group() default "DEFAULT_GROUP";

	String cronExp();
}
