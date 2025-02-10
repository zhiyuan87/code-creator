package com.code.creator.service.impl;

import com.code.creator.config.CreatorConfig;
import com.code.creator.service.RepositoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This is the class comment for the class {@link RepositoryServiceImpl}.
 *
 * @author zy.wu.andy@gmail.com
 * @since 2025-02-10 10:39:00
 */
@Slf4j
@Service
public class RepositoryServiceImpl implements RepositoryService {
    @Resource
    private CreatorConfig creatorConfig;

    @Override
    public void createJPARepository() throws Exception {
        var repositoryClassName = creatorConfig.repositoryClassName();
        try (var writer = new FileWriter(creatorConfig.getTemplate().getRepository().getSavePath() + File.separator + repositoryClassName + ".java")) {
            var template = """
                    package {{packageName}};
                                        
                    import {{entityPackageName}}.{{entityClassName}};
                    import org.springframework.data.jpa.repository.JpaRepository;
                    import org.springframework.stereotype.Repository;
                                        
                    /**
                     * This is the class comment for the class {@link {{className}}}.
                     *
                     * @author {{author}}
                     * @since {{since}}
                     */
                    @Repository
                    public interface {{className}} extends JpaRepository<{{entityClassName}}, Long> {
                    }
                    """
                    .replace("{{since}}", getNow())
                    .replace("{{author}}", creatorConfig.getAuthor())
                    .replace("{{entityClassName}}", creatorConfig.entityClassName())
                    .replace("{{entityPackageName}}", creatorConfig.getTemplate().getEntity().getPackageName())
                    .replace("{{packageName}}", creatorConfig.getTemplate().getRepository().getPackageName())
                    .replace("{{className}}", repositoryClassName);

            log.debug("\n{}", template);
            writer.write(template);
        }
    }

    static String getNow() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now().withNano(0));
    }
}
