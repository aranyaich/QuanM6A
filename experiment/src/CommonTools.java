import java.util.HashMap;

public class CommonTools {
    static public String reverse(String segment){
        HashMap<Character, Character> CompeMap=new HashMap<>();
        CompeMap.put('A','T');
        CompeMap.put('T','A');
        CompeMap.put('C','G');
        CompeMap.put('G','C');
        String comp_Seq="";
        for (int i=0;i<segment.length();i++){
            if (CompeMap.containsKey(segment.charAt(i))){
                comp_Seq=CompeMap.get(segment.charAt(i))+comp_Seq;
            }else {
                comp_Seq="*"+comp_Seq;
            }
        }
        return comp_Seq;
    }
}
