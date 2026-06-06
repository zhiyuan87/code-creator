package com.code.creator.service.impl;

import com.code.creator.generator.GeneratorFactory;
import com.code.creator.generator.GeneratorType;
import com.code.creator.service.CodeGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 代码生成服务实现
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CodeGenerationServiceImpl implements CodeGenerationService {

    private final GeneratorFactory generatorFactory;

    @Override
    public void generateEntity() {
        generatorFactory.generate(GeneratorType.ENTITY);
    }

    @Override
    public void generateService() {
        generatorFactory.generate(GeneratorType.SERVICE);
        generatorFactory.generate(GeneratorType.SERVICE_IMPL);
    }

    @Override
    public void generateDao() {
        generatorFactory.generate(GeneratorType.DAO);
        generatorFactory.generate(GeneratorType.DAO_IMPL);
    }

    @Override
    public void generateMapper() {
        generatorFactory.generate(GeneratorType.MAPPER);
        generatorFactory.generate(GeneratorType.MAPPER_XML);
    }

    @Override
    public void generateRepository() {
        generatorFactory.generate(GeneratorType.REPOSITORY);
    }
}
