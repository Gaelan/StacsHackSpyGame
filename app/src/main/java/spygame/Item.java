package spygame;

public class Item {
    String name;
    String description;

    Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }
}