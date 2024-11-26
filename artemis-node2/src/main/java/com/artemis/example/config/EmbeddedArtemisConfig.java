package com.artemis.example.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.FileDeploymentManager;
import org.apache.activemq.artemis.core.config.impl.FileConfiguration;
import org.apache.activemq.artemis.core.config.impl.LegacyJMSConfiguration;
import org.apache.activemq.artemis.core.config.impl.SecurityConfiguration;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.spi.core.security.ActiveMQJAASSecurityManager;
import org.apache.activemq.artemis.spi.core.security.jaas.InVMLoginModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @ClassName EmbeddedArtemisConfig
 * @Date: Create in 16:20 2024/11/12
 */
@Slf4j
@Component
public class EmbeddedArtemisConfig {

    @Bean
    public ActiveMQJAASSecurityManager securityManager(@Value("${amq.broker.user}") String user,
                                                       @Value("${amq.broker.password}") String password,
                                                       @Value("${amq.broker.role}") String role) {
        log.info("init node2 security manager");
        final SecurityConfiguration securityConfig = new SecurityConfiguration();
        securityConfig.addUser(user, password);
        securityConfig.addRole(user, role);
        securityConfig.setDefaultUser(user);
        return new ActiveMQJAASSecurityManager(InVMLoginModule.class.getName(), securityConfig);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public EmbeddedActiveMQ initEmbeddedArtemis(ActiveMQJAASSecurityManager securityManager) {

        EmbeddedActiveMQ embeddedActiveMQ = new EmbeddedActiveMQ();
        embeddedActiveMQ.setSecurityManager(securityManager);
        embeddedActiveMQ.setConfiguration(this.getEmbeddedActiveMQConfig());
        log.info("ActiveMQ-Artemis node2 load success。。。");
        return embeddedActiveMQ;
    }

    @SneakyThrows
    private Configuration getEmbeddedActiveMQConfig() {
        FileConfiguration configuration = new FileConfiguration();
        LegacyJMSConfiguration legacyJMSConfiguration = new LegacyJMSConfiguration(configuration);
        new FileDeploymentManager()
                .addDeployable(configuration)
                .addDeployable(legacyJMSConfiguration)
                .readConfiguration();
        return configuration;
    }
}
