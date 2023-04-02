import sun.reflect.SignatureIterator;

import java.io.*;
import java.util.*;

/**.
 * Demo class
 *
 * @author dengyongjie
 * @date 2018/03/05
 */

public class ExonIo {
    private HashMap<String,String> transcriptSeqMap = new HashMap<>();
    private HashMap<String,ArrayList<Exon>> transcriptExonsMap = new HashMap<>();
    private HashMap<String,HashSet<String>> geneTranscriptMap = new HashMap<>();
    private TwoBitParser twoBitParser;
    private String MainPath;

    public void envInitialization(String mainPath){
        MainPath = mainPath;
        transcriptSeqMap.clear();
        transcriptExonsMap.clear();
        String gtfFile = mainPath + "Homo_sapiens.GRCh38.91.chr.gtf";
        exonReader(gtfFile);
    }

    public void jointExons(){
        try {
        twoBitParser = new TwoBitParser(new File(MainPath + "hg38.2bit"));
        Iterator it = geneTranscriptMap.keySet().iterator();
        while (it.hasNext()){
            String geneId= (String) it.next();
            String bestTrans = getBestTrans(geneId);
            ArrayList<Exon> exons = transcriptExonsMap.get(bestTrans.split(";")[0]);
            String transSeq = getTransSeq(exons);
//            System.out.println(bestTrans);
//            System.out.println(transSeq);
            transcriptSeqMap.put(bestTrans,transSeq);
        }
            twoBitParser.close();
            twoBitParser.closeParser();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getBestTrans(String geneId){
        int bestLen = -100;
        String bestId = "";
        for(String transId:geneTranscriptMap.get(geneId)){
            ArrayList<Exon> exons = transcriptExonsMap.get(transId);
            int len = 0;
            for(Exon exon:exons){
                len = len + exon.getEnd() - exon.getStart()+1;
            }
            if(len>bestLen) {
                bestId = transId;
                bestLen = len;
            }
        }
        return addPeakRegion(bestId,bestLen);
    }

    private String addPeakRegion(String transId, int len){
        ArrayList<Exon> exons = transcriptExonsMap.get(transId);
        int peakStart = (int)(Math.random()*(len-1));
        int peakEnd = Math.min(peakStart+200,len);
//        待添加限制

        String chr = exons.get(0).getChr();
        String transTitle = transId+";"+chr+";"+String.valueOf(peakStart)+"-"+String.valueOf(peakEnd);

        String stand = exons.get(0).getStand();
        if (stand.equals("-")){
            int peakGap = peakEnd-peakStart;
            peakStart = len - peakEnd;
            peakEnd = peakStart + peakGap;
        }

        Comparator<Exon> exonComparator = new Comparator<Exon>() {
            public int compare(Exon o1, Exon o2) {
                if(o1.getStart()<o2.getStart())
                    return -1;
                    //注意！！返回值必须是一对相反数，否则无效。jdk1.7以后就是这样。
                    //      else return 0; //无效
                else return 1;
            }
        };
        exons.sort(exonComparator);
        int siteStart = 0;
        int siteEnd = 0;
        for (Exon exon : exons) {
            int gap = exon.getEnd()-exon.getStart()+1;
            if(gap>=peakStart && peakStart>=0){
                siteStart = exon.getStart()+peakStart;
                peakStart = peakStart-gap;
            }else {
                peakStart = peakStart-gap;
            }
            if(gap>=peakEnd && peakEnd>=0){
                siteEnd = exon.getStart()+peakEnd;
                peakEnd = peakEnd-gap;
                break;
            }else {
                peakEnd = peakEnd-gap;
            }
        }
        return transTitle+";"+String.valueOf(siteStart)+"-"+String.valueOf(siteEnd)+";"+stand;
    }

    public void seqFileWriter(String fileName){
        int count = 0;
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(MainPath+fileName)));
            Iterator it = transcriptSeqMap.keySet().iterator();
            while (it.hasNext()){
                if (count>=1000){break;}
                String transId= (String) it.next();
                String transSeq= transcriptSeqMap.get(transId);
                if (transSeq.length()<500){continue;}
                bw.write(">"+transId);
                bw.newLine();
                bw.write(transSeq);
                bw.newLine();
                count++;
                if(count%100 ==0){System.out.println(count);}
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private String getTransSeq(ArrayList<Exon> exons){
        Comparator<Exon> exonComparator = new Comparator<Exon>() {
            public int compare(Exon o1, Exon o2) {
                if(o1.getStart()<o2.getStart())
                    return -1;
                    //注意！！返回值必须是一对相反数，否则无效。jdk1.7以后就是这样。
                    //      else return 0; //无效
                else return 1;
            }
        };
        exons.sort(exonComparator);
        String exonsSeq="";
        try {
            twoBitParser.setCurrentSequence(exons.get(0).getChr());
            for (Exon exon : exons) {
                twoBitParser.reset();
                String exonSeq = twoBitParser.loadFragment(exon.getStart()-1,exon.getEnd()-exon.getStart()+1).toUpperCase();
//                System.out.println(exon.getStart());
//                System.out.println(exon.getEnd());
//                System.out.println(exonSeq);
                exonsSeq = exonsSeq.concat(exonSeq);
            }
            twoBitParser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (exons.get(0).getStand().equals("-")){
            exonsSeq =CommonTools.reverse(exonsSeq);
        }
        return exonsSeq;
    }

    private void exonReader(String fileName) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
            while (br.ready()) {
                String gtfLine = br.readLine();
                if (gtfLine.startsWith("#!")) {continue;}
//                if(!gtfLine.contains("ENSG00000088247")){continue;}
                if ("transcript".equals(gtfLine.split("\t")[2])) {
                    addTranscript(gtfLine.split("\t")[8]);
                }
                if (!"exon".equals(gtfLine.split("\t")[2])) {continue;}
                Exon exon = new Exon();
                exon.setFromGtf(gtfLine);
                addExons(exon);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void addTranscript(String note){
        String geneId = note.substring(note.indexOf("gene_id")).split("\"")[1];
        String transId = note.substring(note.indexOf("transcript_id")).split("\"")[1];
        if (geneTranscriptMap.containsKey(geneId)){
            HashSet<String> transSet = geneTranscriptMap.get(geneId);
            transSet.add(transId);
            geneTranscriptMap.put(geneId,transSet);
        }else {
            HashSet<String> transSet = new HashSet<>();
            transSet.add(transId);
            geneTranscriptMap.put(geneId,transSet);
        }
    }

    private void addExons(Exon exon){
        String transId = exon.getTranscriptId();
        if (transcriptExonsMap.containsKey(transId)){
            ArrayList<Exon> exons = transcriptExonsMap.get(transId);
            exons.add(exon);
            transcriptExonsMap.put(transId,exons);
        }else {
            ArrayList<Exon> exons = new ArrayList<>();
            exons.add(exon);
            transcriptExonsMap.put(transId,exons);
        }
    }

    public static void main(String[] args) throws Exception {
        String mainPath = "E:\\2018_01\\m6a_brays\\模拟测试集\\";
        ExonIo exonIo = new ExonIo();
        exonIo.envInitialization(mainPath);
        exonIo.jointExons();
        exonIo.seqFileWriter("transSeq1000.fasta");
    }
}
