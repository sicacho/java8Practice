import com.company.googledrive.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 1/21/2017.
 */

public class TestMigrateGoogleDrive {

  @Test
  public void testMigrate() throws Exception {


    MigrateService migrateService = new GDriveMigrateService();
    migrateService.migrate("https://drive.google.com/open?id=0B3YJQgQ5nWc3VkJJN0NUV2tHcU0","testupload");

  }

  @Test
  public void testCookies() throws Exception {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpClientContext context = HttpClientContext.create();
    HttpGet get = new HttpGet("https://drive.google.com/open?id=0B3YJQgQ5nWc3VkJJN0NUV2tHcU0");
    BasicCookieStore cookieStore = new BasicCookieStore();
    context.setCookieStore(cookieStore);
    CloseableHttpResponse response = httpClient.execute(get,context);
    InputStream inputStream = response.getEntity().getContent();
    String doc = "";
    int ch = 0;
    while (ch != -1) {
      ch = inputStream.read();
      if (ch != -1) doc += (char)ch;
    }
    System.out.println(doc);
    Document document = Jsoup.parse(doc);
    List<UrlDTO> result = null;
    Elements scriptElements = document.getElementsByTag("script");
    String text = scriptElements.get(scriptElements.size()-2).dataNodes().get(0).getWholeData();
    Pattern pattern = Pattern.compile("(?=fmt_stream_map\\\",\\\")(.*)(?=\\\")");
    Matcher matcher = pattern.matcher(text);
    if (matcher.find())
    {
      String[] listUrl = matcher.group(1).replace("\"","").split("\\,");
      result = Arrays.stream(listUrl).
          filter(s -> StringHelper.isNotStartwith(s, Resolution.values())).
          map(s1 -> ConvertEntityToDto.urlDTO(StringEscapeUtils.unescapeJava(s1))).
          collect(Collectors.toList());
      Collections.reverse(result);
    }
    get = new HttpGet(result.get(0).getFile());
    response = httpClient.execute(get,context);
    inputStream = response.getEntity().getContent();

  }
}
