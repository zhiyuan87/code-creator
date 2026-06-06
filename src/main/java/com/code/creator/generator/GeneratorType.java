package com.code.creator.generator;

/**
 * 代码生成器类型枚举
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
public enum GeneratorType {
    ENTITY,         // 实体类
    MAPPER,         // Mapper 接口
    MAPPER_XML,     // Mapper XML
    SERVICE,        // Service 接口
    SERVICE_IMPL,   // Service 实现类
    DAO,            // DAO 接口
    DAO_IMPL,       // DAO 实现类
    REPOSITORY      // Repository
}