package com.jtracer.config;

import com.jtracer.collector.common.DeviceIdentityKnowledgeBase;
import com.jtracer.collector.common.DeviceIdentityResolver;
import com.jtracer.collector.common.KnowledgeBaseLoader;
import com.jtracer.collector.common.OuiVendorLookup;
import com.jtracer.collector.common.RuleBasedDeviceIdentityResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KnowledgeBaseConfig {

    @Bean
    DeviceIdentityKnowledgeBase deviceIdentityKnowledgeBase(KnowledgeBaseLoader loader) {
        return loader.load();
    }

    @Bean
    DeviceIdentityResolver deviceIdentityResolver(DeviceIdentityKnowledgeBase knowledgeBase) {
        return new RuleBasedDeviceIdentityResolver(knowledgeBase);
    }

    @Bean
    OuiVendorLookup ouiVendorLookup(DeviceIdentityKnowledgeBase knowledgeBase) {
        return new OuiVendorLookup(knowledgeBase.getOuiVendors());
    }
}
