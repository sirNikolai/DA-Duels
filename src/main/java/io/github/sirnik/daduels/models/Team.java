package io.github.sirnik.daduels.models;

import java.util.LinkedList;
import java.util.Queue;

public class Team {

    private long index;

    private Queue<DuelPlayer> players;

    private String name;

    public Team(String teamName) {
        this.players = new LinkedList<>();
        this.index = -1;
        this.name = teamName;
    }

    
}
