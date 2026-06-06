package com.code.creator.service;

/**
 * 代码生成服务接口
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
public interface CodeGenerationService {
    /**
     * 生成实体类
     */
    void generateEntity();

    /**
     * 生成 Service 接口及实现类
     */
    void generateService();

    /**
     * 生成 DAO 接口及实现类
     */
    void generateDao();

    /**
     * 生成 Mapper 接口及 XML
     */
    void generateMapper();

    /**
     * 生成 Repository 接口
     */
    void generateRepository();
}
