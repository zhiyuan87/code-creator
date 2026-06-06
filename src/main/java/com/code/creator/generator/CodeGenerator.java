package com.code.creator.generator;

/**
* 代码生成器接口
*
* @author zhiyuan
* * @since 2026-05-29 17:56:42
*/
public interface CodeGenerator {

    /**
     * 生成代码并保存到文件
     */
    void generate();

    /**
     * 是否支持该类型
     *
     * @param type 类型
     * @return 是否支持
     */
    boolean supports(GeneratorType type);
}
