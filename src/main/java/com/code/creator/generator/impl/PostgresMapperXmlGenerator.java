package com.code.creator.generator.impl;

import com.code.creator.generator.AbstractCodeGenerator;
import com.code.creator.generator.GeneratorContext;
import com.code.creator.generator.GeneratorType;
import com.code.creator.model.dto.ColumnDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Mapper XML 代码生成器
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Slf4j
@Component
public class PostgresMapperXmlGenerator extends AbstractCodeGenerator {

    @Resource
    private Environment environment;

    @Override
    public boolean supports(GeneratorType type) {
        return type == GeneratorType.MAPPER_XML && isPostgresDatabase();
    }

    @Override
    protected String getPackageSuffix() {
        return "mapper";
    }

    @Override
    protected String getSubDir() {
        return "mapper";
    }

    @Override
    protected String getClassNameSuffix() {
        return "Mapper";
    }

    @Override
    protected String getFileExtension() {
        return ".xml";
    }

    /**
     * 判断是否为 PostgreSQL 数据库
     */
    private boolean isPostgresDatabase() {
        // 通过驱动类名判断数据库类型
        try {
            if (environment == null) {
                log.debug("Environment is null, returning false for PostgreSQL check");
                return false;
            }
            var driverClassName = environment.getProperty("spring.datasource.driver-class-name", "");
            log.debug("Checking PostgreSQL: driverClassName={}", driverClassName);
            boolean isPostgres = "org.postgresql.Driver".equals(driverClassName);
            log.debug("PostgreSQL check result: {}", isPostgres);
            return isPostgres;
        } catch (Exception e) {
            log.error("Error checking PostgreSQL database type", e);
            return false;
        }
    }

    @Override
    protected String doGenerate(GeneratorContext context) {
        StringBuilder xml = new StringBuilder();

        xml.append(buildXmlDeclaration(context));
        xml.append(buildMapperElement(context));

        return xml.toString().trim() + "\n";
    }

    /**
     * 构建 XML 声明和 DOCTYPE
     */
    private String buildXmlDeclaration(GeneratorContext context) {
        return """
                <?xml version="1.0" encoding="UTF-8" ?>
                <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
                """;
    }

    /**
     * 构建 mapper 根元素及内容
     */
    private String buildMapperElement(GeneratorContext context) {
        String className = toClassName(context.getTableName());
        String mapperName = className + "Mapper";
        String mapperPackage = context.getConfig().getPackageName() + ".mapper";

        StringBuilder mapperElement = new StringBuilder();
        mapperElement.append(String.format("<mapper namespace=\"%s.%s\">\n", mapperPackage, mapperName));

        // 添加各个 SQL 映射节点
//        mapperElement.append(buildGetTotalSelect(context));
//        mapperElement.append(buildListResultSelect(context));
        mapperElement.append(buildInsertStatement(context));
        mapperElement.append(buildUpdateStatement(context));
        mapperElement.append(buildGetByIdSelect(context));
        mapperElement.append(buildDeleteByIdStatement(context));

        mapperElement.append("\n</mapper>");

        return mapperElement.toString();
    }

    /**
     * 构建 getTotal 查询（返回总记录数）
     */
    private String buildGetTotalSelect(GeneratorContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("    <!-- 返回分页查询结果总记录数 -->\n");
        sb.append("    <select id=\"getQueryCount\" resultType=\"java.lang.Long\">\n");
        sb.append("        SELECT count(*)\n");
        sb.append("        FROM ").append(getTableNameWithSchema(context)).append("\n");
        sb.append("        WHERE deleted = false\n");
        sb.append("          AND status = 'ENABLED'\n");
        sb.append("    </select>\n");
        return sb.toString();
    }

    /**
     * 构建 listResult 查询（分页查询结果）
     */
    private String buildListResultSelect(GeneratorContext context) {
        String entityPackage = context.getConfig().getPackageName() + ".model.entity";
        String className = toClassName(context.getTableName());

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("    <!-- 返回分页查询结果 -->\n");
        sb.append("    <select id=\"listQueryResult\" resultType=\"").append(entityPackage).append(".").append(className).append("\">\n");
        sb.append("        SELECT *\n");
        sb.append("        FROM ").append(getTableNameWithSchema(context)).append("\n");
        sb.append("        WHERE deleted = false\n");
        sb.append("        AND status = 'ENABLED'\n");
        sb.append("        <if test=\"sorts != null and sorts.size() > 0\">\n");
        sb.append("            ORDER BY\n");
        sb.append("            <foreach collection=\"sorts\" item=\"sort\" separator=\", \">\n");
        sb.append("                <choose>\n");
        sb.append("                    <when test=\"sort.field == 'created_time'\">created_time</when>\n");
        sb.append("                    <otherwise>id</otherwise>\n");
        sb.append("                </choose>\n");
        sb.append("                <choose>\n");
        sb.append("                    <when test=\"sort.order == 'asc'\">ASC</when>\n");
        sb.append("                    <otherwise>DESC</otherwise>\n");
        sb.append("                </choose>\n");
        sb.append("            </foreach>\n");
        sb.append("        </if>\n");
        sb.append("        LIMIT #{pageSize} OFFSET #{offset}\n");
        sb.append("    </select>\n");
        return sb.toString();
    }

    /**
     * 构建 insert 语句 - PostgreSQL 版本
     */
    private String buildInsertStatement(GeneratorContext context) {
        String entityPackage = context.getConfig().getPackageName() + ".model.entity";
        String className = toClassName(context.getTableName());

        // insert 时忽略更新相关字段和删除相关字段
        List<String> insertColumns = context.getColumns().stream()
                .map(ColumnDTO::getColumnName)
                .filter(col -> shouldIncludeFieldInInsert(col))
                .toList();

        // 找到最长字段名，用于对齐
        int maxColLength = insertColumns.stream()
                .map(String::length)
                .max(Comparator.naturalOrder())
                .orElse(0);

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("    <!-- 插入记录 -->\n");
        sb.append("    <insert id=\"insert\" parameterType=\"")
                .append(entityPackage).append(".").append(className).append("\" useGeneratedKeys=\"true\" keyProperty=\"id\">\n");
        String tableName = getTableNameWithSchema(context);
        // "        INSERT INTO " = 20 chars, " (" = 2 chars，合计与首字段对齐所需缩进
        String columnIndent = " ".repeat(20 + tableName.length() + 2);
        sb.append("        INSERT INTO ").append(tableName).append(" (");

        // 构建字段列表
        for (int i = 0; i < insertColumns.size(); i++) {
            String column = insertColumns.get(i);
            boolean isLast = (i == insertColumns.size() - 1);

            if (i == 0) {
                // 第一个字段紧跟在 ( 后面
                sb.append(column);
            } else {
                // 后续字段与第一个字段动态对齐
                sb.append("\n").append(columnIndent).append(column);
            }

            if (!isLast) {
                sb.append(",");
            } else {
                // 最后一个字段后面加 )
                sb.append(")");
            }
        }

        sb.append("\n        VALUES (");

        // 构建值列表
        for (int i = 0; i < insertColumns.size(); i++) {
            String column = insertColumns.get(i);
            String javaField = toJavaFieldName(column);
            boolean isLast = (i == insertColumns.size() - 1);

            if (i == 0) {
                // 第一个值紧跟在 ( 后面
                sb.append("#{").append(javaField).append("}");
            } else {
                // 后续值与第一个值对齐（16 个空格）
                sb.append("\n                #{").append(javaField).append("}");
            }

            if (!isLast) {
                sb.append(",");
            } else {
                // 最后一个值后面加 )
                sb.append(")");
            }
        }

        sb.append("\n    </insert>\n");
        return sb.toString();
    }

    /**
     * 构建 update 语句
     */
    private String buildUpdateStatement(GeneratorContext context) {
        String entityPackage = context.getConfig().getPackageName() + ".model.entity";
        String className = toClassName(context.getTableName());

        // update 时忽略创建相关字段和删除相关字段
        List<String> updateColumns = context.getColumns().stream()
                .map(ColumnDTO::getColumnName)
                .filter(col -> shouldIncludeFieldInUpdate(col))
                .toList();

        // 找到最长字段名，用于等号对齐
        int maxColLength = updateColumns.stream()
                .map(String::length)
                .max(Comparator.naturalOrder())
                .orElse(0);

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("    <!-- 更新记录 -->\n");
        sb.append("    <update id=\"update\" parameterType=\"")
                .append(entityPackage).append(".").append(className).append("\">\n");
        sb.append("        UPDATE ").append(getTableNameWithSchema(context)).append("\n");
        sb.append("        SET ");

        // 构建更新字段列表，保持等号对齐
        for (int i = 0; i < updateColumns.size(); i++) {
            String column = updateColumns.get(i);
            String javaField = toJavaFieldName(column);
            int padding = maxColLength - column.length();
            boolean isLast = (i == updateColumns.size() - 1);

            if (i > 0) {
                // 换行并对齐到 SET 后的位置（12 个空格）
                sb.append("\n            ");
            }

            sb.append(column);
            sb.append(" ".repeat(padding));
            sb.append(" = #{").append(javaField).append("}");
            if (!isLast) {
                sb.append(",");
            }
        }

        sb.append("\n");
        sb.append("        WHERE id = #{id}\n");
        sb.append("    </update>\n");
        return sb.toString();
    }

    /**
     * 构建 getById 查询
     */
    private String buildGetByIdSelect(GeneratorContext context) {
        String entityPackage = context.getConfig().getPackageName() + ".model.entity";
        String className = toClassName(context.getTableName());

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("    <!-- 根据 id 返回记录 -->\n");
        sb.append("    <select id=\"findById\" resultType=\"")
                .append(entityPackage).append(".").append(className).append("\">\n");
        sb.append("        SELECT *\n");
        sb.append("        FROM ").append(getTableNameWithSchema(context)).append("\n");
        sb.append("        WHERE id = #{id}\n");
        sb.append("        LIMIT 1\n");
        sb.append("    </select>\n");
        return sb.toString();
    }

    /**
     * 构建 deleteById 删除语句
     */
    private String buildDeleteByIdStatement(GeneratorContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("    <!-- 根据 id 删除记录 -->\n");
        sb.append("    <delete id=\"deleteById\">\n");
        sb.append("        DELETE\n");
        sb.append("        FROM ").append(getTableNameWithSchema(context)).append("\n");
        sb.append("        WHERE id = #{id}\n");
        sb.append("    </delete>\n");
        return sb.toString();
    }

    /**
     * 获取带 schema 的表名
     */
    private String getTableNameWithSchema(GeneratorContext context) {
        // 使用默认 public schema，不指定 schema 前缀
        return context.getTableName();
    }

    /**
     * 转换为 Java 字段名（下划线转驼峰）
     */
    private String toJavaFieldName(String columnName) {
        if (columnName == null || columnName.isEmpty()) {
            return "";
        }

        // 按下划线分割转小驼峰
        String[] parts = columnName.split("_");
        StringBuilder fieldName = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) {
                continue;
            }
            if (i == 0) {
                // 第一部分小写
                fieldName.append(part.toLowerCase());
            } else {
                // 后续部分首字母大写
                fieldName.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1).toLowerCase());
            }
        }
        return fieldName.toString();
    }

    /**
     * 判断插入语句中是否应该包含该字段
     * 忽略：updater, updater_id, updated_time, deleted, deleted_id, deleted_time
     */
    private boolean shouldIncludeFieldInInsert(String columnName) {
        return switch (columnName) {
            case "id",
                 "update_by", "update_name", "update_time",
                 "deleted", "deleted_id", "deleted_time" -> false;
            default -> true;
        };
    }

    /**
     * 判断更新语句中是否应该包含该字段
     * 忽略：creator, creator_id, created_time, deleted, deleted_id, deleted_time
     */
    private boolean shouldIncludeFieldInUpdate(String columnName) {
        return switch (columnName) {
            case "id",
                 "create_by", "create_name", "create_time",
                 "deleted", "deleted_id", "deleted_time" -> false;
            default -> true;
        };
    }
}