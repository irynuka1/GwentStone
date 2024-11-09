package org.poo.gamelogic;

import org.poo.fileio.CardInput;

public final class HeroAbilitySelector {
    private HeroAbilitySelector() {
    }

    /**
     * Selects the hero ability based on the hero name.
     * @param name the name of the hero
     * @param table the playing table
     * @param affectedRow the row affected by the ability
     */
    public static void selectHeroAbility(final String name, final CardInput[][] table,
                                         final int affectedRow) {
        switch (name) {
            case "Lord Royce":
                Helpers.royceAbility(table, affectedRow);
                break;
            case "Empress Thorina":
                Helpers.thorinaAbility(table, affectedRow);
                break;
            case "King Mudface":
                Helpers.mudfaceAbility(table, affectedRow);
                break;
            case "General Kocioraw":
                Helpers.kociorawAbility(table, affectedRow);
                break;
            default:
                break;
        }
    }
}
