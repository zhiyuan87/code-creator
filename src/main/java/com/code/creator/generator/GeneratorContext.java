package com.code.creator.generator;

import com.code.creator.config.GeneratorConfig;
import com.code.creator.model.dto.ColumnDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 代码生成上下文
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Data
@Builder
public class GeneratorContext {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表注释
     */
    private String tableComment;

    /**
     * 列信息列表
     */
    private List<ColumnDTO> columns;

    /**
     * 生成配置
     */
    private GeneratorConfig config;
}
