package com.code.creator.dao.impl;

import com.code.creator.dao.DatabaseDao;
import com.code.creator.mapper.DatabaseMapper;
import com.code.creator.model.dto.TableColumnInfoDTO;
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
    public Map<String, String> getBy(String table, String database) {
        return databaseMapper.getBy(table, database);
    }

    @Override
    public List<TableColumnInfoDTO> getPGTableInfo(String table, String database) {
        return databaseMapper.getPGTableInfo(table, database);
    }

    @Override
    public List<Map<String, String>> listBy(String table, String database) {
        return databaseMapper.listBy(table, database);
    }
}