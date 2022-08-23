package ru.skillfactory;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import ru.skillfactory.filters.LoggingFilter;

import javax.servlet.FilterRegistration;

@SpringBootApplication
public class BlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);}

    //регистрация бина
    @Bean
    FilterRegistrationBean<LoggingFilter> loggingFilterFilterRegistrationBean(){
        //создаём обехкт регистрации
        final FilterRegistrationBean<LoggingFilter>  filterRegistrationBean= new FilterRegistrationBean<>();

        //кладём в неё фильтр
        filterRegistrationBean.setFilter(new LoggingFilter());

        //привязываем к нему урл, можно несколько
        filterRegistrationBean.addUrlPatterns("/json/postmethod");

        return filterRegistrationBean;
    }
}
