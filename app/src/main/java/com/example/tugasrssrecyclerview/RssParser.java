package com.example.tugasrssrecyclerview;

import android.util.Log;

import com.example.tugasrssrecyclerview.Artikel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RssParser {
    private static String TAG_ITEM = "item";
    private static String TAG_TITLE = "title";
    private static String TAG_CHANNEL = "channel";
    private static String TAG_LINK = "link";
    private static String TAG_DESCRIPTION = "description";
    private static String TAG_PUBDATE = "pubDate";

    public String loadRssFromUrl(String url) throws IOException {
        Log.d("RSSPARSER", "Start rss parser");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        String xml = response.body().string();
        Log.d("RSSPARSER", "xml " + xml);
        return xml;
    }

    public ArrayList<Artikel> parseRssFromUrl(String xml) throws ParserConfigurationException, IOException, SAXException {
        ArrayList<Artikel> list = new ArrayList<>();
        DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = builder.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document doc = db.parse(is);
        NodeList nodeList = doc.getElementsByTagName(TAG_CHANNEL);
        Element e = (Element) nodeList.item(0);

        NodeList items = e.getElementsByTagName(TAG_ITEM);
        for (int i = 0; i < items.getLength(); i++) {
            Artikel item = new Artikel();
            Element e1 = (Element) items.item(i);

            String judul = getNodeVallue(e1, TAG_TITLE);
            Log.d("parse_judul", judul);
            item.judul=judul;
            list.add(item);

            String link = getNodeVallue(e1, TAG_LINK);
            Log.d("parse_link", link);
            item.link=link;

            String description = getNodeVallue(e1, TAG_DESCRIPTION);;
            Log.d("parse_description", description);
            item.linkGambar = description.substring(description.indexOf("src=")+5, description.indexOf("width")+5);
            String replace = item.linkGambar.replace("\" width", "");
            Log.d("parse_link_gambar_final", replace);
            item.linkGambar = replace;

            String pubDate =getNodeVallue(e1, TAG_PUBDATE);
            Log.d("parse_pub_date", pubDate);
            item.pubDate=pubDate;
        }
        return list;

    }
    public String getNodeVallue(Element e1, String tag){
        NodeList  n= e1.getElementsByTagName(tag);
        Node ne = n.item(0);
        Node child = ne.getFirstChild();
        return child.getNodeValue();
    }
}
