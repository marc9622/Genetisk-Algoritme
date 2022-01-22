import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Backpack {

    static int maxWeight = 5000;
    static List<Item> items = new ArrayList<Item>(Arrays.asList(new Item[]{
        new Item("Kort", 90, 150),
        new Item("Kompas", 130, 35),
        new Item("Vand", 1530, 200),
        new Item("Sandwich", 500, 160),
        new Item("Sukker", 150, 60),
        new Item("Dåsemad", 680, 45),
        new Item("Banan", 270, 60),
        new Item("Æble", 390, 40),
        new Item("Ost", 230, 30),
        new Item("Øl", 520, 10),
        new Item("Solcreme", 110, 70),
        new Item("Kamera", 320, 30),
        new Item("T-shirt", 240, 15),
        new Item("Bukser", 480, 10),
        new Item("Paraply", 730, 40),
        new Item("Vandtætte bukser", 420, 70),
        new Item("Vandtæt overtøj", 430, 75),
        new Item("Pung", 220, 80),
        new Item("Solbriller", 70, 20),
        new Item("Håndklæde", 180, 12),
        new Item("Sokker", 40, 50),
        new Item("Bog", 300, 10),
        new Item("Notesbog", 900, 1),
        new Item("Telt", 2000, 150)
    }));

    public static int getItemSize(List<Boolean> list) {
        return list.stream().reduce(0, (a, b) -> a + (b ? 1 : 0), (a, b) -> a + b);
    }

    public static int getItemSizeFull() {
        return items.size();
    }
    
    public static String toString(List<Boolean> list) {
        return list.stream()
                   .map(a -> a ? 1 : 0)
                   .collect(Collectors.toList())
                   .toString() +
                " has a price of " + getPrice(list) +
                ", weighs " + getWeight(list) +
                " and contains " + getNames(list) + ".";
    }

    public static String getNames(List<Boolean> list) {
        return IntStream.range(0, getItemSizeFull())
                        .filter(i -> list.get(i))
                        .mapToObj(i -> items.get(i).getName())
                        .reduce("", (a, b) -> a + "[" + b + "]", (a, b) -> a + b);
    }

    public static String getNamesFull() {
        return items.stream()
                    .map(i -> i.getName())
                    .reduce("", (a, b) -> a + "[" + b + "]", (a, b) -> a + b);
    }

    public static int getWeight(List<Boolean> list) {
        return IntStream.range(0, getItemSizeFull())
                        .filter(i -> list.get(i))
                        .mapToObj(i -> items.get(i))
                        .reduce(0, (a, b) -> a + b.getWeight(), (a, b) -> a + b);
    }

    public static int getPrice(List<Boolean> list) {
        return IntStream.range(0, getItemSizeFull())
                        .filter(i -> list.get(i))
                        .mapToObj(i -> items.get(i))
                        .reduce(0, (a, b) -> a + b.getPrice(), (a, b) -> a + b);
    }

    public static int getPriceIfAllowed(List<Boolean> list) {
        return Backpack.getWeight(list) > maxWeight ? 0 : Backpack.getPrice(list);}

    public static void addItem(Item item) {
        items.add(item);
    }

    public static void removeItem(String name) {
        Optional<Item> item = items.stream().filter(i -> i.getName().equals(name)).findFirst();
        if(item.isPresent())
            items.remove(item.get());
        else
            throw new IllegalArgumentException("Item with that name doesn't exist");
    }

    public static void removeItem(int index) {
        if(index >= 0 && index < getItemSizeFull())
            items.remove(index);
        else
            throw new IllegalArgumentException("Index out of bounds. Index was " + index + ".");
    }

    public static int getMaxWeight() {
        return maxWeight;
    }

    public static void setMaxWeight(int maxWeight) {
        Backpack.maxWeight = maxWeight;
    }

    public static class Item {
        
        private String name;
        private int weight;
        private int price;

        public Item(String name, int weight, int price) {
            this.name = name;
            this.weight = weight;
            this.price = price;
        }

        public String getName() {return name;}
        public int getWeight() {return weight;}
        public int getPrice() {return price;}

        public void setName(String name) {this.name = name;}
        public void setWeight(int weight) {this.weight = weight;}
        public void setPrice(int price) {this.price = price;}

        public String toString() {
            return "[" + name + "," + weight + "," + price + "]";
        }

    }

}