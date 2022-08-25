package ru.skillfactory;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import ru.skillfactory.dto.TakenData;
import ru.skillfactory.filters.LoggingFilter;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spring.web.WebMvcRequestHandler;
import springfox.documentation.spring.web.paths.Paths;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static springfox.documentation.RequestHandler.byPatternsCondition;

@SpringBootApplication
public class BlogApplication {



    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }

    //регистрация бина
    @Bean
    FilterRegistrationBean<LoggingFilter> loggingFilterFilterRegistrationBean() {
        //создаём обехкт регистрации
        final FilterRegistrationBean<LoggingFilter> filterRegistrationBean = new FilterRegistrationBean<>();

        //кладём в неё фильтр
        filterRegistrationBean.setFilter(new LoggingFilter());

        //привязываем к нему урл, можно несколько
        filterRegistrationBean.addUrlPatterns("/json/postmethod");

        return filterRegistrationBean;
    }

    @Bean
    public TakenData takenDataBean() {
        TakenData takenData = new TakenData();
        takenData.setData("nothing");
        return takenData;
    }


    @Bean
    public InitializingBean removeSpringfoxHandlerProvider(DocumentationPluginsBootstrapper bootstrapper) {
        return () -> bootstrapper.getHandlerProviders().removeIf(WebMvcRequestHandlerProvider.class::isInstance);
    }


    //какая-то хуйня
    //без неё swagger не работает
    @Bean
    public RequestHandlerProvider customRequestHandlerProvider(Optional<ServletContext> servletContext, HandlerMethodResolver methodResolver, List<RequestMappingInfoHandlerMapping> handlerMappings) {
        String contextPath = servletContext.map(ServletContext::getContextPath).orElse(Paths.ROOT);
        return () -> handlerMappings.stream()
                .filter(mapping -> !mapping.getClass().getSimpleName().equals("IntegrationRequestMappingHandlerMapping"))
                .map(mapping -> mapping.getHandlerMethods().entrySet())
                .flatMap(Set::stream)
                .map(entry -> new WebMvcRequestHandler(contextPath, methodResolver, tweakInfo(entry.getKey()), entry.getValue()))
                .sorted(byPatternsCondition())
                .collect(toList());
    }

    RequestMappingInfo tweakInfo(RequestMappingInfo info) {
        if (info.getPathPatternsCondition() == null) return info;
        String[] patterns = info.getPathPatternsCondition().getPatternValues().toArray(String[]::new);
        return info.mutate().options(new RequestMappingInfo.BuilderConfiguration()).paths(patterns).build();
    }

}
