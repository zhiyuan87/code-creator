package com.code.creator.service;

import java.util.List;
import java.util.Map;

/**
 * This is the class comment for the class {@link DatabaseService}.
 *
 * @author zhiyuan
 * @since 2023-08-18 10:29:00
 */
public interface DatabaseService {
    /**
     * 根据条件查询记录
     *
     * @param database
     * @param table
     * @return
     */
    Map<String, String> findTableByDatabaseAndTable(String database, String table);

    /**
     * 根据条件查询记录
     *
     * @param database
     * @param table
     * @return
     */
    List<Map<String, String>> findColumnByDatabaseAndTable(String database, String table);
}