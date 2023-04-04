package ru.jug.netflixZuulGateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import ru.jug.netflixZuulGateway.configuration.ClientVersionsConfiguration;

import java.net.MalformedURLException;
import java.net.URL;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor

public class CustomUrlZuulFilter extends ZuulFilter {

    private final String X_CLIENT_VERSION_HEADER = "x-client-version";
    private final String X_SERVICE_VERSION_HEADER = "x-service-version";

    private final ClientVersionsConfiguration clientVersionsConfiguration;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return PRE_DECORATION_FILTER_ORDER + 1;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().getRouteHost().getPort() == -1;
    }

    @Override
    public Object run() {

        final RequestContext context = RequestContext.getCurrentContext();
        var contextRouteHost = context.getRouteHost();

        if (context.get(SERVICE_ID_KEY) == null) {

            var clientVersion = context.getRequest().getHeader(X_CLIENT_VERSION_HEADER);

            if (clientVersion == null) {
                clientVersion = "default";
            }

            var proxy = (String) context.get(PROXY_KEY);
            var targetServicePort = clientVersionsConfiguration.getServiceVersion(clientVersion, proxy).toString();

            try {
                var targetUrl = new URL(contextRouteHost + ":" + targetServicePort);
                context.setRouteHost(targetUrl);
                context.getResponse().addHeader(X_SERVICE_VERSION_HEADER, "v" + targetServicePort.substring(3));
            } catch (MalformedURLException e) {
                log.error("Url creation error", e);
            }
        }
        return null;
    }

}
