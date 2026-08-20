package hydrogen.lib.jsoup;

import hydrogen.lib.jsoup.Jsoup;
import hydrogen.lib.jsoup.Connection_2;

public final class Jsoup {
    private Jsoup() {
    }

    public static Document a(String html) {
        return new Document(org.jsoup.Jsoup.parse(html));
    }

    public static Connection_2 b(String url) {
        return Connection_2.b(url);
    }
}
