package com.code.creator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * {@code @Classname} CreatorConfig
 * {@code @Description}
 * {@code @Date} 2023-12-29 15:00:00
 * {@code @Created} by zhiyuan
 */
@Data
@ConfigurationProperties(prefix = "creator.config")
public class CreatorConfig {

    private String project;
    private Module module;
    private String table;
    private String database;
    private String author;
    private List<String> insertIgnoreField;
    private List<String> updateIgnoreField;

    @Data
    public static class Module {

        private String name;
        private String fullName;
        private String packageName;
    }
}

