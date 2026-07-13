package com.code.creator.generator.impl;

import com.code.creator.generator.GeneratorType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * MySQL Mapper XML 代码生成器。
 */
@Slf4j
@Component
public class MysqlMapperXmlGenerator extends MapperXmlGenerator {

    @Resource
    private Environment environment;

    @Override
    public boolean supports(GeneratorType type) {
        return type == GeneratorType.MAPPER_XML && isMySqlDatabase();
    }

    private boolean isMySqlDatabase() {
        try {
            if (environment == null) {
                log.debug("Environment is null, returning false for MySQL check");
                return false;
            }
            String driverClassName = environment.getProperty("spring.datasource.driver-class-name", "");
            boolean isMySql = "com.mysql.cj.jdbc.Driver".equals(driverClassName)
                    || "com.mysql.jdbc.Driver".equals(driverClassName);
            log.debug("MySQL check result: {}", isMySql);
            return isMySql;
        } catch (Exception e) {
            log.error("Error checking MySQL database type", e);
            return false;
        }
    }
}