package io.github.sirnik.daduels.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.nikmang.daspells.spells.Spell;
import io.github.sirnik.daduels.DADuels;
import io.github.sirnik.daduels.models.ArenaSpell;
import io.github.sirnik.daduels.models.DuelArena;
import io.github.sirnik.daduels.models.DuelSpell;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main connector class between plugin and database
 */
public class Connector {
    private static Connector instance;

    private ArenaTable arenaTable;
    private ArenaSpellsTable arenaSpellsTable;
    private SpellsTable spellsTable;

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

        HikariDataSource dataSource = new HikariDataSource(config);

        arenaSpellsTable = new ArenaSpellsTable(dataSource);
        spellsTable = new SpellsTable(dataSource);
        arenaTable = new ArenaTable(dataSource);
    }

    /**
     * Initializes all tables.
     * Should be done at startup <b>only</b>.
     */
    public void initializeTables() {
        spellsTable.createTable();
        arenaTable.createTable();
        arenaSpellsTable.createTable();
    }

    /**
     * Retrieves all pairings of Duel Arena and spell blacklists in database.
     *
     * @return All rows of arena spell blacklists.
     */
    public List<ArenaSpell> getAllArenaSpellBlacklists() {
        return arenaSpellsTable.getAll();
    }

    /**
     * Save arena to database.
     *
     * @param arena Arena to be saved.
     */
    public void saveArena(DuelArena arena) {
        arenaTable.saveSingle(arena);
    }

    /**
     * Retrieve spells from cache or insert if they do not currently exist.
     * Caveat: currently does <b>not</b> utilize batch statements therefore will be slow in large loads.
     *
     * @param spells Spells to be added.
     *
     * @return List of duel spell objects created or retrieved.
     */
    public List<DuelSpell> getOrInsertSpells(List<Spell> spells) {
        return spells.stream().map(s -> spellsTable.getOrInsertSpell(s.toString())).collect(Collectors.toList());
    }

    public void addSpellsToArena(Collection<DuelSpell> spells, DuelArena arena) {
        arenaSpellsTable.saveSingle(new ArenaSpell(arena, spells));
    }

    public void removeSpellsFromArena(Collection<DuelSpell> spells, DuelArena arena) {
        arenaSpellsTable.deleteSingle(new ArenaSpell(arena, spells));
    }

    /**
     * Retrives all spells in database.
     *
     * @return All spells in database.
     */
    public List<DuelSpell> getAllSpells() {
        return spellsTable.getAll();
    }

    /**
     * Retrieve all arenas saved in database.
     *
     * @return Gets all Arenas currently in database.
     */
    public List<DuelArena> getArenas() {
        return arenaTable.getAll();
    }


    /**
     * Delete arena and data associated (spell blacklist).
     *
     * @param arena Arena which will be removed.
     */
    public void deleteArena(DuelArena arena) {
        arenaSpellsTable.deleteAllForArena(arena);
        arenaTable.deleteSingle(arena);
    }
}
