package com.code.creator.generator;

import com.code.creator.exception.CodeGeneratorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 代码生成器抽象基类
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Slf4j
public abstract class AbstractCodeGenerator implements CodeGenerator {

    @Autowired
    private GeneratorContext generatorContext;

    @Override
    public void generate() {
        String code = doGenerate(generatorContext);
        String filePath = buildOutputPath(generatorContext);
        log.debug("Generated code for {}: {} chars -> {}", generatorContext.getTableName(), code.length(), filePath);
        saveToFile(code, filePath);
    }

    /**
     * 返回包名后缀（如 "model.entity"、"mapper" 等，空串表示使用基础包名）
     */
    protected abstract String getPackageSuffix();

    /**
     * 返回子目录（如 "model/entity"、"mapper" 等）
     */
    protected abstract String getSubDir();

    /**
     * 返回类名后缀（如 ""、"Mapper"、"Service" 等）
     */
    protected abstract String getClassNameSuffix();

    /**
     * 返回文件扩展名（默认 .java，XML 生成器覆盖为 .xml）
     */
    protected String getFileExtension() {
        return ".java";
    }

    /**
     * 计算当前生成器的完整包名
     */
    protected String getPackageName(GeneratorContext context) {
        String base = context.getConfig().getPackageName();
        String suffix = getPackageSuffix();
        return suffix.isEmpty() ? base : base + "." + suffix;
    }

    /**
     * 执行生成（子类实现，返回代码字符串）
     */
    protected abstract String doGenerate(GeneratorContext context);

    /**
     * 构建输出文件路径
     */
    private String buildOutputPath(GeneratorContext context) {
        String baseDir = resolveBaseOutputDir(context);
        String className = toClassName(context.getTableName()) + getClassNameSuffix();
        return Paths.get(baseDir, getSubDir(), className + getFileExtension()).toString();
    }

    /**
     * 获取基础输出目录（来自配置或根据包名/模块名自动推导）
     */
    private String resolveBaseOutputDir(GeneratorContext context) {
        String outputDirectory = context.getConfig().getOutputDirectory();
        if (outputDirectory != null && !outputDirectory.isBlank()) {
            return outputDirectory;
        }
        return convertPackageNameToPath(context.getConfig().getPackageName(), context.getConfig().getModuleName());
    }

    /**
     * 将包名转换为文件路径
     * 例如：com.pictostar.api.book -> pictostar-api/src/main/java/com/pictostar/api/book
     */
    private String convertPackageNameToPath(String packageName, String moduleName) {
        if (moduleName == null || moduleName.isBlank()) {
            moduleName = "app";
        }
        String baseDir = moduleName + "/src/main/java";
        return Paths.get(baseDir, packageName.replace(".", "/")).toString();
    }

    /**
     * 保存代码到文件
     */
    private void saveToFile(String code, String filePath) {
        Path path = Paths.get(filePath);
        try {
            Files.createDirectories(path.getParent());
            if (Files.exists(path)) {
                log.warn("File already exists: {}", filePath);
                // TODO: 根据配置决定是否覆盖
            }
            Files.writeString(path, code, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            log.info("Code saved successfully: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to save code to: {}", filePath, e);
            throw new CodeGeneratorException("Failed to save code", e);
        }
    }

    /**
     * 转换表名为类名（下划线转驼峰）
     * 自动去除 t_ 前缀
     */
    protected String toClassName(String tableName) {
        if (tableName != null && tableName.toLowerCase().startsWith("t_")) {
            tableName = tableName.substring(2);
        }
        String[] parts = tableName.split("_");
        StringBuilder className = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                className.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1).toLowerCase());
            }
        }
        return className.toString();
    }
}
