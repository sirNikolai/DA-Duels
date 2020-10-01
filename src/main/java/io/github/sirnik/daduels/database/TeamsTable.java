package io.github.sirnik.daduels.database;

import com.zaxxer.hikari.HikariDataSource;
import io.github.sirnik.daduels.models.Team;

import java.util.List;

public class TeamsTable extends GenericTable<Team> {
    
    public TeamsTable(HikariDataSource dataSource, String tableName) {
        super(dataSource, tableName);
    }

    @Override
    public void createTable() {

    }

    @Override
    public void saveSingle(Team team) {

    }

    @Override
    public void deleteSingle(Team team) {

    }

    @Override
    public List<Team> getAll() {
        return null;
    }
}
