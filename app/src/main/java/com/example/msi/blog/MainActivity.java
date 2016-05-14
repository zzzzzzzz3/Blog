package com.example.msi.blog;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button mPostButton;
    private EditText mID, mUser, mPassw, mTitle, mContent;

    private String method = "metaWeblog.newPost";
    private String path = "http://blog.csdn.net/qq798435167";
    private String blogID ="";
    private String account="";
    private String pwd = "";
    private String title ="";
    private String content="";
    Handler h=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle b=msg.getData();
            String s=b.getString("mess");
            showDialog(s);
            System.out.print(s);
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        mPostButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                 blogID = mID.getText().toString();
                 account = mUser.getText().toString();
                 pwd = mPassw.getText().toString();
                 title = mTitle.getText().toString();
                 content = mContent.getText().toString();

                if (blogID.equals("") || account.equals("") || pwd.equals("") || title.equals("") || content.equals("")) {
                    showDialog("没有填写内容");
                } else {
                    Runnable r=new Runnable() {
                        @Override
                        public void run() {
                            String outs = getPostString(method, blogID, account, pwd, title, content);
                            String re = sendPost(outs);
                            showDialog(re);
                            Message mess=h.obtainMessage();
                            Bundle b=new Bundle();
                            b.putString("mess",re);
                            mess.setData(b);
                            h.sendMessage(mess);
                        }
                    };
                    Thread th=new Thread(r);
                    th.start();
                }
            }
        });
    }

    private String sendPost(String outs) {
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

    private String parseXML(InputStream inputStream) {
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

    private String getPostString(String method, String blogID, String account, String pwd, String title, String content) {
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

    private void showDialog(String str) {
        new AlertDialog.Builder(MainActivity.this).setTitle("message").setMessage(str).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void initView() {
        mPostButton = (Button) findViewById(R.id.post);
        mUser = (EditText) findViewById(R.id.et_user);
        mPassw = (EditText) findViewById(R.id.et_passw);
        mID = (EditText) findViewById(R.id.et_id);
        mContent = (EditText) findViewById(R.id.et_content);
        mTitle = (EditText) findViewById(R.id.et_title);
    }
}
