package io.github.sirnik.daduels.listeners;

import io.github.nikmang.daspells.SpellData;
import io.github.nikmang.daspells.SpellPlayer;
import io.github.nikmang.daspells.events.SpellCastEvent;
import io.github.nikmang.daspells.utils.PlayerManager;
import io.github.sirnik.daduels.models.DuelArena;
import io.github.sirnik.daduels.utils.ArenaManager;
import io.github.sirnik.daduels.utils.MessageManager;
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

        SpellPlayer spellPlayer = PlayerManager.INSTANCE.getPlayerByUUID(event.getPlayer().getUniqueId());

        SpellData spellData = spellPlayer.getSpells().get(event.getSpell());

        spellData.setLastUsed(0L);
    }

}
