package io.github.sirnik.daduels.commands;

import io.github.nikmang.daspells.spells.Spell;
import io.github.nikmang.daspells.utils.SpellController;
import io.github.sirnik.daduels.DADuels;
import io.github.sirnik.daduels.commands.management.CommandInfo;
import io.github.sirnik.daduels.commands.management.ExecutableCommand;
import io.github.sirnik.daduels.database.Connector;
import io.github.sirnik.daduels.models.DuelArena;
import io.github.sirnik.daduels.models.DuelSpell;
import io.github.sirnik.daduels.utils.ArenaManager;
import io.github.sirnik.daduels.utils.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        description = "add/remove spells from arena blacklist",
        aliases = {"blacklist", "bl"},
        mandatoryArgs = 3,
        usage = "<add/remove> <Arena> <Spell1,Spell2,...,SpellN>")
public class BlacklistSpells extends ExecutableCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        DuelArena a = ArenaManager.INSTANCE.getArena(args[1]);

        if(a == null) {
            MessageManager.getManager(sender).sendMessage(MessageManager.MessageType.BAD, String.format("Arena %s not found!", args[1]));
            return;
        }

        StringBuilder joinedArgs = new StringBuilder();

        for(int i = 2; i < args.length; i++) {
            joinedArgs.append(args[i]).append(" ");
        }

        String[] splitSpells = joinedArgs.toString().trim().split(",");
        List<Spell> spells = new ArrayList<>();

        for(String s : splitSpells) {
            Spell spell = SpellController.INSTANCE.getSpellByName(s);

            if(spell == null) {
                MessageManager.getManager(sender).sendMessage(MessageManager.MessageType.BAD, String.format("Spell %s not found. Will not be included", s));
            } else {
                spells.add(spell);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                List<DuelSpell> duelSpells = Connector.getInstance().getOrInsertSpells(spells);

                if(args[0].equalsIgnoreCase("add")) {
                    duelSpells.forEach(a::addSpellToBlacklist);
                    Connector.getInstance().addSpellsToArena(duelSpells, a);
                    MessageManager.getManager(sender).sendMessage(MessageManager.MessageType.GOOD, "Spells added to blacklist");
                } else {
                    duelSpells.forEach(a::removeSpellFromBlackList);
                    Connector.getInstance().removeSpellsFromArena(duelSpells, a);
                    MessageManager.getManager(sender).sendMessage(MessageManager.MessageType.GOOD, "Spells removed from blacklist");
                }
            }
        }.runTaskAsynchronously(DADuels.getInstance());
    }
}
