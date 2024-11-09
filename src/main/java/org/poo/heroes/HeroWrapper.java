package org.poo.heroes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class HeroWrapper {
    private HeroWrapper() {
    }

    /**
     * Converts a hero to an ObjectNode.
     * @param hero hero to be converted
     * @return the hero as an ObjectNode
     */
    public static ObjectNode toObjectNode(final Hero hero) {
        ObjectNode wrapper = new ObjectNode(new ObjectMapper().getNodeFactory());
        wrapper.put("mana", hero.getMana());
        wrapper.put("description", hero.getDescription());

        ArrayNode colorsArray = wrapper.putArray("colors");
        for (String color : hero.getColors()) {
            colorsArray.add(color);
        }

        wrapper.put("name", hero.getName());
        wrapper.put("health", hero.getHealth());

        return wrapper;
    }
}
