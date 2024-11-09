package org.poo.gamelogic;

import org.poo.fileio.CardInput;

public final class AbilitySelector {
    private AbilitySelector() {
    }

    /**
     * Selects the ability of a card.
     * @param cardName the name of the attackerCard
     * @param card1 the attackerCard
     * @param card2 the attackedCard
     */
    public static void selectAbility(final String cardName, final CardInput card1,
                                     final CardInput card2) {
        switch (cardName) {
            case "Disciple":
                Helpers.discipleAbility(card2);
                break;
            case "The Ripper":
                Helpers.ripperAbility(card2);
                break;
            case "The Cursed One":
                Helpers.cursedOneAbility(card2);
                break;
            case "Miraj":
                Helpers.mirajAbility(card1, card2);
                break;
            default:
                break;
        }
    }
}
