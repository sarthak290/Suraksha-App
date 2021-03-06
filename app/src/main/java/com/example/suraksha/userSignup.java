package com.example.suraksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class userSignup extends AppCompatActivity {
    EditText et1, et2, et3,et4;
    Button bt;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);
        et1 = findViewById(R.id.signup_et1);
        et2 = findViewById(R.id.signup_et2);
        et3 = findViewById(R.id.signup_et3);
        et4=findViewById(R.id.signup_et4);
        bt = findViewById(R.id.signup_bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    String pno=et2.getText().toString();
                                    int f=1;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if(document.getData().get("pno").toString().equals(pno)){
                                            f=0;
                                            break;
                                        }
                                    }
                                    if(f==1)
                                    {
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("name", et1.getText().toString());
                                        user.put("pno", pno);
                                        user.put("email", et3.getText().toString());
                                        user.put("password", et4.getText().toString());

// Add a new document with a generated ID
                                        db.collection("users")
                                                .add(user)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Toast.makeText(userSignup.this, "SignUp successful", Toast.LENGTH_SHORT).show();
                                                        Intent in = new Intent(userSignup.this, userSignin.class);
                                                        startActivity(in);
                                                        userSignup.this.finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(userSignup.this, "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                    else if(f==0)
                                    {
                                        Toast.makeText(userSignup.this, "Phone Number exists", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(userSignup.this, "Error in matching username", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }
}
