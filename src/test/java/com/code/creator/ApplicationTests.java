package com.code.creator;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ApplicationTests {

    static {
        System.setProperty("logback.configurationFile", new File("src\\main\\resources\\logback-spring.xml").getAbsolutePath());
    }

    private static final Logger logger = LoggerFactory.getLogger(ApplicationTests.class);

    @Test
    public void run() {
    }
}