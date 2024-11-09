package org.poo.heroes;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter@Setter
public final class Hero {
    private static final int MAX_HEALTH = 30;

    private int mana;
    private int health = MAX_HEALTH;
    private String description;
    private ArrayList<String> colors;
    private String name;
    private boolean hasAttacked = false;

    public Hero() {
    }
}
