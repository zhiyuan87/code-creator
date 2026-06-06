package com.code.creator.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据库列信息 DTO
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Data
public class ColumnDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8620294755914921254L;

    /**
     * 字段名
     */
    private String columnName;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 字段注释
     */
    private String columnComment;

    /**
     * 是否可空（YES/NO）
     */
    private String isNullable;

    /**
     * 默认值
     */
    private String columnDefault;
}