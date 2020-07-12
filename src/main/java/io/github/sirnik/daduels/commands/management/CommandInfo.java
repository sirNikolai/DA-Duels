package io.github.sirnik.daduels.commands.management;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {

    String description();

    String usage() default "";

    String[] aliases();

    boolean nonPlayer() default true;

    boolean hidden() default false;

    int mandatoryArgs() default 0;
}
