/**.
 * Demo class
 *
 * @author dengyongjie
 * @date 2018/03/03
 */
public class Exon {
    private String transcriptId;
    private String exonNum;
    private String chr;
    private int start;
    private int end;
    private String stand;

    void setFromGtf(final String gtfLine) {
        String[] gtfNotes = gtfLine.split("\t");
        String anx = gtfNotes[8];
        this.start = Integer.valueOf(gtfNotes[3]);
        this.end = Integer.valueOf(gtfNotes[4]);
        this.chr = gtfNotes[0];
        this.transcriptId = anx.substring(anx.indexOf("transcript_id")).split("\"")[1];
        this.exonNum = anx.substring(anx.indexOf("exon_number")).split("\"")[1];
        this.stand = gtfNotes[6];
    }

    void show(){
        System.out.println("transcriptId: "+this.transcriptId);
        System.out.println("exon_num: "+this.exonNum);
        System.out.println("chr  : "+this.chr);
        System.out.println("start: "+this.start);
        System.out.println("end  : "+this.end);
        System.out.println("stand: "+this.stand+"\n");
    }

    String getTranscriptId() {
        return transcriptId;
    }

    public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }

    public String getExon_num() {
        return exonNum;
    }

    public void setExon_num(String exon_num) {
        this.exonNum = exon_num;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getStand() {
        return stand;
    }

    public void setStand(String stand) {
        this.stand = stand;
    }

    public String getExonNum() {
        return exonNum;
    }

    public void setExonNum(String exonNum) {
        this.exonNum = exonNum;
    }

    public String getChr() {
        return chr;
    }

    public void setChr(String chr) {
        this.chr = chr;
    }

    public static void main(String[] args) {
        String test = "1\thavana\tExon\t11869\t12227\t.\t+\t.\tgene_id \"ENSG00000223972\"; gene_version \"5\"; transcriptId \"ENST00000456328\"; transcript_version \"2\"; exon_number \"1\"; gene_name \"DDX11L1\"; gene_source \"havana\"; gene_biotype \"transcribed_unprocessed_pseudogene\"; transcript_name \"DDX11L1-202\"; transcript_source \"havana\"; transcript_biotype \"processed_transcript\"; exon_id \"ENSE00002234944\"; exon_version \"1\"; tag \"basic\"; transcript_support_level \"1\";\n" ;
        Exon nn = new Exon();
        nn.setFromGtf(test);
        nn.show();

    }
}
