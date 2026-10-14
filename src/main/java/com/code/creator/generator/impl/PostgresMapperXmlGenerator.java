package com.code.creator.generator.impl;

import com.code.creator.generator.GeneratorType;
import com.code.creator.model.dto.ColumnDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * PostgreSQL Mapper XML 代码生成器。
 */
@Slf4j
@Component
public class PostgresMapperXmlGenerator extends MapperXmlGenerator {

    @Resource
    private Environment environment;

    @Override
    public boolean supports(GeneratorType type) {
        return type == GeneratorType.MAPPER_XML && isPostgresDatabase();
    }

    private boolean isPostgresDatabase() {
        try {
            if (environment == null) {
                log.debug("Environment is null, returning false for PostgreSQL check");
                return false;
            }
            String driverClassName = environment.getProperty("spring.datasource.driver-class-name", "");
            boolean isPostgres = "org.postgresql.Driver".equals(driverClassName);
            log.debug("PostgreSQL check result: {}", isPostgres);
            return isPostgres;
        } catch (Exception e) {
            log.error("Error checking PostgreSQL database type", e);
            return false;
        }
    }

    @Override
    protected String buildParameterExpression(ColumnDTO column) {
        String parameterExpression = super.buildParameterExpression(column);
        if ("JSON".equalsIgnoreCase(column.getDataType())) {
            return "CAST(" + parameterExpression + " AS JSONB)";
        }
        return parameterExpression;
    }
}
