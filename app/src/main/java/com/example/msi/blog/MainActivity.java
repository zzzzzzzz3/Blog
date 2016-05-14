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
    private String blogID = "";
    private String account = "";
    private String pwd = "";
    private String title = "";
    private String content = "";
    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            String s = b.getString("mess");
            showDialog(s);
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
                    new SendPost(h, method, blogID, account, pwd, title, content, path).start();
                }
            }
        });
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
