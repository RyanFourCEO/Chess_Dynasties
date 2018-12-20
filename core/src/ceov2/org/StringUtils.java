package ceov2.org;

public class StringUtils {
    //see if a character is a number from 0-7
    static boolean isValidNumber(char ch) {
        boolean isValid = false;
        for (int x = 0; x != 8; x++) {
            if (String.valueOf(x).equals(String.valueOf(ch))) {
                isValid = true;
            }
        }
        return isValid;
    }

    static String removeNonNumberCharacters(String string) {
        String newString = "";
        for (int x = 0; x != string.length(); x++) {
            if (isNumber(string.charAt(x))) {
                newString += String.valueOf(string.charAt(x));
            }
        }
        return newString;
    }

    static boolean isNumber(char ch) {
        boolean isValid = false;
        for (int x = 0; x != 10; x++) {
            if (String.valueOf(x).equals(String.valueOf(ch))) {
                isValid = true;
            }
        }
        return isValid;
    }

    static String convertToHex(String string) {
        StringBuilder hex = new StringBuilder();
        for (int x = 0; x != string.length(); x++) {
            //put ascii code of character into an int
            int asciiOfChar = (int) string.charAt(x);
            //convert ascii code to hex
            String hexCode = Integer.toHexString(asciiOfChar);
            //hex numbers must always be sent in twos,as per our TCP protocol, Padded with 0s.
            //so if a character has only one digit,a 0 is added to it
            if (hexCode.length() == 1) {
                hexCode = "0" + hexCode;
            }
            //add the hex number to the stringBuilder
            hex.append(hexCode);
        }
        return hex.toString();
    }

    static String convertFromHex(String hex) {
        StringBuilder notHex = new StringBuilder();
//loop through the string half as many times as it's length
        for (int x = 0; x < hex.length() - 1; x += 2) {
            //get two digits from the hex string
            String twoDigit = hex.substring(x, x + 2);
            //convert the two digits to the ASCII code for the character
            int characterCode = Integer.parseInt(twoDigit, 16);
            //cast the code to a character and add it to the String
            notHex.append((char) characterCode);
        }
        return notHex.toString();
    }

    static int countOccurrences(String test, char contains) {
        int occurrences = 0;
        for (int x = 0; x != test.length(); x++) {
            if (test.charAt(x) == contains) {
                occurrences++;
            }
        }
        return occurrences;
    }

    static boolean isAlphaNumeric(String test){
        boolean isAlphanumeric = true;
        for (int i = 0; i != test.length();i++){
            if (!Character.isLetterOrDigit(test.charAt(i))){
                isAlphanumeric = false;
            }
        }
        return isAlphanumeric;
    }
}
