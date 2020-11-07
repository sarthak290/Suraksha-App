package com.example.suraksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class userSignin extends AppCompatActivity {
    EditText et1,et2;
    Button bt;
    TextView tv;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signin);
        et1=findViewById(R.id.signin_et1);
        et2=findViewById(R.id.signin_et2);
        bt=findViewById(R.id.signin_bt);
        tv=findViewById(R.id.tv);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp=getSharedPreferences("mysp",MODE_PRIVATE);
                final SharedPreferences.Editor ed=sp.edit();
                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if(document.getData().get("pno").toString().equals(et1.getText().toString())){
                                            if(document.getData().get("password").toString().equals(et2.getText().toString())){
                                                Toast.makeText(userSignin.this, "SIgnIN Successful", Toast.LENGTH_SHORT).show();
                                                ed.putString("username",document.getData().get("name").toString());
                                                ed.putString("pno",document.getData().get("pno").toString());
                                                ed.commit();
                                                Intent in=new Intent(userSignin.this,userhome.class);
                                                startActivity(in);
                                                userSignin.this.finish();
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(userSignin.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(userSignin.this,userSignup.class);
                startActivity(in);
            }
        });
    }
}
