package io.github.sirnik.daduels;

import io.github.sirnik.daduels.commands.*;
import io.github.sirnik.daduels.commands.management.CommandManager;
import io.github.sirnik.daduels.database.Connector;
import io.github.sirnik.daduels.listeners.*;
import io.github.sirnik.daduels.models.DuelArena;
import io.github.sirnik.daduels.utils.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class DADuels extends JavaPlugin {

    private static DADuels instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        //Config
        if (!this.getDataFolder().exists()) {
            if (!this.getDataFolder().mkdirs()) {
                Bukkit.getLogger().warning("Could not create main plugin folder. Need to do so manually");
            }
        }

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        Connector.getInstance().createTable();
        List<DuelArena> arenas = Connector.getInstance().getArenas();

        for(DuelArena arena : arenas) {
            ArenaManager.INSTANCE.addArena(arena, true);
        }

        //Listeners
        Bukkit.getPluginManager().registerEvents(new ArenaCreator(),this);
        Bukkit.getPluginManager().registerEvents(new InventoryEvents(),this);
        Bukkit.getPluginManager().registerEvents(new MatchEvents(),this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(),this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitTeleport(),this);

        //Commands
        CommandManager commandManager = new CommandManager("daduels");

        getCommand("daduels").setExecutor(commandManager);

        commandManager.addCommand(new CreateArena());
        commandManager.addCommand(new EditArena());
        commandManager.addCommand(new DeleteArena());
        commandManager.addCommand(new ExitArena());
        commandManager.addCommand(new OpenGui());
        commandManager.addCommand(new SaveArena());
        commandManager.addCommand(new ToggleArena());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static DADuels getInstance() {
        return instance;
    }
}
