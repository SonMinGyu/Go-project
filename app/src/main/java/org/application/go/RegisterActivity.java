package org.application.go;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.application.go.Model.UserModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    EditText name;
    Button registerButton;
    Spinner levelSpinner;

    int positionNumber = 0;
    String stGoLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = (EditText) findViewById(R.id.register_emailText);
        password = (EditText) findViewById(R.id.register_passwordText);
        name = (EditText) findViewById(R.id.register_nameText);
        registerButton = (Button) findViewById(R.id.register_registerButton);
        levelSpinner = (Spinner) findViewById(R.id.register_levelSpinner);

        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                positionNumber = adapterView.getSelectedItemPosition();
                stGoLevel = (String) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userID = email.getText().toString();
                final String userPassword = password.getText().toString();
                final String userName = name.getText().toString();
                //final int userAge = Integer.parseInt(ageText.getText().toString());

                if((userID.equals("")) || (userPassword.equals("")) || (userName.equals("")))
                {
                    Toast.makeText(getApplicationContext(), "빈칸을 모두 채워주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!isEmail(userID))
                {
                    Toast.makeText(getApplicationContext(), "알맞은 이메일 형식이 아닙니다!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if((userPassword.length() < 6))
                {
                    Toast.makeText(getApplicationContext(), "비밀번호는 6자리 이상으로 해주세요!",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(positionNumber == 0)
                {
                    Toast.makeText(getApplicationContext(), "바둑 기력을 선택해 주세요!",Toast.LENGTH_SHORT).show();
                    return;
                }

                // firebase auth에 회원내용 생성
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(),
                        password.getText().toString()).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Toast.makeText(getApplicationContext(), "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        // firebase realtime db에 uid를 통해서 데이터 넣기
                        String uid = task.getResult().getUser().getUid();
                        //String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        UserModel userModel = new UserModel();
                        userModel.setUserEmail(email.getText().toString());
                        userModel.setUserPassword(password.getText().toString());
                        userModel.setUserUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        userModel.setUserName(name.getText().toString());
                        userModel.setUserGoLevel(stGoLevel);

                        FirebaseDatabase.getInstance().getReference().child("users").child(uid + "+" + name.getText().toString()).setValue(userModel);

                        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        RegisterActivity.this.startActivity(loginIntent);
                        finish();
                    }
                });
            }

        });
    }

    public static boolean isEmail(String email){
        boolean returnValue = false;
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if(m.matches()){
            returnValue = true;
        }

        return returnValue;
    }
}