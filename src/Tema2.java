import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public class Tema2 {
    public static int threads;
    //fisierul de intrare
    public static String file;
    //fisierul de iesire
    public static String fileOut;
    //dimensiunea fisierului
    public static int dimFragm;
    //numarul de fisiere
    public static int numberDoc;
    //rezultatele de la etapa de combinare
    public static List<RTask> tasks = Collections.synchronizedList(new Vector<>());
    //lista cu taskurile pentru etapa Reduce
    public static List<ReducedTask> tasksReduced = Collections.synchronizedList(new ArrayList<>());
    //lista cu rezultatele finale pentru etapa Process
    public static List<Result> results = Collections.synchronizedList(new ArrayList<>());

    //functie pentru sortarea rezultatelor finale dupa rang
    static class SortRangs implements Comparator<Result> {
        //sortare descrescatoare dupa rang
        public int compare(Result a, Result b) {

            if (a.getRang() > b.getRang()) {
                return -1;
            }

            if (a.getRang() < b.getRang()) {
                return 1;
            }

            return 0;


        }
    }

    public static void main(String[] args) throws Exception {
        //vector care contine numele fisierelor care urmeaza sa fie procesate
        Vector<String> filesName = new Vector<String>(100);
        //ma asigur ca citesc de fiecare data 3 argumente in consola
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }
        //numarul de thread-uri
        threads =  Integer.parseInt(args[0]);
        //fisierul de intrare
        file = args[1];
        //fisierul de iesire
        fileOut = args[2];

        //citesc fisierul
        FileReader fileRead = new FileReader(file);
        //stochez continutul in buffer
        BufferedReader buffer = new BufferedReader(fileRead);
        String s;
        int counter = 0, i = 0;

        //citesc continutul si il salvez
        while ((s = buffer.readLine()) != null) {
            //counter-ul l-am folosit pentru a ma asigura sa citesc
            //dimensiunea fragmentului si numarul de fisiere
            counter++;
            if(counter == 1) {
                dimFragm = Integer.parseInt(s);
            } else if(counter == 2) {
                numberDoc = Integer.parseInt(s);
            } else {
                //dupa ce citesc primele 2 variabile
                //adaug numele fisierului de procesat in vector
                filesName.add(s);
            }
        }

        fileRead.close();

        //inQueue si tpe le utilizez pentru ExecutorService
        AtomicInteger inQueue = new AtomicInteger(0);
        ExecutorService tpe = Executors.newFixedThreadPool(threads);

        for(i = 0; i < numberDoc; i++) {
            //accesez fisierul si calculez lungimea acestuia
            File fileSmall = new File(filesName.get(i));
            long fileSize = fileSmall.length();
            //initializez taskul pentru etapa Reduce
            tasksReduced.add(new ReducedTask(filesName.get(i)));

            //offset ma ajuta sa citesc fisierul dintr-o pozitie anume
            int offset = 0;
            //cat timp offset-ul este mai mic decat lungimea fisierului
            while(offset <= fileSize) {
                //initializez rezultatul care va fi obtinut din etapa Map cu numele fisierului
                tasks.add(new RTask(filesName.get(i)));
                //counterTask ma va ajuta sa adaug rezultatul in pozitia corecta din vector
                int counterTask = tasks.size();
                //incrementez coada
                inQueue.incrementAndGet();
                //adaug taskul
                tpe.submit(new MyRunnable(filesName.get(i), tpe, inQueue, offset, fileSize, threads, dimFragm, counterTask - 1));
                //incrementarea offsetului cu dimensiunea fragmentului
                offset+=dimFragm;
                counterTask++;

            }


        }
        //astept ca toate taskurile sa fie procesate
        while(!tpe.isTerminated());

        //etapa de Combinare
        //iau toate rezultatele din fragmente si le combin pentru urmatoare etapa
        for(RTask element : tasks) {
            for(ReducedTask r : tasksReduced) {
                r.addRTask(element);
            }
        }
        //initilizez urmatoarele variabile pentru etapa de Procesare
        AtomicInteger inQueue1 = new AtomicInteger(0);
        ExecutorService tpe1 = Executors.newFixedThreadPool(threads);

        for(ReducedTask r : tasksReduced) {
                //pentru fiecare dictionar calculez rangul
                results.add(new Result());
                //counterResult ma va ajuta sa pozitionez bine rezultatul in results
                int counterResult = results.size();
                inQueue1.incrementAndGet();
                tpe1.submit(new Process(tpe1, inQueue1, r, counterResult - 1));

        }
        //astept taskurile sa se termine
        while(!tpe1.isTerminated());
        //sortez dupa rang
        Collections.sort(results, new SortRangs());
        //fac afisarea in fisier
        String output = "";
        for(Result element : results) {
            output+= element.getS();

        }

        //scriu in fisier
        FileWriter g = new FileWriter(fileOut);
        g.write(output);
        g.close();
    }
}
