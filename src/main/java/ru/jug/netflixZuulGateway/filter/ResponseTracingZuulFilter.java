package ru.jug.netflixZuulGateway.filter;

import brave.Tracer;
import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResponseTracingZuulFilter extends ZuulFilter {

    @Autowired
    private Tracer tracer;

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        List<Pair<String, String>> zuulResponseHeaders = ctx.getZuulResponseHeaders();
        if (zuulResponseHeaders != null) {
            String traceId = tracer.currentSpan().context().traceIdString();
            zuulResponseHeaders.add(new Pair<>("Trace-Id", traceId));
        }
        return null;
    }
}
