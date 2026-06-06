package com.code.creator.generator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 代码生成器工厂
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeneratorFactory {

    private final List<CodeGenerator> generatorList;

    /**
     * 初始化（仅记录日志）
     */
    @PostConstruct
    public void init() {
        log.info("GeneratorFactory initialized with {} generators", generatorList.size());

        // 打印所有注册的生成器
        generatorList.forEach(generator -> log.debug("Registered generator: {}", generator.getClass().getSimpleName()));
    }

    /**
     * 生成代码并保存到文件
     */
    public void generate(GeneratorType generatorType) {
        if (generatorType == null) {
            throw new IllegalArgumentException("GeneratorType cannot be null");
        }

        // 查找第一个支持该类型的生成器
        for (var generator : generatorList) {
            if (generator.supports(generatorType)) {
                log.debug("Found generator {} for type {}", generator.getClass().getSimpleName(), generatorType);

                generator.generate();
                return;
            }
        }

        log.error("No generator found for type: {}", generatorType);
        throw new IllegalArgumentException("No generator found for type: " + generatorType);
    }
}