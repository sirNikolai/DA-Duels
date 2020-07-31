package io.github.sirnik.daduels.database;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

/**
 * Class meant to abstract away database actions.
 * <i>Currently just simplifies actions for myself rather than abstract anything.</i>
 *
 * @param <T> Object for which the table exists
 */
public abstract class GenericTable<T> {

    private HikariDataSource dataSource;
    private String tableName;

    public GenericTable(HikariDataSource dataSource, String tableName) {
        this.dataSource = dataSource;
        this.tableName = tableName;
    }

    /**
     * Create a Table for provided type
     */
    public abstract void createTable();

    /**
     * Save a single value for provided value.
     * This is used for both insert and update.
     *
     * @param t Single variant of what is saved.
     */
    public abstract void saveSingle(T t);

    /**
     * Delete a single object of provided type.
     * May or may not be existing in table.
     *
     * @param t Value to ber removed.
     */
    public abstract void deleteSingle(T t);

    /**
     * Get all values for provided type.
     *
     * @return All values in database for provided type.
     */
    public abstract List<T> getAll();

    void createTable(List<String> parameters) {
        String stmt = String.format(
                "CREATE TABLE IF NOT EXISTS %s (%s)",
                this.getTableName(),
                String.join(", ", parameters));

        try(Connection connection = this.getDataSource().getConnection(); PreparedStatement ps = connection.prepareStatement(stmt)) {
            Bukkit.getLogger().log(Level.INFO, "Executing statement: " + stmt);

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    HikariDataSource getDataSource() {
        return dataSource;
    }

    String getTableName() {
        return tableName;
    }
}
