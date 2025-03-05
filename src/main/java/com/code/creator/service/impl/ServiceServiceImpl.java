package com.code.creator.service.impl;

import com.code.creator.config.CreatorConfig;
import com.code.creator.service.ServiceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This is the class comment for the class {@link ServiceServiceImpl}.
 *
 * @author zy.wu.andy@gmail.com
 * @since 2025-02-08 13:52:00
 */
@Slf4j
@Service
public class ServiceServiceImpl implements ServiceService {
    @Resource
    private CreatorConfig creatorConfig;

    @Override
    public void createService() throws Exception {
//        var template = new ClassPathResource("template/java_service.template").getContentAsString(Charset.defaultCharset());
//        template = template.replace("${packageName}", creatorConfig.getModule().getPackageName());
//        template = template.replace("${project}", creatorConfig.getProject());
//        template = template.replace("${author}", creatorConfig.getAuthor());
//        template = template.replace("${create_at}", getNow());
//        template = template.replace("${entity_class}", this.splitToCamelCase(creatorConfig.getTable()));
//        Files.writeString(Files.createDirectories(Paths.get(STR."\{creatorConfig.getModule().getFullName()}/src/main/java/\{creatorConfig.getModule().getPackageName().replaceAll("\\.", "/")}/service")).resolve(STR."\{this.splitToCamelCase(creatorConfig.getTable())}Service.java"), template);
//
//        template = new ClassPathResource("template/java_service_impl.template").getContentAsString(Charset.defaultCharset());
//        template = template.replace("${packageName}", creatorConfig.getModule().getPackageName());
//        template = template.replace("${project}", creatorConfig.getProject());
//        template = template.replace("${author}", creatorConfig.getAuthor());
//        template = template.replace("${create_at}", getNow());
//        template = template.replace("${entity_class}", this.splitToCamelCase(creatorConfig.getTable()));
//        template = template.replace("${entity_name}", this.uncapitalize(this.splitToCamelCase(creatorConfig.getTable())));
//        Files.writeString(Files.createDirectories(Paths.get(STR."\{creatorConfig.getModule().getFullName()}/src/main/java/\{creatorConfig.getModule().getPackageName().replaceAll("\\.", "/")}/service/impl")).resolve(STR."\{this.splitToCamelCase(creatorConfig.getTable())}ServiceImpl.java"), template);
    }

    @Override
    public void createJPAService() throws Exception {
        var serviceClassName = creatorConfig.serviceClassName();
        try (var writer = new FileWriter(creatorConfig.getTemplate().getService().getSavePath() + File.separator + serviceClassName + ".java")) {
            var template = """
                    package {{servicePackageName}};
                                        
                    /**
                     * This is the class comment for the class {@link {{serviceClassName}}}.
                     *
                     * @author {{author}}
                     * @since {{since}}
                     */
                    public interface {{serviceClassName}} {
                    }
                    """
                    .replace("{{since}}", getNow())
                    .replace("{{author}}", creatorConfig.getAuthor())
                    .replace("{{serviceClassName}}", serviceClassName)
                    .replace("{{servicePackageName}}", creatorConfig.getTemplate().getService().getPackageName());

            log.debug("\n{}", template);
            writer.write(template);
        }
    }

    @Override
    public void createJPAServiceImpl() throws Exception {
        var serviceClassName = creatorConfig.serviceClassName();
        try (var writer = new FileWriter(creatorConfig.getTemplate().getService().getSavePath() + File.separator + "impl" + File.separator + serviceClassName + "Impl.java")) {
            var template = """
                    package {{servicePackageName}}.impl;
                                        
                    import {{repositoryPackageName}}.{{repositoryClassName}};
                    import {{servicePackageName}}.{{serviceClassName}};
                    import jakarta.annotation.Resource;
                    import lombok.extern.slf4j.Slf4j;
                    import org.springframework.stereotype.Service;
                                        
                    /**
                     * This is the class comment for the class {@link {{serviceClassName}}Impl}.
                     *
                     * @author {{author}}
                     * @since {{since}}
                     */
                    @Slf4j
                    @Service
                    public class {{serviceClassName}}Impl implements {{serviceClassName}} {
                        @Resource
                        private {{repositoryClassName}} {{repositoryFieldName}};
                    }

                    """
                    .replace("{{since}}", getNow())
                    .replace("{{author}}", creatorConfig.getAuthor())
                    .replace("{{serviceClassName}}", serviceClassName)
                    .replace("{{servicePackageName}}", creatorConfig.getTemplate().getService().getPackageName())
                    .replace("{{repositoryClassName}}", creatorConfig.repositoryClassName())
                    .replace("{{repositoryFieldName}}", StringUtils.uncapitalize(creatorConfig.repositoryClassName()))
                    .replace("{{repositoryPackageName}}", creatorConfig.getTemplate().getRepository().getPackageName());

            log.debug("\n{}", template);
            writer.write(template);
        }
    }

    static String getNow() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now().withNano(0));
    }
}
