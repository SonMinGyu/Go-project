package org.application.go;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.application.go.Model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    Button loginButton;
    Button registerButton;

    EditText emailText;
    EditText passwordText;

    public String userLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        //auth.signOut();

        loginButton = (Button) findViewById(R.id.login_loginButton);
        registerButton = (Button) findViewById(R.id.login_registerButton);

        emailText = (EditText) findViewById(R.id.login_email_editText);
        passwordText = (EditText) findViewById(R.id.login_password_editText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEvent();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) // 로그인
                {
                    Intent loginSuccessintent = new Intent(LoginActivity.this, GoWatingRoomActivity.class);
                    LoginActivity.this.startActivity(loginSuccessintent);
                    finish();
                }
                else // 로그아웃
                {

                }
            }
        };

    }

    public void loginEvent()
    {
        auth.signInWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()) // 로그인 실패한 부분
                {
                    Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
                /*
                else
                {
                    Map<String, Object> taskMap = new HashMap<String, Object>();
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    UserModel userModel = new UserModel();
                    if(autoLogin.isChecked())
                    {
                        taskMap.put("isAutoLoginChecked", "true");
                    }
                    else
                    {
                        taskMap.put("isAutoLoginChecked", "false");
                    }
                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(taskMap);
                }

                 */
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authStateListener);
    }
}