package org.poo.gamelogic;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;
import org.poo.fileio.Coordinates;
import org.poo.heroes.Hero;

public final class Helpers {
    private static final int NUMBER_OF_COLUMNS = 5;

    private Helpers() {
    }

    /**
     * Finds the row for a given card to be placed on the table.
     * @param card the card
     * @param playerIdx the player index
     * @return the row index
     */
    public static int getRow(final CardInput card, final int playerIdx) {
        boolean isBackRowCard = card.getName().equals("Sentinel")
                || card.getName().equals("Berserker")
                || card.getName().equals("The Cursed One") || card.getName().equals("Disciple");

        return isBackRowCard ? (playerIdx == 1 ? 3 : 0) : (playerIdx == 1 ? 2 : 1);
    }

    /**
     * Checks if a card is a tank.
     * @param card the card
     * @return true if the card is a tank, false otherwise
     */
    public static boolean isTank(final CardInput card) {
        return card.getName().equals("Goliath") || card.getName().equals("Warden");
    }

    /**
     * Checks if the attack coordinates are valid.
     * @param playerIdx the player index
     * @param coords the coordinates of the attack
     * @return true if the attack is valid, false otherwise
     */
    public static boolean correctAttack(final int playerIdx, final Coordinates coords) {
        if (playerIdx == 1) {
            return coords.getX() == 0 || coords.getX() == 1;
        } else {
            return coords.getX() == 2 || coords.getX() == 3;
        }
    }

    /**
     * Checks if the enemy has a tank.
     * @param table the playing table
     * @param playerIdx the player index
     * @return true if the enemy has a tank, false otherwise
     */
    public static boolean checkForTank(final CardInput[][] table, final int playerIdx) {
        if (playerIdx == 1) {
            for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
                if (table[1][i] != null && isTank(table[1][i])) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
                if (table[2][i] != null && isTank(table[2][i])) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Resets the hasAttacked and the frozen status of the cards and hero of a player.
     * @param table the playing table
     * @param playerIdx the player index
     * @param playerHero the player's hero
     */
    public static void resetStatus(final CardInput[][] table, final int playerIdx,
                                   final Hero playerHero) {
        playerHero.setHasAttacked(false);
        int startRow = playerIdx == 1 ? 2 : 0;

        for (int i = startRow; i < startRow + 2; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                if (table[i][j] != null) {
                    table[i][j].setFrozen(false);
                    table[i][j].setHasAttacked(false);
                }
            }
        }
    }

    /**
     * Checks for the case when the attacker card is frozen or has already attacked.
     * @param card the attacker card
     * @param wrapper the wrapper for the output (contains the error message)
     * @param output the output array
     * @return true if the attacker card is frozen or has already attacked, false otherwise
     */
    public static boolean checkFrozenAndAttacked(final CardInput card, final ObjectNode wrapper,
                                                 final ArrayNode output) {
        if (card.isFrozen()) {
            wrapper.put("error", "Attacker card is frozen.");
            output.add(wrapper);
            return true;
        }

        if (card.isHasAttacked()) {
            wrapper.put("error", "Attacker card has already attacked this turn.");
            output.add(wrapper);
            return true;
        }

        return false;
    }

    /**
     * Uses Disciple's ability.
     * @param ally the ally card
     */
    public static void discipleAbility(final CardInput ally) {
        ally.setHealth(ally.getHealth() + 2);
    }

    /**
     * Uses Berserker's ability.
     * @param enemy the enemy card
     */
    public static void ripperAbility(final CardInput enemy) {
        enemy.setAttackDamage(Math.max(0, enemy.getAttackDamage() - 2));
    }

    /**
     * Uses The Cursed One's ability.
     * @param enemy the enemy card
     */
    public static void cursedOneAbility(final CardInput enemy) {
        int temp = enemy.getHealth();
        enemy.setHealth(enemy.getAttackDamage());
        enemy.setAttackDamage(temp);
    }

    /**
     * Uses Miraj's ability.
     * @param attacker the attacker card
     * @param enemy the enemy card
     */
    public static void mirajAbility(final CardInput attacker, final CardInput enemy) {
        int temp = attacker.getHealth();
        attacker.setHealth(enemy.getHealth());
        enemy.setHealth(temp);
    }

    /**
     * Uses Lord Royce's ability.
     * @param table the playing table
     * @param affectedRow the affected row
     */
    public static void royceAbility(final CardInput[][] table, final int affectedRow) {
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            if (table[affectedRow][i] != null) {
                table[affectedRow][i].setFrozen(true);
            }
        }
    }

    /**
     * Uses Empress Thorina's ability.
     * @param table the playing table
     * @param affectedRow the affected row
     */
    public static void thorinaAbility(final CardInput[][] table, final int affectedRow) {
        int maxHealth = 0;
        int maxHealthIdx = -1;

        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            if (table[affectedRow][i] != null && table[affectedRow][i].getHealth() > maxHealth) {
                maxHealth = table[affectedRow][i].getHealth();
                maxHealthIdx = i;
            }
        }

        if (maxHealthIdx != -1) {
            table[affectedRow][maxHealthIdx] = null;
            for (int i = maxHealthIdx + 1; i < NUMBER_OF_COLUMNS; i++) {
                table[affectedRow][i - 1] = table[affectedRow][i];
                table[affectedRow][i] = null;
            }
        }
    }

    /**
     * Uses King Mudface's ability.
     * @param table the playing table
     * @param affectedRow the affected row
     */
    public static void mudfaceAbility(final CardInput[][] table, final int affectedRow) {
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            if (table[affectedRow][i] != null) {
                table[affectedRow][i].setHealth(table[affectedRow][i].getHealth() + 1);
            }
        }
    }

    /**
     * Uses General Kocioraw's ability.
     * @param table the playing table
     * @param affectedRow the affected row
     */
    public static void kociorawAbility(final CardInput[][] table, final int affectedRow) {
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            if (table[affectedRow][i] != null) {
                table[affectedRow][i].setAttackDamage(table[affectedRow][i].getAttackDamage() + 1);
            }
        }
    }
}
