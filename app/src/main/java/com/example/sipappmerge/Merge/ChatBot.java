package com.example.sipappmerge.Merge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sipappmerge.R;
import com.example.sipappmerge.Utils.CallApi;
import com.example.sipappmerge.Utils.ShaUtilss;
import com.example.sipappmerge.Utils.Util;
import com.example.sipappmerge.Utils.VolleyResponseListener;
import com.example.sipappmerge.adapter.ChatAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;


public class ChatBot extends AppCompatActivity {

    private RecyclerView rvContacts;
    private EditText etMessage;
    private ImageView imgRefresh,imgSend,imgClose,imgVideo;
    private ArrayList<Contact> chatList;
    ChatAdapter chatAdapter;
    WebView webview;
    VideoView videoView,videoView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        init();
    }

    private void init() {
        etMessage = findViewById(R.id.etMessage);
        imgRefresh = findViewById(R.id.imgRefresh);
        imgSend = findViewById(R.id.imgSend);
        imgVideo = findViewById(R.id.imgVideo);
        rvContacts = findViewById(R.id.rvContacts);
        videoView = findViewById(R.id.videoView);
        videoView1 = findViewById(R.id.videoView1);

        videoView.setVideoURI(Uri.parse("http://220.225.104.135/SAMPLEVIDEO/APR%20report.mp4"));
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.start();
        videoView.pause();

        videoView1.setVideoURI(Uri.parse("http://220.225.104.135/SAMPLEVIDEO/APR%20report.mp4"));
        videoView1.setMediaController(new MediaController(this));
        videoView1.requestFocus();
        //videoView.start();
       //videoView.pause();
        //webview = findViewById(R.id.webview);

        //webview.getSettings().setJavaScriptEnabled(true);
        //WebSettings webSettings = webview.getSettings();

        //webSettings.setMediaPlaybackRequiresUserGesture(false);
        //webview.loadUrl("http://220.225.104.135/SAMPLEVIDEO/APR%20report.mp4");
        imgClose = findViewById(R.id.imgClose);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatBot.this,LinearLayoutManager.VERTICAL,false);
        rvContacts.setLayoutManager(layoutManager);
        rvContacts.setHasFixedSize(true);
        chatList = new ArrayList<Contact>();
        chatAdapter = new ChatAdapter(chatList,ChatBot.this);
        rvContacts.setAdapter(chatAdapter);
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateList();
            }
        });
        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatList.clear();
                chatAdapter.notifyDataSetChanged();
                loadList("intro");
            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imgVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatBot.this,VideoListActivity.class));
            }
        });
        loadList("intro");
    }

    private void updateList() {
        if(!etMessage.getText().toString().isEmpty()) {
            Contact c1 = new Contact();
            c1.setStrMessage(etMessage.getText().toString());
            c1.setStrTime(Util.getTime());
            c1.setIsIn("yes");
            chatList.add(c1);
            int newMsgPosition = chatList.size() - 1;

            chatAdapter.notifyItemInserted(newMsgPosition);
            rvContacts.scrollToPosition(newMsgPosition);
            loadList(etMessage.getText().toString());

        }
    }

    private void loadList(String strQuestion) {







        try {
            JSONObject params = new JSONObject();
            params.put("session_id", random());
            params.put("user_question", strQuestion);
            Log.e("LoadChatBot", " Params > " + params.toString());
            CallApi.postResponse(ChatBot.this, params.toString(), "http://220.225.104.138:4009/v1/chatbot/", new VolleyResponseListener() {

                @Override
                public void onError(String message) {
                    Log.e("LoadChatBot error",message);

                }

                @Override
                public void onResponse(JSONObject response) {
                    Log.e("LoadChatBot Response", response.toString());
                    if(response!= null)
                    {
                        try {
                            Contact contact = new Contact();
                            contact.setStrMessage(response.getString("answer"));
                            contact.setIsIn("no");
                            contact.setStrTime(Util.getTime());
                            chatList.add(contact);
                            int newMsgPosition = chatList.size() - 1;
                            etMessage.setText("");
                            chatAdapter.notifyItemInserted(newMsgPosition);
                            rvContacts.scrollToPosition(newMsgPosition);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public  String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(8);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
