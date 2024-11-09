package org.poo.fileio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class CardWrapper {
    /**
     * Takes a card and creates a JSON object from it.
     * @param card the card to be converted
     * @return the JSON object
     */
    public ObjectNode toObjectNode(final CardInput card) {
        ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        wrapper.put("mana", card.getMana());
        wrapper.put("attackDamage", card.getAttackDamage());
        wrapper.put("health", card.getHealth());
        wrapper.put("description", card.getDescription());

        ArrayNode colorsArray = wrapper.putArray("colors");
        for (String color : card.getColors()) {
            colorsArray.add(color);
        }

        wrapper.put("name", card.getName());

        return wrapper;
    }
}
