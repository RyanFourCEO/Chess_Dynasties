package ceov2.org;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

public class Lang {

    private Map<String, String> translation;

    private Lang(String lang) {
        String langFile = "../../assets/Languages" + lang + ".csv";
        try {
            BufferedReader br = new BufferedReader(new FileReader(langFile));
            while (br.readLine() != null) {
                String s[] = br.readLine().split(",");
                translation.put(s[0], s[1]);
            }
        } catch (Exception e) {
            System.out.println("no translation file");
        }
    }

    String getTranslation(String r) {
        String ret;
        if (translation.get(r).isEmpty()) {
            try {
                ret = translation.get(r);
            } catch (Exception e) {
                ret = r;
            }
        } else ret = r;
        return ret;
    }
}
