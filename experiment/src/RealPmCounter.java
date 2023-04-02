import java.io.*;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class RealPmCounter {
    private HashMap<String,int[]> tranSiteMap = new HashMap<>();
    private HashMap<String,Double> tranPmMap = new HashMap<>();
    private HashMap<String,Integer> ipPeakCountMap = new HashMap<>();
    private HashMap<String,Integer> inputPeakCountMap = new HashMap<>();
    private HashMap<String,Integer> ipCountMap = new HashMap<>();
    private HashMap<String,Integer> inputCountMap = new HashMap<>();

    private void setSites(String noteFile){
        tranSiteMap.clear();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(noteFile)));
            while (br.ready()){
                String strLine = br.readLine();
                if(strLine.startsWith("transId")){continue;}
                String[] notes = strLine.split("\t");
                String transId = notes[0];
                int[] sites ={Integer.valueOf(notes[1]),Integer.valueOf(notes[2])};
                double prePm = Double.valueOf(notes[3]);
                tranSiteMap.put(transId,sites);
                tranPmMap.put(transId,prePm);
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void samReader(String samFile,String type){
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(samFile)));
            while (br.ready()){
                String strLine = br.readLine();
                if(strLine.startsWith("@")){continue;}
                int realLen=strLine.split("\t")[9].length();
                if (realLen<20){
                    continue;
                }
                findPeak(strLine,type);
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void findPeak(String samLine,String type){
        String[]notes = samLine.split("\t");
        String transId = notes[0].split(";")[0];

        int site = Integer.valueOf(notes[3]);
        int[] transSite = tranSiteMap.get(transId);
        if(type.equals("IP")){
            countPlus(ipCountMap,transId);
        }else {
            countPlus(inputCountMap,transId);
        }
        if (site>transSite[0] && site<transSite[1]){
            if(type.equals("IP")){
                countPlus(ipPeakCountMap,transId);
            }else {
                countPlus(inputPeakCountMap,transId);
            }
        }
    }

    private void countPlus(HashMap<String,Integer> countMap,String transId){
        if (countMap.containsKey(transId)){
            int old_count = countMap.get(transId);
            countMap.put(transId,old_count+1);
        }else {
            countMap.put(transId,1);
        }
    }

    private void pmCountCliWriter(String resultFile){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(resultFile)));
            Iterator it = inputPeakCountMap.keySet().iterator();
            while (it.hasNext()){
                String transId = (String) it.next();
                if (!ipPeakCountMap.containsKey(transId)){
                    System.out.println(transId+ "don't match");
                    continue;
                }
                int ipPCount = ipPeakCountMap.get(transId);
                int inputPCount = inputPeakCountMap.get(transId);

                int ipCount = ipCountMap.get(transId);
                int inputCount = inputCountMap.get(transId);

                int cliInputPCount =(int) (ipCount*inputPCount)/inputCount;


                double realPm =(double) (ipPCount-cliInputPCount)/(double)ipPCount;

                System.out.println(transId+"\t"+ipPCount+"\t"+cliInputPCount+"\t"+realPm+"\t"+tranPmMap.get(transId));

            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void pmCountWriter(String resultFile){
        Iterator it = inputPeakCountMap.keySet().iterator();
        while (it.hasNext()){
            String transId = (String) it.next();
            if (!ipPeakCountMap.containsKey(transId)){
                System.out.println(transId+ "don't match");
                continue;
            }
            int ipPCount = ipPeakCountMap.get(transId);
            int inputPCount = inputPeakCountMap.get(transId);

            int ipCount = ipCountMap.get(transId);
            int inputCount = inputCountMap.get(transId);
            double realPm =(double) (ipPCount-inputPCount)/(double)ipPCount;

            System.out.println(transId+"\t"+ipCount+"\t"+inputCount+"\t"+realPm+"\t"+tranPmMap.get(transId));

        }
    }

    public static void main(String[] args) {
        String MainPath = "E:\\2018_01\\m6a_brays\\模拟测试集\\1000gene_0.3\\";
        String NoteFile = "IPNote1000.txt";
        RealPmCounter realPmCounter = new RealPmCounter();
        realPmCounter.setSites(MainPath+NoteFile);
        realPmCounter.samReader(MainPath+"ip_result\\accepted_hits.sam","IP");
        realPmCounter.samReader(MainPath+"input_result\\accepted_hits.sam","Input");

        realPmCounter.pmCountWriter(MainPath+"pm_0.3_result.txt");


    }

}

