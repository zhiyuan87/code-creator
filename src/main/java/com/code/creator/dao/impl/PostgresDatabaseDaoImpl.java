package com.code.creator.dao.impl;

import com.code.creator.dao.DatabaseDao;
import com.code.creator.mapper.PostgresDatabaseMapper;
import com.code.creator.model.dto.ColumnDTO;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * PostgreSQL 数据库 DAO 实现类
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Repository
@ConditionalOnProperty(name = "spring.datasource.driver-class-name", havingValue = "org.postgresql.Driver")
public class PostgresDatabaseDaoImpl extends DatabaseDao {

    @Resource
    private PostgresDatabaseMapper postgresDatabaseMapper;

    @Override
    public String findTableComment(String database, String table) {
        return postgresDatabaseMapper.findTableComment(table, database);
    }

    @Override
    protected List<Map<String, Object>> queryRawColumns(String database, String table) {
        return postgresDatabaseMapper.findTableColumns(table, database);
    }

    @Override
    protected ColumnDTO convertMapToColumnDTO(Map<String, Object> resultMap) {
        var columnDTO = new ColumnDTO();
        columnDTO.setColumnName(getString(resultMap, "column_name"));
        columnDTO.setDataType(convertDataType(getString(resultMap, "data_type")));
        columnDTO.setColumnComment(getString(resultMap, "column_comment"));
        columnDTO.setIsNullable("YES".equalsIgnoreCase(getString(resultMap, "is_nullable")) ? "1" : "0");
        columnDTO.setColumnDefault(getString(resultMap, "column_default"));
        return columnDTO;
    }

    @Override
    protected String convertDataType(String dataType) {
        if (dataType == null || dataType.isEmpty()) {
            return "VARCHAR";
        }

        return switch (dataType.toLowerCase()) {
            case "bigint", "bigserial" -> "BIGINT";
            case "integer", "int", "serial" -> "INTEGER";
            case "smallint", "smallserial" -> "SMALLINT";
            case "boolean" -> "BOOLEAN";
            case "character varying", "varchar" -> "VARCHAR";
            case "character", "char" -> "CHAR";
            case "text" -> "TEXT";
            case "timestamp without time zone", "timestamp" -> "TIMESTAMP";
            case "timestamp with time zone", "timestamptz" -> "TIMESTAMP";
            case "date" -> "DATE";
            case "time without time zone", "time" -> "TIME";
            case "numeric", "decimal" -> "DECIMAL";
            case "real", "float4" -> "FLOAT";
            case "double precision", "float8" -> "DOUBLE";
            case "bytea" -> "BLOB";
            case "json", "jsonb" -> "JSON";
            case "uuid" -> "VARCHAR";
            default -> "VARCHAR";
        };
    }
}
