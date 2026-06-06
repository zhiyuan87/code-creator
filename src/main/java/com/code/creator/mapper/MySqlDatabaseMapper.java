package com.code.creator.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * MySQL 数据库 Mapper 接口
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Mapper
public interface MySqlDatabaseMapper {
    
    /**
     * 查询 MySQL 表注释
     *
     * @param database 数据库名
     * @param table 表名
     * @return 表注释
     */
    String findTableComment(@Param("database") String database, @Param("table") String table);

    /**
     * 查询 MySQL 表列信息
     *
     * @param database 数据库名
     * @param table 表名
     * @return 列信息列表（Map格式）
     */
    List<Map<String, Object>> findTableColumns(@Param("database") String database, @Param("table") String table);
}
