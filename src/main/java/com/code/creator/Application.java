package com.code.creator;

import com.code.creator.utils.date.DateUtils;
import com.code.creator.service.DatabaseService;
import com.mysql.cj.MysqlType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class Application implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Resource
    private DatabaseService databaseService;

    private final String table = "t_user_item";
    private final String database = "danft";

    private final String author = "zhiyuan";
    private final List<String> insertIgnoreField = List.of("id", "updater", "updater_id", "updated_time");
    private final List<String> updateIgnoreField = List.of("id", "creator", "creator_id", "created_time");

    private final String module = "danft-common-service";
    private final String packages = "com.danft.common.user";

    private List<Map<String, String>> columnList;

    @PostConstruct
    void started() {
        columnList = databaseService.findColumnByDatabaseAndTable(database, table);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        parse2Entity();
        parse2Service();
        parse2Dao();
        parse2Mapper();
        System.exit(0);
    }

    /*解析处理(生成实体类主体代码)*/
    private void parse2Entity() throws Exception {
        var template = new ClassPathResource("template/java_entity.template").getContentAsString(Charset.defaultCharset());
        template = template.replace("${package}", packages);
        template = template.replace("${table_name}", table);
        template = template.replace("${table_desc}", databaseService.findTableByDatabaseAndTable(database, table).get("TABLE_COMMENT"));
        template = template.replace("${author}", author);
        template = template.replace("${create_at}", DateUtils.parse(LocalDateTime.now()));
        template = template.replace("${package_import}", columnList.stream().map(column -> parse2JavaType(column.get("COLUMN_TYPE"))).filter(a -> a.matches("^java\\.(math|time).*")).distinct().sorted().map(a -> "import " + a + ";").collect(Collectors.joining("\n")));
        template = template.replace("${entity_class}", this.splitToCamelCase(table));
        template = template.replace("${serial_id}", String.valueOf(UUID.randomUUID().getMostSignificantBits()));
        template = template.replace("${fields}", columnList.stream().map(column -> String.format("/**\n\t * %s\n\t */\n\tprivate %s %s;", column.get("COLUMN_COMMENT"), parse2JavaType(column.get("COLUMN_TYPE")).replaceAll("^.*\\.", ""), uncapitalize(splitToCamelCase(column.get("COLUMN_NAME"))))).collect(Collectors.joining("\n\n\t")));
        Files.writeString(Files.createDirectories(Paths.get(module + "/src/main/java/" + packages.replaceAll("\\.", "/") + "/model/entity")).resolve(this.splitToCamelCase(table) + ".java"), template);
    }

    /*解析处理(生成Service&&ServiceImpl类)*/
    private void parse2Service() throws Exception {
        var template = new ClassPathResource("template/java_service.template").getContentAsString(Charset.defaultCharset());
        template = template.replace("${package}", packages);
        template = template.replace("${author}", author);
        template = template.replace("${create_at}", DateUtils.parse(LocalDateTime.now()));
        template = template.replace("${entity_class}", this.splitToCamelCase(table));
        Files.writeString(Files.createDirectories(Paths.get(module + "/src/main/java/" + packages.replaceAll("\\.", "/") + "/service")).resolve(this.splitToCamelCase(table) + "Service.java"), template);

        template = new ClassPathResource("template/java_service_impl.template").getContentAsString(Charset.defaultCharset());
        template = template.replace("${package}", packages);
        template = template.replace("${author}", author);
        template = template.replace("${create_at}", DateUtils.parse(LocalDateTime.now()));
        template = template.replace("${entity_class}", this.splitToCamelCase(table));
        template = template.replace("${entity_name}", this.uncapitalize(this.splitToCamelCase(table)));
        Files.writeString(Files.createDirectories(Paths.get(module + "/src/main/java/" + packages.replaceAll("\\.", "/") + "/service/impl")).resolve(this.splitToCamelCase(table) + "ServiceImpl.java"), template);
    }

    /*解析处理(生成Dao&&DaoImpl类)*/
    private void parse2Dao() throws Exception {
        var template = new ClassPathResource("template/java_dao.template").getContentAsString(Charset.defaultCharset());
        template = template.replace("${package}", packages);
        template = template.replace("${author}", author);
        template = template.replace("${create_at}", DateUtils.parse(LocalDateTime.now()));
        template = template.replace("${entity_class}", this.splitToCamelCase(table));
        Files.writeString(Files.createDirectories(Paths.get(module + "/src/main/java/" + packages.replaceAll("\\.", "/") + "/dao")).resolve(this.splitToCamelCase(table) + "Dao.java"), template);

        template = new ClassPathResource("template/java_dao_impl.template").getContentAsString(Charset.defaultCharset());
        template = template.replace("${package}", packages);
        template = template.replace("${author}", author);
        template = template.replace("${create_at}", DateUtils.parse(LocalDateTime.now()));
        template = template.replace("${entity_class}", this.splitToCamelCase(table));
        template = template.replace("${entity_name}", this.uncapitalize(this.splitToCamelCase(table)));
        Files.writeString(Files.createDirectories(Paths.get(module + "/src/main/java/" + packages.replaceAll("\\.", "/") + "/dao/impl")).resolve(this.splitToCamelCase(table) + "DaoImpl.java"), template);
    }

    /*生成Mapper.class和Mapper.xml文件*/
    private void parse2Mapper() throws Exception {
        var template = new ClassPathResource("template/java_mapper.template").getContentAsString(Charset.defaultCharset());
        template = template.replace("${package}", packages);
        template = template.replace("${author}", author);
        template = template.replace("${create_at}", DateUtils.parse(LocalDateTime.now()));
        template = template.replace("${entity_class}", this.splitToCamelCase(table));
        Files.writeString(Files.createDirectories(Paths.get(module + "/src/main/java/" + packages.replaceAll("\\.", "/") + "/mapper")).resolve(this.splitToCamelCase(table) + "Mapper.java"), template);

        template = new ClassPathResource("template/java_mapper_xml.template").getContentAsString(Charset.defaultCharset());
        template = template.replace("${package}", packages);
        template = template.replace("${table_name}", database + "." + table);
        template = template.replace("${entity_class}", this.splitToCamelCase(table));
        template = template.replace("${insert_mysql_field}", columnList.stream().map(c -> c.get("COLUMN_NAME")).filter(s -> !insertIgnoreField.contains(s)).collect(Collectors.joining(", ")));
        template = template.replace("${insert_mybatis_field}", columnList.stream().map(c -> c.get("COLUMN_NAME")).filter(s -> !insertIgnoreField.contains(s)).map(field -> "#{" + uncapitalize(splitToCamelCase(field)) + "}").collect(Collectors.joining(", ")));
        var maximumFieldLength = columnList.stream().map(c -> c.get("COLUMN_NAME")).filter(s -> !updateIgnoreField.contains(s)).map(String::length).max(Comparator.naturalOrder()).orElse(0);
        template = template.replace("${update_field}", columnList.stream().map(c -> c.get("COLUMN_NAME")).filter(s -> !updateIgnoreField.contains(s)).map(field -> field + String.format("%" + (maximumFieldLength - field.length() + 1) + "s", " ") + "= #{" + uncapitalize(splitToCamelCase(field)) + "}").collect(Collectors.joining(",\n\t\t\t")));
        Files.writeString(Files.createDirectories(Paths.get(module + "/src/main/java/" + packages.replaceAll("\\.", "/") + "/mapper")).resolve(this.splitToCamelCase(table) + "Mapper.xml"), template);
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
        return switch (MysqlType.getByName(dataType).getJdbcType()) {
            case Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR -> String.class.getName();
            case Types.INTEGER, Types.TINYINT, Types.SMALLINT -> Integer.class.getName();
            case Types.BIGINT -> Long.class.getName();
            case Types.DECIMAL -> BigDecimal.class.getName();
            case Types.DATE -> LocalDate.class.getName();
            case Types.TIME -> LocalTime.class.getName();
            case Types.TIMESTAMP -> LocalDateTime.class.getName();
            default -> Object.class.getName();
        };
    }
}