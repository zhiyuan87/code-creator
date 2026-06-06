package com.code.creator.service.impl;

import com.code.creator.dao.DatabaseDao;
import com.code.creator.model.dto.ColumnDTO;
import com.code.creator.service.DatabaseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据库服务实现类（自动识别数据库类型）
 *
 * @author zhiyuan
 * @since 2026-05-29 17:56:42
 */
@Service
@AllArgsConstructor
public class DatabaseServiceImpl implements DatabaseService {

    private final List<DatabaseDao> databaseDaoList;


    @Override
    public String findTableComment(String table) {
        return databaseDaoList.getFirst().findTableComment(table);
    }

    @Override
    public List<ColumnDTO> findTableColumns(String table) {
        return databaseDaoList.getFirst().findTableColumns(table);
    }
}
