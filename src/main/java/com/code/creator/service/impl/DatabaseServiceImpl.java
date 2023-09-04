package com.code.creator.service.impl;

import com.code.creator.dao.DatabaseDao;
import com.code.creator.service.DatabaseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * This is the class comment for the class {@link DatabaseServiceImpl}.
 *
 * @author zhiyuan
 * @since 2023-08-18 10:30:00
 */
@Service
public class DatabaseServiceImpl implements DatabaseService {

    @Resource
    private DatabaseDao databaseDao;

    @Override
    public Map<String, String> findTableByDatabaseAndTable(String database, String table) {
        return databaseDao.findTableByDatabaseAndTable(database, table);
    }

    @Override
    public List<Map<String, String>> findColumnByDatabaseAndTable(String database, String table) {
        return databaseDao.findColumnByDatabaseAndTable(database, table);
    }
}