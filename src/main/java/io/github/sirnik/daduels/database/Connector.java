package io.github.sirnik.daduels.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.sirnik.daduels.DADuels;
import io.github.sirnik.daduels.models.DuelArena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class Connector {

    private static final String TABLE_NAME = "duel_arenas";
    private static Connector instance;

    private HikariDataSource dataSource;

    public static Connector getInstance() {
        if(instance == null) {
            instance = new Connector(
                    DADuels.getInstance().getConfig().getString("dbPath"),
                    DADuels.getInstance().getConfig().getString("username"),
                    DADuels.getInstance().getConfig().getString("password"));
        }

        return instance;
    }


    private Connector(String connectionString, String username, String password) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(connectionString);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    /**
     * Creates the table if it does not exist (will send warning otherwise).
     */
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

        String stmt = String.format(
                "CREATE TABLE IF NOT EXISTS %s (%s)",
                TABLE_NAME,
                String.join(", ", names));

        try(Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(stmt)) {
            Bukkit.getLogger().log(Level.INFO, "Executing statement: " + stmt);

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets all arenas currently saved.
     *
     * @return List of arenas in database
     */
    public List<DuelArena> getArenas() {
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + TABLE_NAME)) {
            List<DuelArena> lst = new ArrayList<>();

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                World world = Bukkit.getWorld(rs.getString(3));
                DuelArena arena = new DuelArena(rs.getString(2));

                Location player1Location = new Location(world, rs.getInt(4), rs.getInt(5), rs.getInt(6));
                Location player2Location = new Location(world, rs.getInt(7), rs.getInt(8), rs.getInt(9));

                arena.setPlayer1Location(player1Location);
                arena.setPlayer2Location(player2Location);

                lst.add(arena);
            }

            rs.close();
            return lst;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    public void deleteArena(DuelArena arena) {
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE arena_name = ?")) {
            ps.setString(1, arena.getName());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a new or overwrites a current arena.
     *
     * @param arena Arena to be saved.
     */
    public void saveArena(DuelArena arena) {
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
                TABLE_NAME,
                arena.getPlayer1Location().getBlockX(),
                arena.getPlayer1Location().getBlockY(),
                arena.getPlayer1Location().getBlockZ(),
                arena.getPlayer2Location().getBlockX(),
                arena.getPlayer2Location().getBlockY(),
                arena.getPlayer2Location().getBlockZ());

        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)){

            if(isUpdate) {
                statement.setString(1, arena.getPlayer1Location().getWorld().getName());
            } else {
                statement.setString(1, arena.getName());
                statement.setString(2, arena.getPlayer1Location().getWorld().getName());
            }

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean arenaExists(DuelArena arena) {
        boolean exists = false;

        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE arena_name = ?")) {
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
