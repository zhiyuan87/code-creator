package com.code.creator.dao.impl;

import com.code.creator.dao.DatabaseDao;
import com.code.creator.mapper.DatabaseMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * This is the class comment for the class {@link DatabaseDaoImpl}.
 *
 * @author zhiyuan
 * @since 2023-08-18 10:31:00
 */
@Repository
public class DatabaseDaoImpl implements DatabaseDao {

    @Resource
    private DatabaseMapper databaseMapper;

    @Override
    public Map<String, String> findTableByDatabaseAndTable(String database, String table) {
        return databaseMapper.findTableByDatabaseAndTable(database, table);
    }

    @Override
    public List<Map<String, String>> findColumnByDatabaseAndTable(String database, String table) {
        return databaseMapper.findColumnByDatabaseAndTable(database, table);
    }
}