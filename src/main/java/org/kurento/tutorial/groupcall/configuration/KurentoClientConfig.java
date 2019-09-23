package org.kurento.tutorial.groupcall.configuration;

import org.kurento.client.KurentoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KurentoClientConfig {
    @Bean
    public KurentoClient kurentoClient() {
        return KurentoClient.create();
    }
}
