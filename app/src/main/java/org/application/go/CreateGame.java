package org.application.go;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.application.go.Model.GameModel;
import org.application.go.Model.UserModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateGame extends AppCompatActivity {

    EditText gameTitle;
    CheckBox goCheckBox;
    CheckBox omokCheckBox;
    CheckBox blackCheckBox;
    CheckBox whiteCheckBox;
    Button createButton;

    String userLevel = bringUserLevel();
    String userName = bringUserName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        gameTitle = (EditText) findViewById(R.id.createGame_gameName_editText);
        goCheckBox = (CheckBox) findViewById(R.id.createGame_go_checkBox);
        omokCheckBox = (CheckBox) findViewById(R.id.createGame_omok_checkBox);
        blackCheckBox = (CheckBox) findViewById(R.id.createGame_black_checkBox);
        whiteCheckBox = (CheckBox) findViewById(R.id.createGame_white_checkBox);
        createButton = (Button) findViewById(R.id.createGame_createButton);

        userLevel = bringUserLevel();

        goCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(goCheckBox.isChecked())
                {
                    omokCheckBox.setChecked(false);
                }
            }
        });

        omokCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(omokCheckBox.isChecked())
                {
                    goCheckBox.setChecked(false);
                }
            }
        });

        blackCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(blackCheckBox.isChecked())
                {
                    whiteCheckBox.setChecked(false);
                }
            }
        });

        whiteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(whiteCheckBox.isChecked())
                {
                    blackCheckBox.setChecked(false);
                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameTitle.getText().toString().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "게임 제목을 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 오목 만들면 수정해야함
                if (!goCheckBox.isChecked() && !omokCheckBox.isChecked()) {
                    Toast.makeText(getApplicationContext(), "게임을 선택하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!blackCheckBox.isChecked() && !whiteCheckBox.isChecked()) {
                    Toast.makeText(getApplicationContext(), "흑백을 선택하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String stGameType = null;
                GameModel gameModel = new GameModel();

                if(goCheckBox.isChecked()) {
                    gameModel.setGameTitle(gameTitle.getText().toString());
                    stGameType = "Go";
                    gameModel.setGameType(stGameType);
                    gameModel.setHostUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    if (blackCheckBox.isChecked()) {
                        gameModel.setHostColor(1);
                        gameModel.setParticipantColor(2);
                    } else if (whiteCheckBox.isChecked()) {
                        gameModel.setHostColor(2);
                        gameModel.setParticipantColor(1);
                    }
                    gameModel.setGameKey(stGameType + " + " +gameTitle.getText().toString() + " + " + getToDay());
                    gameModel.setHostLevel(userLevel);
                    gameModel.setNumberOfUsers(1);
                    gameModel.setHostName(userName);
                    gameModel.setDeathBlackStones(0);
                    gameModel.setDeathWhiteStones(0);
                    gameModel.setBlackTimer("00:30:00");
                    gameModel.setWhiteTimer("00:30:00");
                    gameModel.setOrder(1);
                    gameModel.setFinish(false);
                    gameModel.setParticipantName("");

                    FirebaseDatabase.getInstance().getReference().child("Game")
                            .child(stGameType + " + " +gameTitle.getText().toString() + " + " + getToDay()).setValue(gameModel);

                    /*
                    FirebaseDatabase.getInstance().getReference().child("WatingRoom")
                            .child(stGameType + " + " +gameTitle.getText().toString() + " + " + getToDay()).setValue(gameModel);

                     */

                    Intent gameIntent = new Intent(CreateGame.this, MainActivity.class);
                    gameIntent.putExtra("gamekey", gameModel.getGameKey());
                    gameIntent.putExtra("gameTitle", gameModel.getGameTitle());
                    gameIntent.putExtra("hostUid", gameModel.getHostUid());
                    CreateGame.this.startActivity(gameIntent);
                }
                else if(omokCheckBox.isChecked())
                {
                    gameModel.setGameTitle(gameTitle.getText().toString());
                    stGameType = "Omok";
                    gameModel.setGameType(stGameType);
                    gameModel.setHostUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    if (blackCheckBox.isChecked()) {
                        gameModel.setHostColor(1);
                        gameModel.setParticipantColor(2);
                    } else if (whiteCheckBox.isChecked()) {
                        gameModel.setHostColor(2);
                        gameModel.setParticipantColor(1);
                    }
                    gameModel.setGameKey(stGameType + " + " +gameTitle.getText().toString() + " + " + getToDay());
                    gameModel.setNumberOfUsers(1);
                    gameModel.setHostName(userName);
                    gameModel.setOrder(1);
                    gameModel.setFinish(false);
                    gameModel.setParticipantName("");

                    FirebaseDatabase.getInstance().getReference().child("Game")
                            .child(stGameType + " + " +gameTitle.getText().toString() + " + " + getToDay()).setValue(gameModel);

                    Intent gameIntent = new Intent(CreateGame.this, OmokMultiplayActivity.class);
                    gameIntent.putExtra("gamekey", gameModel.getGameKey());
                    gameIntent.putExtra("gameTitle", gameModel.getGameTitle());
                    gameIntent.putExtra("hostUid", gameModel.getHostUid());
                    CreateGame.this.startActivity(gameIntent);
                }

                finish();
            }
        });

    }

    public static String getToDay()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy+MM+dd+HH+mm+ss");
        return sdf.format(new Date());
    }


    public String bringUserLevel()
    {
        final List<UserModel> userModels = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModels.clear();
                for(DataSnapshot item :snapshot.getChildren())
                {
                    if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(item.getValue(UserModel.class).getUserUid()))
                    {
                        userLevel = item.getValue(UserModel.class).getUserGoLevel();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return userLevel;
    }

    public String bringUserName()
    {
        final List<UserModel> userModels = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModels.clear();
                for(DataSnapshot item :snapshot.getChildren())
                {
                    if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(item.getValue(UserModel.class).getUserUid()))
                    {
                        userName = item.getValue(UserModel.class).getUserName();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return userName;
    }
}