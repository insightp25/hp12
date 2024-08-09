package io.clean.tdd.hp12.configuration;

import io.clean.tdd.hp12.interfaces.common.WaitingQueueInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

    private final WaitingQueueInterceptor waitingQueueInterceptor;

    private final List<String> pathPatternsToAdd = List.of(
        "/**"
    );

    private final List<String> pathPatternsToExclude = List.of(
    );

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
            .addInterceptor(waitingQueueInterceptor)
            .addPathPatterns(pathPatternsToAdd)
            .excludePathPatterns(pathPatternsToExclude);
    }
}
