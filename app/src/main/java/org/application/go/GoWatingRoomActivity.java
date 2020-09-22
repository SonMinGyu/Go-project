package org.application.go;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.application.go.Model.GameModel;
import org.application.go.Model.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoWatingRoomActivity extends AppCompatActivity {

    FloatingActionButton createGameButton;
    RecyclerView gameListRecyclerView;
    String GWRUserLevel = GWRBringUserLevel();
    String GWRUserName = GWRBringUserName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_wating_room);

        gameListRecyclerView = (RecyclerView) findViewById(R.id.goWatingRoom_recyclerView);
        gameListRecyclerView.setAdapter(new GameListRecyclerViewAdapter());
        gameListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        createGameButton = (FloatingActionButton) findViewById(R.id.goWatingRoom_floationButton);

        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(GoWatingRoomActivity.this, CreateGame.class);
                GoWatingRoomActivity.this.startActivity(createIntent);
            }
        });
    }

    class GameListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<GameModel> gameModels = new ArrayList<>();
        private ArrayList<String> keyList = new ArrayList<>();
        private ArrayList<String> titleList = new ArrayList<>();
        private ArrayList<String> hostUidList = new ArrayList<>();
        private String uid;

        public GameListRecyclerViewAdapter() {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("Game").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    gameModels.clear();
                    keyList.clear();
                    titleList.clear();
                    hostUidList.clear();
                    for(DataSnapshot item :dataSnapshot.getChildren())
                    {
                        gameModels.add(item.getValue(GameModel.class));
                        keyList.add(item.getValue(GameModel.class).getGameKey());
                        titleList.add(item.getValue(GameModel.class).getGameTitle());
                        hostUidList.add(item.getValue(GameModel.class).getHostUid());
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

        @NonNull
        @Override // item_studyroom을 리사이클러뷰에 연결
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);

            CustomViewHolder holder = new CustomViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final CustomViewHolder customViewHolder = (CustomViewHolder) holder;

            customViewHolder.imageView.setImageResource(R.drawable.go_icon);
            customViewHolder.gameTitle.setText("방 이름: " + gameModels.get(position).getGameTitle());
            customViewHolder.gameType.setText("게임 유형: " + gameModels.get(position).getGameType());
            if(gameModels.get(position).getGameType().equals("Go")) {
                customViewHolder.hostLevel.setText("바둑 기력: " + gameModels.get(position).getHostLevel());
            }
            else
            {
                customViewHolder.hostLevel.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return (gameModels != null ? gameModels.size() : 1);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView gameTitle;
            public TextView gameType;
            public TextView hostLevel;

            public CustomViewHolder(View view) {
                super(view);
                imageView =(ImageView) view.findViewById(R.id.item_game_imageView);
                gameTitle = (TextView) view.findViewById(R.id.item_game_gameTitle);
                gameType = (TextView) view.findViewById(R.id.item_game_gameType);
                hostLevel = (TextView) view.findViewById(R.id.item_game_hostLevel);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            if(gameModels.get(position).getNumberOfUsers() == 2)
                            {
                                Toast.makeText(getApplicationContext(), "인원이 다 찬 게임입니다!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String gamekey = keyList.get(position);
                            String gameTitle = titleList.get(position);
                            String hostUid = hostUidList.get(position);

                            if(!gameModels.get(position).getHostUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            {
                                Map<String, Object> taskMap = new HashMap<String, Object>();
                                taskMap.put("numberOfUsers", 2);
                                taskMap.put("participantLevel", GWRUserLevel);
                                taskMap.put("participantUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                taskMap.put("participantName", GWRUserName);
                                FirebaseDatabase.getInstance().getReference().child("Game").child(gamekey).updateChildren(taskMap);
                            }

                            Intent gameIntent = new Intent(GoWatingRoomActivity.this, MainActivity.class);
                            gameIntent.putExtra("gamekey", gamekey);
                            gameIntent.putExtra("gameTitle", gameTitle);
                            gameIntent.putExtra("hostUid", hostUid);
                            GoWatingRoomActivity.this.startActivity(gameIntent);
                            //Toast.makeText(getContext(),"클릭",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }
    }

    public String GWRBringUserLevel()
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
                        GWRUserLevel = item.getValue(UserModel.class).getUserGoLevel();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return GWRUserLevel;
    }

    public String GWRBringUserName()
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
                        GWRUserName = item.getValue(UserModel.class).getUserName();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return GWRUserName;
    }
}