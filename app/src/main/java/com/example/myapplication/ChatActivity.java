package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ChatActivity extends AppCompatActivity {

    private EditText message;
    private Button sendMessageButton;
    public static final String USER_ID_KEY = "userId";
    private String currentUserId;
    private ListView listView;
    private ArrayList<Message> mMessages;
    private ChatAdapter mAdapter;
    private Handler handler = new Handler() {
        @Override
        public void publish(LogRecord record) {

        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }
    };
    private static final int MAX_MSG_TO_SHOW = 70;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getCurrentUser();
    }

    private void getCurrentUser(){
        currentUserId = ParseUser.getCurrentUser().getObjectId();
        messagePosting();

    }

    private void messagePosting() {

        message = (EditText) findViewById(R.id.newMessage);
        sendMessageButton = (Button) findViewById(R.id.buttonSend);
        listView = (ListView) findViewById(R.id.listciew_chat);
        mMessages = new ArrayList<Message>();
        mAdapter = new ChatAdapter(ChatActivity.this,currentUserId,mMessages);
        listView.setAdapter(mAdapter);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!message.getText().toString().equals("")){
                    Message msg = new Message();
                    msg.setUserID(currentUserId);
                    msg.setBody(message.getText().toString());
                    msg.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            receiveMessages();
                        }
                    });
                    message.setText("");
                }else{
                    Toast.makeText(getApplicationContext(), "Empty Message", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void receiveMessages() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.setLimit(MAX_MSG_TO_SHOW);
        query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, ParseException e) {
                if(e == null){
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged();
                    listView.invalidate();
                }else{
                    Log.v("Error:","Error:" + e.getMessage());
                }

            }
        });
    }

    private void refreshMessages(){
        receiveMessages();
    }
}
