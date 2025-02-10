package com.code.creator.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private String entityPackage;
    private String entitySavePath;
    private String baseEntityPackage;
    private List<String> entityIgnoreField;

    private Template template;

    public String entityClassName() {
        return Arrays.stream(this.table.replaceAll("^t_", "").split("_")).map(StringUtils::capitalize).collect(Collectors.joining());
    }

    public String serviceClassName() {
        return this.entityClassName() + "Service";
    }

    public String repositoryClassName() {
        return this.entityClassName() + "Repository";
    }

    @Data
    public static class Module {

        private String name;
        private String fullName;
        private String packageName;
    }

    @Getter
    @Setter
    public static class Template implements Serializable {
        @Serial
        private static final long serialVersionUID = 9139113463676438576L;

        private Entity entity;
        private Service service;
        private Repository repository;

        protected String moduleName;
        protected String packageName;

        public String getSavePath() {
            return getModuleName() + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + getPackageName().replace(".", File.separator);
        }

        public static class Entity extends Template {
            @Serial
            private static final long serialVersionUID = 6373976465532465576L;

        }

        public static class Service extends Template {
            @Serial
            private static final long serialVersionUID = 6373976465532465576L;
        }

        public static class Repository extends Template {
            @Serial
            private static final long serialVersionUID = 2418666658102852886L;
        }
    }
}

