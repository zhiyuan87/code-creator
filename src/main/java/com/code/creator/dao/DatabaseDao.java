package com.code.creator.dao;

import java.util.List;
import java.util.Map;

/**
 * This is the class comment for the class {@link DatabaseDao}.
 *
 * @author zhiyuan
 * @since 2023-08-18 10:31:00
 */
public interface DatabaseDao {
    /**
     * 查询表信息
     *
     * @param table
     * @param database
     * @return
     */
    Map<String, String> getBy(String table, String database);

    /**
     * 查询列信息
     *
     * @param table
     * @param database
     * @return
     */
    List<Map<String, String>> listBy(String table, String database);
}