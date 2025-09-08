package fouriertransform.parallel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class Parallel {
    private final ExecutorService exec;
    private final int NUM_THREADS;

    public Parallel() {
        NUM_THREADS = Runtime.getRuntime().availableProcessors();
        exec = Executors.newFixedThreadPool(NUM_THREADS);
    }

    public Parallel(int threads) {
        NUM_THREADS = threads;
        exec = Executors.newFixedThreadPool(NUM_THREADS);
    }

    public void For(int start, int end, Consumer<Integer> consumer) {
        ArrayList<Future<?>> futures = new ArrayList<>();
        try {
            for (int i = start; i < end; i++) {
                final int I = i;
                futures.add(exec.submit(() -> consumer.accept(I)));
            }
        } finally {
            exec.shutdown();
        }

        while (!futures.isEmpty()) {
            futures.removeIf(f -> f.isDone());
        }
    }
}
