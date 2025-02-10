package com.code.creator.service;

import com.code.creator.model.dto.TableColumnInfoDTO;

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
     * 查询表信息
     *
     * @param table
     * @param database
     * @return
     */
    Map<String, String> getBy(String table, String database);

    List<TableColumnInfoDTO> getPGTableInfo(String table, String database);

    /**
     * 查询列信息
     *
     * @param table
     * @param database
     * @return
     */
    List<Map<String, String>> listBy(String table, String database);
}