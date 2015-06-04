package de.fromscratch.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Code {

	String name() default "";
	String desc() default "";
}