package com.code.creator.generator.impl;

import com.code.creator.config.GeneratorConfig;
import com.code.creator.generator.AbstractCodeGenerator;
import com.code.creator.generator.GeneratorContext;
import com.code.creator.generator.GeneratorType;
import com.code.creator.model.dto.ColumnDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 实体类代码生成器
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Slf4j
@Component
public class EntityGenerator extends AbstractCodeGenerator {

    @Override
    public boolean supports(GeneratorType type) {
        return type == GeneratorType.ENTITY;
    }

    @Override
    protected String getPackageSuffix() {
        return "model.entity";
    }

    @Override
    protected String getSubDir() {
        return "model/entity";
    }

    @Override
    protected String getClassNameSuffix() {
        return "";
    }

    @Override
    protected String doGenerate(GeneratorContext context) {
        StringBuilder code = new StringBuilder();

        code.append(buildPackage(context));
        code.append(buildImports(context));
        code.append(buildClassDeclaration(context));
        code.append(buildFields(context));
        code.append("}\n");

        return code.toString();
    }

    /**
     * 将标准 SQL 类型转换为 Java 类型
     *
     * @param dataType 标准 SQL 类型（如 VARCHAR, BIGINT, TIMESTAMP）
     * @return Java 类型（如 String, Long, LocalDateTime）
     */
    private String getJavaType(String dataType) {
        if (dataType == null || dataType.isEmpty()) {
            return "String";
        }

        return switch (dataType.toUpperCase()) {
            // 字符串类型
            case "VARCHAR", "CHAR", "TEXT" -> "String";

            // 数值类型
            case "BIGINT" -> "Long";
            case "INTEGER" -> "Integer";
            case "SMALLINT" -> "Short";
            case "TINYINT" -> "Byte";
            case "DECIMAL", "NUMERIC" -> "BigDecimal";
            case "FLOAT" -> "Float";
            case "DOUBLE" -> "Double";

            // 布尔类型
            case "BOOLEAN" -> "Boolean";

            // 日期时间类型
            case "DATE" -> "LocalDate";
            case "TIME" -> "LocalTime";
            case "TIMESTAMP" -> "LocalDateTime";

            // 二进制类型
            case "BLOB" -> "byte[]";

            // JSON 类型
            case "JSON" -> "String";

            // 默认
            default -> "String";
        };
    }

    /**
     * 构建包声明
     */
    private String buildPackage(GeneratorContext context) {
        return String.format("package %s;\n\n", getPackageName(context));
    }

    /**
     * 构建导入语句
     */
    private String buildImports(GeneratorContext context) {
        StringBuilder imports = new StringBuilder();

        GeneratorConfig config = context.getConfig();

        // 基础类导入
        imports.append(String.format("import %s.BaseEntity;\n",
                config.getBaseEntityPackage()));

        // Lombok 导入（使用 @Getter 和 @Setter）
        imports.append("import lombok.Getter;\n");
        imports.append("import lombok.Setter;\n\n");

        // Java 标准库导入
        imports.append("import java.io.Serial;\n");
        imports.append("import java.io.Serializable;\n");

        // 根据字段类型添加导入
        context.getColumns().stream()
                .filter(column -> !shouldIgnoreFieldInEntity(column.getColumnName()))
                .map(column -> getJavaType(column.getDataType()))
                .distinct()
                .forEach(type -> {
                    switch (type) {
                        case "BigDecimal" -> imports.append("import java.math.BigDecimal;\n");
                        case "LocalDate" -> imports.append("import java.time.LocalDate;\n");
                        case "LocalTime" -> imports.append("import java.time.LocalTime;\n");
                        case "LocalDateTime" -> imports.append("import java.time.LocalDateTime;\n");
                    }
                });

        imports.append("\n");
        return imports.toString();
    }

    /**
     * 构建类声明
     */
    private String buildClassDeclaration(GeneratorContext context) {
        String className = toClassName(context.getTableName());

        StringBuilder declaration = new StringBuilder();

        // 类注释
        declaration.append("/**\n");
        declaration.append(String.format(" * Entity class for table {@code %s}.\n", context.getTableName()));
        if (context.getTableComment() != null && !context.getTableComment().isEmpty()) {
            declaration.append(String.format(" * %s\n", context.getTableComment()));
        }
        declaration.append(" *\n");
        declaration.append(String.format(" * @author %s\n", context.getConfig().getAuthor()));
        declaration.append(String.format(" * @since %s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        declaration.append(" */\n");

        // Lombok 注解（使用 @Getter 和 @Setter）
        declaration.append("@Getter\n");
        declaration.append("@Setter\n");

        // 类声明
        declaration.append(String.format("public class %s extends BaseEntity implements Serializable {\n", className));

        return declaration.toString();
    }

    /**
     * 构建字段
     */
    private String buildFields(GeneratorContext context) {
        StringBuilder fields = new StringBuilder();

        // serialVersionUID（使用 UUID 生成）
        fields.append("\t@Serial\n");
        fields.append(String.format("\tprivate static final long serialVersionUID = %dL;\n\n", generateSerialVersionUID()));

        // 字段声明
        context.getColumns().stream()
                .filter(column -> !shouldIgnoreFieldInEntity(column.getColumnName()))
                .forEach(column -> {
                    log.debug("Generating field {}", column);
                    // 字段注释
                    if (column.getColumnComment() != null) {
                        fields.append(String.format("\t/**\n\t * %s\n\t */\n",
                                column.getColumnComment()));
                    }

                    // 字段声明（含默认值）
                    fields.append(buildFieldWithDefault(column));
                });

        return fields.toString();
    }

    /**
     * 构建字段（含默认值）
     */
    private String buildFieldWithDefault(ColumnDTO column) {
        StringBuilder fieldString = new StringBuilder();
        String javaFieldType = getJavaType(column.getDataType());

        fieldString.append(String.format("\tprivate %s %s", javaFieldType, toFieldName(column.getColumnName())));

        var columnDefault = column.getColumnDefault();
        if (columnDefault != null && StringUtils.isNotBlank(columnDefault.toString())) {
            String defaultValue = columnDefault.toString();

            if (javaFieldType.equals("String")) {
                // 处理 PostgreSQL 类型转换标记
                defaultValue = defaultValue.replace("::text", "")
                        .replace("::character varying", "")
                        .replace("'", "");

                if (StringUtils.isBlank(defaultValue)) {
                    if ("YES".equalsIgnoreCase(column.getIsNullable())) {
                        fieldString.append(" = \"\"");
                    }
                } else if (defaultValue.equals("NULL")) {
                    // NULL 值不处理
                } else {
                    fieldString.append(" = \"").append(defaultValue).append("\"");
                }
            } else if (javaFieldType.equals("Integer")) {
                fieldString.append(" = ").append(defaultValue);
            } else if (javaFieldType.equals("Long")) {
                fieldString.append(" = ").append(defaultValue);
            } else if (javaFieldType.equals("Boolean")) {
                // 处理 Boolean 类型的默认值
                String boolValue = defaultValue.toLowerCase()
                        .replace("'", "")
                        .replace("::boolean", "");
                if ("1".equals(boolValue) || "true".equals(boolValue) || "t".equals(boolValue)) {
                    fieldString.append(" = true");
                } else if ("0".equals(boolValue) || "false".equals(boolValue) || "f".equals(boolValue)) {
                    fieldString.append(" = false");
                }
            } else if (javaFieldType.equals("BigDecimal")) {
                fieldString.append(" = new BigDecimal(\"").append(defaultValue).append("\")");
            } else if (javaFieldType.equals("LocalDate") && defaultValue.equals("CURRENT_DATE")) {
                fieldString.append(" = LocalDate.now()");
            } else if (javaFieldType.equals("LocalDateTime") && defaultValue.equals("CURRENT_TIMESTAMP")) {
                fieldString.append(" = LocalDateTime.now()");
            }
        }

        fieldString.append(";\n\n");
        return fieldString.toString();
    }

    /**
     * 生成 serialVersionUID（使用 UUID）
     */
    private long generateSerialVersionUID() {
        return UUID.randomUUID().getMostSignificantBits();
    }

    /**
     * 将下划线命名转换为驼峰命名
     *
     * @param columnName 数据库字段名（如 user_name）
     * @return Java 字段名（如 userName）
     */
    private String toFieldName(String columnName) {
        if (columnName == null || columnName.isEmpty()) {
            return "";
        }

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
     * 判断实体类中是否应该忽略该字段
     * 默认忽略：
     */
    private boolean shouldIgnoreFieldInEntity(String columnName) {
        return switch (columnName) {
            case "id", "create_by", "create_name", "create_time",
                 "update_by", "update_name", "update_time",
                 "deleted", "version", "remark" -> true;
            default -> false;
        };
    }
}
