package io.github.sirnik.daduels.database;

import com.zaxxer.hikari.HikariDataSource;
import io.github.sirnik.daduels.models.ArenaSpell;
import io.github.sirnik.daduels.models.DuelArena;
import io.github.sirnik.daduels.models.DuelSpell;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Joining table between Arenas and spells.
 */
public class ArenaSpellsTable extends GenericTable<ArenaSpell> {

    public ArenaSpellsTable(HikariDataSource dataSource) {
        super(dataSource, "arena_spells");
    }

    @Override
    public void createTable() {
        List<String> names = Arrays.asList(
                "id INT NOT NULL AUTO_INCREMENT",
                "spell_id INT NOT NULL",
                "arena_id INT NOT NULL",
                "PRIMARY KEY (id)",
                "FOREIGN KEY (spell_id) REFERENCES spells(id)",
                "FOREIGN KEY (arena_id) REFERENCES duel_arenas(id)",
                "UNIQUE (spell_id, arena_id");

        super.createTable(names);
    }

    @Override
    public void saveSingle(ArenaSpell arenaSpell) {
        String sql = String.format("INSERT INTO %s (spell_id, arena_id) VALUES (?, ?)", this.getTableName());

        try(
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)){
            connection.setAutoCommit(false);

            for(DuelSpell spell : arenaSpell.getSpells()) {
                if(arenaSpellExists(arenaSpell.getArena(), spell)) {
                    continue;
                }

                statement.setLong(1, spell.getIndex());
                statement.setLong(2, arenaSpell.getArena().getIndex());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSingle(ArenaSpell arenaSpell) {
        try(
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement ps = connection.prepareStatement("DELETE FROM " + this.getTableName() + " WHERE arena_id = ? AND spell_id = ?")) {
            connection.setAutoCommit(false);

            for(DuelSpell spell : arenaSpell.getSpells()) {
                if(!arenaSpellExists(arenaSpell.getArena(), spell)) {
                    continue;
                }

                ps.setLong(2, spell.getIndex());
                ps.setLong(1, arenaSpell.getArena().getIndex());
                ps.addBatch();
            }

            ps.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllForArena(DuelArena arena) {
        if(arena.getIndex() == -1) {
            return;
        }

        try(
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement ps = connection.prepareStatement("DELETE FROM " + this.getTableName() + " WHERE arena_id = ?")) {
            ps.setLong(1, arena.getIndex());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean arenaSpellExists(DuelArena arena, DuelSpell spell) {
        boolean exists = false;

        try(
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + this.getTableName() + " WHERE spell_id = ? AND arena_id = ?")) {
            ps.setLong(1, spell.getIndex());
            ps.setLong(2, arena.getIndex());

            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                exists = true;
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }

    @Override
    public List<ArenaSpell> getAll() {
        return null;
    }
}
