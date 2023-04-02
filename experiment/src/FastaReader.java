import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class FastaReader {
    private HashMap<String,String> seqMap;

    public void readFromFile(String fileName){
        seqMap = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
            String seqKey = "";
            while (br.ready()){
                String strLine = br.readLine().replace("\n","");
                if (strLine.startsWith(">")){
                    seqKey = strLine.replace(">","");
                }else {
                    seqMap.put(seqKey, strLine);
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getSeqMap() {
        return seqMap;
    }

    public ArrayList<String> getSeqList(){
        ArrayList<String> seqList = new ArrayList<>();
        Iterator it = seqMap.keySet().iterator();
        while (it.hasNext()){
            String seqKey = (String) it.next();
            seqList.add(seqMap.get(seqKey));
        }
        return seqList;
    }

}
