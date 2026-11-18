package com.travixa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.api")
public class ApiProperties {

    private String title = "TRAVIXA API";
    private String version = "1.0.0";
    private String description = "Smart tourism and hospitality platform API";
    private long asyncRequestTimeoutMillis = 30_000;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getAsyncRequestTimeoutMillis() {
        return asyncRequestTimeoutMillis;
    }

    public void setAsyncRequestTimeoutMillis(long asyncRequestTimeoutMillis) {
        this.asyncRequestTimeoutMillis = asyncRequestTimeoutMillis;
    }
}
