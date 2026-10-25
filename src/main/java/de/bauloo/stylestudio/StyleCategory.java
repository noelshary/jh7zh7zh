package de.bauloo.stylestudio;

import java.util.ArrayList;
import java.util.List;

public record StyleCategory(String name, List<String> styles) {

    /** Platzhalter-Daten. Spaeter aus resourcepacks/style-studio/ einlesen. */
    public static List<StyleCategory> defaults() {
        String[] names = {"Crystal", "Obsidian", "Anchor", "Armor", "Tools", "Gaps & Pearls",
                "Totem", "HUD", "Inventory", "Fire", "Sky", "Deepslate", "Stone", "Snow",
                "Grass", "Shield", "Mace", "Cobwebs"};
        List<StyleCategory> out = new ArrayList<>();
        for (String n : names) {
            List<String> s = new ArrayList<>();
            s.add("Vanilla");
            for (int i = 1; i <= 11; i++) {
                s.add(n + " " + i);
            }
            out.add(new StyleCategory(n, s));
        }
        return out;
    }
}
