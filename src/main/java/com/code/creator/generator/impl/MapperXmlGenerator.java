package com.code.creator.generator.impl;

import com.code.creator.generator.AbstractCodeGenerator;
import com.code.creator.generator.GeneratorContext;
import com.code.creator.model.dto.ColumnDTO;
import org.apache.commons.lang3.Strings;

import java.util.Comparator;
import java.util.List;

/**
 * Mapper XML 通用代码生成器。
 */
public abstract class MapperXmlGenerator extends AbstractCodeGenerator {

    @Override
    protected final String getPackageSuffix() {
        return "mapper";
    }

    @Override
    protected final String getSubDir() {
        return "mapper";
    }

    @Override
    protected final String getClassNameSuffix() {
        return "Mapper";
    }

    @Override
    protected final String getFileExtension() {
        return ".xml";
    }

    @Override
    protected final String doGenerate(GeneratorContext context) {
        return (buildXmlDeclaration() + buildMapperElement(context)).trim() + "\n";
    }

    private String buildXmlDeclaration() {
        return """
                <?xml version="1.0" encoding="UTF-8" ?>
                <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
                """;
    }

    private String buildMapperElement(GeneratorContext context) {
        String className = toClassName(context.getTableName());
        String mapperName = className + "Mapper";
        String mapperPackage = context.getConfig().getPackageName() + ".mapper";

        StringBuilder mapperElement = new StringBuilder();
        mapperElement.append(String.format("<mapper namespace=\"%s.%s\">\n", mapperPackage, mapperName));

        if (shouldGeneratePaginationStatements()) {
            mapperElement.append(buildGetTotalSelect(context));
            mapperElement.append(buildListResultSelect(context));
        }
        mapperElement.append(buildInsertStatement(context));
        mapperElement.append(buildUpdateStatement(context));
        mapperElement.append(buildGetByIdSelect(context));
        mapperElement.append(buildDeleteByIdStatement(context));
        mapperElement.append("\n</mapper>");

        return mapperElement.toString();
    }

    /**
     * 是否生成分页查询语句。默认生成，数据库子类可以按需关闭。
     */
    protected boolean shouldGeneratePaginationStatements() {
        return false;
    }

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

    private String buildListResultSelect(GeneratorContext context) {
        String entityPackage = getEntityPackage(context);
        String className = toClassName(context.getTableName());

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("    <!-- 返回分页查询结果 -->\n");
        sb.append("    <select id=\"listQueryResult\" resultType=\"")
                .append(entityPackage).append(".").append(className).append("\">\n");
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

    private String buildInsertStatement(GeneratorContext context) {
        String entityPackage = getEntityPackage(context);
        String className = toClassName(context.getTableName());
        List<String> insertColumns = context.getColumns().stream()
                .map(ColumnDTO::getColumnName)
                .filter(this::shouldIncludeFieldInInsert)
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("    <!-- 插入记录 -->\n");
        sb.append("    <insert id=\"insert\" parameterType=\"")
                .append(entityPackage).append(".").append(className)
                .append("\" useGeneratedKeys=\"true\" keyProperty=\"id\">\n");

        String tableName = getTableNameWithSchema(context);
        String columnIndent = " ".repeat(20 + tableName.length() + 2);
        sb.append("        INSERT INTO ").append(tableName).append(" (");
        appendInsertColumns(sb, insertColumns, columnIndent);

        sb.append("\n        VALUES (");
        appendInsertValues(sb, insertColumns);
        sb.append("\n    </insert>\n");
        return sb.toString();
    }

    private void appendInsertColumns(StringBuilder sb, List<String> columns, String indent) {
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                sb.append("\n").append(indent);
            }
            sb.append(columns.get(i));
            sb.append(i < columns.size() - 1 ? "," : ")");
        }
    }

    private void appendInsertValues(StringBuilder sb, List<String> columns) {
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                sb.append("\n                ");
            }
            sb.append("#{").append(toFieldName(columns.get(i))).append("}");
            sb.append(i < columns.size() - 1 ? "," : ")");
        }
    }

    private String buildUpdateStatement(GeneratorContext context) {
        String entityPackage = getEntityPackage(context);
        String className = toClassName(context.getTableName());
        List<String> updateColumns = context.getColumns().stream()
                .map(ColumnDTO::getColumnName)
                .filter(this::shouldIncludeFieldInUpdate)
                .toList();
        boolean hasVersionColumn = updateColumns.contains("version");
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

        for (int i = 0; i < updateColumns.size(); i++) {
            String column = updateColumns.get(i);
            if (i > 0) {
                sb.append("\n            ");
            }
            sb.append(column);
            sb.append(" ".repeat(maxColLength - column.length()));
            if ("version".equals(column)) {
                sb.append(" = version + 1");
            } else {
                sb.append(" = #{").append(toFieldName(column)).append("}");
            }
            if (i < updateColumns.size() - 1) {
                sb.append(",");
            }
        }

        sb.append("\n");
        sb.append("        WHERE id = #{id}\n");
        if (hasVersionColumn) {
            sb.append("          AND version = #{version}\n");
        }
        sb.append("    </update>\n");
        return sb.toString();
    }

    private String buildGetByIdSelect(GeneratorContext context) {
        String entityPackage = getEntityPackage(context);
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

    protected String getTableNameWithSchema(GeneratorContext context) {
        return context.getTableName();
    }

    private boolean shouldIncludeFieldInInsert(String columnName) {
        return !Strings.CS.equalsAny(columnName, "id", "update_by", "update_name", "update_time", "deleted", "deleted_id", "deleted_time");
    }

    private boolean shouldIncludeFieldInUpdate(String columnName) {
        return !Strings.CS.equalsAny(columnName, "id", "create_by", "create_name", "create_time", "deleted", "deleted_id", "deleted_time");
    }

    private String getEntityPackage(GeneratorContext context) {
        return context.getConfig().getPackageName() + ".model.entity";
    }

}
