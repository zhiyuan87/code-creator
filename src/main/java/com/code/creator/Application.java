package com.code.creator;

import com.code.creator.config.CreatorConfig;
import com.code.creator.service.DatabaseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(CreatorConfig.class)
public class Application {

    @Resource
    private CreatorConfig creatorConfig;

    @Resource
    private DatabaseService databaseService;

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).beanNameGenerator(new DefaultBeanNameGenerator()).run(args);
    }

    @EventListener
    public void listen(ApplicationReadyEvent applicationReadyEvent) throws Exception {
        parse2Entity();

        parse2Service();
        parse2Dao();
        parse2Mapper();
        System.exit(0);
    }

    /*解析处理(生成实体类主体代码)*/
    private void parse2Entity() throws Exception {
        var template = new ClassPathResource("template/java_entity.template").getContentAsString(Charset.defaultCharset());
        template = template.replace("${packageName}", creatorConfig.getModule().getPackageName());
        template = template.replace("${author}", creatorConfig.getAuthor());
        template = template.replace("${create_at}", getNow());
        template = template.replace("${entity_class}", this.splitToCamelCase(creatorConfig.getTable()));
        template = template.replace("${serial_id}", String.valueOf(UUID.randomUUID().getMostSignificantBits()));

        template = template.replace("${table_name}", creatorConfig.getTable());
        template = template.replace("${table_comment}", databaseService.getBy(creatorConfig.getTable(), creatorConfig.getDatabase()).get("TABLE_COMMENT"));

        var columnList = databaseService.listBy(creatorConfig.getTable(), creatorConfig.getDatabase());
        template = template.replace("${import}", columnList.stream().map(column -> parse2JavaType(column.get("DATA_TYPE"))).filter(a -> a.matches("^java\\.(math|time).*")).distinct().sorted().map(a -> "import " + a + ";").collect(Collectors.joining("\n")));
        template = template.replace("${fields}", columnList.stream().map(column -> String.format("/**\n\t * %s\n\t */\n\tprivate %s %s;", column.get("COLUMN_COMMENT"), parse2JavaType(column.get("DATA_TYPE")).replaceAll("^.*\\.", ""), uncapitalize(splitToCamelCase(column.get("COLUMN_NAME"))))).collect(Collectors.joining("\n\n\t")));

        Files.writeString(Files.createDirectories(Paths.get(STR."\{creatorConfig.getModule().getFullName()}/src/main/java/\{creatorConfig.getModule().getPackageName().replaceAll("\\.", "/")}/model/entity")).resolve(STR."\{this.splitToCamelCase(creatorConfig.getTable())}.java"), template);
    }

    /*解析处理(生成Service&&ServiceImpl类)*/
    private void parse2Service() throws Exception {
        var template = new ClassPathResource("template/java_service.template").getContentAsString(Charset.defaultCharset());
        template = template.replace("${packageName}", creatorConfig.getModule().getPackageName());
        template = template.replace("${project}", creatorConfig.getProject());
        template = template.replace("${author}", creatorConfig.getAuthor());
        template = template.replace("${create_at}", getNow());
        template = template.replace("${entity_class}", this.splitToCamelCase(creatorConfig.getTable()));
        Files.writeString(Files.createDirectories(Paths.get(STR."\{creatorConfig.getModule().getFullName()}/src/main/java/\{creatorConfig.getModule().getPackageName().replaceAll("\\.", "/")}/service")).resolve(STR."\{this.splitToCamelCase(creatorConfig.getTable())}Service.java"), template);

        template = new ClassPathResource("template/java_service_impl.template").getContentAsString(Charset.defaultCharset());
        template = template.replace("${packageName}", creatorConfig.getModule().getPackageName());
        template = template.replace("${project}", creatorConfig.getProject());
        template = template.replace("${author}", creatorConfig.getAuthor());
        template = template.replace("${create_at}", getNow());
        template = template.replace("${entity_class}", this.splitToCamelCase(creatorConfig.getTable()));
        template = template.replace("${entity_name}", this.uncapitalize(this.splitToCamelCase(creatorConfig.getTable())));
        Files.writeString(Files.createDirectories(Paths.get(STR."\{creatorConfig.getModule().getFullName()}/src/main/java/\{creatorConfig.getModule().getPackageName().replaceAll("\\.", "/")}/service/impl")).resolve(STR."\{this.splitToCamelCase(creatorConfig.getTable())}ServiceImpl.java"), template);
    }

    /*解析处理(生成Dao&&DaoImpl类)*/
    private void parse2Dao() throws Exception {
        var template = new ClassPathResource("template/java_dao.template").getContentAsString(Charset.defaultCharset());
        template = template.replace("${packageName}", creatorConfig.getModule().getPackageName());
        template = template.replace("${project}", creatorConfig.getProject());
        template = template.replace("${author}", creatorConfig.getAuthor());
        template = template.replace("${create_at}", getNow());
        template = template.replace("${entity_class}", this.splitToCamelCase(creatorConfig.getTable()));
        Files.writeString(Files.createDirectories(Paths.get(STR."\{creatorConfig.getModule().getFullName()}/src/main/java/\{creatorConfig.getModule().getPackageName().replaceAll("\\.", "/")}/dao")).resolve(STR."\{this.splitToCamelCase(creatorConfig.getTable())}Dao.java"), template);

        template = new ClassPathResource("template/java_dao_impl.template").getContentAsString(Charset.defaultCharset());
        template = template.replace("${packageName}", creatorConfig.getModule().getPackageName());
        template = template.replace("${project}", creatorConfig.getProject());
        template = template.replace("${author}", creatorConfig.getAuthor());
        template = template.replace("${create_at}", getNow());
        template = template.replace("${entity_class}", this.splitToCamelCase(creatorConfig.getTable()));
        template = template.replace("${entity_name}", this.uncapitalize(this.splitToCamelCase(creatorConfig.getTable())));
        Files.writeString(Files.createDirectories(Paths.get(STR."\{creatorConfig.getModule().getFullName()}/src/main/java/\{creatorConfig.getModule().getPackageName().replaceAll("\\.", "/")}/dao/impl")).resolve(STR."\{this.splitToCamelCase(creatorConfig.getTable())}DaoImpl.java"), template);
    }

    /*生成Mapper.class和Mapper.xml文件*/
    private void parse2Mapper() throws Exception {
        var template = new ClassPathResource("template/java_mapper.template").getContentAsString(Charset.defaultCharset());
        template = template.replace("${packageName}", creatorConfig.getModule().getPackageName());
        template = template.replace("${project}", creatorConfig.getProject());
        template = template.replace("${author}", creatorConfig.getAuthor());
        template = template.replace("${create_at}", getNow());
        template = template.replace("${entity_class}", this.splitToCamelCase(creatorConfig.getTable()));
        Files.writeString(Files.createDirectories(Paths.get(STR."\{creatorConfig.getModule().getFullName()}/src/main/java/\{creatorConfig.getModule().getPackageName().replaceAll("\\.", "/")}/mapper")).resolve(STR."\{this.splitToCamelCase(creatorConfig.getTable())}Mapper.java"), template);

        template = new ClassPathResource("template/java_mapper_xml.template").getContentAsString(Charset.defaultCharset());
        template = template.replace("${packageName}", creatorConfig.getModule().getPackageName());
        template = template.replace("${table_name}", creatorConfig.getDatabase() + "." + creatorConfig.getTable());
        template = template.replace("${entity_class}", this.splitToCamelCase(creatorConfig.getTable()));

        var columnList = databaseService.listBy(creatorConfig.getTable(), creatorConfig.getDatabase());
        template = template.replace("${insert_mysql_field}", columnList.stream().map(c -> c.get("COLUMN_NAME")).filter(s -> !creatorConfig.getInsertIgnoreField().contains(s)).collect(Collectors.joining(", ")));
        template = template.replace("${insert_mybatis_field}", columnList.stream().map(c -> c.get("COLUMN_NAME")).filter(s -> !creatorConfig.getInsertIgnoreField().contains(s)).map(field -> "#{" + uncapitalize(splitToCamelCase(field)) + "}").collect(Collectors.joining(", ")));
        var maximumFieldLength = columnList.stream().map(c -> c.get("COLUMN_NAME")).filter(s -> !creatorConfig.getUpdateIgnoreField().contains(s)).map(String::length).max(Comparator.naturalOrder()).orElse(0);
        template = template.replace("${update_field}", columnList.stream().map(c -> c.get("COLUMN_NAME")).filter(s -> !creatorConfig.getUpdateIgnoreField().contains(s)).map(field -> field + String.format("%" + (maximumFieldLength - field.length() + 1) + "s", " ") + "= #{" + uncapitalize(splitToCamelCase(field)) + "}").collect(Collectors.joining(",\n\t\t\t")));
        Files.writeString(Files.createDirectories(Paths.get(STR."\{creatorConfig.getModule().getFullName()}/src/main/java/\{creatorConfig.getModule().getPackageName().replaceAll("\\.", "/")}/mapper")).resolve(STR."\{this.splitToCamelCase(creatorConfig.getTable())}Mapper.xml"), template);
    }

    private String capitalize(final String str) {
        return StringUtils.capitalize(str);
    }

    private String uncapitalize(final String str) {
        return StringUtils.uncapitalize(str);
    }

    private String splitToCamelCase(final String str) {
        return Arrays.stream(StringUtils.splitByWholeSeparator(str.replaceFirst("^t_", ""), "_")).map(this::capitalize).collect(Collectors.joining());
    }

    private String parse2JavaType(String dataType) {
        return switch (dataType) {
            case "char", "varchar" -> String.class.getName();
            case "bigint" -> Long.class.getName();
            case "tinyint" -> Integer.class.getName();
            case "decimal" -> BigDecimal.class.getName();
            case "timestamp" -> LocalDateTime.class.getName();
            default -> Object.class.getName();
        };
    }

    public static String getNow() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now().withNano(0));
    }
}