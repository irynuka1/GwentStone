package org.poo.gamelogic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;
import org.poo.fileio.Coordinates;
import org.poo.fileio.GameInput;
import org.poo.fileio.DecksInput;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.CardWrapper;
import org.poo.heroes.Hero;
import org.poo.heroes.HeroWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public final class Game {
    private static final int NUMBER_OF_ROWS = 4;
    private static final int NUMBER_OF_COLUMNS = 5;
    private static final int MAX_MANA = 10;

    private static ArrayList<CardInput> playerOneDeck = null;
    private static ArrayList<CardInput> playerTwoDeck = null;

    private static Hero playerOneHero = null;
    private static Hero playerTwoHero = null;

    private static int firstHandSize;
    private static int secondHandSize;

    private static Boolean playerTurn = null;

    private static int firstMana;
    private static int secondMana;

    private static CardInput[][] table;

    private static int manaCounter;
    private static int newRoundCheck;

    private static int totalGamesPlayed;
    private static int p1Wins;
    private static int p2Wins;

    private static ArrayNode output = null;

    private static boolean gameEnded;

    private Game() {
    }

    public static int getP1Wins() {
        return p1Wins;
    }

    public static int getP2Wins() {
        return p2Wins;
    }

    /**
     * Initializes the game with the given input
     * @param gameInput the input for the game
     * @param p1 player one's decks
     * @param p2 player two's decks
     * @param gamesPlayed the number of games played
     * @param playerOneWins the number of wins for player one
     * @param playerTwoWins the number of wins for player two
     */
    public static void initNewGame(final GameInput gameInput, final DecksInput p1,
                                   final DecksInput p2, final int gamesPlayed,
                                   final int playerOneWins, final int playerTwoWins) {
        firstMana = 1;
        secondMana = 1;
        firstHandSize = 1;
        secondHandSize = 1;
        manaCounter = 1;
        newRoundCheck = 0;

        playerOneDeck = new ArrayList<>();
        for (CardInput card : p1.getDecks().get(gameInput.getStartGame().getPlayerOneDeckIdx())) {
            playerOneDeck.add(new CardInput(card));
        }

        playerTwoDeck = new ArrayList<>();
        for (CardInput card : p2.getDecks().get(gameInput.getStartGame().getPlayerTwoDeckIdx())) {
            playerTwoDeck.add(new CardInput(card));
        }

        Collections.shuffle(playerOneDeck,
                new Random(gameInput.getStartGame().getShuffleSeed()));

        Collections.shuffle(playerTwoDeck,
                new Random(gameInput.getStartGame().getShuffleSeed()));

        playerOneHero = gameInput.getStartGame().getPlayerOneHero();
        playerTwoHero = gameInput.getStartGame().getPlayerTwoHero();

        playerTurn = gameInput.getStartGame().getStartingPlayer() == 1;

        table = new CardInput[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];

        Game.totalGamesPlayed = gamesPlayed;
        Game.p1Wins = playerOneWins;
        Game.p2Wins = playerTwoWins;

        gameEnded = false;
    }

    /**
     * Selects the action to be performed
     * @param actionsInput the action input
     */
    public static void playerActions(final ActionsInput actionsInput) {
        ActionSelector.selectAction(actionsInput.getCommand(), actionsInput);
    }

    /**
     * Returns the hand of the player
     * @param deck the player's deck
     * @param handSize the hand size
     * @return a subarray of the deck from 0 to handSize
     */
    public static ArrayList<CardInput> getHand(final ArrayList<CardInput> deck,
                                               final int handSize) {
        if (deck.size() < handSize) {
            return new ArrayList<>(deck);
        }

        return new ArrayList<>(deck.subList(0, handSize));
    }

    /**
     * Returns the deck of the player
     * @param deck the player's deck
     * @param handSize the hand size
     * @return a subarray of the deck from handSize to the end
     */
    public static ArrayList<CardInput> getDeck(final ArrayList<CardInput> deck,
                                               final int handSize) {
        if (deck.size() < handSize) {
            return new ArrayList<>();
        }

        return new ArrayList<>(deck.subList(handSize, deck.size()));
    }

    /**
     * Starts the game and goes through all the actions
     * @param gameInput the input for the game
     */
    public static void startGame(final GameInput gameInput) {
        for (int i = 0; i < gameInput.getActions().size(); i++) {
            playerActions(gameInput.getActions().get(i));
        }
    }

    /**
     * Sets the output object
     * @param out the output object
     */
    public static void setOutputObject(final ArrayNode out) {
        Game.output = out;
    }

    /**
     * Ends the player's turn and sets the required variables
     */
    public static void endPlayerTurn() {
        if (gameEnded) {
            return;
        }

        Helpers.resetStatus(table, playerTurn ? 1 : 2, playerTurn ? playerOneHero : playerTwoHero);
        playerTurn = !playerTurn;

        newRoundCheck++;
        if (newRoundCheck == 2) {
            newRoundCheck = 0;
            manaCounter = Math.min(manaCounter + 1, MAX_MANA);
            firstMana += manaCounter;
            firstHandSize++;
            secondMana += manaCounter;
            secondHandSize++;
        }
    }

    /**
     * Outputs the player's turn and the command
     * @param actionsInput the input for the actions
     */
    public static void getPlayerTurn(final ActionsInput actionsInput) {
        ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());

        wrapper.put("command", actionsInput.getCommand());
        wrapper.put("output", playerTurn ? 1 : 2);

        output.add(wrapper);
    }

    /**
     * Outputs the player's deck and the command
     * @param actionsInput the input for the actions
     */
    public static void getPlayerDeck(final ActionsInput actionsInput) {
        int playerIdx = actionsInput.getPlayerIdx();
        int handSize = playerIdx == 1 ? firstHandSize : secondHandSize;
        ArrayList<CardInput> playerDeck = getDeck(playerIdx == 1 ? playerOneDeck : playerTwoDeck,
                handSize);

        ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        wrapper.put("command", "getPlayerDeck");
        wrapper.put("playerIdx", actionsInput.getPlayerIdx());

        ArrayNode deckArray = wrapper.putArray("output");
        for (CardInput card : playerDeck) {
            deckArray.add(new CardWrapper().toObjectNode(card));
        }

        output.add(wrapper);
    }

    /**
     * Outputs the player's hero and the command
     * @param actionsInput the input for the actions
     */
    public static void getPlayerHero(final ActionsInput actionsInput) {
        Hero playerHero = actionsInput.getPlayerIdx() == 1 ? playerOneHero : playerTwoHero;

        ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        wrapper.put("command", "getPlayerHero");
        wrapper.put("playerIdx", actionsInput.getPlayerIdx());
        wrapper.set("output", HeroWrapper.toObjectNode(playerHero));

        output.add(wrapper);
    }

    /**
     * Outputs the player's hand and the command
     * @param actionsInput the input for the actions
     */
    public static void getCardsInHand(final ActionsInput actionsInput) {
        int playerIdx = actionsInput.getPlayerIdx();
        int handSize = playerIdx == 1 ? firstHandSize : secondHandSize;
        ArrayList<CardInput> playerDeck = playerIdx == 1 ? playerOneDeck : playerTwoDeck;
        ArrayList<CardInput> hand = getHand(playerDeck, handSize);

        ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        wrapper.put("command", "getCardsInHand");
        wrapper.put("playerIdx", actionsInput.getPlayerIdx());

        ArrayNode handArray = wrapper.putArray("output");
        for (CardInput card : hand) {
            handArray.add(new CardWrapper().toObjectNode(card));
        }

        output.add(wrapper);
    }

    /**
     * Places a card on the table and if the card cannot be placed, outputs an error
     * @param actionsInput the input for the actions
     */
    public static void placeCard(final ActionsInput actionsInput) {
        if (gameEnded) {
            return;
        }

        int playerIdx = playerTurn ? 1 : 2;
        int mana = playerTurn ? firstMana : secondMana;
        int handSize = playerTurn ? firstHandSize : secondHandSize;
        ArrayList<CardInput> playerDeck = playerTurn ? playerOneDeck : playerTwoDeck;
        ArrayList<CardInput> hand = getHand(playerDeck, handSize);
        CardInput cardToPlace = hand.get(actionsInput.getHandIdx());

        if (cardToPlace.getMana() > mana) {
            ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
            wrapper.put("command", "placeCard");
            wrapper.put("handIdx", actionsInput.getHandIdx());
            wrapper.put("error", "Not enough mana to place card on table.");

            output.add(wrapper);
            return;
        }

        if (playerTurn) {
            firstMana -= cardToPlace.getMana();
        } else {
            secondMana -= cardToPlace.getMana();
        }

        boolean cardPlaced = isCardPlaced(playerIdx, cardToPlace, playerDeck);
        if (!cardPlaced) {
            ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
            wrapper.put("command", "placeCard");
            wrapper.put("handIdx", actionsInput.getHandIdx());
            wrapper.put("error", "Cannot place card on table since row is full.");

            output.add(wrapper);
        }
    }

    /**
     * Checks if the card can be placed on the table
     * @param playerIdx the player's index
     * @param cardToPlace the card to place
     * @param playerDeck the player's deck
     * @return true if the card can be placed, false otherwise
     */
    private static boolean isCardPlaced(final int playerIdx, final CardInput cardToPlace,
                                        final ArrayList<CardInput> playerDeck) {
        int row = Helpers.getRow(cardToPlace, playerIdx);

        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            if (table[row][i] == null) {
                table[row][i] = cardToPlace;
                playerDeck.remove(cardToPlace);

                if (playerTurn) {
                    firstHandSize--;
                } else {
                    secondHandSize--;
                }
                return true;
            }
        }

        return false;
    }

    /**
     * Outputs the cards on the table and the command
     */
    public static void getCardsOnTable() {
        ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        wrapper.put("command", "getCardsOnTable");
        ArrayNode tableArray = wrapper.putArray("output");

        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            ArrayNode rowArray = tableArray.addArray();
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                if (table[i][j] != null) {
                    rowArray.add(new CardWrapper().toObjectNode(table[i][j]));
                }
            }
        }

        output.add(wrapper);
    }

    /**
     * Outputs the player's mana and the command
     * @param actionsInput the input for the actions
     */
    public static void getPlayerMana(final ActionsInput actionsInput) {
        ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        wrapper.put("command", "getPlayerMana");
        wrapper.put("playerIdx", actionsInput.getPlayerIdx());
        wrapper.put("output", actionsInput.getPlayerIdx() == 1 ? firstMana : secondMana);

        output.add(wrapper);
    }

    /**
     * Prepares the wrapper for the actions
     * @param attacker the attacker's coordinates
     * @param attacked the coordinates of the card that is attacked
     * @param command the command to be executed
     * @return the wrapper object
     */
    public static ObjectNode wrapperHelper(final Coordinates attacker, final Coordinates attacked,
                                           final String command) {
        ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        if (command.equals("cardUsesAttack")) {
            wrapper.put("command", "cardUsesAttack");
        } else {
            wrapper.put("command", "cardUsesAbility");
        }

        ObjectNode attackerWrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        attackerWrapper.put("x", attacker.getX());
        attackerWrapper.put("y", attacker.getY());
        ObjectNode attackedWrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        attackedWrapper.put("x", attacked.getX());
        attackedWrapper.put("y", attacked.getY());

        wrapper.set("cardAttacker", attackerWrapper);
        wrapper.set("cardAttacked", attackedWrapper);

        return wrapper;
    }

    /**
     * Checks if the card can attack and if it can, attacks the card at the given coordinates
     * If the card cannot attack, outputs an error
     * @param actionsInput the input for the actions
     */
    public static void cardUsesAttack(final ActionsInput actionsInput) {
        if (gameEnded) {
            return;
        }

        Coordinates attackerCoords = actionsInput.getCardAttacker();
        Coordinates attackedCoords = actionsInput.getCardAttacked();
        CardInput attackerCard = table[attackerCoords.getX()][attackerCoords.getY()];
        CardInput attackedCard = table[attackedCoords.getX()][attackedCoords.getY()];

        ObjectNode wrapper = wrapperHelper(attackerCoords, attackedCoords, "cardUsesAttack");

        int playerIdx = playerTurn ? 1 : 2;
        if (!Helpers.correctAttack(playerIdx, attackedCoords)) {
            wrapper.put("error", "Attacked card does not belong to the enemy.");
            output.add(wrapper);
            return;
        }

        if (Helpers.checkFrozenAndAttacked(attackerCard, wrapper, output)) {
            return;
        }

        if (Helpers.checkForTank(table, playerIdx) && !Helpers.isTank(attackedCard)) {
            wrapper.put("error", "Attacked card is not of type 'Tank'.");
            output.add(wrapper);
            return;
        }

        attackedCard.setHealth(attackedCard.getHealth() - attackerCard.getAttackDamage());
        attackerCard.setHasAttacked(true);

        if (attackedCard.getHealth() <= 0) {
            for (int i = attackedCoords.getY(); i < NUMBER_OF_ROWS; i++) {
                table[attackedCoords.getX()][i] = table[attackedCoords.getX()][i + 1];
            }
            table[attackedCoords.getX()][NUMBER_OF_ROWS] = null;
        }
    }


    /**
     * Outputs the card at the given position and the command
     * @param actionsInput the input for the actions
     */
    public static void getCardAtPosition(final ActionsInput actionsInput) {
        ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        wrapper.put("command", "getCardAtPosition");
        wrapper.put("x", actionsInput.getX());
        wrapper.put("y", actionsInput.getY());

        int xCoord = actionsInput.getX();
        int yCoord = actionsInput.getY();

        if (table[xCoord][yCoord] == null) {
            wrapper.put("output", "No card available at that position.");
            output.add(wrapper);
            return;
        }

        wrapper.set("output", new CardWrapper().toObjectNode(table[xCoord][yCoord]));

        output.add(wrapper);
    }

    /**
     * Checks if the card can use its ability and if it can, uses the ability on the given card
     * If the card cannot use its ability, outputs an error
     * @param actionsInput the input for the actions
     */
    public static void cardUsesAbility(final ActionsInput actionsInput) {
        if (gameEnded) {
            return;
        }

        Coordinates attackerCoords = actionsInput.getCardAttacker();
        Coordinates attackedCoords = actionsInput.getCardAttacked();

        CardInput attackerCard = table[attackerCoords.getX()][attackerCoords.getY()];
        CardInput attackedCard = table[attackedCoords.getX()][attackedCoords.getY()];

        ObjectNode wrapper = wrapperHelper(attackerCoords, attackedCoords, "cardUsesAbility");

        if (Helpers.checkFrozenAndAttacked(attackerCard, wrapper, output)) {
            return;
        }

        if (attackerCard.getName().equals("Disciple")) {
            boolean isAlliedCard = (playerTurn && attackedCoords.getX() >= 2)
                    || (!playerTurn && attackedCoords.getX() <= 1);
            if (!isAlliedCard) {
                wrapper.put("error", "Attacked card does not belong to the current player.");
                output.add(wrapper);
                return;
            }
        }

        if (attackerCard.getName().equals("The Cursed One")
                || attackerCard.getName().equals("Miraj")
                || attackerCard.getName().equals("The Ripper")) {

            boolean isEnemyCard = (playerTurn && attackedCoords.getX() <= 1)
                    || (!playerTurn && attackedCoords.getX() >= 2);
            if (!isEnemyCard) {
                wrapper.put("error", "Attacked card does not belong to the enemy.");
                output.add(wrapper);
                return;
            }

            int playerIdx = playerTurn ? 1 : 2;
            if (Helpers.checkForTank(table, playerIdx) && !Helpers.isTank(attackedCard)) {
                wrapper.put("error", "Attacked card is not of type 'Tank'.");
                output.add(wrapper);
                return;
            }
        }

        AbilitySelector.selectAbility(attackerCard.getName(), attackerCard, attackedCard);
        attackerCard.setHasAttacked(true);

        if (attackerCard.getName().equals("The Cursed One") && attackedCard.getHealth() <= 0) {
            for (int i = attackedCoords.getY(); i < NUMBER_OF_ROWS; i++) {
                table[attackedCoords.getX()][i] = table[attackedCoords.getX()][i + 1];
            }
            table[attackedCoords.getX()][NUMBER_OF_ROWS] = null;
        }
    }

    /**
     * Checks if the card can attack the hero and if it can, attacks the hero
     * If the card cannot attack the hero, outputs an error
     * If the hero's health reaches 0, the game ends
     * @param actionsInput the input for the actions
     */
    public static void useAttackHero(final ActionsInput actionsInput) {
        if (gameEnded) {
            return;
        }

        ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        wrapper.put("command", "useAttackHero");

        Coordinates attackerCoords = actionsInput.getCardAttacker();
        CardInput attackerCard = table[attackerCoords.getX()][attackerCoords.getY()];

        ObjectNode attackerWrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        attackerWrapper.put("x", attackerCoords.getX());
        attackerWrapper.put("y", attackerCoords.getY());

        wrapper.set("cardAttacker", attackerWrapper);

        if (Helpers.checkFrozenAndAttacked(attackerCard, wrapper, output)) {
            return;
        }

        int playerIdx = playerTurn ? 1 : 2;
        if (Helpers.checkForTank(table, playerIdx)) {
            wrapper.put("error", "Attacked card is not of type 'Tank'.");
            output.add(wrapper);
            return;
        }

        Hero attackedHero = playerTurn ? playerTwoHero : playerOneHero;
        attackedHero.setHealth(attackedHero.getHealth() - attackerCard.getAttackDamage());
        attackerCard.setHasAttacked(true);

        if (attackedHero.getHealth() <= 0) {
            gameEnded = true;

            ObjectNode gameEnd = new ObjectNode(new ObjectMapper().getNodeFactory());
            if (playerTurn) {
                gameEnd.put("gameEnded", "Player one killed the enemy hero.");
                p1Wins++;
            } else {
                gameEnd.put("gameEnded", "Player two killed the enemy hero.");
                p2Wins++;
            }

            output.add(gameEnd);
        }
    }

    /**
     * Checks if the hero can use its ability and if it can, uses the ability
     * If the hero cannot attack, outputs an error
     * @param actionsInput the input for the actions
     */
    public static void useHeroAbility(final ActionsInput actionsInput) {
        if (gameEnded) {
            return;
        }

        ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        wrapper.put("command", "useHeroAbility");

        Hero hero = playerTurn ? playerOneHero : playerTwoHero;
        int mana = playerTurn ? firstMana : secondMana;
        int affectedRow = actionsInput.getAffectedRow();

        wrapper.put("affectedRow", affectedRow);

        if (hero.getMana() > mana) {
            wrapper.put("error", "Not enough mana to use hero's ability.");
            output.add(wrapper);
            return;
        }

        if (hero.isHasAttacked()) {
            wrapper.put("error", "Hero has already attacked this turn.");
            output.add(wrapper);
            return;
        }

        if (hero.getName().equals("Lord Royce") || hero.getName().equals("Empress Thorina")) {
            if (playerTurn && (affectedRow == 2 || affectedRow == 3)) {
                wrapper.put("error", "Selected row does not belong to the enemy.");
                output.add(wrapper);
                return;
            } else if (!playerTurn && (affectedRow == 0 || affectedRow == 1)) {
                wrapper.put("error", "Selected row does not belong to the enemy.");
                output.add(wrapper);
                return;
            }
        }

        if (hero.getName().equals("General Kocioraw") || hero.getName().equals("King Mudface")) {
            if (playerTurn && (affectedRow == 0 || affectedRow == 1)) {
                wrapper.put("error", "Selected row does not belong to the current player.");
                output.add(wrapper);
                return;
            } else if (!playerTurn && (affectedRow == 2 || affectedRow == 3)) {
                wrapper.put("error", "Selected row does not belong to the current player.");
                output.add(wrapper);
                return;
            }
        }

        if (playerTurn) {
            firstMana -= hero.getMana();
        } else {
            secondMana -= hero.getMana();
        }
        hero.setHasAttacked(true);
        HeroAbilitySelector.selectHeroAbility(hero.getName(), table, affectedRow);
    }

    /**
     * Outputs the frozen cards on the table and the command
     */
    public static void getFrozenCardsOnTable() {
        ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        wrapper.put("command", "getFrozenCardsOnTable");

        ArrayNode frozenCards = wrapper.putArray("output");
        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                if (table[i][j] != null && table[i][j].isFrozen()) {
                    ObjectNode card = new CardWrapper().toObjectNode(table[i][j]);
                    frozenCards.add(card);
                }
            }
        }

        output.add(wrapper);
    }

    /**
     * Outputs the first player's wins and the command
     */
    public static void getPlayerOneWins() {
        ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        wrapper.put("command", "getPlayerOneWins");
        wrapper.put("output", p1Wins);

        output.add(wrapper);
    }

    /**
     * Outputs the second player's wins and the command
     */
    public static void getPlayerTwoWins() {
        ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        wrapper.put("command", "getPlayerTwoWins");
        wrapper.put("output", p2Wins);

        output.add(wrapper);
    }

    /**
     * Outputs the total games played and the command
     */
    public static void getTotalGamesPlayed() {
        ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        wrapper.put("command", "getTotalGamesPlayed");
        wrapper.put("output", totalGamesPlayed);

        output.add(wrapper);
    }
}
