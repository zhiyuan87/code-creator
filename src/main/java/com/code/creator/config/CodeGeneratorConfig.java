package com.code.creator.config;

import com.code.creator.generator.GeneratorContext;
import com.code.creator.generator.GeneratorFactory;
import com.code.creator.generator.GeneratorType;
import com.code.creator.service.CodeGenerationService;
import com.code.creator.service.DatabaseService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;

/**
 * 代码生成器配置
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class CodeGeneratorConfig {

    private final DatabaseService databaseService;
    private final GeneratorConfig generatorConfig;

    @Lazy
    @Resource
    private CodeGenerationService codeGenerationService;

    @Lazy
    @Resource
    private GeneratorFactory generatorFactory;

    @Bean
    public GeneratorContext buildContext() {
        var tableName = generatorConfig.getTableName();

        var columns = databaseService.findTableColumns(tableName);
        if (columns.isEmpty()) {
            throw new IllegalArgumentException("No columns found for table: " + tableName);
        }

        String tableComment = null;
        try {
            tableComment = databaseService.findTableComment(tableName);
        } catch (Exception e) {
            log.warn("Failed to get table comment for {}: {}", tableName, e.getMessage());
        }

        return GeneratorContext.builder()
                .tableName(tableName)
                .config(generatorConfig)
                .columns(columns)
                .tableComment(tableComment)
                .build();
    }

    @EventListener
    public void listen(ApplicationReadyEvent applicationReadyEvent) {
        codeGenerationService.generateEntity();

        codeGenerationService.generateService();

        codeGenerationService.generateDao();

        generatorFactory.generate(GeneratorType.MAPPER);

        generatorFactory.generate(GeneratorType.MAPPER_XML);

        System.exit(0);
    }
}
