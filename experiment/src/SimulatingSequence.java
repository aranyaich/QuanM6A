
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SimulatingSequence {
    private String mainPath;

    private void ini(String path){
        mainPath = path;
        deleteDir(mainPath);
        recordNote("transId","chr","peakStart","peakEnd","truePm","totalRead","inputPeakCount","ipPeakCount");
    }

    private void genReads(String seq, String seqName,double pm){
        double k = 1.0/Math.pow(10,9);
        double RPKM = 1+Math.random()*10;
        double N = Math.pow(10,9);
        int len = seq.length();
        int readCount = (int) Math.floor(k* N*len *RPKM);

        String transId = seqName.split(";")[0];
        String chr = seqName.split(";")[1];
        String[] peakSites = seqName.split(";")[3].split("-");

        int inputPeakCount = fragSeq(seq,seqName,"Input",readCount,0);
        int addCount =(int)Math.floor(inputPeakCount*pm/(1-pm));
        double zipRate = readCount/(addCount+readCount);
        int newAddCount = (int) Math.ceil(addCount*zipRate);
        int ipPeakCount = fragSeq(seq,seqName,"IP",readCount-newAddCount,newAddCount);

        double test = newAddCount/ (double)ipPeakCount;
//        if (newAddCount<10){
//            System.out.println(zipRate);
//            System.out.println(readCount+"\t"+inputPeakCount+"\t"+addCount+"\t"+newAddCount+"\t"+test+"\t"+pm);
//        }
        recordNote(transId,chr,peakSites[0],peakSites[1],String.valueOf(pm),String.valueOf(readCount),String.valueOf(inputPeakCount),String.valueOf(ipPeakCount));
    }

    private int fragSeq(String seq, String seqName,String type,int readCount,int addCount){
//        ArrayList<String> readList = new ArrayList<>();
        HashMap<String,String> readMap = new HashMap<>();

        int peakReadCount = 0;
        String[] peaks = seqName.split(";")[2].split("-");
        String transId = seqName.split(";")[0];
        int peakStart = Integer.valueOf(peaks[0]);
        int peakEnd = Integer.valueOf(peaks[1]);
        String[] peakSites = seqName.split(";")[3].split("-");

        int peakCenter =(int) Math.floor((peakEnd+peakStart)/2);

        Random random = new Random();
        for(int i=0;i<readCount;i++){
            int cutPoint =(int) Math.ceil(Math.random()*seq.length());
            int m = (int) (Math.sqrt(25)*random.nextGaussian()+250);
            String subSeq = seq.substring(cutPoint,Math.min(cutPoint + m,seq.length()));
            String read = readSequencing(subSeq);

            if (read.startsWith("-")){
                cutPoint += Math.max(0,subSeq.length()-50);
            }

            if(cutPoint>=peakCenter-250&&cutPoint<=peakCenter+200){
                peakReadCount++;
            }

            String readName = transId+";"+i+";"+cutPoint;
            readMap.put(readName,read.substring(1));
        }

        if (type.equals("IP")){
            int i = 0;
            while (i<addCount){
                int cutPoint =(int) Math.ceil(Math.random()*seq.length());
                if (cutPoint>peakCenter||cutPoint<peakCenter-250){continue;}
                int m = (int) (Math.sqrt(25)*random.nextGaussian()+250);
                String subSeq = seq.substring(cutPoint,Math.min(cutPoint + m,seq.length()));
                String read = readSequencing(subSeq);

                if (read.startsWith("-")){
                    cutPoint += Math.max(0,subSeq.length()-50);
                }
                int cutSite = Integer.valueOf(peakSites[0])+cutPoint;
                if (seqName.split(";")[4].equals("-")){
                    cutSite = Integer.valueOf(peakSites[1])-cutPoint;
                }

                String readName = transId+";plus"+i+";"+cutSite;
                readMap.put(readName,read.substring(1));
                i++;
            }
        }
        recordRead(type, readMap);

        return peakReadCount+addCount;
    }
    private void recordNote(String transId,String chr,String peakStart,String peakEnd,String truePm,String totalRead,String inputPeakCount,String ipPeeakCount){
        String noteFile = mainPath + "IPNote1000.txt";
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(noteFile),true));
            bw.write(transId+"\t");
            bw.write(chr+"\t");
            bw.write(peakStart+"\t");
            bw.write(peakEnd+"\t");
            bw.write(truePm+"\t");
            bw.write(totalRead+"\t");
            bw.write(inputPeakCount+"\t");
            bw.write(ipPeeakCount);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recordRead(String type,HashMap<String,String> readMap){
        String fileName = "";
        if (type.equals("IP")){fileName = mainPath+"ipReads1000.fasta";}
        if (type.equals("Input")){fileName = mainPath+"inputReads1000.fasta";}
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileName),true));
            Iterator<String> it = readMap.keySet().iterator();
            while (it.hasNext()){
                String readName = it.next();
                String read = readMap.get(readName);
                bw.write(">"+readName);
                bw.newLine();
                bw.write(read);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String readSequencing(String pre_read){
        int readLen = 50;
        String read = "+"+pre_read.substring(0,Math.min(readLen,pre_read.length()));
        double verPoss = Math.random();
        if(verPoss<=0.5){
            read = pre_read.substring(Math.max(0,pre_read.length()-readLen),pre_read.length());
            read = "-"+CommonTools.reverse(read);
        }
        return read;
    }

    private void deleteDir(String path){
        File file = new File(path);
        if(!file.exists()){//判断是否待删除目录是否存在
            file.mkdirs();
//            System.err.println("The dir are not exists!");
        }
        String[] content = file.list();//取得当前目录下所有文件和文件夹
        assert content != null;
        for(String name : content){
            File temp = new File(path, name);
            if(temp.isDirectory()){//判断是否是目录
                deleteDir(temp.getAbsolutePath());//递归调用，删除目录里的内容
                temp.delete();//删除空目录
            }else{
                if(!temp.delete()){//直接删除文件
                    System.err.println("Failed to delete " + name);
                }
            }
        }
    }

    public static void main(String[] args) {
        //Read File

        String MainPath = "E:\\2018_01\\m6a_brays\\模拟测试集\\";
        String seqFile = MainPath+"transSeq1000.fasta";
        FastaReader fr = new FastaReader();
        fr.readFromFile(seqFile);
        HashMap<String,String> seqMap=fr.getSeqMap();

        //生成pm list
        int n = seqMap.size();
        double gap = 1.0/(n+1);
        ArrayList<Double> pmList = new ArrayList<>();
        pmList.add(gap);
        for (int i=0;i<=n;i++){
            double tmp_pm = pmList.get(i)+gap;
            pmList.add(tmp_pm);
        }

//        //模拟生成测序reads
        SimulatingSequence simulatingSequence = new SimulatingSequence();
        simulatingSequence.ini(MainPath+"gene1000\\");
        int cc = 0;
        Iterator it = seqMap.keySet().iterator();
        while (it.hasNext()){
            String seqKey = (String) it.next();
            String seqValue = seqMap.get(seqKey);
//            if (cc>3) break;
            simulatingSequence.genReads(seqValue, seqKey,pmList.get(cc));
            cc++;
            if(cc%100 ==0){System.out.println(cc);}
        }
    }

}
