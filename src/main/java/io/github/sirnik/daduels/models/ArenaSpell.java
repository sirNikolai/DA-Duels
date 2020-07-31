package io.github.sirnik.daduels.models;

import io.github.nikmang.daspells.spells.Spell;

import java.util.Collection;
import java.util.Collections;

/**
 * Holder object for the join between {@linkplain DuelArena} and {@linkplain DuelSpell}.
 */
public class ArenaSpell {

    private DuelArena arena;
    private Collection<DuelSpell> spells;

    public ArenaSpell(DuelArena arena, Collection<DuelSpell> spells) {
        this.arena = arena;
        this.spells = spells;
    }

    public DuelArena getArena() {
        return arena;
    }

    public Collection<DuelSpell> getSpells() {
        return Collections.unmodifiableCollection(spells);
    }

    public void addSpell(DuelSpell spell) {
        spells.add(spell);
    }
}
