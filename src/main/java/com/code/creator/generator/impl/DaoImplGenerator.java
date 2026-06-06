package com.code.creator.generator.impl;

import com.code.creator.generator.AbstractCodeGenerator;
import com.code.creator.generator.GeneratorContext;
import com.code.creator.generator.GeneratorType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DAO 实现类代码生成器
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Component
public class DaoImplGenerator extends AbstractCodeGenerator {

    @Override
    public boolean supports(GeneratorType type) {
        return type == GeneratorType.DAO_IMPL;
    }

    @Override
    protected String getPackageSuffix() {
        return "dao.impl";
    }

    @Override
    protected String getSubDir() {
        return "dao/impl";
    }

    @Override
    protected String getClassNameSuffix() {
        return "DaoImpl";
    }

    @Override
    protected String doGenerate(GeneratorContext context) {
        StringBuilder code = new StringBuilder();

        code.append(buildPackage(context));
        code.append(buildImports(context));
        code.append(buildImplementationClass(context));
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

        String className = toClassName(context.getTableName());
        String basePackage = context.getConfig().getPackageName();

        // Mapper 导入
        imports.append(String.format("import %s.mapper.%sMapper;\n", basePackage, className));

        // Entity 导入
        imports.append(String.format("import %s.model.entity.%s;\n", basePackage, className));

        // DAO 接口导入
        imports.append(String.format("import %s.dao.%sDao;\n", basePackage, className));

        // BaseDao 导入（使用配置中的 baseDaoPackage）
        if (context.getConfig().getBaseDaoPackage() != null && !context.getConfig().getBaseDaoPackage().isEmpty()) {
            imports.append(String.format("import %s.BaseDao;\n", context.getConfig().getBaseDaoPackage()));
        } else {
            imports.append(String.format("import %s.common.support.base.BaseDao;\n", getProjectPrefix(context)));
        }

        // Spring 注解导入
        imports.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        imports.append("import org.springframework.stereotype.Repository;\n");

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
     * 构建实现类声明
     */
    private String buildImplementationClass(GeneratorContext context) {
        String className = toClassName(context.getTableName());
        String implName = className + "DaoImpl";
        String entityName = Character.toLowerCase(className.charAt(0)) + className.substring(1);

        StringBuilder declaration = new StringBuilder();
        declaration.append("/**\n");
        declaration.append(String.format(" * This is the class comment for the class {@link %s}.\n", implName));
        declaration.append(" *\n");
        declaration.append(String.format(" * @author %s\n", context.getConfig().getAuthor()));
        declaration.append(String.format(" * @since %s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        declaration.append(" */\n");
        declaration.append("@Repository\n");
        declaration.append(String.format("public class %s extends BaseDao<%s, Long> implements %sDao {\n", implName, className, className));

        // Mapper 字段
        declaration.append(String.format("    private final %sMapper %sMapper;\n\n", className, entityName));

        // 构造器
        declaration.append("    @Autowired\n");
        declaration.append(String.format("    public %s(%sMapper %sMapper) {\n", implName, className, entityName));
        declaration.append(String.format("        super(%sMapper);\n", entityName));
        declaration.append(String.format("        this.%sMapper = %sMapper;\n", entityName, entityName));
        declaration.append("    }\n");

        return declaration.toString();
    }
}
