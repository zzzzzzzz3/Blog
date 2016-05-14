package com.example.msi.blog;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 文 件 名: SendPost
 * 创 建 人: ZhangRonghua
 * 创建日期: 2016/5/14 10:56
 * 邮   箱: qq798435167@gmail.com
 * 博   客: http://zzzzzzzz3.github.io
 * 修改时间：
 * 修改备注：
 */
public class SendPost extends Thread {
    private static final String TAG = "SendPost";
    private String method = "";
    private String path = "";
    private String blogID = "";
    private String account = "";
    private String pwd = "";
    private String title = "";
    private String content = "";
    private Handler h = null;

    public SendPost(Handler handler, String method, String blogID, String account, String pwd, String title, String content, String path) {
        this.account = account;
        this.blogID = blogID;
        this.content = content;
        this.method = method;
        this.path = path;
        this.pwd = pwd;
        this.title = title;
        this.h = handler;
    }

    public String sendPost(String outs) {
        HttpURLConnection conn = null;
        String result = "";
        URL url = null;

        try {
            url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/xml");
            conn.setRequestProperty("Charset", "UTF-8");
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
            out.write(outs);
            out.flush();
            out.close();
            result = parseXML(conn.getInputStream());
            conn.disconnect();
        } catch (Exception e) {
            conn.disconnect();
            Log.i(TAG, e.toString());
            //showDialog("" + e);
        }
        return result;
    }

    public String parseXML(InputStream inputStream) {
        String result = "";
        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.newDocument();
            doc.getDocumentElement().normalize();
            int fault = doc.getElementsByTagName("fault").getLength();
            if (fault > 0) {
                result += "post error\n";
                NodeList errcode = doc.getElementsByTagName("int");
                for (int i = 0; i < errcode.getLength(); i++) {
                    String err = errcode.item(i).getChildNodes().item(0).getNodeValue();
                    result += "错误代码：" + err;
                }
                NodeList errstr = doc.getElementsByTagName("string");
                for (int i = 0; i < errstr.getLength(); i++) {
                    String err = errstr.item(i).getChildNodes().item(0).getNodeValue();
                    result += "错误信息：" + err;
                }
            } else {
                NodeList l = doc.getElementsByTagName("string");
                for (int i = 0; i < l.getLength(); i++) {
                    String id = l.item(i).getChildNodes().item(0).getNodeValue();
                    result += "发布成功 文章编号：" + id;
                }
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            //showDialog("" + e);
        }
        return result;
    }

    public String getPostString(String method, String blogID, String account, String pwd, String title, String content) {
        String s = "";
        s += "<methodCall>";
        s += "<methodName>" + method + "</methodName>";
        s += "<params>";
        s += "<params><value><string>" + blogID + "</string></value></params>";
        s += "<params><value><string>" + account + "</string></value></params>";
        s += "<params><value><string>" + pwd + "</string></value></params>";
        s += "<params><value><string>" + title + "</string></value></params>";
        s += "<params><value><string>" + content + "</string></value></params>";
        s += "<params><value><boolean>1</boolean></value></params>";
        s += "</params>";
        s += "</methodCall>";
        return s;
    }

    @Override
    public void run() {
        String outs = getPostString(method, blogID, account, pwd, title, content);
        String re = sendPost(outs);
        Message mess = h.obtainMessage();
        Bundle b = new Bundle();
        b.putString("mess", re);
        mess.setData(b);
        h.sendMessage(mess);
        super.run();
    }
}
