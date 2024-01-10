package com.code.creator.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * This is the class comment for the class {@link DatabaseMapper}.
 *
 * @author zhiyuan
 * @since 2023-08-18 10:34:00
 */
@Mapper
public interface DatabaseMapper {
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
