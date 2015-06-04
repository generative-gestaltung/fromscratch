package de.fromscratch.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Control {

	String name() default "";
	
	String desc() default "";
	
	double min()  default 0;
	
	double max()  default 1;
	
	String precision() default "";
}
