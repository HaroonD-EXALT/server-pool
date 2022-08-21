package com.project.serverpool.config;

import com.aerospike.client.Host;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.config.AerospikeDataSettings;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotEmpty;
import java.util.Collection;

@EnableAerospikeRepositories(basePackages = "com.project.serverpool.repositories")
@EnableConfigurationProperties(AerospikeConfiguration.AerospikeConfigurationProperties.class)
@Configuration
public class AerospikeConfiguration extends AbstractAerospikeDataConfiguration {
    @Autowired
    AerospikeConfigurationProperties properties;

    @Override
    protected Collection<Host> getHosts() {
        return Host.parseServiceHosts(properties.getHosts());
    }

    @Override
    protected String nameSpace() {
        return properties.getNamespace();
    }

    @Data
    @Validated // add this annotation if you want @ConfigurationProperties to be validated!
    @ConfigurationProperties("aerospike")
    public static class AerospikeConfigurationProperties {

        @NotEmpty
        String hosts;

        @NotEmpty
        String namespace;
    }

      @Bean
    public AerospikeDataSettings aerospikeDataSettings() {
        return AerospikeDataSettings.builder().scansEnabled(true).sendKey(true).build();
    }
}