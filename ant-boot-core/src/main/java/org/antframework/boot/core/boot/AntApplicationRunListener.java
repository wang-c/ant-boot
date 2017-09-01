/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-08-22 11:53 创建
 */
package org.antframework.boot.core.boot;

import org.antframework.boot.core.AntBootApplication;
import org.antframework.boot.core.Apps;
import org.antframework.boot.core.ContextHolder;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.SpringVersion;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * ant应用RunListener
 */
public class AntApplicationRunListener implements SpringApplicationRunListener {
    private static final String SERVER_PORT_PROPERTY_NAME = "server.port";

    private SpringApplication springApplication;
    private ApplicationArguments arguments;

    public AntApplicationRunListener(SpringApplication springApplication, String[] args) {
        this.springApplication = springApplication;
        this.arguments = new DefaultApplicationArguments(args);
        initApp(springApplication);
    }

    @Override
    public void starting() {
        if (!SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_8)) {
            throw new IllegalStateException("请使用jdk1.8及以上版本");
        }
        if (Integer.parseInt(SpringVersion.getVersion().substring(0, 1)) < 4) {
            throw new IllegalStateException("请使用spring4.x版本");
        }
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        if (environment.getActiveProfiles().length != 1) {
            throw new IllegalStateException("profile必须设置，且必须为一个");
        }
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        ContextHolder.initContext(context);
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        System.setProperty(SERVER_PORT_PROPERTY_NAME, Integer.toString(Apps.getHttpPort()));
    }

    @Override
    public void finished(ConfigurableApplicationContext context, Throwable exception) {

    }

    // 初始化App
    private void initApp(SpringApplication springApplication) {
        AntBootApplication annotation = null;
        for (Object source : springApplication.getSources()) {
            if (!(source instanceof Class)) {
                throw new IllegalArgumentException("source必须是Class");
            }
            annotation = AnnotatedElementUtils.findMergedAnnotation((Class) source, AntBootApplication.class);
            if (annotation != null) {
                break;
            }
        }
        if (annotation == null) {
            throw new IllegalArgumentException("sources中无@AntBootApplication注解");
        }

        Apps.initApp(annotation.appCode(), annotation.httpPort());
    }
}