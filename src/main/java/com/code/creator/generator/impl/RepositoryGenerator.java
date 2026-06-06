package com.code.creator.generator.impl;

import com.code.creator.generator.AbstractCodeGenerator;
import com.code.creator.generator.GeneratorContext;
import com.code.creator.generator.GeneratorType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Repository 接口代码生成器
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Component
public class RepositoryGenerator extends AbstractCodeGenerator {

    @Override
    public boolean supports(GeneratorType type) {
        return type == GeneratorType.REPOSITORY;
    }

    @Override
    protected String getPackageSuffix() {
        return "repository";
    }

    @Override
    protected String getSubDir() {
        return "repository";
    }

    @Override
    protected String getClassNameSuffix() {
        return "Repository";
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

    private String buildPackage(GeneratorContext context) {
        return String.format("package %s;\n\n", getPackageName(context));
    }

    private String buildImports(GeneratorContext context) {
        StringBuilder imports = new StringBuilder();

        String entityPackage = context.getConfig().getPackageName() + ".model.entity";
        imports.append(String.format("import %s.%s;\n", entityPackage, toClassName(context.getTableName())));
        imports.append("import org.springframework.data.jpa.repository.JpaRepository;\n");
        imports.append("import org.springframework.stereotype.Repository;\n");
        imports.append("\n");

        return imports.toString();
    }

    private String buildInterfaceDeclaration(GeneratorContext context) {
        String className = toClassName(context.getTableName());
        String interfaceName = className + "Repository";

        StringBuilder declaration = new StringBuilder();
        declaration.append("/**\n");
        declaration.append(String.format(" * This is the class comment for the class {@link %s}.\n", interfaceName));
        declaration.append(" *\n");
        declaration.append(String.format(" * @author %s\n", context.getConfig().getAuthor()));
        declaration.append(String.format(" * @since %s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        declaration.append(" */\n");
        declaration.append("@Repository\n");
        declaration.append(String.format("public interface %s extends JpaRepository<%s, Long> {\n\n", interfaceName, className));
        declaration.append("\t// TODO: Add custom query methods here\n");

        return declaration.toString();
    }
}
