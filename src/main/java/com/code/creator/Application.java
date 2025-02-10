package com.code.creator;

import com.code.creator.config.CreatorConfig;
import com.code.creator.service.DatabaseService;
import com.code.creator.service.EntityService;
import com.code.creator.service.RepositoryService;
import com.code.creator.service.ServiceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(CreatorConfig.class)
public class Application {

    @Resource
    private CreatorConfig creatorConfig;

    @Resource
    private DatabaseService databaseService;

    @Resource
    private EntityService entityService;

    @Resource
    private ServiceService serviceService;

    @Resource
    private RepositoryService repositoryService;

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).beanNameGenerator(new DefaultBeanNameGenerator()).run(args);
    }

    @EventListener
    public void listen(ApplicationReadyEvent applicationReadyEvent) throws Exception {
        entityService.createJPAEntity();
        serviceService.createJPAService();
        serviceService.createJPAServiceImpl();
        repositoryService.createJPARepository();

//        parse2Dao();
//        parse2Mapper();
        System.exit(0);
    }

//    /*解析处理(生成Dao&&DaoImpl类)*/
//    private void parse2Dao() throws Exception {
//        var template = new ClassPathResource("template/java_dao.template").getContentAsString(Charset.defaultCharset());
//        template = template.replace("${packageName}", creatorConfig.getModule().getPackageName());
//        template = template.replace("${project}", creatorConfig.getProject());
//        template = template.replace("${author}", creatorConfig.getAuthor());
//        template = template.replace("${create_at}", getNow());
//        template = template.replace("${entity_class}", this.splitToCamelCase(creatorConfig.getTable()));
//        Files.writeString(Files.createDirectories(Paths.get(STR."\{creatorConfig.getModule().getFullName()}/src/main/java/\{creatorConfig.getModule().getPackageName().replaceAll("\\.", "/")}/dao")).resolve(STR."\{this.splitToCamelCase(creatorConfig.getTable())}Dao.java"), template);
//
//        template = new ClassPathResource("template/java_dao_impl.template").getContentAsString(Charset.defaultCharset());
//        template = template.replace("${packageName}", creatorConfig.getModule().getPackageName());
//        template = template.replace("${project}", creatorConfig.getProject());
//        template = template.replace("${author}", creatorConfig.getAuthor());
//        template = template.replace("${create_at}", getNow());
//        template = template.replace("${entity_class}", this.splitToCamelCase(creatorConfig.getTable()));
//        template = template.replace("${entity_name}", this.uncapitalize(this.splitToCamelCase(creatorConfig.getTable())));
//        Files.writeString(Files.createDirectories(Paths.get(STR."\{creatorConfig.getModule().getFullName()}/src/main/java/\{creatorConfig.getModule().getPackageName().replaceAll("\\.", "/")}/dao/impl")).resolve(STR."\{this.splitToCamelCase(creatorConfig.getTable())}DaoImpl.java"), template);
//    }
//
//    /*生成Mapper.class和Mapper.xml文件*/
//    private void parse2Mapper() throws Exception {
//        var template = new ClassPathResource("template/java_mapper.template").getContentAsString(Charset.defaultCharset());
//        template = template.replace("${packageName}", creatorConfig.getModule().getPackageName());
//        template = template.replace("${project}", creatorConfig.getProject());
//        template = template.replace("${author}", creatorConfig.getAuthor());
//        template = template.replace("${create_at}", getNow());
//        template = template.replace("${entity_class}", this.splitToCamelCase(creatorConfig.getTable()));
//        Files.writeString(Files.createDirectories(Paths.get(STR."\{creatorConfig.getModule().getFullName()}/src/main/java/\{creatorConfig.getModule().getPackageName().replaceAll("\\.", "/")}/mapper")).resolve(STR."\{this.splitToCamelCase(creatorConfig.getTable())}Mapper.java"), template);
//
//        template = new ClassPathResource("template/java_mapper_xml.template").getContentAsString(Charset.defaultCharset());
//        template = template.replace("${packageName}", creatorConfig.getModule().getPackageName());
//        template = template.replace("${table_name}", creatorConfig.getDatabase() + "." + creatorConfig.getTable());
//        template = template.replace("${entity_class}", this.splitToCamelCase(creatorConfig.getTable()));
//
//        var columnList = databaseService.listBy(creatorConfig.getTable(), creatorConfig.getDatabase());
//        template = template.replace("${insert_mysql_field}", columnList.stream().map(c -> c.get("COLUMN_NAME")).filter(s -> !creatorConfig.getInsertIgnoreField().contains(s)).collect(Collectors.joining(", ")));
//        template = template.replace("${insert_mybatis_field}", columnList.stream().map(c -> c.get("COLUMN_NAME")).filter(s -> !creatorConfig.getInsertIgnoreField().contains(s)).map(field -> "#{" + uncapitalize(splitToCamelCase(field)) + "}").collect(Collectors.joining(", ")));
//        var maximumFieldLength = columnList.stream().map(c -> c.get("COLUMN_NAME")).filter(s -> !creatorConfig.getUpdateIgnoreField().contains(s)).map(String::length).max(Comparator.naturalOrder()).orElse(0);
//        template = template.replace("${update_field}", columnList.stream().map(c -> c.get("COLUMN_NAME")).filter(s -> !creatorConfig.getUpdateIgnoreField().contains(s)).map(field -> field + String.format("%" + (maximumFieldLength - field.length() + 1) + "s", " ") + "= #{" + uncapitalize(splitToCamelCase(field)) + "}").collect(Collectors.joining(",\n\t\t\t")));
//        Files.writeString(Files.createDirectories(Paths.get(STR."\{creatorConfig.getModule().getFullName()}/src/main/java/\{creatorConfig.getModule().getPackageName().replaceAll("\\.", "/")}/mapper")).resolve(STR."\{this.splitToCamelCase(creatorConfig.getTable())}Mapper.xml"), template);
//    }

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