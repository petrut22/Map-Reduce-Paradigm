import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class MyRunnable implements Runnable {
    private final String path;
    private final ExecutorService tpe;
    private final AtomicInteger inQueue;
    //pozitia de citire din fisier
    private int offset;
    //dimensiunea fisierului
    public  long fileSize;
    public final int threads;
    public  int dimFragm;
    public  int indexFile;
    //string care cotine separatorii
    public String sep;

    public MyRunnable(String path, ExecutorService tpe, AtomicInteger inQueue, int offset, long fileSize, int threads, int dimFragm, int indexFile) {
        this.path = path;
        this.tpe = tpe;
        this.inQueue = inQueue;
        this.offset = offset;
        this.fileSize = fileSize;
        this.threads = threads;
        this.dimFragm = dimFragm;
        this.sep = ";:/?˜\\.,><‘[]{}()!@#$%ˆ&- +’=*”| \t\n";
        this.indexFile = indexFile;
    }

    @Override
    public void run(){
        // se efectueaza citirea din fisier
        try {
            FileReader fileRead = new FileReader(path);
            int c = 0;
            //initilizez primul caracter citit din fisier
            char chFirst = '=';
            //daca citirea fisierului se face de la inceput
            //sar de cazul in care fragmentul incepe cu un cuvant incomplet
            if(offset == 0) {
                fileRead.skip(offset);
            } else {
                fileRead.skip(offset - 1);

            }
            BufferedReader buffer = new BufferedReader(fileRead);
            if(offset != 0) {
                c = buffer.read();
                //stochez primul caracter din fragment
                chFirst = (char) c;
            }

            int counter = offset;
            String fragment ="";
            char ch = ' ';
            //citirea caracterelor se va face pana cand counter-ul ajunge la finalul fisierului sau
            //a atins dimensiunea fragmentului
            while(counter <= fileSize && counter <= offset + dimFragm -1 && (c = buffer.read()) != -1) {
                ch = (char) c;
                //adaug in fragment caracterul citit
                if(sep.indexOf(chFirst) != -1) {
                    fragment += ch;
                }

                //cazul pentru un cuvant incomplet
                //citesc caracterele pana in momentul in care
                //intalnesc caractere din separator
                if(sep.indexOf(ch) != -1) {
                    //in momentul in care intalnesc un separator inseamna ca am
                    //scapat de cuvantul incomplet si pot sa adaug in fragment
                    chFirst = ch;
                }
                counter++;
            }
            //trec la urmatoarea litera
            counter++;

            //cazul in care fragmentul se termina cu un cuvant incomplet
            if(counter <= fileSize && sep.indexOf(ch) == -1) {

                c = buffer.read();
                ch = (char) c;
                //verific daca litera nu este un separator
                if(sep.indexOf(ch) == -1) {
                    //il adaug
                    fragment += ch;
                    //adaug intreg cuvantul in fragment
                    while( (sep.indexOf(ch) == -1) && counter <= fileSize && (c = buffer.read()) != -1) {
                        ch = (char) c;
                        fragment += ch;
                    }
                }

            }

            //string pentru stocarea cuvantului cu lungimea maxima din fragment
            String maxString = "";
            //dimensiunea acestuia
            int maxLength = -1;

            //daca fragmentul nu este vid
            if(fragment != "") {
                //impart fragmentul in cuvinte
                String[] elements = fragment.split("[;:/?˜\\.,><'\\[\\]{}()!@#$%ˆ&\\- +=*\\”| \r\t\n]");
                for (String element : elements) {
                    //determin lungimea fragmentului
                    int lengthS = element.length();
                        //adaug in map lungimea
                        if(lengthS != 0) {
                            Tema2.tasks.get(indexFile).addMap(lengthS);
                        //actualizez cuvantul cu lungime maxima
                        if(lengthS > maxLength) {
                            maxString = new String(element);
                            maxLength = lengthS;
                        }
                    }
                }
                //fac update la datele din rezultatul taskului
                Tema2.tasks.get(indexFile).addElement(maxString);

            }


            int left = inQueue.decrementAndGet();
            if (left == 0) {
                tpe.shutdown();
            }

        } catch(IOException e) {
            System.err.println("The file cannot ne opened or Buffer is corrupted by TJ Miles");
        }



    }
}
