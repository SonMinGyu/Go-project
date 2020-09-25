package org.application.go;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GameMenuActivity extends AppCompatActivity {

    Button multiplayButton;
    Button goButton;
    Button omokButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        multiplayButton = (Button) findViewById(R.id.gameMenu_multiplayButton);
        goButton = (Button) findViewById(R.id.gameMenu_goButton);
        omokButton = (Button) findViewById(R.id.gameMenu_omokButton);

        multiplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameMenuActivity.this, GoWatingRoomActivity.class);
                GameMenuActivity.this.startActivity(intent);
                //Intent loginSuccessintent = new Intent(LoginActivity.this, OneDevicePlayActivity.class); // 2인용바둑
                //Intent loginSuccessintent = new Intent(LoginActivity.this, OmokOneDevicePlayActivity.class); // 2인용오목
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameMenuActivity.this, OneDevicePlayActivity.class);
                GameMenuActivity.this.startActivity(intent);
            }
        });

        omokButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameMenuActivity.this, OmokOneDevicePlayActivity.class);
                GameMenuActivity.this.startActivity(intent);
            }
        });

    }
}