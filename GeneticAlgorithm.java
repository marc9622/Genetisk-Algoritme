import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GeneticAlgorithm {
    
    private static final Random random = new Random();

    /**
     * @param <Type> the object type of the genes.
     * @param generationAmount the number of generations to iterate through.
     * @param generationSize the number of children within each generation.
     * @param parentAmount the number of children used as parents for the next generation.
     * @param childSize the number of genes within each child.
     * @param geneSupplier a function that supplies random genes,
     * which is used to make the first generation of children and used to make mutations within the children.
     * @param childScorer a function that takes a child and gives a score to determine its effectiveness,
     * which is used to compare it to other children.
     * @return the best child after a specified number of generations.
     */
    public static <Type> List<Type>       getBestChildAfterGenerations  (int generationAmount, int generationSize, int parentAmount, int childSize,
                                                                         Supplier<Type> geneSupplier, Function<List<Type>, Integer> childScorer) {
        return getBestChild(makeGenerations(generationAmount, generationSize, parentAmount, childSize, geneSupplier, childScorer), childScorer);
    }

    /**
     * @param <Type> the object type of the genes.
     * @param generationAmount the number of generations to iterate through.
     * @param generationSize the number of children within each generation.
     * @param parentAmount the number of children used as parents for the next generation.
     * @param childSize the number of genes within each child.
     * @param geneSupplier a function that supplies random genes,
     * which is used to make the first generation of children and used to make mutations within the children.
     * @param childScorer a function that takes a child and gives a score to determine its effectiveness,
     * which is used to compare it to other children.
     * @return a new generation after a specified number of generations.
     */
    public static <Type> List<List<Type>> makeGenerations               (int generationAmount, int generationSize, int parentAmount, int childSize,
                                                                         Supplier<Type> geneSupplier, Function<List<Type>, Integer> childScorer) {
        List<List<Type>> generation = makeGenerationFromSupplier(generationSize, childSize, geneSupplier);
        generation = makeGenerationsFromGeneration(generation, generationAmount - 1, generation.size(), parentAmount, geneSupplier, childScorer);
        return generation;
    }

    /**
     * @param <Type> the object type of the genes.
     * @param generation the previous generation of which to make the new one.
     * @param generationAmount the number of generations to iterate through.
     * @param generationSize the number of children within each generation.
     * @param parentAmount the number of children used as parents for the next generation.
     * @param geneSupplier a function that supplies random genes,
     * which is used to make mutations within the children.
     * @param childScorer a function that takes a child and gives a score to determine its effectiveness,
     * which is used to compare it to other children.
     * @return a new generation from a previous generation after a specified number of generations.
     */
    public static <Type> List<List<Type>> makeGenerationsFromGeneration (List<List<Type>> generation, int generationAmount, int generationSize, int parentAmount,
                                                                         Supplier<Type> geneSupplier, Function<List<Type>, Integer> childScorer) {
        for(int i = 0; i < generationAmount; i++)
            generation = makeGenerationFromGeneration(generation, generationSize, parentAmount, geneSupplier, childScorer);
        return generation;
    }

    /**
     * @param <Type> the object type of the genes.
     * @param generationSize the number of children within the generation.
     * @param childSize the number of genes within each child.
     * @param geneSupplier a function that supplies random genes,
     * which is used to make the children.
     * @return a new generation made from the gene supplier.
     */
    public static <Type> List<List<Type>> makeGenerationFromSupplier    (int generationSize, int childSize, Supplier<Type> geneSupplier) {
        return Stream.generate(() -> makeChildFromSupplier(childSize, geneSupplier))
                     .limit(generationSize)
                     .collect(Collectors.toList());
    }

    /**
     * @param <Type> the object type of the genes.
     * @param generation the previous generation of which to make the new one.
     * @param generationSize the number of children within the generation.
     * @param parentAmount the amount of children used as parents for the new generation.
     * @param geneSupplier a function that supplies random genes,
     * which is used to make mutations within the children.
     * @param childScorer a function that takes a child and gives a score to determine its effectiveness,
     * which is used to compare it to other children.
     * @return a new generation made from the previous generation.
     */
    public static <Type> List<List<Type>> makeGenerationFromGeneration  (List<List<Type>> generation, int generationSize, int parentAmount,
                                                                         Supplier<Type> geneSupplier, Function<List<Type>, Integer> childScorer) {
        return makeGenerationFromParents(getBestChildren(generation, parentAmount, childScorer),
                                         generationSize,
                                         geneSupplier);
    }

    /**
     * @param <Type> the object type of the genes.
     * @param parents the parents of which to make the generation.
     * @param generationSize the number of children within the generation.
     * @param geneSupplier a function that supplies random genes,
     * which is used to make mutations within the children.
     * @return a mutated generation made from the parents.
     */
    public static <Type> List<List<Type>> makeGenerationFromParents     (List<List<Type>> parents, int generationSize, Supplier<Type> geneSupplier) {
        if(generationSize < 1)
            throw new IllegalArgumentException("generationSize was " + generationSize + " but cannot be less than 1.");
        return makeMutatedGeneration(Stream.generate(() ->  GeneticAlgorithm.makeChildFromParents(parents))
                                           .limit(generationSize)
                                           .collect(Collectors.toList()),
                                     geneSupplier);
    }
    
    /**
     * @param <Type> the object type of the genes.
     * @param childSize the number of genes within the child.
     * @param geneSupplier a function that supplies random genes,
     * which is used to make the child.
     * @return a child made with the genes supplied from the gene supplier.
     */
    public static <Type> List<Type>       makeChildFromSupplier         (int childSize, Supplier<Type> geneSupplier) {
        if(childSize < 1)
            throw new IllegalArgumentException("childSize was " + childSize + " but cannot be less than 1.");
        return Stream.generate(geneSupplier)
                     .limit(childSize)
                     .collect(Collectors.toList());
    }

    /**
     * @param <Type> the object type of the genes.
     * @param parents the parents of which to make the child.
     * @return a child made from randomly picking genes from the parents.
     */
    public static <Type> List<Type>       makeChildFromParents          (List<List<Type>> parents) {
        return IntStream.range(0, parents.get(0).size())
                        .mapToObj(i -> parents.get(random.nextInt(parents.size())).get(i))
                        .collect(Collectors.toList());
    }

    /**
     * @param <Type> the object type of the genes.
     * @param child the child of which to make the mutated child.
     * @param mutateAmount the amount of genes to mutate.
     * @param geneSupplier a function that supplies random genes,
     * which is used to make mutations within the child.
     * @return a mutated variant of the child.
     */
    public static <Type> List<Type>       makeMutatedChild              (List<Type> child, int mutateAmount, Supplier<Type> geneSupplier) {
        if(mutateAmount < 0)
            throw new IllegalArgumentException("mutateAmount was " + mutateAmount + " but cannot be less than 0.");
        if(mutateAmount > child.size())
            throw new IllegalArgumentException("mutateAmount was " + mutateAmount + " but cannot be larger than childSize which was " + child.size() + ".");
        List<Type> newChild = new ArrayList<Type>(child);
        random.ints(0, Backpack.getItemSizeFull())
              .distinct()
              .limit(mutateAmount)
              .forEach(i -> newChild.set(i, geneSupplier.get()));
        return newChild;
    }

    /**
     * @param <Type> the object type of the genes.
     * @param generation the generation of which to make the mutated generation.
     * @param geneSupplier a function that supplies random genes,
     * which is used to make mutations within the children.
     * @return a mutated variation of the generation.
     */
    public static <Type> List<List<Type>> makeMutatedGeneration         (List<List<Type>> generation, Supplier<Type> geneSupplier) {
        return IntStream.range(0, generation.size())
                        .mapToObj(i -> makeMutatedChild(
                                           generation.get(i),
                                           (int)((float)Backpack.getItemSizeFull() / generation.size() / 2 * i),
                                           geneSupplier
                                       ))
                        .collect(Collectors.toList());
    }

    /**
     * @param <Type> the object type of the genes.
     * @param generation the generation from which to get the best child.
     * @param childScorer a function that takes a child and gives a score to determine its effectiveness,
     * which is used to compare it to other children.
     * @return the best child from the generation.
     */
    public static <Type> List<Type>       getBestChild                  (List<List<Type>> generation, Function<List<Type>, Integer> childScorer) {
        return generation.stream()
                         .map(a -> new SimpleEntry<List<Type>, Integer>(a, childScorer.apply(a)))
                         .max((a, b) -> a.getValue() - b.getValue())
                         .get()
                         .getKey();
    }

    /**
     * @param <Type> the object type of the genes.
     * @param generation the generation from which to get the best children.
     * @param childrenAmount the number of children to get.
     * @param childScorer a function that takes a child and gives a score to determine its effectiveness,
     * which is used to compare it to other children.
     * @return the best children from the generation.
     */
    public static <Type> List<List<Type>> getBestChildren               (List<List<Type>> generation, int childrenAmount, Function<List<Type>, Integer> childScorer) {
        if(childrenAmount < 1)
            throw new IllegalArgumentException("childrenAmount was " + childrenAmount + " but cannot be less than 1.");
        if(childrenAmount > generation.size())
            throw new IllegalArgumentException("childrenAmount was " + childrenAmount + " but cannot be larger than generationSize which was " + generation.size() + ".");
        return generation.stream()
                         .map(a -> new SimpleEntry<List<Type>, Integer>(a, childScorer.apply(a)))
                         .sorted((a, b) -> b.getValue() - a.getValue()) //Reversed because we want the highest scores first.
                         .limit(childrenAmount)
                         .map(a -> a.getKey())
                         .collect(Collectors.toList());
    }

}