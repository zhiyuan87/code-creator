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
    public Map<String, String> getBy(String table, String database) {
        return databaseDao.getBy(table, database);
    }

    @Override
    public List<Map<String, String>> listBy(String table, String database) {
        return databaseDao.listBy(table, database);
    }
}