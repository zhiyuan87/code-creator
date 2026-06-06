package com.code.creator.dao.impl;

import com.code.creator.dao.DatabaseDao;
import com.code.creator.mapper.MySqlDatabaseMapper;
import com.code.creator.model.dto.ColumnDTO;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * MySQL 数据库 DAO 实现类
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Repository
@ConditionalOnProperty(name = "spring.datasource.driver-class-name", havingValue = "com.mysql.cj.jdbc.Driver")
public class MySqlDatabaseDaoImpl extends DatabaseDao {

    @Resource
    private MySqlDatabaseMapper mySqlDatabaseMapper;

    @Override
    public String findTableComment(String database, String table) {
        return mySqlDatabaseMapper.findTableComment(database, table);
    }

    @Override
    protected List<Map<String, Object>> queryRawColumns(String database, String table) {
        return mySqlDatabaseMapper.findTableColumns(database, table);
    }

    @Override
    protected ColumnDTO convertMapToColumnDTO(Map<String, Object> resultMap) {
        var columnDTO = new ColumnDTO();
        columnDTO.setColumnName(getString(resultMap, "COLUMN_NAME"));
        columnDTO.setDataType(convertDataType(getString(resultMap, "DATA_TYPE")));
        columnDTO.setColumnComment(getString(resultMap, "COLUMN_COMMENT"));
        columnDTO.setIsNullable(getString(resultMap, "IS_NULLABLE"));
        columnDTO.setColumnDefault(getString(resultMap, "COLUMN_DEFAULT"));
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
            case "tinyint" -> "TINYINT";
            case "boolean", "bit" -> "BOOLEAN";
            case "character varying", "varchar" -> "VARCHAR";
            case "character", "char" -> "CHAR";
            case "text", "tinytext", "mediumtext", "longtext" -> "TEXT";
            case "timestamp without time zone", "timestamp", "datetime" -> "TIMESTAMP";
            case "timestamp with time zone", "timestamptz" -> "TIMESTAMP";
            case "date" -> "DATE";
            case "time without time zone", "time" -> "TIME";
            case "numeric", "decimal" -> "DECIMAL";
            case "real", "float4", "float" -> "FLOAT";
            case "double precision", "float8", "double" -> "DOUBLE";
            case "blob", "binary", "varbinary", "longblob", "tinyblob", "mediumblob" -> "BLOB";
            case "json", "jsonb" -> "JSON";
            case "uuid" -> "VARCHAR";
            default -> "VARCHAR";
        };
    }
}
