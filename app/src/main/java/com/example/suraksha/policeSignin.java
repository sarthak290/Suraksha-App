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

public class policeSignin extends AppCompatActivity {
    EditText et1,et2;
    Button bt;
    TextView tv;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_signin);
        et1=findViewById(R.id.signin_et1);
        et2=findViewById(R.id.signin_et2);
        bt=findViewById(R.id.signin_bt);
        tv=findViewById(R.id.tv);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp1=getSharedPreferences("mysp1",MODE_PRIVATE);
                final SharedPreferences.Editor ed1=sp1.edit();
                db.collection("stations")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if(document.getData().get("pno").toString().equals(et1.getText().toString())){
                                            if(document.getData().get("password").toString().equals(et2.getText().toString()))
                                            {
                                                Toast.makeText(policeSignin.this, "SignIN successful", Toast.LENGTH_SHORT).show();
                                                ed1.putString("station_name",document.getData().get("station_name").toString());
                                                ed1.commit();
                                                Intent in=new Intent(policeSignin.this,policehome.class);
                                                startActivity(in);
                                                policeSignin.this.finish();

                                            }
                                        }
                                    }
                                } else {

                                }
                            }
                        });
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(policeSignin.this,policeSignup.class);
                startActivity(in);
            }
        });
    }
}
