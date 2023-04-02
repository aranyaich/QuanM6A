import java.io.*;
import java.util.*;

public class tmp {
    private HashMap<String,String> transcriptChrMap = new HashMap<>();

    private void noteFileIO(String filePath){
        String oldFile = filePath+"IPNote1000.txt";
        String newFile = filePath+"IPNote1000_new.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(oldFile)));
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(newFile)));
            bw.write("chr\ttransId\tpeakStart\tpeakEnd\ttruePm\tPeakCount\tAddCount");
            bw.newLine();
            while (br.ready()){
                String strLine = br.readLine();
                if(strLine.startsWith("transId")){continue;}
                String[] notes = strLine.split("\t");
                String transId = notes[0];
                if (transcriptChrMap.containsKey(transId)){
                    String chr = transcriptChrMap.get(transId);
                    bw.write(chr+"\t"+strLine);
                    bw.newLine();
                }else {
                    System.out.println(transId+" has not Chr information!");
                }
            }
            br.close();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void chrReader(String fileName) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
            while (br.ready()) {
                String gtfLine = br.readLine();
                if (gtfLine.startsWith("#!")) {continue;}
                if (!"transcript".equals(gtfLine.split("\t")[2])) {continue;}
                String[] gtfNotes = gtfLine.split("\t");
                String anx = gtfNotes[8];
                String chr = gtfNotes[0];
                String transcriptId = anx.substring(anx.indexOf("transcript_id")).split("\"")[1];
                transcriptChrMap.put(transcriptId,chr);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        String MainPath = "E:\\2018_01\\m6a_brays\\模拟测试集\\";
        tmp tmp_test = new tmp();

        tmp_test.chrReader(MainPath+"Homo_sapiens.GRCh38.91.chr.gtf");

        tmp_test.noteFileIO(MainPath+"1000gene\\");

    }

}
