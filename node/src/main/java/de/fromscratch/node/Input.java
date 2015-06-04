package de.fromscratch.node;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Input {

	String name() default "";
	String desc() default "";
}