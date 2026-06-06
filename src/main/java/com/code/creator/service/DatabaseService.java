package com.code.creator.service;

import com.code.creator.model.dto.ColumnDTO;

import java.util.List;

/**
 * 数据库服务接口
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
public interface DatabaseService {
    
    /**
     * 查询表注释（自动识别数据库类型）
     *
     * @param table 表名
     * @return 表注释
     */
    String findTableComment(String table);

    /**
     * 查询表列信息（自动识别数据库类型）
     *
     * @param table 表名
     * @return 列信息列表
     */
    List<ColumnDTO> findTableColumns(String table);
}
