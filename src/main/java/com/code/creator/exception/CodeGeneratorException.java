package com.code.creator.exception;

import java.io.Serial;

/**
 * 代码生成器异常类
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
public class CodeGeneratorException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -6709706937463715004L;

    public CodeGeneratorException(String message) {
        super(message);
    }

    public CodeGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
}