import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
//rezultatele de la etapa Map vor fi stocate intr-un vector de acest tip
public class RTask {
    //mapa care va retine lungimea cuvantului + numarul de cuvinte de aceasta lungime
    Map<Integer, Integer> syncMap;
    //string care va retine cuvantul cu lungimea cea mai mare
    String element;
    //numele fisierului
    String fileName;

    public RTask(String fileName) {
        this.fileName = fileName;
        syncMap = Collections.synchronizedMap(new HashMap<>());

    }

    //adaugarea in map, key - lungimea cuvantului
    public void addMap(int key){
        //verific daca exista deja cheia
        Integer value = syncMap.get(key);
        if(value != null) {
            //daca exista deja cheia, incrementez nr de cuvinte
            value++;
            //adaug in map
            syncMap.put(key, value);
        } else {
            //daca nu, setez nr de cuvinte cu prima aparitie gasita
            syncMap.put(key, 1);
        }

    }
    //adaug cuvantul cu lungime maxima
    public void addElement(String element) {
        this.element = element;

    }

    public String getElement() {
        return element;

    }

    public String getFileName() {
        return fileName;
    }
    //returnez map-ul
    public Map<Integer, Integer> getSyncMap() {
        return syncMap;
    }

    public String toString() {
        String s="";
        s = fileName + "\n" + element + "\n" + syncMap + "\n";
        return s;
    }


}
