package com.code.creator.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This is the class comment for the class {@link TableColumnInfoDTO}.
 *
 * @author zy.wu.andy@gmail.com
 * @since 2025-02-08 13:57:00
 */
@Getter
@Setter
public class TableColumnInfoDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -900481599782283383L;

    private String tableName;
    private String tableComment;
    private String columnName;
    private String columnDefault;
    private String columnComment;
    private String dataType;
    private String isNullable;
    private Integer characterMaximumLength;
    private Integer numericPrecision;
    private Integer numericScale;

    private String parse2JavaType(String dataType) {
        return switch (dataType) {
            case "char", "varchar", "text", "character varying", "uuid" -> String.class.getName();
            case "bigint" -> Long.class.getName();
            case "boolean" -> Boolean.class.getName();
            case "tinyint", "integer" -> Integer.class.getName();
            case "decimal", "numeric" -> BigDecimal.class.getName();
            case "timestamp" -> LocalDateTime.class.getName();
            case "timestamp without time zone", "timestamp with time zone" -> Instant.class.getName();
            case "date" -> LocalDate.class.getName();
            default -> Object.class.getName();
        };
    }

    public String getDataType() {
        return parse2JavaType(this.dataType).replaceAll("^.*\\.", "");
    }

    public String getJavaClassName() {
        return Arrays.stream(this.tableName.replaceAll("^t_", "").split("_")).map(StringUtils::capitalize).collect(Collectors.joining());
    }

    public String getJavaFieldName() {
        return StringUtils.uncapitalize(Arrays.stream(this.columnName.split("_")).map(StringUtils::capitalize).collect(Collectors.joining()));
    }
}