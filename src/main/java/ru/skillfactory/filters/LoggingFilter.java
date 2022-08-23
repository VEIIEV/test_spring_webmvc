package ru.skillfactory.filters;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;




//с бутом не нужно менять Web.xml файл не нужно, ебашить странныен анотации, достаточно написасать
//аннотацию @Component и  фильтер сам запамиться  к контролерам, чётко заебись
//не есть нюанс, он будет применяться ко всем контроллерам

//@Component
//@WebFilter(filterName="LoggingFilter ann name",urlPatterns="/json/postmethod")

//что бы он работал на конкреттый тебе нужно его зарегистрировать в мейн классе
public class LoggingFilter extends OncePerRequestFilter {


    //создаём экземпляр логгера
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    // лист форматов файла,с которыми мы планируем работать
    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON,
            MediaType.valueOf("application/*+json"),
            MediaType.valueOf("application/*+xml"),
            MediaType.MULTIPART_FORM_DATA
    );


    //переопределенный фильтр, на входе запрос, ответ, цепь фильтров
    //
    //Фильтров может быть несколько, и они последовательно обрабатывают запрос (и ответ).
    // ни объединены в так называемую цепочку — и для них даже есть специальный класс FilterChain.
    //После обработка запроса в методе doFilter() нужно вызвать метод doFilter() следующего фильтра в цепочке.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //isAsyncDispatch(request) =Этот метод возвращает значение true, если фильтр в данный момент выполняется в рамках асинхронной отправки
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        //если отправка не асинхронная используем самописный doFilterWrapper
        } else {
            //на вход идут кастомные объекты wrapreq и wrapresp
            doFilterWrapper(wrapRequest(request), wrapResponse(response), filterChain);
        }


    }

    //запросы ответы, могут быть прочитаны лишь однажды, поэтому что бы работать с ними, их нужно завернуть в ContentCachingRequestWrapper

    //HttpServletResponse wrapper that caches all content written to the output stream and writer, and allows this content to be retrieved via a byte array.

    protected void doFilterWrapper(ContentCachingRequestWrapper contentCachingRequestWrapper, ContentCachingResponseWrapper contentCachingResponseWrapper, FilterChain filterChain) throws IOException, ServletException {
        try {
            //выполняем действия перед выполнением запроса, тоже кастом функция
            beforeRequest(contentCachingRequestWrapper, contentCachingResponseWrapper);
            //запускае ещё 1 цикл фильтрации
            filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);
        } finally {
            //выполняем действие после запроса
            afterRequest(contentCachingRequestWrapper, contentCachingResponseWrapper);
            contentCachingResponseWrapper.copyBodyToResponse();
        }
    }

    protected void beforeRequest(ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper) {
        if (logger.isInfoEnabled()) {
            //вызываем кастомную функцию для записи хедера с инфой о методе и url
            //а также все что в нем есть
            logRequestHeader(requestWrapper, requestWrapper.getRemoteAddr() + "|>");
        }
    }

    protected void afterRequest(ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper) {
        if (logger.isInfoEnabled()) {
            //вызываем кастомные функции для записи тела тела запроса и ответа
            //
            logRequestBody(requestWrapper, requestWrapper.getRemoteAddr() + "|>");
            logResponce(responseWrapper, requestWrapper.getRemoteAddr() + "|>");
        }
    }


    private static void logRequestHeader(ContentCachingRequestWrapper requestWrapper, String prefix) {
        String queryString = requestWrapper.getQueryString();
        if (queryString == null) {
            logger.info("{} {} {}", prefix, requestWrapper.getMethod(), requestWrapper.getRequestURL());
        } else {
            logger.info("{} {} {}?{}", prefix, requestWrapper.getMethod(), requestWrapper.getRequestURL(), queryString);
        }

        Collections.list(requestWrapper.getHeaderNames()).forEach(headerName ->
                Collections.list(requestWrapper.getHeaders(headerName)).forEach(headerValue -> logger.info("{} {} {}", prefix, headerName, headerValue)));
        logger.info("{}", prefix);

    }

    private static void logRequestBody(ContentCachingRequestWrapper requestWrapper, String prefix) {
        //записываем значение запроса как последовательность бит
        byte[] content = requestWrapper.getContentAsByteArray();
        //если она !=0  логируем её  кастомной функций, в неё передаёт содержание и кодировку
        if (content.length > 0) {
            logContent(content, requestWrapper.getContentType(), requestWrapper.getCharacterEncoding(), prefix);
        }
    }

    private static void logResponce(ContentCachingResponseWrapper responseWrapper, String prefix) {
        int status = responseWrapper.getStatus();
        logger.info("{} {} {}", prefix, status, HttpStatus.valueOf(status).getReasonPhrase());
        responseWrapper.getHeaderNames().forEach(header ->
                responseWrapper.getHeaders(header).forEach(headervalue ->
                        logger.info("{} {} {}", prefix, header, headervalue)));
        logger.info("{}", prefix);

        byte[] content = responseWrapper.getContentAsByteArray();

        if (content.length > 0) {
            logContent(content, responseWrapper.getContentType(), responseWrapper.getCharacterEncoding(), prefix);
        }
    }
    //логируем контент
    private static void logContent(byte[] content, String contentType, String contentEncoding, String prefix) {
        MediaType mediaType = MediaType.valueOf(contentType);
        boolean visible = VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
        if (visible) {
            String contentString = null;
            try {
                contentString = new String(content, contentEncoding);
                Stream.of(contentString.split("\r\n|\r\n")).forEach(line -> logger.info("{} {}", prefix, line));
            } catch (UnsupportedEncodingException e) {
                logger.info("{}, [{} bytes content]", prefix, content.length);
            }

        } else {
            logger.info("{}, [{} bytes content]", prefix, content.length);
        }
    }
//заворачиваем запрос в класс  обёртку   ContentCachingRequestWrapper, если он уже им не является
    private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request){
        if(request  instanceof HttpServletRequestWrapper){
            return (ContentCachingRequestWrapper) request;
        }else{
            return new ContentCachingRequestWrapper(request);
        }
    }

    //заворачиваем ответ в класс обёртку ContentCachingResponseWrapper, если он уже им не является
    private static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response){
        if(response  instanceof HttpServletResponseWrapper){
            return (ContentCachingResponseWrapper)  response;
        }else{
            return new ContentCachingResponseWrapper(response);
        }
    }

}
