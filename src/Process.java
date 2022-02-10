import java.io.*;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class Process implements Runnable {
    private final ExecutorService tpe;
    private final AtomicInteger inQueue;
    //reducedTask va contine dictionarul fiecarui fisier obtinut la etapa de combinare
    private final ReducedTask reducedTask;
    private final int index;

    public Process (ExecutorService tpe, AtomicInteger inQueue, ReducedTask reducedTask, int index) {
        this.tpe= tpe;
        this.inQueue = inQueue;
        this.reducedTask = reducedTask;
        this.index = index;
    }
    //functie pentru sirul fibonnaci
    public long fibonacci(long x) {
        if(x == 1 || x == 2) {
            return 1;
        }
        return fibonacci(x - 1) + fibonacci(x - 2);
    }

    @Override
    public void run() {
        //initilizez variabilele
        double rang = 0;
        double nr = 0;
        int maxKey = -1;
        int maxValue = -1;

        for (Map.Entry<Integer,Integer> entry : reducedTask.getSyncMap().entrySet()) {
            //key - dimensiunea cuvantului gasit in dictionar
            Integer key = entry.getKey();
            //value - numarul de cuvinte cu dimensiunea key
            Integer value = entry.getValue();
            //determin dimensiunea maxima a cuvantului
            if(key > maxKey) {
                maxKey = key;
                maxValue = value;
            }
            //calculez rangul conform formulei
            rang+= fibonacci(key + 1) * value;
            //stochez numarul total de cuvinte
            nr+= value;
        }
        //calculez rangul
        rang = rang / nr;
        //obtin numele fisierului din cale
        String[] elements = reducedTask.getFileName().split("/");
        //rezultatul va fi sub forma de string
        String s = elements[2]+ "," + String.format("%.2f", rang) + "," + maxKey + "," + maxValue + "\n";
        //se adauga rezultatul in vectorul results
        Tema2.results.get(index).setRang(rang);
        Tema2.results.get(index).setS(s);

        int left = inQueue.decrementAndGet();
        if (left == 0) {
            tpe.shutdown();
        }

    }
}
