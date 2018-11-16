package ceov2.org;

import com.badlogic.gdx.Gdx;

import java.util.Map;

public class Lang {

    Map<String, String> translation;

    Lang(String lang) {
        String text = Gdx.files.internal("Languages/" + lang + ".csv").readString();
        text = text.replace("\n",",");
        text = text.replace("\",\"",",");
        text = text.replace("\"\"",",");
        text = text.replace("\"","");
        String s[] = text.split(",");
        if (!s[0].isEmpty()) {
            for (int i = 0; i < s.length; i += 2) {
                System.out.println(i + "/" + s.length);
                translation.put(s[i], s[i + 1]);
            }
        }
    }

    String getTranslation(String r) {
        String ret;
        try {
            ret = translation.get(r);
        } catch (Exception e) {
            ret = r;
        }
        return ret;
    }

    String[] getTranslation(String[] r) { // override array
        String[] ret = new String[r.length];
        for (int i = 0; i < r.length; i++) {
            try {
                ret[i] = translation.get(r[i]);
            } catch (Exception e) {
                ret[i] = r[i];
            }
        }
        return ret;
    }
}
