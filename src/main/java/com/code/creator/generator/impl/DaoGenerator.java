package com.code.creator.generator.impl;

import com.code.creator.generator.AbstractCodeGenerator;
import com.code.creator.generator.GeneratorContext;
import com.code.creator.generator.GeneratorType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DAO 接口代码生成器
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Component
public class DaoGenerator extends AbstractCodeGenerator {

    @Override
    public boolean supports(GeneratorType type) {
        return type == GeneratorType.DAO;
    }

    @Override
    protected String getPackageSuffix() {
        return "dao";
    }

    @Override
    protected String getSubDir() {
        return "dao";
    }

    @Override
    protected String getClassNameSuffix() {
        return "Dao";
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

        String entityPackage = context.getConfig().getPackageName() + ".model.entity";
        String className = toClassName(context.getTableName());
        imports.append(String.format("import %s.%s;\n", entityPackage, className));

        // IDao 导入（使用配置中的 baseDaoPackage 或默认路径）
        if (context.getConfig().getBaseDaoPackage() != null && !context.getConfig().getBaseDaoPackage().isEmpty()) {
            imports.append(String.format("import %s.IDao;\n", context.getConfig().getBaseDaoPackage()));
        } else {
            imports.append(String.format("import %s.common.support.interfaces.IDao;\n", getProjectPrefix(context)));
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
        String daoName = className + "Dao";

        StringBuilder declaration = new StringBuilder();
        declaration.append("/**\n");
        declaration.append(String.format(" * This is the class comment for the class {@link %s}.\n", daoName));
        declaration.append(" *\n");
        declaration.append(String.format(" * @author %s\n", context.getConfig().getAuthor()));
        declaration.append(String.format(" * @since %s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        declaration.append(" */\n");
        declaration.append(String.format("public interface %s extends IDao<%s, Long> {\n", daoName, className));

        return declaration.toString();
    }
}
