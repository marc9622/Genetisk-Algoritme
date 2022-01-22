import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) {

        GenAlgoVisualiser window = new GenAlgoVisualiser();

    }

}

class Tester {

    public static <R> R test(Callable<R> callable, Function<R, ? extends Number> reducer) {
        Result<R> result = new Result<R>(callable);
        System.out.println("Took " + round(result.getTimeMs(), 2) + "ms and gave " + result.getValue().toString());
        return result.getValue();
    }

    public static <R, N extends Number> R test(Callable<R> callable, Function<R, N> reducer, int repetitions, boolean shouldLogAll) {
        List<Double> times = new ArrayList<Double>();
        List<Number> values = new ArrayList<Number>();
        Result<R> bestResult = null;
        for(int i = 0; i < repetitions; i++) {
            Result<R> result = new Result<R>(callable);
            times.add(result.getTimeMs());
            values.add(reducer.apply(result.getValue()));
            if(reducer.apply(result.getValue()).doubleValue() > (bestResult == null ? 0 : reducer.apply(bestResult.getValue()).doubleValue()))
                bestResult = result;
            if(shouldLogAll)
                System.out.println("Took " + round(result.getTimeMs(), 2) + "ms and gave " + result.getValue().toString());
        }
        double totalTime = round(times.stream().mapToDouble(i -> i).sum(), 1);
            double initialAverageTime = times.stream().mapToDouble(i -> i).average().getAsDouble();
            times.removeIf(i -> (i - initialAverageTime) / initialAverageTime > 1);
        double averageTime = round(times.stream().mapToDouble(i -> i).average().getAsDouble(), 3);
        double averageValue = round(values.stream().mapToDouble(i -> i.doubleValue()).average().getAsDouble(), 1);
        System.out.println("Took " + totalTime + "ms in total, " +
                           "took " + averageTime + "ms on average, and " +
                           "gave " + averageValue + " on average");
        return bestResult.getValue();
    }

    private static final double round(double number, int decimals) {
        return (int)(number * Math.pow(10, decimals)) / Math.pow(10, decimals);
    }

    private static class Result<Type> {

        private long time;
        private Callable<Type> callable;
        private Type value = null;

        public Result(Callable<Type> callable) {
            this.callable = callable;
            run();
        }

        private void run() {
            time = System.nanoTime();
            try {
                value = callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            time = System.nanoTime() - time;
        }

        public double getTimeMs() {
            return time / 1000000d;
        }

        public Type getValue() {
            return value;
        }
    }

}

/* Bedste taskepakning:
    Indeholder:
        Kort
        Kompas
        Vand
        Sandwich
        Sukker
        Banan
        Æble
        Ost
        Solcreme
        Kamera
        Vandtætte bukser
        Vandtæt overtøj
        Pung
        Solbriller
        Sokker
    Vægt: 4900
    Pris: 1130
*/