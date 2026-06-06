package com.code.creator.config;

import lombok.Builder;
import lombok.Data;

/**
 * 代码生成配置
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Data
@Builder
public class GeneratorConfig {
    /**
     * 生成的包名
     */
    private String packageName;

    /**
     * 基础实体类包名
     */
    private String baseEntityPackage;

    /**
     * 基础 DAO 类包名
     */
    private String baseDaoPackage;

    /**
     * 基础 Service 类包名
     */
    private String baseServicePackage;

    /**
     * 基础 Mapper 接口包名
     */
    private String baseMapperPackage;

    /**
     * 作者
     */
    private String author;

    /**
     * 模块名称（用于自动生成 outputDirectory）
     * 例如：pictostar
     */
    private String moduleName;

    /**
     * 输出目录
     */
    private String outputDirectory;
}