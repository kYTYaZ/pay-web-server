package com.huateng.pay.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public abstract class BaseLinstener implements ServletContextListener{
    private WebApplicationContext ctx;
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
        init();
    }
    
    public Object getBean(String serviceName){
        return ctx.getBean(serviceName);
    }
    
    public abstract void init();
}
