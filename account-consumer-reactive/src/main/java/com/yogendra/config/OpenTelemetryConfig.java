package com.yogendra.config;

import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {
    @Value("${otel.trace.endpoint}")
    private String traceEndPoint;

    private SpanExporter spanExporter() {
        return OtlpHttpSpanExporter
                .builder()
                .setEndpoint(traceEndPoint)
                .build();
    }

    private SpanProcessor spanProcessor() {
        return SimpleSpanProcessor
                .builder(spanExporter())
                .build();
    }

    private SdkTracerProvider sdkTracerProvider() {
        Resource resource = Resource.builder().put("service.name", "account-consumer-reactive").build();
        return SdkTracerProvider
                .builder()
                .setResource(resource)
                .addSpanProcessor(spanProcessor())
                .build();
    }

    @Bean
    public OpenTelemetrySdk openTelemetrySdk() {
        return OpenTelemetrySdk
                .builder()
                .setTracerProvider(sdkTracerProvider())
                .build();
    }
}
