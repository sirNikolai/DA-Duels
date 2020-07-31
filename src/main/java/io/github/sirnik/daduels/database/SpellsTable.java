package io.github.sirnik.daduels.database;

import com.zaxxer.hikari.HikariDataSource;
import io.github.nikmang.daspells.spells.Spell;
import io.github.nikmang.daspells.utils.SpellController;
import io.github.sirnik.daduels.models.DuelSpell;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

class SpellsTable extends GenericTable<DuelSpell> {

    private Map<String, DuelSpell> cache;

    public SpellsTable(HikariDataSource dataSource) {
        super(dataSource, "spells");

        cache = new HashMap<>();
    }

    @Override
    public void createTable() {
        List<String> names = Arrays.asList(
                "id INT NOT NULL AUTO_INCREMENT",
                "spell_name VARCHAR(255) NOT NULL UNIQUE",
                "PRIMARY KEY (id)");

        super.createTable(names);

        List<DuelSpell> spells = getAll();

        spells.forEach(s -> cache.put(s.getSpellName().toUpperCase(), s));
    }

    public DuelSpell getOrInsertSpell(String spellName) {
        if(cache.containsKey(spellName.toUpperCase())) {
            return cache.get(spellName.toUpperCase());
        }

        DuelSpell spell = new DuelSpell(spellName);
        saveSingle(spell);

        return spell;
    }

    /**
     * Attempts to save any spells that are do not exist in database.
     *
     * @param spells Spells to be added.
     *
     * @return List of all spells that were actually added to database.
     *
     * @deprecated currently untested and unused in code
     */
    public List<DuelSpell> saveMany(Collection<DuelSpell> spells) {
        List<DuelSpell> filtered = spells.stream().filter(s -> !spellExists(s)).collect(Collectors.toList());

        String sql = String.format("INSERT INTO %s (spell_name) VALUES (?)", this.getTableName());

        try(
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, new String[] {"id", "spell_name"})) {
            connection.setAutoCommit(false);

            for(DuelSpell spell : filtered) {
                statement.setString(1, spell.getSpellName());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();

            List<DuelSpell> createdSpells = new ArrayList<>();

            try(ResultSet rs = statement.getGeneratedKeys()) {
                while(rs.next()) {
                    createdSpells.add(new DuelSpell(rs.getString(2), rs.getLong(1)));
                }
            }

            return createdSpells;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    @Override
    public void saveSingle(DuelSpell spell) {
        if(spellExists(spell)) {
            return;
        }

        String sql = String.format("INSERT INTO %s (spell_name) VALUES (?)", this.getTableName());

        try(
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, spell.getSpellName());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating duellspell failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    spell.setIndex(generatedKeys.getLong(1));
                }
            }

            cache.put(spell.getSpellName().toUpperCase(), spell);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSingle(DuelSpell spell) {
        try(
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement ps = connection.prepareStatement("DELETE FROM " + this.getTableName() + " WHERE id = ?")) {
            ps.setLong(1, spell.getIndex());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<DuelSpell> getAll() {
        try(
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + this.getTableName())) {
            List<DuelSpell> lst = new ArrayList<>();
            List<String> invalidSpells = new ArrayList<>();

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                Spell spell = SpellController.INSTANCE.getSpellByName(rs.getString(2));

                if(spell == null) {
                    invalidSpells.add(rs.getString(1));
                } else {
                    lst.add(new DuelSpell(rs.getString(2), rs.getLong(1)));
                }
            }

            Bukkit.getLogger().log(Level.SEVERE, String.format("%s spells were not found and need to be manually deleted", String.join(",", invalidSpells)));

            rs.close();
            return lst;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    //TODO: this is almost a carbon copy of ArenaTable
    private boolean spellExists(DuelSpell spell) {
        boolean exists = false;

        if(spell.getIndex() == -1) {
            return false;
        }

        try(
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + this.getTableName() + " WHERE id = ?")) {
            ps.setLong(1, spell.getIndex());

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
