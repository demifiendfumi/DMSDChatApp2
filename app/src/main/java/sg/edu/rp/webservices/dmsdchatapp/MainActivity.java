package sg.edu.rp.webservices.dmsdchatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView tvWeather;
    private EditText etMessage;
    private Button btnMessage;

    private ListView lvMessage;
    ArrayList<Message> alMessages;
    ArrayAdapter aaMessages;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference messageListRef;
    private String displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvWeather = findViewById(R.id. textViewWeather);
        etMessage = findViewById(R.id. editTextMessage);
        btnMessage = findViewById(R.id.btnAddMessage);
        lvMessage = findViewById(R.id.lvMessage);
        alMessages = new ArrayList<Message>();
        aaMessages = new CustomMessage(MainActivity.this, R.layout.message_row, alMessages);
        lvMessage.setAdapter(aaMessages);

        Intent i = getIntent();
        displayName = i.getStringExtra("name");

        HttpRequest request = new HttpRequest
                ("https://api.data.gov.sg/v1/environment/2-hour-weather-forecast");
        request.setOnHttpResponseListener(mHttpResponseListener);
        request.setMethod("GET");
        request.execute();

        firebaseDatabase = FirebaseDatabase.getInstance();
        messageListRef = firebaseDatabase.getReference("messages");

        messageListRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("MainActivity", "onChildAdded()");
                Message message = dataSnapshot.getValue(Message.class);
                if (message != null) {
                    message.setId(dataSnapshot.getKey());

                    alMessages.add(message);
                    aaMessages.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Log.i("MainActivity", "onChildChanged()");
                String selectedId = dataSnapshot.getKey();
                Message message = dataSnapshot.getValue(Message.class);
                if (message != null) {
                    for (int i = 0; i < alMessages.size(); i++) {
                        if (alMessages.get(i).getId().equals(selectedId)) {
                            message.setId(selectedId);
                            alMessages.set(i, message);
                            break;
                        }
                    }
                    aaMessages.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.i("MainActivity", "onChildRemoved()");
                String selectedId = dataSnapshot.getKey();
                for(int i= 0; i < alMessages.size(); i++) {
                    if (alMessages.get(i).getId().equals(selectedId)) {
                        alMessages.remove(i);
                        break;
                    }
                }
                aaMessages.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("MainActivity", "onChildMoved()");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", "Database error occurred", databaseError.toException());
            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = etMessage.getText().toString();
                String user = displayName;
                Message message = new Message(msg, user);

                messageListRef.push().setValue(message);

            }
        });

//        lvMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Message inventory = alMessages.get(i);  // Get the selected Student
//                messageListRef.child(inventory.getId()).removeValue();
//
//                Toast.makeText(getApplicationContext(), " Student record deleted successfully", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.logout) {

            firebaseAuth.signOut();

            Intent i = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    private HttpRequest.OnHttpResponseListener mHttpResponseListener =
            new HttpRequest.OnHttpResponseListener() {
                @Override
                public void onResponse(String response){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("items");
                        for (int i=0; i<jsonArray.length(); i++){
                            JSONObject jsonObj = jsonArray.getJSONObject(0);
                            JSONArray jsonArr = jsonObj.getJSONArray("forecasts");
                            for(int j = 0; j < jsonArr.length(); j++){
                                JSONObject jsonObjFore = jsonArr.getJSONObject(j);
                                String area = jsonObjFore.getString("area");
                                Log.d("area", area);
                                if(area.equals("Woodlands")){
                                    tvWeather.setText("Weather Forecast @ Woodlands:\n" + jsonObjFore.getString("forecast"));
                                }
                            }
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            };
}


