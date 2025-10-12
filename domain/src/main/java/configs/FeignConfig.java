package configs;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor tracingRequestInterceptor(@Autowired Tracer tracer) {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                try {
                    if (tracer != null && tracer.currentSpan() != null) {
                        TraceContext traceContext = tracer.currentSpan().context();
                        String traceId = traceContext.traceId();
                        String spanId = traceContext.spanId();

                        template.header("X-Trace-Id", traceId);
                        template.header("X-Span-Id", spanId);

                        template.header("X-B3-TraceId", traceId);
                        template.header("X-B3-SpanId", spanId);
                        template.header("b3", traceId + "-" + spanId);
                    } else {
                        log.warn("FeignConfig: No current span found for tracing propagation to {}",
                                template.feignTarget().name());
                    }
                } catch (Exception e) {
                    log.error("FeignConfig: Error applying tracing headers", e);
                }
            }
        };
    }
}
