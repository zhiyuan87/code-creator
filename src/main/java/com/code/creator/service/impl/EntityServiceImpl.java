package com.code.creator.service.impl;

import com.code.creator.config.CreatorConfig;
import com.code.creator.service.DatabaseService;
import com.code.creator.service.EntityService;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.UUID;

/**
 * This is the class comment for the class {@link EntityServiceImpl}.
 *
 * @author zy.wu.andy@gmail.com
 * @since 2025-02-08 11:12:00
 */
@Slf4j
@Service
public class EntityServiceImpl implements EntityService {
    @Resource
    private CreatorConfig creatorConfig;

    @Resource
    private DatabaseService databaseService;

    @Override
    public void createEntity() throws Exception {
//        var template = new ClassPathResource("template/java_entity.template").getContentAsString(Charset.defaultCharset());
//        template = template.replace("${packageName}", creatorConfig.getModule().getPackageName());
//        template = template.replace("${author}", creatorConfig.getAuthor());
//        template = template.replace("${create_at}", getNow());
//        template = template.replace("${entity_class}", this.splitToCamelCase(creatorConfig.getTable()));
//        template = template.replace("${serial_id}", String.valueOf(UUID.randomUUID().getMostSignificantBits()));
//
//        template = template.replace("${table_name}", creatorConfig.getTable());
//        template = template.replace("${table_comment}", databaseService.getBy(creatorConfig.getTable(), creatorConfig.getDatabase()).get("TABLE_COMMENT"));
//
//        var columnList = databaseService.listBy(creatorConfig.getTable(), creatorConfig.getDatabase());
//        template = template.replace("${import}", columnList.stream().map(column -> parse2JavaType(column.get("DATA_TYPE"))).filter(a -> a.matches("^java\\.(math|time).*")).distinct().sorted().map(a -> "import " + a + ";").collect(Collectors.joining("\n")));
//        template = template.replace("${fields}", columnList.stream().map(column -> String.format("/**\n\t * %s\n\t */\n\tprivate %s %s;", column.get("COLUMN_COMMENT"), parse2JavaType(column.get("DATA_TYPE")).replaceAll("^.*\\.", ""), uncapitalize(splitToCamelCase(column.get("COLUMN_NAME"))))).collect(Collectors.joining("\n\n\t")));
//
//        Files.writeString(Files.createDirectories(Paths.get(STR."\{creatorConfig.getModule().getFullName()}/src/main/java/\{creatorConfig.getModule().getPackageName().replaceAll("\\.", "/")}/model/entity")).resolve(STR."\{this.splitToCamelCase(creatorConfig.getTable())}.java"), template);
    }

    @Override
    public void createJPAEntity() throws Exception {
        var tableColumnInfoList = databaseService.getPGTableInfo(creatorConfig.getTable(), creatorConfig.getDatabase());
        tableColumnInfoList.forEach(o -> log.debug("{}", new Gson().toJson(o)));

        boolean hasBigDecimalField = false;
        boolean hasInstantField = false;
        boolean hasLocalDateField = false;

        var fieldStringList = new ArrayList<String>();
        for (var tableColumnInfo : tableColumnInfoList) {
            var columnName = tableColumnInfo.getColumnName();
            if (creatorConfig.getEntityIgnoreField().contains(columnName)) {
                continue;
            }
            var fieldString = new StringBuilder();

            var javaFieldType = tableColumnInfo.getDataType();
            if (javaFieldType.equals("BigDecimal")) {
                hasBigDecimalField = true;
            } else if (javaFieldType.equals("Instant")) {
                hasInstantField = true;
            } else if (javaFieldType.equals("LocalDate")) {
                hasLocalDateField = true;
            }

            // ADD @Comment
            var columnComment = tableColumnInfo.getColumnComment();
            if (StringUtils.isNotBlank(columnComment)) {
                fieldString.append(String.format("\t@Comment(\"%s\")\n", columnComment));
            }

            // ADD @Column
            fieldString.append("\t@Column(name = \"").append(columnName).append("\"");
            if (tableColumnInfo.getIsNullable().equals("NO")) {
                fieldString.append(", nullable = false");
            }
            if (javaFieldType.equals("String")) {
                var characterMaximumLength = tableColumnInfo.getCharacterMaximumLength();
                if (characterMaximumLength != null && characterMaximumLength != 255) {
                    fieldString.append(", length = ").append(characterMaximumLength);
                }
            } else if (javaFieldType.equals("BigDecimal")) {
                var numericPrecision = tableColumnInfo.getNumericPrecision();
                if (numericPrecision != null && numericPrecision != 0) {
                    fieldString.append(", precision = ").append(numericPrecision);
                }
                var numericScale = tableColumnInfo.getNumericScale();
                if (numericScale != null && numericScale != 0) {
                    fieldString.append(", scale = ").append(numericScale);
                }
            }
            fieldString.append(")\n");

            // ADD Field
            fieldString.append(String.format("\tprivate %s %s", javaFieldType, tableColumnInfo.getJavaFieldName()));
            var columnDefault = tableColumnInfo.getColumnDefault();
            if (StringUtils.isNotBlank(columnDefault)) {
                if (javaFieldType.equals("String")) {
                    columnDefault = columnDefault.replace("::text", "").replace("::character varying", "").replace("'", "");
                    if (StringUtils.isBlank(columnDefault)) {
                        if (tableColumnInfo.getIsNullable().equals("YES")) {
                            fieldString.append(" = \"\"");
                        }
                    } else {
                        fieldString.append(" = ").append("\"").append(columnDefault).append("\"");
                    }
                } else if (javaFieldType.equals("Integer")) {
                    fieldString.append(" = ").append(columnDefault);
                } else if (javaFieldType.equals("Boolean")) {
                    fieldString.append(" = ").append(columnDefault);
                } else if (javaFieldType.equals("LocalDate") && columnDefault.equals("CURRENT_DATE")) {
                    fieldString.append(" = LocalDate.now()");
                }
            }
            fieldString.append(";\n");

            fieldStringList.add(fieldString.toString());
        }

        var template = new StringBuilder();
        template.append(String.format("package %s;\n\n", creatorConfig.getEntityPackage()));
        template.append(String.format("import %s.BaseEntity;\n", creatorConfig.getBaseEntityPackage()));
        template.append("import jakarta.persistence.Column;\n");
        template.append("import jakarta.persistence.Entity;\n");
        template.append("import jakarta.persistence.Table;\n");
        template.append("import lombok.Getter;\n");
        template.append("import lombok.Setter;\n");
        template.append("import org.hibernate.annotations.Comment;\n\n");
        template.append("import java.io.Serial;\n");
        if (hasBigDecimalField) {
            template.append("import java.math.BigDecimal;\n");
        }
        if (hasInstantField) {
            template.append("import java.time.Instant;\n");
        }
        if (hasLocalDateField) {
            template.append("import java.time.LocalDate;\n");
        }
        template.append("\n");
        template.append("@Getter\n");
        template.append("@Setter\n");
        template.append("@Entity\n");
        template.append(String.format("@Table(name = \"%s\")\n", tableColumnInfoList.getFirst().getTableName()));
        template.append(String.format("@Comment(\"%s\")\n", tableColumnInfoList.getFirst().getTableComment()));
        template.append(String.format("public class %s extends BaseEntity {\n", tableColumnInfoList.getFirst().getJavaClassName()));
        template.append("\t@Serial\n");
        template.append(String.format("\tprivate static final long serialVersionUID = %sL;\n\n", UUID.randomUUID().getMostSignificantBits()));
        template.append(String.join("\n", fieldStringList));
        template.append("}\n");
        log.debug("\n{}", template);

        try (var writer = new FileWriter(creatorConfig.getEntitySavePath() + File.separator + tableColumnInfoList.getFirst().getJavaClassName() + ".java")) {
            writer.write(template.toString());
        }
    }
}
