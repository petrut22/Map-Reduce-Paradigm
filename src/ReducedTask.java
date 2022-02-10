import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
//acest clasa va fi folosita pentru construirea dictionarului la fiecare fisier
//in etapa de combinare
public class ReducedTask {
    //map care va retine disctionarul final de cuvinte din fisier
    Map<Integer, Integer> syncMap;
    //lista cu cuvintele de lunfime maxima din fiecare fragment
    ArrayList<String> elements;
    String fileName;

    public ReducedTask(String fileName) {
        this.fileName = fileName;
        syncMap = Collections.synchronizedMap(new HashMap<>());
        elements = new ArrayList<String>();

    }
    //aceasta functie va combina toate mapurile din fiecare fragment
    public void addMap(Map<Integer, Integer> smallMap){
        for (Map.Entry<Integer,Integer> entry : smallMap.entrySet()) {

            Integer smallKey = entry.getKey();
            Integer smallValue = entry.getValue();

            Integer value = syncMap.get(smallKey);

            if(value != null) {
                value+= smallValue;
                syncMap.put(smallKey, value);
            } else {
                syncMap.put(smallKey, smallValue);
            }
        }
    }

    public Map<Integer, Integer> getSyncMap() {
        return syncMap;
    }

    public ArrayList<String> getElements() {
        return elements;
    }

    public String getFileName() {
        return fileName;
    }

    public void addString(String element) {
        elements.add(element);

    }

    public void addRTask(RTask task) {
        //rezultatele din fiecare task vor fi combinate
        if(fileName.equals(task.getFileName()) == true) {
            addString(task.getElement());
            addMap(task.getSyncMap());
        }
    }

    public String toString() {
        String s="";
        s = fileName + "\n";

        for(String e : elements) {
            s = s + e + " ";
        }

        s += "\n";

        s = s+ syncMap + "\n";

        return s;
    }


}
