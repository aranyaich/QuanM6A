import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ReadSplit {
    private HashMap<String,String> transcriptSeqMap = new HashMap<>();
    private HashMap<String,ArrayList<String>> ReadsMap = new HashMap<>();

    private void SequenceReader(String fileName){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(fileName)));
            String seqId = "";
            while (br.ready()){
                String strLine = br.readLine();
                strLine = strLine.replace("\n","");
                if (strLine.startsWith(">")){
                    seqId = strLine.replace(">","");
                }else {
                    ArrayList<String> reads = sequenceSplit(strLine,150);
                    this.ReadsMap.put(seqId,reads);
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showResults(){
        Iterator<String> transIter = ReadsMap.keySet().iterator();
        if (transIter.hasNext()){
            String transId = transIter.next();
            ArrayList<String> reads = ReadsMap.get(transId);
            System.out.println(transId+"————"+reads.size());
            int n=0;
            for (String read:reads){
                if (n>5) {break;}
                System.out.print(read+"\t");
                n++;

            }
            System.out.println();
        }
    }

    private ArrayList<String> sequenceSplit(String seq,int splitLen){
        ArrayList<String> reads = new ArrayList<>();
        int seqSize = seq.length();
        if(seqSize<=splitLen){
            reads.add(seq);
            return reads;
        }
        for (int i=0;i<=seqSize-splitLen;i++){
            String read = seq.substring(i,i+splitLen);
            reads.add(read);
        }
        return reads;
    }

    public static void main(String[] args) {
        String fileName = "E:\\2018_01\\m6a_brays\\模拟测试集\\transSeq.fasta";
        ReadSplit readSplit = new ReadSplit();
        readSplit.SequenceReader(fileName);
        readSplit.showResults();
    }
}
