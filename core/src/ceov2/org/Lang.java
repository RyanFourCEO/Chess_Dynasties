package ceov2.org;

import com.badlogic.gdx.Gdx;

import java.util.HashMap;

public class Lang {

    HashMap<String, String> translation = new HashMap<String, String>();

    Lang(String lang) {
        String text = Gdx.files.internal("Languages/" + lang + ".csv").readString();
        text = text.replace("\n", ",");
        text = text.replace("\",\"", ",");
        text = text.replace("\"\"", ",");
        text = text.replace("\"", "");
        String s[] = text.split(",");
        if (!s[0].isEmpty()) {
            for (int i = 0; i < s.length; i += 2) {
                System.out.println(i + "/" + s.length);
                translation.put(s[i], s[i + 1]);
            }
        }
    }

    String getTranslation(String r) {
        String ret = r;
        try {
            if (!translation.get(r).isEmpty()) {
                ret = translation.get(r);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return ret;
    }
}
