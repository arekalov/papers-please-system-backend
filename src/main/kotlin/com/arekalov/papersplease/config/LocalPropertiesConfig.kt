package com.arekalov.papersplease.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.io.FileSystemResource
import java.io.File

@Configuration
class LocalPropertiesConfig {

    @Bean
    fun propertySourcesPlaceholderConfigurer(): PropertySourcesPlaceholderConfigurer {
        val configurer = PropertySourcesPlaceholderConfigurer()
        val localPropertiesFile = File("local.properties")

        if (localPropertiesFile.exists()) {
            configurer.setLocation(FileSystemResource(localPropertiesFile))
            configurer.setIgnoreResourceNotFound(false)
        } else {
            configurer.setIgnoreResourceNotFound(true)
        }

        return configurer
    }
}
