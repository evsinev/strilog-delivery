package com.strilog.delivery.receiver.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PreventStackTraceErrorFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(PreventStackTraceErrorFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            long id = System.currentTimeMillis();
            LOG.error("Error while processing trace {}", id, e);
            try {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setStatus(501);
                response.getOutputStream().println("Error " + id);
            } catch (IOException e1) {
                LOG.error("Cannot write error", e1);
            }
        }
    }

    @Override
    public void destroy() {

    }
}
