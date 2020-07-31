package io.github.sirnik.daduels.database;

import com.zaxxer.hikari.HikariDataSource;
import io.github.sirnik.daduels.models.DuelArena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class ArenaTable extends GenericTable<DuelArena> {

    public ArenaTable(HikariDataSource hikariDataSource) {
        super(hikariDataSource, "duel_arenas");
    }

    @Override
    public void createTable() {
        List<String> names = Arrays.asList(
                "id INT NOT NULL AUTO_INCREMENT",
                "arena_name VARCHAR(255) NOT NULL UNIQUE",
                "world VARCHAR(255)",
                "x1 INTEGER",
                "y1 INTEGER",
                "z1 INTEGER",
                "x2 INTEGER",
                "y2 INTEGER",
                "z2 INTEGER",
                "PRIMARY KEY (id)");

        super.createTable(names);
    }

    @Override
    public List<DuelArena> getAll() {
        try(
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + this.getTableName())) {
            List<DuelArena> lst = new ArrayList<>();

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                World world = Bukkit.getWorld(rs.getString(3));
                DuelArena arena = new DuelArena(rs.getString(2));

                Location player1Location = new Location(world, rs.getInt(4), rs.getInt(5), rs.getInt(6));
                Location player2Location = new Location(world, rs.getInt(7), rs.getInt(8), rs.getInt(9));

                arena.setPlayer1Location(player1Location);
                arena.setPlayer2Location(player2Location);

                arena.setIndex(rs.getLong(1));

                lst.add(arena);
            }

            rs.close();
            return lst;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    @Override
    public void deleteSingle(DuelArena arena) {
        try(
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement ps = connection.prepareStatement("DELETE FROM " + this.getTableName() + " WHERE arena_name = ?")) {
            ps.setString(1, arena.getName());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveSingle(DuelArena arena) {
        String sql;
        boolean isUpdate = false;

        if(this.arenaExists(arena)) {
            sql = "UPDATE %s SET world = ?, x1 = %d, y1 = %d, z1 = %d, x2 = %d, y2 = %d, z2 = %d";
            isUpdate = true;
        } else {
            sql = "INSERT INTO %s (arena_name, world, x1, y1, z1, x2, y2, z2) VALUES (?, ?, %d, %d, %d, %d, %d, %d)";
        }

        sql = String.format(
                sql,
                this.getTableName(),
                arena.getPlayer1Location().getBlockX(),
                arena.getPlayer1Location().getBlockY(),
                arena.getPlayer1Location().getBlockZ(),
                arena.getPlayer2Location().getBlockX(),
                arena.getPlayer2Location().getBlockY(),
                arena.getPlayer2Location().getBlockZ());

        try(
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            if(isUpdate) {
                statement.setString(1, arena.getPlayer1Location().getWorld().getName());
                statement.execute();
                return;
            }

            statement.setString(1, arena.getName());
            statement.setString(2, arena.getPlayer1Location().getWorld().getName());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating arena failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    arena.setIndex(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean arenaExists(DuelArena arena) {
        boolean exists = false;

        if(arena.getIndex() == -1) {
            return false;
        }

        try(
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + this.getTableName() + " WHERE arena_name = ?")) {
            ps.setString(1, arena.getName());

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
}
