package io.github.sirnik.daduels.models;

import io.github.nikmang.daspells.spells.Spell;

import java.util.Objects;

/**
 * Container for {@linkplain io.github.nikmang.daspells.spells.Spell} which contains database ID.
 */
public class DuelSpell {

    private String spellName;
    private long index;

    public DuelSpell(String spellName) {
        this.spellName = spellName;
        this.index = -1;
    }

    public DuelSpell(String spellName, long index) {
        this.spellName = spellName;
        this.index = index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getSpellName() {
        return spellName;
    }

    public long getIndex() {
        return index;
    }

    @Override
    public int hashCode() {
        return spellName.toUpperCase().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DuelSpell duelSpell = (DuelSpell) o;
        return spellName.equalsIgnoreCase(duelSpell.spellName);
    }
}
