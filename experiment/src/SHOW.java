import java.io.*;
import java.util.Random;

public class SHOW {

    private void samReader(String samFile){
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(samFile)));
            while (br.ready()){
                String strLine = br.readLine();
                if(strLine.startsWith("@")){continue;}
                int realLen=strLine.split("\t")[9].length();
                if (strLine.contains("ENST00000320560")&&realLen>20){
                    int site = Integer.valueOf(strLine.split("\t")[3]);
//                    System.out.println(strLine);
                    if (site>45429608 && site<45429808){
                        System.out.println(strLine);
                    }
                }

            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        String MainPath = "E:\\2018_01\\m6a_brays\\模拟测试集\\1000gene\\";
//        String NoteFile = "IPNote1000_new.txt";
//        SHOW show = new SHOW();
////        show.samReader(MainPath+"ip_result\\accepted_hits.sam","IP");
//        show.samReader(MainPath+"ip_result\\accepted_hits.sam");

        for (int i=0;i<100;i++){
            int cutPoint =(int) Math.round(Math.random()*500);
            System.out.println(cutPoint+"\t"+i);
        }


    }


}
