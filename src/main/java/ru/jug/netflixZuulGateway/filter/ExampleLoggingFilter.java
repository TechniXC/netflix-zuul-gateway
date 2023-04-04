package ru.jug.netflixZuulGateway.filter;

import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class ExampleLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        try {
            HttpServletRequest unmodifiedRequest = (HttpServletRequest) request;
            HttpServletResponse unmodifiedResponse = (HttpServletResponse) response;

            // Здесь можно использовать  кастомные обертки, для того что бы модифицировать request / response перед отправкой далее.
//            HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper((HttpServletRequest) request);
//            HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper ((HttpServletResponse) response);

            log.debug("Pre. filter logic executed!");
            log.info("{} request to {} has been received", unmodifiedRequest.getMethod(), unmodifiedRequest.getRequestURL().toString());

            chain.doFilter(unmodifiedRequest, unmodifiedResponse);

//            chain.doFilter(wrappedRequest, wrappedResponse);

            log.debug("Post. filter logic executed!");
            log.info("Response with status {} has been provided: ", unmodifiedResponse.getStatus());

        } catch (Exception ex) {
            log.warn("Filter {} skipped due the exception", this.getClass().getSimpleName(), ex);
            chain.doFilter(request, response);
        }
    }
}
