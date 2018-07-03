package ceov2.org;

public class StringUtils {
    //see if a character is a number from 0-7
    static boolean isValidNumber(char ch){
        boolean isValid=false;
        for(int x=0;x!=8;x++){
            if (String.valueOf(x).equals(String.valueOf(ch))){
                isValid=true;
            }
        }

        return isValid;
    }

}
