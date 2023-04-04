package ru.jug.netflixZuulGateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Фильтр для обработки ошибок
 */
@Component
@Slf4j
@AllArgsConstructor
public class ZuulExceptionFilter extends ZuulFilter {

    private static final String FILTER_TYPE = "error";
    private static final String THROWABLE_KEY = "throwable";

    // Needs to run before SendErrorFilter which has filterOrder == 0
    private static final int FILTER_ORDER = -1;

    @Override
    public String filterType() {
        return FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().containsKey(THROWABLE_KEY);
    }

    @Override
    public Object run() {
        try {
            RequestContext ctx = RequestContext.getCurrentContext();

            ServletRequest request = ctx.getRequest();
            ServletResponse response = ctx.getResponse();
            Exception exception = (Exception) ctx.get(THROWABLE_KEY);

            log.warn("Exception during filtering request: {}, response {}", request, response, exception);

        } catch (Exception ex) {
            log.error("Exception filtering in custom error filter", ex);
            ReflectionUtils.rethrowRuntimeException(ex);
        }
        return null;
    }
}
