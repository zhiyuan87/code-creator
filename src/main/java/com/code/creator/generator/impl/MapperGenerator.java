package com.code.creator.generator.impl;

import com.code.creator.config.GeneratorConfig;
import com.code.creator.generator.AbstractCodeGenerator;
import com.code.creator.generator.GeneratorContext;
import com.code.creator.generator.GeneratorType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Mapper 接口代码生成器
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Component
public class MapperGenerator extends AbstractCodeGenerator {

    @Override
    public boolean supports(GeneratorType type) {
        return type == GeneratorType.MAPPER;
    }

    @Override
    protected String getPackageSuffix() {
        return "mapper";
    }

    @Override
    protected String getSubDir() {
        return "mapper";
    }

    @Override
    protected String getClassNameSuffix() {
        return "Mapper";
    }

    @Override
    protected String doGenerate(GeneratorContext context) {
        StringBuilder code = new StringBuilder();

        code.append(buildPackage(context));
        code.append(buildImports(context));
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
    private String buildImports(GeneratorContext context) {
        StringBuilder imports = new StringBuilder();

        GeneratorConfig config = context.getConfig();

        String entityPackage = context.getConfig().getPackageName() + ".model.entity";
        String className = toClassName(context.getTableName());
        imports.append(String.format("import %s.%s;\n", entityPackage, className));

        // BaseMapper 导入（使用配置中的 baseMapperPackage）
        if (context.getConfig().getBaseMapperPackage() != null && !context.getConfig().getBaseMapperPackage().isEmpty()) {
            imports.append(String.format("import %s.BaseMapper;\n", context.getConfig().getBaseMapperPackage()));
        } else {
            imports.append(String.format("import %s.common.support.base.BaseMapper;\n", getProjectPrefix(context)));
        }

        // 添加 @Mapper 注解导入
        imports.append("import org.apache.ibatis.annotations.Mapper;\n\n");

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
        String interfaceName = className + "Mapper";

        StringBuilder declaration = new StringBuilder();

        declaration.append("/**\n");
        declaration.append(String.format(" * This is the class comment for the class {@link %s}.\n", interfaceName));
        declaration.append(" *\n");
        declaration.append(String.format(" * @author %s\n", context.getConfig().getAuthor()));
        declaration.append(String.format(" * @since %s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        declaration.append(" */\n");
        declaration.append("@Mapper\n");
        declaration.append(String.format("public interface %s extends BaseMapper<%s, Long> {\n", interfaceName, className));

        return declaration.toString();
    }
}
