package de.fromscratch.node;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Output {

	String name() default "";
	String desc() default "";
}