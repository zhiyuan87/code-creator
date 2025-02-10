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
import java.util.UUID;
import java.util.stream.Collectors;

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

        var template = new StringBuilder();
        template.append(String.format("package %s;\n\n", creatorConfig.getEntityPackage()));
        template.append(String.format("import %s.BaseEntity;\n", creatorConfig.getBaseEntityPackage()));
        template.append("import jakarta.persistence.Column;\n");
        template.append("import jakarta.persistence.Entity;\n");
        template.append("import jakarta.persistence.Table;\n");
        template.append("import lombok.Getter;\n");
        template.append("import lombok.Setter;\n");
        template.append("import org.hibernate.annotations.Comment;\n\n");
        template.append("import java.io.Serial;\n\n");
        template.append("@Getter\n");
        template.append("@Setter\n");
        template.append("@Entity\n");
        template.append(String.format("@Table(name = \"%s\")\n", tableColumnInfoList.getFirst().getTableName()));
        template.append(String.format("@Comment(\"%s\")\n", tableColumnInfoList.getFirst().getTableComment()));
        template.append(String.format("public class %s extends BaseEntity {\n", tableColumnInfoList.getFirst().getJavaClassName()));
        template.append("\t@Serial\n");
        template.append(String.format("\tprivate static final long serialVersionUID = %sL;\n\n", UUID.randomUUID().getMostSignificantBits()));

        // ADD Fields
        var fieldString = tableColumnInfoList.parallelStream().filter(m -> creatorConfig.getEntityIgnoreField().stream().noneMatch(s -> s.equals(m.getColumnName()))).map(tableColumnInfo -> {
            var fieldString1 = new StringBuilder();

            // ADD @Comment
            if (StringUtils.isNotBlank(tableColumnInfo.getColumnComment())) {
                fieldString1.append(String.format("\t@Comment(\"%s\")\n", tableColumnInfo.getColumnComment()));
            }

            // ADD @Column
            fieldString1.append("\t@Column(name = \"").append(tableColumnInfo.getColumnName()).append("\"");
            if (tableColumnInfo.getIsNullable().equals("NO")) {
                fieldString1.append(", nullable = false");
            }
            if (tableColumnInfo.getCharacterMaximumLength() != null) {
                fieldString1.append(", length = ").append(tableColumnInfo.getCharacterMaximumLength());
            }
            fieldString1.append(")\n");

            // ADD Field
            fieldString1.append(String.format("\tprivate %s %s;\n", tableColumnInfo.getDataType(), tableColumnInfo.getJavaFieldName()));

            return fieldString1.toString();
        }).collect(Collectors.joining("\n"));
        template.append(fieldString);

        template.append("}\n");
        log.debug("\n{}", template);

        try (var writer = new FileWriter(creatorConfig.getEntitySavePath() + File.separator + tableColumnInfoList.getFirst().getJavaClassName() + ".java")) {
            writer.write(template.toString());
        }
    }
}
