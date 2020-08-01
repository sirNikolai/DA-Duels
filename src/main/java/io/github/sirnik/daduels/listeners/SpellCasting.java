package io.github.sirnik.daduels.listeners;

import io.github.nikmang.daspells.SpellData;
import io.github.nikmang.daspells.SpellPlayer;
import io.github.nikmang.daspells.events.SpellCastEvent;
import io.github.nikmang.daspells.events.SpellHitBlockEvent;
import io.github.nikmang.daspells.events.SpellHitEntityEvent;
import io.github.nikmang.daspells.spells.Spell;
import io.github.nikmang.daspells.utils.PlayerManager;
import io.github.sirnik.daduels.models.DuelArena;
import io.github.sirnik.daduels.utils.ArenaManager;
import io.github.sirnik.daduels.utils.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SpellCasting implements Listener {

    private static final long GRACE_PERIOD = 2000L;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCast(SpellCastEvent event) {
        DuelArena a = ArenaManager.INSTANCE.getArenaForPlayer(event.getPlayer());
        if(a == null) {
            return;
        }

        if(a.getStartTime() + GRACE_PERIOD > System.currentTimeMillis()) {
            event.setCancelled(true);
            MessageManager.getManager(event.getPlayer()).sendMessage(MessageManager.MessageType.BAD, "2 second grace period!");
            return;
        }

        if(a.isSpellBlackListed(event.getSpell().toString())) {
            event.setCancelled(true);
            MessageManager.getManager(event.getPlayer()).sendMessage(MessageManager.MessageType.BAD, "Spell is banned!");
            return;
        }

        updateSpellData(event.getPlayer(), event.getSpell());
    }

    // These two are here because spells plugin is bugged and updates last used in 2 places so these are here until that updates
    @EventHandler
    public void onHitBlock(SpellHitBlockEvent event) {
        if(!ArenaManager.INSTANCE.isPlayerInGame(event.getPlayer())) {
            return;
        }

        updateSpellData(event.getPlayer(), event.getSpell());
    }

    @EventHandler
    public void onHitEntity(SpellHitEntityEvent event) {
        if(!ArenaManager.INSTANCE.isPlayerInGame(event.getPlayer())) {
            return;
        }

        updateSpellData(event.getPlayer(), event.getSpell());
    }

    private void updateSpellData(Player player, Spell spell) {
        SpellPlayer spellPlayer = PlayerManager.INSTANCE.getPlayerByUUID(player.getUniqueId());

        SpellData spellData = spellPlayer.getSpells().get(spell);

        spellData.setLastUsed(0L);
    }
}
