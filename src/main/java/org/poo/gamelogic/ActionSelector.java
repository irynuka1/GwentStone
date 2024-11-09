package org.poo.gamelogic;

import org.poo.fileio.ActionsInput;

public final class ActionSelector {
    private ActionSelector() {
    }

    /**
     * Selects the action to be performed
     * @param action the name of the action
     * @param actionsInput the input for the action
     */
    public static void selectAction(final String action, final ActionsInput actionsInput) {
        switch (action) {
            case "endPlayerTurn":
                Game.endPlayerTurn();
                break;
            case "getCardsInHand":
                Game.getCardsInHand(actionsInput);
                break;
            case "cardUsesAttack":
                Game.cardUsesAttack(actionsInput);
                break;
            case "getPlayerHero":
                Game.getPlayerHero(actionsInput);
                break;
            case "getPlayerDeck":
                Game.getPlayerDeck(actionsInput);
                break;
            case "getPlayerTurn":
                Game.getPlayerTurn(actionsInput);
                break;
            case "placeCard":
                Game.placeCard(actionsInput);
                break;
            case "getPlayerMana":
                Game.getPlayerMana(actionsInput);
                break;
            case "getCardsOnTable":
                Game.getCardsOnTable();
                break;
            case "useHeroAbility":
                Game.useHeroAbility(actionsInput);
                break;
            case "getCardAtPosition":
                Game.getCardAtPosition(actionsInput);
                break;
            case "cardUsesAbility":
                Game.cardUsesAbility(actionsInput);
                break;
            case "useAttackHero":
                Game.useAttackHero(actionsInput);
                break;
            case "getFrozenCardsOnTable":
                Game.getFrozenCardsOnTable();
                break;
            case "getPlayerOneWins":
                Game.getPlayerOneWins();
                break;
            case "getPlayerTwoWins":
                Game.getPlayerTwoWins();
                break;
            case "getTotalGamesPlayed":
                Game.getTotalGamesPlayed();
                break;
            default:
                break;
        }
    }
}
