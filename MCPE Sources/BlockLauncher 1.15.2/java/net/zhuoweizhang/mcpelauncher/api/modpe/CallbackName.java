package net.zhuoweizhang.mcpelauncher.api.modpe;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CallbackName {
    String[] args() default {};

    String name();

    boolean prevent() default false;
}
