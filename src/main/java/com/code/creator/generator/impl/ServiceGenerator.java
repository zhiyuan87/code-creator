package com.code.creator.generator.impl;

import com.code.creator.generator.AbstractCodeGenerator;
import com.code.creator.generator.GeneratorContext;
import com.code.creator.generator.GeneratorType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service 接口代码生成器（包含接口和实现类）
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Component
public class ServiceGenerator extends AbstractCodeGenerator {

    @Override
    public boolean supports(GeneratorType type) {
        return type == GeneratorType.SERVICE;
    }

    @Override
    protected String getPackageSuffix() {
        return "service";
    }

    @Override
    protected String getSubDir() {
        return "service";
    }

    @Override
    protected String getClassNameSuffix() {
        return "Service";
    }

    @Override
    protected String doGenerate(GeneratorContext context) {
        StringBuilder code = new StringBuilder();

        code.append(buildPackage(context));
        code.append(buildImports(context, false));
        code.append(buildInterfaceDeclaration(context));
        code.append("}\n");

        return code.toString();
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
    private String buildImports(GeneratorContext context, boolean isImpl) {
        StringBuilder imports = new StringBuilder();

        String basePackage = context.getConfig().getPackageName();
        String entityPackage = basePackage + ".model.entity";
        String className = toClassName(context.getTableName());

        // Entity 导入
        imports.append(String.format("import %s.%s;\n", entityPackage, className));

        if (isImpl) {
            // Service 接口导入（实现类需要）
            String servicePackage = basePackage + ".service";
            imports.append(String.format("import %s.%sService;\n", servicePackage, className));

            // DAO 导入
            String daoPackage = basePackage + ".dao";
            imports.append(String.format("import %s.%sDao;\n", daoPackage, className));

            // Spring 注解导入
            imports.append("import org.springframework.beans.factory.annotation.Autowired;\n");
            imports.append("import org.springframework.stereotype.Service;\n");

            // BaseService 导入（使用配置中的 baseServicePackage）
            if (context.getConfig().getBaseServicePackage() != null && !context.getConfig().getBaseServicePackage().isEmpty()) {
                imports.append(String.format("import %s.BaseService;\n", context.getConfig().getBaseServicePackage()));
            } else {
                imports.append(String.format("import %s.common.support.base.BaseService;\n", getProjectPrefix(context)));
            }
        } else {
            // IService 导入（使用配置中的 baseMapperPackage 或默认路径）
            if (context.getConfig().getBaseMapperPackage() != null && !context.getConfig().getBaseMapperPackage().isEmpty()) {
                imports.append(String.format("import %s.IService;\n", context.getConfig().getBaseMapperPackage()));
            } else {
                imports.append(String.format("import %s.common.support.interfaces.IService;\n", getProjectPrefix(context)));
            }
        }

        imports.append("\n");

        return imports.toString();
    }

    /**
     * 获取项目前缀（包名的第一部分）
     */
    private String getProjectPrefix(GeneratorContext context) {
        String packageName = context.getConfig().getPackageName();
        int firstDot = packageName.indexOf('.');
        return firstDot > 0 ? packageName.substring(0, firstDot) : packageName;
    }

    /**
     * 构建接口声明
     */
    private String buildInterfaceDeclaration(GeneratorContext context) {
        String className = toClassName(context.getTableName());
        String interfaceName = className + "Service";

        StringBuilder declaration = new StringBuilder();

        declaration.append("/**\n");
        declaration.append(String.format(" * This is the class comment for the class {@link %s}.\n", interfaceName));
        declaration.append(" *\n");
        declaration.append(String.format(" * @author %s\n", context.getConfig().getAuthor()));
        declaration.append(String.format(" * @since %s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        declaration.append(" */\n");
        declaration.append(String.format("public interface %s extends IService<%s, Long> {\n", interfaceName, className));

        return declaration.toString();
    }
}
