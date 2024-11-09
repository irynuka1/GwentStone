package org.poo.fileio;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter@Setter
public final class CardInput {
    private int mana;
    private int attackDamage;
    private int health;
    private String description;
    private ArrayList<String> colors;
    private String name;
    private boolean frozen = false;
    private boolean hasAttacked = false;

    public CardInput() {
    }

    public CardInput(final CardInput copy) {
        this.mana = copy.mana;
        this.attackDamage = copy.attackDamage;
        this.health = copy.health;
        this.description = copy.description;
        this.colors = new ArrayList<>(copy.colors);
        this.name = copy.name;
        this.frozen = copy.frozen;
        this.hasAttacked = copy.hasAttacked;
    }

    @Override
    public String toString() {
        return "CardInput{"
                +  "mana="
                + mana
                +  ", attackDamage="
                + attackDamage
                + ", health="
                + health
                +  ", description='"
                + description
                + '\''
                + ", colors="
                + colors
                + ", name='"
                + name
                + '\''
                + '}';
    }
}
