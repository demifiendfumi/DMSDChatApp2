package sg.edu.rp.webservices.dmsdchatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SetDisplayNameActivity extends AppCompatActivity {

    private static final String TAG = "SetDisplayNameActivity";
    private EditText etName;
    private Button btnSetDisplayName;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userNameRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_display_name);

        etName = findViewById(R.id. etName);
        btnSetDisplayName = findViewById(R.id. btnSubmit);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        userNameRef = firebaseDatabase.getReference("profiles/" + firebaseUser.getUid());

        userNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "userProfileRef.addValueEventListener -- onDataChange()");
                //DataSnapshot profile = dataSnapshot.getValue()
                if (dataSnapshot.getValue(String.class) != null) {
                    etName.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error occurred", databaseError.toException());
            }
        });

        btnSetDisplayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();

                userNameRef.setValue(name);

                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.putExtra("name", name);
                startActivity(i);
            }
        });
    }
}
