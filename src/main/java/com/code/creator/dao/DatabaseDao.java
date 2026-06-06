package com.code.creator.dao;

import com.code.creator.model.dto.ColumnDTO;

import java.util.List;
import java.util.Map;

/**
 * 数据库 DAO 抽象基类
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
public abstract class DatabaseDao {
    /**
     * 查询表注释
     *
     * @param database 数据库名
     * @param table    表名
     * @return 表注释
     */
    public abstract String findTableComment(String database, String table);

    /**
     * 查询表列信息
     *
     * @param database 数据库名
     * @param table    表名
     * @return 列信息列表
     */
    public List<ColumnDTO> findTableColumns(String database, String table) {
        List<Map<String, Object>> rawColumns = queryRawColumns(database, table);
        return convertToColumnDTO(rawColumns);
    }

    /**
     * 查询原始列数据（由子类实现）
     *
     * @param database 数据库名
     * @param table    表名
     * @return 原始列数据列表
     */
    protected abstract List<Map<String, Object>> queryRawColumns(String database, String table);

    /**
     * 将原始数据转换为 ColumnDTO（通用逻辑）
     *
     * @param resultMap 原始列数据
     * @return ColumnDTO 列表
     */
    private List<ColumnDTO> convertToColumnDTO(List<Map<String, Object>> resultMap) {
        if (resultMap == null || resultMap.isEmpty()) {
            return List.of();
        }

        return resultMap.stream().map(this::convertMapToColumnDTO).toList();
    }

    /**
     * 将单个 Map 转换为 ColumnDTO（由子类实现）
     *
     * @param raw 原始数据 Map
     * @return ColumnDTO
     */
    protected abstract ColumnDTO convertMapToColumnDTO(Map<String, Object> raw);

    /**
     * 转换数据库数据类型到标准 SQL 类型（由子类实现）
     *
     * @param dataType 原始数据类型
     * @return 标准化后的数据类型
     */
    protected abstract String convertDataType(String dataType);

    /**
     * 安全获取字符串值
     *
     * @param map 数据 Map
     * @param key 键名
     * @return 字符串值，null 时返回空字符串
     */
    protected String getString(Map<String, Object> map, String key) {
        return String.valueOf(map.getOrDefault(key, ""));
    }
}
