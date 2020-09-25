package org.application.go;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.application.go.Model.Go_point;

import java.util.ArrayList;

// 디바이스 하나에서 같이 하는 플레이
public class OneDevicePlayActivity extends AppCompatActivity {

    GridView gridView;
    MyAdapter adapter;
    int order = 1; // order가1 면 흑돌차례, 2면 백돌차례
    int image[] = new int[19*19];
    ArrayList<Go_point> go_points;
    EnemyStoneStack enemyStoneStack;
    DeleteStack deleteStack;
    DeleteStack deleteStack1;
    DeleteStack liveOrDie;
    int DeathFlag = 0; // 0은 아무것도아님, 1은 죽은돌, 2는 산돌, 연결되어 있는 돌 다 검사해서 활로가 없으면 liveOrDie 스택에 1주고 있으면 2줌
    boolean liveFlag = false; // liveOrDie 스택을 모두 검사해서 하나라도 2 즉 활로가 있어서 산 돌이있으면 true반환
    int deathwhiteStones = 0;
    int deathBlackStones = 0;
    boolean isStart = false;
    boolean isFinish = false;
    int numOfcount = 0;

    EnemyStoneStack canPutEnemyStoneStack;
    DeleteStack canPutDeleteStack;
    DeleteStack canPutLiveOrDie;
    DeleteStack booleanStack;
    int canPutDeathFlag = 0; // 0은 아무것도아님, 1은 죽은돌, 2는 산돌, 연결되어 있는 돌 다 검사해서 활로가 없으면 liveOrDie 스택에 1주고 있으면 2줌
    boolean canPutLiveFlag = false;

    TextView turnText;
    TextView deathWhiteCount;
    TextView deathBlackCount;
    Button startButton;

    String whiteCount;
    String blackCount;

    // 복기를 위한 선언
    Button finishButton;
    Button replayButton;
    Button nextButton;
    Button beforeButton;
    CheckBox autoReplay;
    GridView replayGridView;
    ReplayAdapter replayAdapter;
    int replayImage[] = new int[19*19];
    ArrayList<Go_point> replay_go_points;
    EnemyStoneStack replayEnemyStoneStack;
    DeleteStack replayDeleteStack;
    DeleteStack replayDeleteStack1;
    DeleteStack replayLiveOrDie;
    int replayDeathFlag = 0;
    boolean replayLiveFlag = false;
    int replayOrder = 0;
    int beforeOrder = 0;

    Queue goQueue;

    final Handler handler = new Handler();

    // 타이머를 위한 선언
    long blackTmepMillisecond;
    long whiteTmepMillisecond;
    TextView blacktimerText;
    TextView whitetimerText;
    boolean blackFirstTimerEnd = false;
    boolean whiteFirstTimerEnd = false;
    int blackTime;
    int whiteTime;
    TextView blackUserText;
    TextView whiteUserText;
    boolean isFirstTurn = true;
    BlackTimer blackTimer;
    BlackTimer blackTimer1;
    BlackTimer lastBlackTimer;
    WhiteTimer whiteTimer;
    WhiteTimer lastWhiteTimer;
    int blackTimerCount = 6;
    int whiteTimerCount = 6;
    TextView black_chanceText;
    TextView white_chanceText;

    //계가를 위한 선언
    DeleteStack emptyStoneStack;
    DeleteStack inspectionStack;
    double blackHouseCount = 0;
    double whiteHouseCount = 0;

    //Button button;
    String gameKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //button = (Button) findViewById(R.id.button);
        //gameKey = getIntent().getExtras().getString("gamekey");

        turnText = (TextView) findViewById(R.id.main_turn_textView);
        deathWhiteCount = (TextView) findViewById(R.id.main_black_score);
        deathBlackCount = (TextView) findViewById(R.id.main_white_score);
        startButton = (Button) findViewById(R.id.main_startButton);

        finishButton = (Button) findViewById(R.id.main_finishButton);
        replayButton = (Button) findViewById(R.id.main_replayButton);
        beforeButton = (Button) findViewById(R.id.main_beforeButton);
        nextButton = (Button) findViewById(R.id.main_nextButton);
        autoReplay = (CheckBox) findViewById(R.id.main_autoReplay_checkBox);

        blacktimerText = (TextView) findViewById(R.id.main_black_timer);
        whitetimerText = (TextView) findViewById(R.id.main_white_timer);
        blackUserText = (TextView) findViewById(R.id.main_black_user);
        whiteUserText = (TextView) findViewById(R.id.main_white_user);

        black_chanceText = (TextView) findViewById(R.id.main_black_chanceText);
        white_chanceText = (TextView) findViewById(R.id.main_white_chanceText);

        goQueue = new Queue(19*19);

        setIcon(image);

        go_points = new ArrayList<>();

        for(int i = 0; i < 19*19; i++)
        {
            Go_point go_point = new Go_point();
            go_point.setStone_position(i);
            go_points.add(go_point);
            sideSetting(go_points, i);
        }

        turnText.setText("대국 준비");

        whiteCount = "죽은 백돌: " + deathwhiteStones + " 개";
        blackCount = "죽은 흑돌: " + deathBlackStones + " 개";
        deathWhiteCount.setText(whiteCount);
        deathBlackCount.setText(blackCount);

        adapter = new MyAdapter(getApplicationContext(), R.layout.item_square, image);
        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(adapter);

        /*
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(order == 1)
                {
                    order = 2;
                }
                else
                {
                    order = 1;
                }
            }
        });

         */

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnText.setText("흑돌 턴 입니다");
                isStart = true;
                blackTime = calculateTime(blacktimerText.getText().toString());
                blackTimer = new BlackTimer(blackTime, 1000);
                blackTimer.start();
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replayButton.setVisibility(View.VISIBLE);
                isFinish = true;

                offTimer();

                emptyStoneStack = new DeleteStack(19*19);
                inspectionStack = new DeleteStack(19*19);

                //OutcomeDesicion outcomeDesicion = new OutcomeDesicion();
                emptySearch(go_points, emptyStoneStack);
                for(int i = emptyStoneStack.getTop() + 1; i > 0; i--) {
                    getEmptyStone(go_points, emptyStoneStack, inspectionStack);
                    //
                    countHouse(go_points, inspectionStack);
                    //
                }

                /*
                if(blackHouseCount == whiteHouseCount)
                {
                    Toast.makeText(getApplicationContext(), "비겼습니다!", Toast.LENGTH_SHORT).show();
                }
                else if(blackHouseCount > whiteHouseCount)
                {
                    Toast.makeText(getApplicationContext(), "흑이 이겼습니다!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "백이 이겼습니다!", Toast.LENGTH_SHORT).show();
                }

                 */


                if((blackHouseCount + deathwhiteStones) == (whiteHouseCount + deathBlackStones))
                {
                    double result = (blackHouseCount + deathwhiteStones);
                    //int numOfcount = blackNumOfCount + whiteNumOfCount;
                    final LinearLayout linearLayout = (LinearLayout) View.inflate(OneDevicePlayActivity.this, R.layout.activity_black_win, null);
                    TextView winText = (TextView) linearLayout.findViewById(R.id.black_win_defeatText);
                    winText.setText(Integer.toString(numOfcount) + "수 " +Double.toString(result) + "집으로 비겼습니다!");
                    final AlertDialog.Builder customDialog = new AlertDialog.Builder(OneDevicePlayActivity.this);
                    customDialog.setView(linearLayout)
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    final AlertDialog alertDialog = customDialog.create();
                    alertDialog.show();
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            //finish();
                        }
                    });
                }
                else if((blackHouseCount + deathwhiteStones) > (whiteHouseCount + deathBlackStones))
                {
                    double result = (blackHouseCount + deathwhiteStones) - (whiteHouseCount + deathBlackStones);
                    //int numOfcount = blackNumOfCount + whiteNumOfCount;
                    final LinearLayout linearLayout = (LinearLayout) View.inflate(OneDevicePlayActivity.this, R.layout.activity_black_win, null);
                    TextView winText = (TextView) linearLayout.findViewById(R.id.black_win_defeatText);
                    winText.setText(Integer.toString(numOfcount) + "수 " + "흑 " + Double.toString(result) + "집 승(勝)");
                    final AlertDialog.Builder customDialog = new AlertDialog.Builder(OneDevicePlayActivity.this);
                    customDialog.setView(linearLayout)
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    final AlertDialog alertDialog = customDialog.create();
                    alertDialog.show();
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            //finish();
                        }
                    });
                }
                else
                {
                    double result = (whiteHouseCount + deathBlackStones) - (blackHouseCount + deathwhiteStones);
                    //int numOfcount = blackNumOfCount + whiteNumOfCount;
                    final LinearLayout linearLayout = (LinearLayout) View.inflate(OneDevicePlayActivity.this, R.layout.activity_white_win, null);
                    TextView winText = (TextView) linearLayout.findViewById(R.id.white_win_defeatText);
                    winText.setText(Integer.toString(numOfcount) + "수 " + "백 " + Double.toString(result) + "집 승(勝)");
                    final AlertDialog.Builder customDialog = new AlertDialog.Builder(OneDevicePlayActivity.this);
                    customDialog.setView(linearLayout)
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    final AlertDialog alertDialog = customDialog.create();
                    alertDialog.show();
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            //finish();
                        }
                    });
                }
                System.out.println("mainmainmain " + Double.toString(blackHouseCount));
                System.out.println("mainmainmain " + Double.toString(whiteHouseCount));
            }
        });

        // 복기
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beforeButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
                autoReplay.setVisibility(View.VISIBLE);

                order = 1;
                deathBlackStones = 0;
                deathwhiteStones = 0;
                deathWhiteCount.setText(whiteCount);
                deathBlackCount.setText(blackCount);
                turnText.setText("복기 시작");
                setIcon(replayImage);
                replay_go_points = new ArrayList<>();

                for(int i = 0; i < 19*19; i++)
                {
                    Go_point replay_go_point = new Go_point();
                    replay_go_point.setStone_position(i);
                    replay_go_points.add(replay_go_point);
                    sideSetting(replay_go_points, i);
                }

                replayAdapter = new ReplayAdapter(getApplicationContext(), R.layout.item_square, replayImage);
                replayGridView = (GridView) findViewById(R.id.gridView);
                replayGridView.setAdapter(replayAdapter);
            }
        });

        autoReplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(autoReplay.isChecked()) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!goQueue.empty()) {
                                nextButton.callOnClick();
                                handler.postDelayed(this, 2000);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "복기가 모두 종료되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, 2000);
                }
                else
                {
                    handler.removeMessages(0);
                }
            }
        });
    }

    public int calculateTime(String time)
    {
        String[] array = time.split(":");
        int int_hour = Integer.parseInt(array[0]);
        int int_minute = Integer.parseInt(array[1]);
        int int_second = Integer.parseInt(array[2]);;

        int millisTime = ((int_hour * 60 *60) + (int_minute * 60) + int_second) * 1000;

        return millisTime;
    }

    public class BlackTimer extends CountDownTimer
    {
        public BlackTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            blackTmepMillisecond = millisUntilFinished;
            int millisMinute = (int)(millisUntilFinished/1000) % 3600;
            int millisSecond = (int) millisMinute % 60;
            blacktimerText.setText(Integer.toString((int) (millisUntilFinished/1000)/3600) + ":"
                    + Integer.toString(millisMinute/60) + ":" + Integer.toString(millisSecond));
        }

        @Override
        public void onFinish() {
            blackFirstTimerEnd = true;

            if(blackFirstTimerEnd)
            {
                blackTimerCount--;
                black_chanceText.setVisibility(View.VISIBLE);
                black_chanceText.setText("초읽기 " + Integer.toString(blackTimerCount) + "회");
            }

            if(blackTimerCount >= 1) {
                blacktimerText.setText("0:0:10");
                blackTime = calculateTime(blacktimerText.getText().toString());
                lastBlackTimer = new BlackTimer(blackTime, 1000);
                lastBlackTimer.start();
            }
            else
            {
                final LinearLayout linearLayout = (LinearLayout) View.inflate(OneDevicePlayActivity.this, R.layout.activity_black_timeout, null);
                final AlertDialog.Builder customDialog = new AlertDialog.Builder(OneDevicePlayActivity.this);
                customDialog.setView(linearLayout)
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                final AlertDialog alertDialog = customDialog.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        finish();
                    }
                });
            }
        }

    }

    public class WhiteTimer extends CountDownTimer
    {
        public WhiteTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            whiteTmepMillisecond = millisUntilFinished;
            int millisMinute = (int)(millisUntilFinished/1000) % 3600;
            int millisSecond = (int) millisMinute % 60;
            whitetimerText.setText(Integer.toString((int) (millisUntilFinished/1000)/3600) + ":"
                    + Integer.toString(millisMinute/60) + ":" + Integer.toString(millisSecond));
        }

        @Override
        public void onFinish() {
            whiteFirstTimerEnd = true;

            if(whiteFirstTimerEnd)
            {
                whiteTimerCount--;
                white_chanceText.setVisibility(View.VISIBLE);
                white_chanceText.setText("초읽기 " + Integer.toString(whiteTimerCount) + "회");
            }

            if(whiteTimerCount >= 1) {
                whitetimerText.setText("0:0:10");
                whiteTime = calculateTime(whitetimerText.getText().toString());
                lastWhiteTimer = new WhiteTimer(whiteTime, 1000);
                lastWhiteTimer.start();
            }
            else
            {
                final LinearLayout linearLayout = (LinearLayout) View.inflate(OneDevicePlayActivity.this, R.layout.activity_white_timeout, null);
                final AlertDialog.Builder customDialog = new AlertDialog.Builder(OneDevicePlayActivity.this);
                customDialog.setView(linearLayout)
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                final AlertDialog alertDialog = customDialog.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        finish();
                    }
                });
            }
        }

    }

    class MyAdapter extends BaseAdapter {
        Context context;
        int layout;
        int img[];
        LayoutInflater inf;

        public MyAdapter(Context context, int layout, int[] img) {
            this.context = context;
            this.layout = layout;
            this.img = img;
            inf = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return img.length;
        }

        @Override
        public Object getItem(int position) {
            return img[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView==null)
                convertView = inf.inflate(layout, null);

            final ImageView iv = (ImageView)convertView.findViewById(R.id.square_image);
            //final ImageView stone = (ImageView) convertView.findViewById(R.id.stone_image);

            iv.setImageResource(img[position]);

            iv.setOnClickListener(new View.OnClickListener() { // 돌을 놓으려고 클릭 할때 실행
                @Override
                public void onClick(View view) {

                    if(!isStart) // 대국시작을 누르지 않으면 클릭불가(시작안됨)
                    {
                        return;
                    }

                    if(isFinish) // 계가하기를 누르면 클릭불가(종료 됨)
                    {
                        return;
                    }

                    deleteStack = new DeleteStack(19*19);
                    deleteStack1 = new DeleteStack(19*19);
                    liveOrDie = new DeleteStack(19*19);
                    enemyStoneStack = new EnemyStoneStack(4);

                    canPutDeleteStack = new DeleteStack(19*19);
                    canPutLiveOrDie = new DeleteStack(19*19);
                    canPutEnemyStoneStack = new EnemyStoneStack(4);
                    booleanStack = new DeleteStack(4);

                    if(go_points.get(position).isExistence_stone()) // 돌이 있는 곳에 돌을 놓을 경우
                    {
                        Toast.makeText(getApplicationContext(),"돌이 있는 곳에는 놓을 수 없습니다!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!canPutTheStone(go_points, position)) // 착수금지 체크를 위한 부분
                    {
                        if(order == 1) // 실제로 돌을 놓는 것은 아니지만 검사를 위해 돌을 놓았다고 가정, 밑에서 다시 돌을 뺄거임
                        {
                            go_points.get(position).setExistence_stone(true);
                            go_points.get(position).setStone_color(order);
                            go_points.get(position).setStone_position(position);
                            surroundingSearch(go_points, position);
                        }
                        else if(order == 2)
                        {
                            go_points.get(position).setExistence_stone(true);
                            go_points.get(position).setStone_color(order);
                            go_points.get(position).setStone_position(position);
                            surroundingSearch(go_points, position);
                        }

                        for(int i = 0; i < 19*19; i++) // 돌을 놓은 후 착수 금지 판단을 위해 돌들의 주변 상황을 최신 결과로 업데이트
                        {
                            surroundingSearch(go_points, i);
                        }

                        // 착수금지 원리
                        // 위에서 터치한 곳에 놓으려는 돌의 정보를 넣고 그 돌로 인해 주변 상대 돌이 죽는게 있나 검사
                        // 죽는게 있으면 착수 금지가 아닌것이고 죽는 돌이 없으면 착수금지로 판단

                        canPutEnemySearch(go_points, position, order);
                        // 착점 주변에 상대돌 있나 찾는 함수, canPutEnemyStoneStack에 push한다(착수금지에서는 같은 편 돌도 포함)
                        if(!canPutEnemyStoneStack.empty()) {
                            for (int j = canPutEnemyStoneStack.getTop() + 1; j > 0; j--) {
                                canPutGetStone(go_points);
                                // canPutGetStone에서 canPutEnemyStoneStack에 push된 돌을 하나씩 pop하고
                                // 그 돌과 연결된 돌이 있다면 그 돌들을 모두 canPutDeleteStack에 push

                                if(!canPutDeleteStack.empty()) {
                                    for (int i = canPutDeleteStack.getTop() + 1; i > 0; i--) {
                                        int deletePositon = canPutDeleteStack.pop();
                                        // canPutDeleteStack에 push된 돌들을 하나씩 pop하여 그 돌중에 활로
                                        // 즉 상하좌우중 빈 공간이 있나 검사, 연결된 돌들 모두 상하좌우에 빈공간이 없으면 죽은돌로 판정

                                        if(go_points.get(deletePositon).getUpStoneSame() == 0
                                                || go_points.get(deletePositon).getLeftStoneSame() == 0
                                                || go_points.get(deletePositon).getDownStoneSame() == 0
                                                || go_points.get(deletePositon).getRightStoneSame() == 0)
                                        {
                                            canPutDeathFlag = 2; // 상하좌우 중 하나라도 활로(빈공간)이 있다면 canPutDeathFlag를 2로 반환
                                        }
                                        else
                                        {
                                            canPutDeathFlag = 1;// 상하좌우 중 활로(빈공간)가 하나도 없다면 canPutDeathFlag를 1로 반환
                                        }

                                        canPutLiveOrDie.push(canPutDeathFlag); // 반환한 canPutDeathFlag를 canPutLiveOrDie에 push
                                    }
                                }

                                // canPutLiveOrDie에 push된 값을 pop하여 검사,
                                // pop된 값이 2이면 연결된 돌들 중 활로가 있다는것, 따라서 canPutLiveFlag를 true로 반환
                                for(int i = canPutLiveOrDie.getTop() + 1; i > 0; i--)
                                {
                                    int canPutLiveOrDiePop = canPutLiveOrDie.pop();
                                    if(canPutLiveOrDiePop == 2)
                                    {
                                        canPutLiveFlag = true;
                                    }
                                }

                                // canPutLiveFlag가 true면 booleanstack에 1을 push
                                if(canPutLiveFlag)
                                {
                                    booleanStack.push(1);
                                }
                                else
                                {
                                    booleanStack.push(2);
                                }

                                canPutLiveFlag = false;
                                canPutDeathFlag = 0;
                            }

                        }

                        // booleanStack을 pop하여 검사, push된 값중 2가 있으면 놓으려는 곳 주변에 놓으려는 돌로 인하여 죽는 돌이 발생한다는 뜻
                        // 따라서 booleanStack에 2가 있다면 cantPut을 0으로 바꿔준다
                        // 하지만 booleanStack에 2가 없고 1만 있다면 놓으려는 돌로 인해서 죽는 돌이 없다는 뜻이므로 착수금지한 위치이다
                        int cantPut = 1;
                        for(int i = booleanStack.getTop() + 1; i > 0; i--)
                        {
                            if(booleanStack.pop() == 2)
                            {
                                cantPut = 0;
                            }
                        }

                        if(order == 1) // 실제로 돌을 놓는 것이 아니므로 다시 돌이 없는 설정으로 바꿔준다
                        {
                            go_points.get(position).setExistence_stone(false);
                            go_points.get(position).setStone_color(0);
                            go_points.get(position).setStone_position(-1);
                        }
                        else if(order == 2)
                        {
                            go_points.get(position).setExistence_stone(false);
                            go_points.get(position).setStone_color(0);
                            go_points.get(position).setStone_position(-1);
                        }

                        for(int i = 0; i < 19*19; i++) // 연결된 돌들을 검사하는 도중 바뀐 것들을 모두 초기화
                        {
                            go_points.get(i).setCanPutFirstSearch(true);
                            go_points.get(i).setUpStoneSame(0);
                            go_points.get(i).setLeftStoneSame(0);
                            go_points.get(i).setDownStoneSame(0);
                            go_points.get(i).setRightStoneSame(0);
                        }

                        // 위에서 모든 위치의 설정을 초기화 했으므로 실제 돌이 남아있는 부분은 다시 설정해준다
                        for(int i = 0; i < 19*19; i++)
                        {
                            surroundingSearch(go_points, i); // position i의 위치에 돌이 있다면 상하좌우를 검사하여 정보를 다시 설정
                            sideSetting(go_points, i); // 맨위와 맨 왼쪽, 맨 아래, 맨 오른쪽의 정보를 설정(벽이 있으므로)
                        }

                        if(cantPut == 1) // 착수금지 시키고 return하여 종료
                        {
                            Toast.makeText(getApplicationContext(),"선택하신 위치는 착수금지인 곳입니다!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    // 착수금지가 아니면 실제로 돌을 놓음
                    if(order == 1)
                    {
                        go_points.get(position).setExistence_stone(true);
                        go_points.get(position).setStone_color(order);
                        go_points.get(position).setStone_position(position);
                        surroundingSearch(go_points, position);
                        setStone(image, position);
                        iv.setImageResource(image[position]);
                        order = 2;
                        goQueue.insert(position);
                    }
                    else if(order == 2)
                    {
                        go_points.get(position).setExistence_stone(true);
                        go_points.get(position).setStone_color(order);
                        go_points.get(position).setStone_position(position);
                        surroundingSearch(go_points, position);
                        setStone(image, position);
                        iv.setImageResource(image[position]);
                        order = 1;
                        goQueue.insert(position);
                    }

                    for(int i = 0; i < 19*19; i++) // 돌을 놓은 후 돌들의 주변 상황을 최신 결과로 업데이트
                    {
                        surroundingSearch(go_points, i);
                    }

                    enemySearch(go_points, position, order, enemyStoneStack); // 착점 주변에 상대돌 있나 찾는 함수
                    if(!enemyStoneStack.empty())
                    {
                        for (int j = enemyStoneStack.getTop() + 1; j > 0; j--)
                        {
                            // 상대돌이 있으면 getStone함수에서 그 돌을 pop해서 연결된 돌들을 모두 deleteStack에 push한다
                            getStone(go_points, enemyStoneStack, deleteStack);

                            if(!deleteStack.empty()) {
                                for (int i = deleteStack.getTop() + 1; i > 0; i--) {
                                    int deletePositon = deleteStack.pop();
                                    // deleteStack에 push된, 연결되어 있는 돌들을 pop해서 모두 검사
                                    // 마찬가지로 활로가 있으면 산돌, 활로가 없으면 죽은돌로 판정

                                    if(go_points.get(deletePositon).getUpStoneSame() == 0
                                            || go_points.get(deletePositon).getLeftStoneSame() == 0
                                            || go_points.get(deletePositon).getDownStoneSame() == 0
                                            || go_points.get(deletePositon).getRightStoneSame() == 0)
                                    {
                                        DeathFlag = 2; // 활로가 있는 경우 DeathFlag를 2로 반환함
                                    }
                                    else
                                    {
                                        DeathFlag = 1; // 활로가 없는 경우 DeathFlag를 1로 반환
                                    }

                                    liveOrDie.push(DeathFlag);
                                    deleteStack1.push(deletePositon);
                                }
                            }

                            // liveOrDie에 push된 결과를 pop하여 모든 돌들이 활로가 없으면(즉 DeathFlag가 모두 1이면)
                            // liveFlag를 false로 반환하여 삭제하는 과정을 진행
                            for(int i = liveOrDie.getTop() + 1; i > 0; i--)
                            {
                                int liveOrDiePop = liveOrDie.pop();
                                if(liveOrDiePop == 2)
                                {
                                    liveFlag = true;
                                }
                            }

                            if(!liveFlag) // 죽은돌들을 삭제하는 과정
                            {
                                for(int i = deleteStack1.getTop() + 1; i > 0; i--)
                                {
                                    int popPosition = deleteStack1.pop();
                                    go_points.get(popPosition).setExistence_stone(false);
                                    go_points.get(popPosition).setStone_position(popPosition);
                                    go_points.get(popPosition).setStone_color(0);
                                    go_points.get(popPosition).setFirstSearch(true);
                                    go_points.get(popPosition).setUpStoneSame(0);
                                    go_points.get(popPosition).setLeftStoneSame(0);
                                    go_points.get(popPosition).setDownStoneSame(0);
                                    go_points.get(popPosition).setRightStoneSame(0);

                                    if(order == 1)
                                    {
                                        deathBlackStones++;
                                    }
                                    else
                                    {
                                        deathwhiteStones++;
                                    }

                                    resetIcon(image, popPosition);
                                    gridView.setAdapter(adapter); // 돌이 놓이면 그림을 다시 그려서 업데이트
                                }
                            }

                            for(int i = 0; i < 19*19; i++) // 죽은 돌을 지운후 모든 돌의 검사결과 0으로 초기화
                            {
                                go_points.get(i).setFirstSearch(true);
                                go_points.get(i).setUpStoneSame(0);
                                go_points.get(i).setLeftStoneSame(0);
                                go_points.get(i).setDownStoneSame(0);
                                go_points.get(i).setRightStoneSame(0);
                            }

                            for(int i = 0; i < 19*19; i++) // 돌 주변 검사결과를 초기화 한다음 남아있는 돌들의 주변을 검사하여 다시 넣어주기
                            {
                                surroundingSearch(go_points, i);
                                sideSetting(go_points, i);
                            }

                            DeathFlag = 0;
                            liveFlag = false;

                            if(!deleteStack1.empty())
                            {
                                for(int i = deleteStack1.getTop() + 1; i > 0; i--)
                                {
                                    deleteStack1.pop();
                                }
                            }
                        }
                    }

                    if(order == 1) {
                        turnText.setText("흑돌 턴 입니다");
                    }
                    else
                    {
                        turnText.setText("백돌 턴 입니다");
                    }

                    // 죽은 돌들을 카운트 해줌
                    String whiteCount = "죽은 백돌: " + deathwhiteStones + " 개";
                    String blackCount = "죽은 흑돌: " + deathBlackStones + " 개";
                    deathWhiteCount.setText(whiteCount);
                    deathBlackCount.setText(blackCount);

                    numOfcount++;

                    // 4가지 경우 생각해서 해야함
                    if(order == 2 && isStart && !whiteFirstTimerEnd && !blackFirstTimerEnd)
                    {
                        offTimer();

                        int millisMinute = (int)(blackTmepMillisecond/1000) % 3600;
                        int millisSecond = (int) millisMinute % 60;
                        blacktimerText.setText(Integer.toString((int) (blackTmepMillisecond/1000)/3600) + ":"
                                + Integer.toString(millisMinute/60) + ":" + Integer.toString(millisSecond));

                        whiteTime = calculateTime(whitetimerText.getText().toString());
                        whiteTimer = new WhiteTimer(whiteTime, 1000);
                        whiteTimer.start();
                    }
                    else if(order == 1 && isStart && !whiteFirstTimerEnd && !blackFirstTimerEnd)
                    {
                        offTimer();

                        int millisMinute = (int)(whiteTmepMillisecond/1000) % 3600;
                        int millisSecond = (int) millisMinute % 60;
                        whitetimerText.setText(Integer.toString((int) (whiteTmepMillisecond/1000)/3600) + ":"
                                + Integer.toString(millisMinute/60) + ":" + Integer.toString(millisSecond));

                        blackTime = calculateTime(blacktimerText.getText().toString());
                        blackTimer1 = new BlackTimer(blackTime, 1000);
                        blackTimer1.start();
                        isFirstTurn = false;
                    }

                    if(order == 2 && isStart && whiteFirstTimerEnd && !blackFirstTimerEnd)
                    {
                        offTimer();

                        int millisMinute = (int)(blackTmepMillisecond/1000) % 3600;
                        int millisSecond = (int) millisMinute % 60;
                        blacktimerText.setText(Integer.toString((int) (blackTmepMillisecond/1000)/3600) + ":"
                                + Integer.toString(millisMinute/60) + ":" + Integer.toString(millisSecond));

                        whiteTime = calculateTime(whitetimerText.getText().toString());
                        lastWhiteTimer = new WhiteTimer(whiteTime, 1000);
                        lastWhiteTimer.start();
                    }
                    else if(order == 1 && isStart && whiteFirstTimerEnd && !blackFirstTimerEnd)
                    {
                        offTimer();

                        whitetimerText.setText("0:0:10");

                        blackTime = calculateTime(blacktimerText.getText().toString());
                        blackTimer1 = new BlackTimer(blackTime, 1000);
                        blackTimer1.start();
                    }

                    if(order == 2 && isStart && !whiteFirstTimerEnd && blackFirstTimerEnd)
                    {
                        offTimer();

                        blacktimerText.setText("0:0:10");

                        whiteTime = calculateTime(whitetimerText.getText().toString());
                        whiteTimer = new WhiteTimer(whiteTime, 1000);
                        whiteTimer.start();
                    }
                    else if(order == 1 && isStart && !whiteFirstTimerEnd && blackFirstTimerEnd)
                    {
                        offTimer();

                        whiteTimer.cancel();

                        int millisMinute = (int)(whiteTmepMillisecond/1000) % 3600;
                        int millisSecond = (int) millisMinute % 60;
                        whitetimerText.setText(Integer.toString((int) (whiteTmepMillisecond/1000)/3600) + ":"
                                + Integer.toString(millisMinute/60) + ":" + Integer.toString(millisSecond));

                        blackTime = calculateTime(blacktimerText.getText().toString());
                        lastBlackTimer = new BlackTimer(blackTime, 1000);
                        lastBlackTimer.start();
                    }

                    if(order == 2 && isStart && whiteFirstTimerEnd && blackFirstTimerEnd)
                    {
                        offTimer();

                        blacktimerText.setText("0:0:10");

                        whiteTime = calculateTime(whitetimerText.getText().toString());
                        lastWhiteTimer = new WhiteTimer(whiteTime, 1000);
                        lastWhiteTimer.start();
                    }
                    else if(order == 1 && isStart && whiteFirstTimerEnd && blackFirstTimerEnd)
                    {
                        offTimer();

                        whitetimerText.setText("0:0:10");

                        blackTime = calculateTime(blacktimerText.getText().toString());
                        lastBlackTimer = new BlackTimer(blackTime, 1000);
                        lastBlackTimer.start();
                    }
                }
            });

            return convertView;
        }
    }

    class ReplayAdapter extends BaseAdapter {
        Context context;
        int layout;
        int img[];
        LayoutInflater inf;

        public ReplayAdapter(Context context, int layout, int[] img) {
            this.context = context;
            this.layout = layout;
            this.img = img;
            inf = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return img.length;
        }

        @Override
        public Object getItem(int position) {
            return img[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView==null)
                convertView = inf.inflate(layout, null);

            final ImageView iv = (ImageView)convertView.findViewById(R.id.square_image);
            //final ImageView stone = (ImageView) convertView.findViewById(R.id.stone_image);

            iv.setImageResource(img[position]);

            beforeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    replayDeleteStack = new DeleteStack(19*19);
                    replayDeleteStack1 = new DeleteStack(19*19);
                    replayLiveOrDie = new DeleteStack(19*19);
                    replayEnemyStoneStack = new EnemyStoneStack(4);

                    if(beforeOrder == 0)
                    {
                        Toast.makeText(getApplicationContext(), "이전의 수가 없습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(beforeOrder - 1 == 0)
                    {
                        Toast.makeText(getApplicationContext(), "첫 수 입니다", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    order = 1;
                    deathBlackStones = 0;
                    deathwhiteStones = 0;
                    setIcon(replayImage);

                    for(int i = 0; i < 19*19; i++)
                    {
                        sideSetting(replay_go_points, i);
                        replay_go_points.get(i).setExistence_stone(false);
                        replay_go_points.get(i).setStone_position(i);
                        replay_go_points.get(i).setStone_color(0);
                        replay_go_points.get(i).setFirstSearch(true);
                        replay_go_points.get(i).setUpStoneSame(0);
                        replay_go_points.get(i).setLeftStoneSame(0);
                        replay_go_points.get(i).setDownStoneSame(0);
                        replay_go_points.get(i).setRightStoneSame(0);
                    }
                    //replayGridView.setAdapter(replayAdapter);

                    goQueue.setFront(0);
                    replayOrder--;
                    for(int q = 0; q < beforeOrder - 1; q++) {
                        if (!goQueue.empty()) {
                            int replayPosition = goQueue.remove();

                            if (order == 1) {
                                replay_go_points.get(replayPosition).setExistence_stone(true);
                                replay_go_points.get(replayPosition).setStone_color(order);
                                replay_go_points.get(replayPosition).setStone_position(replayPosition);
                                surroundingSearch(replay_go_points, replayPosition);
                                setStone(replayImage, replayPosition);
                                //iv.setImageResource(replayImage[replayPosition]);
                                replayGridView.setAdapter(replayAdapter);
                                order = 2;
                            } else if (order == 2) {
                                replay_go_points.get(replayPosition).setExistence_stone(true);
                                replay_go_points.get(replayPosition).setStone_color(order);
                                replay_go_points.get(replayPosition).setStone_position(replayPosition);
                                surroundingSearch(replay_go_points, replayPosition);
                                setStone(replayImage, replayPosition);
                                //iv.setImageResource(replayImage[replayPosition]);
                                replayGridView.setAdapter(replayAdapter);
                                order = 1;
                            }

                            for (int i = 0; i < 19 * 19; i++) // 돌을 놓은 후 돌들의 주변 상황을 최신 결과로 업데이트
                            {
                                surroundingSearch(replay_go_points, i);
                            }

                            enemySearch(replay_go_points, replayPosition, order, replayEnemyStoneStack); // 착점 주변에 상대돌 있나 찾는 함수
                            if (!replayEnemyStoneStack.empty()) {
                                for (int j = replayEnemyStoneStack.getTop() + 1; j > 0; j--) {
                                    // 상대돌이 있으면 getStone함수에서 그 돌을 pop해서 연결된 돌들을 모두 deleteStack에 push한다
                                    getStone(replay_go_points, replayEnemyStoneStack, replayDeleteStack);

                                    if (!replayDeleteStack.empty()) {
                                        for (int i = replayDeleteStack.getTop() + 1; i > 0; i--) {
                                            int deletePositon = replayDeleteStack.pop();
                                            // deleteStack에 push된, 연결되어 있는 돌들을 pop해서 모두 검사
                                            // 마찬가지로 활로가 있으면 산돌, 활로가 없으면 죽은돌로 판정

                                            if (replay_go_points.get(deletePositon).getUpStoneSame() == 0
                                                    || replay_go_points.get(deletePositon).getLeftStoneSame() == 0
                                                    || replay_go_points.get(deletePositon).getDownStoneSame() == 0
                                                    || replay_go_points.get(deletePositon).getRightStoneSame() == 0) {
                                                replayDeathFlag = 2; // 활로가 있는 경우 DeathFlag를 2로 반환함
                                            } else {
                                                replayDeathFlag = 1; // 활로가 없는 경우 DeathFlag를 1로 반환
                                            }

                                            replayLiveOrDie.push(replayDeathFlag);
                                            replayDeleteStack1.push(deletePositon);
                                        }
                                    }

                                    // liveOrDie에 push된 결과를 pop하여 모든 돌들이 활로가 없으면(즉 DeathFlag가 모두 1이면)
                                    // liveFlag를 false로 반환하여 삭제하는 과정을 진행
                                    for (int i = replayLiveOrDie.getTop() + 1; i > 0; i--) {
                                        int replayLiveOrDiePop = replayLiveOrDie.pop();
                                        if (replayLiveOrDiePop == 2) {
                                            replayLiveFlag = true;
                                        }
                                    }

                                    if (!replayLiveFlag) // 죽은돌들을 삭제하는 과정
                                    {
                                        for (int i = replayDeleteStack1.getTop() + 1; i > 0; i--) {
                                            int replayPopPosition = replayDeleteStack1.pop();
                                            replay_go_points.get(replayPopPosition).setExistence_stone(false);
                                            replay_go_points.get(replayPopPosition).setStone_position(replayPopPosition);
                                            replay_go_points.get(replayPopPosition).setStone_color(0);
                                            replay_go_points.get(replayPopPosition).setFirstSearch(true);
                                            replay_go_points.get(replayPopPosition).setUpStoneSame(0);
                                            replay_go_points.get(replayPopPosition).setLeftStoneSame(0);
                                            replay_go_points.get(replayPopPosition).setDownStoneSame(0);
                                            replay_go_points.get(replayPopPosition).setRightStoneSame(0);

                                            if (order == 1) {
                                                deathBlackStones++;
                                            } else {
                                                deathwhiteStones++;
                                            }

                                            resetIcon(replayImage, replayPopPosition);
                                            replayGridView.setAdapter(replayAdapter); // 돌이 놓이면 그림을 다시 그려서 업데이트
                                        }
                                    }

                                    for (int i = 0; i < 19 * 19; i++) // 죽은 돌을 지운후 모든 돌의 검사결과 0으로 초기화
                                    {
                                        replay_go_points.get(i).setFirstSearch(true);
                                        replay_go_points.get(i).setUpStoneSame(0);
                                        replay_go_points.get(i).setLeftStoneSame(0);
                                        replay_go_points.get(i).setDownStoneSame(0);
                                        replay_go_points.get(i).setRightStoneSame(0);
                                    }

                                    for (int i = 0; i < 19 * 19; i++) // 돌 주변 검사결과를 초기화 한다음 남아있는 돌들의 주변을 검사하여 다시 넣어주기
                                    {
                                        surroundingSearch(replay_go_points, i);
                                        sideSetting(replay_go_points, i);
                                    }

                                    replayDeathFlag = 0;
                                    replayLiveFlag = false;

                                    if (!replayDeleteStack1.empty()) {
                                        for (int i = replayDeleteStack1.getTop() + 1; i > 0; i--) {
                                            replayDeleteStack1.pop();
                                        }
                                    }
                                }
                            }

                            String replayText = replayOrder + " 수";
                            turnText.setText(replayText);


                            // 죽은 돌들을 카운트 해줌
                            String whiteCount = "죽은 백돌: " + deathwhiteStones + " 개";
                            String blackCount = "죽은 흑돌: " + deathBlackStones + " 개";
                            deathWhiteCount.setText(whiteCount);
                            deathBlackCount.setText(blackCount);
                        } else {
                            Toast.makeText(getApplicationContext(), "복기가 모두 종료되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    beforeOrder--;
                }
            });

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    replayDeleteStack = new DeleteStack(19*19);
                    replayDeleteStack1 = new DeleteStack(19*19);
                    replayLiveOrDie = new DeleteStack(19*19);
                    replayEnemyStoneStack = new EnemyStoneStack(4);

                    if(!goQueue.empty()) {
                        int replayPosition = goQueue.remove();
                        replayOrder++;
                        beforeOrder++;

                        if (order == 1) {
                            replay_go_points.get(replayPosition).setExistence_stone(true);
                            replay_go_points.get(replayPosition).setStone_color(order);
                            replay_go_points.get(replayPosition).setStone_position(replayPosition);
                            surroundingSearch(replay_go_points, replayPosition);
                            setStone(replayImage, replayPosition);
                            //iv.setImageResource(replayImage[replayPosition]);
                            replayGridView.setAdapter(replayAdapter);
                            order = 2;
                        } else if (order == 2) {
                            replay_go_points.get(replayPosition).setExistence_stone(true);
                            replay_go_points.get(replayPosition).setStone_color(order);
                            replay_go_points.get(replayPosition).setStone_position(replayPosition);
                            surroundingSearch(replay_go_points, replayPosition);
                            setStone(replayImage, replayPosition);
                            //iv.setImageResource(replayImage[replayPosition]);
                            replayGridView.setAdapter(replayAdapter);
                            order = 1;
                        }

                        for (int i = 0; i < 19 * 19; i++) // 돌을 놓은 후 돌들의 주변 상황을 최신 결과로 업데이트
                        {
                            surroundingSearch(replay_go_points, i);
                        }

                        enemySearch(replay_go_points, replayPosition, order, replayEnemyStoneStack); // 착점 주변에 상대돌 있나 찾는 함수
                        if (!replayEnemyStoneStack.empty()) {
                            for (int j = replayEnemyStoneStack.getTop() + 1; j > 0; j--) {
                                // 상대돌이 있으면 getStone함수에서 그 돌을 pop해서 연결된 돌들을 모두 deleteStack에 push한다
                                getStone(replay_go_points, replayEnemyStoneStack, replayDeleteStack);

                                if (!replayDeleteStack.empty()) {
                                    for (int i = replayDeleteStack.getTop() + 1; i > 0; i--) {
                                        int deletePositon = replayDeleteStack.pop();
                                        // deleteStack에 push된, 연결되어 있는 돌들을 pop해서 모두 검사
                                        // 마찬가지로 활로가 있으면 산돌, 활로가 없으면 죽은돌로 판정

                                        if (replay_go_points.get(deletePositon).getUpStoneSame() == 0
                                                || replay_go_points.get(deletePositon).getLeftStoneSame() == 0
                                                || replay_go_points.get(deletePositon).getDownStoneSame() == 0
                                                || replay_go_points.get(deletePositon).getRightStoneSame() == 0) {
                                            replayDeathFlag = 2; // 활로가 있는 경우 DeathFlag를 2로 반환함
                                        } else {
                                            replayDeathFlag = 1; // 활로가 없는 경우 DeathFlag를 1로 반환
                                        }

                                        replayLiveOrDie.push(replayDeathFlag);
                                        replayDeleteStack1.push(deletePositon);
                                    }
                                }

                                // liveOrDie에 push된 결과를 pop하여 모든 돌들이 활로가 없으면(즉 DeathFlag가 모두 1이면)
                                // liveFlag를 false로 반환하여 삭제하는 과정을 진행
                                for (int i = replayLiveOrDie.getTop() + 1; i > 0; i--) {
                                    int replayLiveOrDiePop = replayLiveOrDie.pop();
                                    if (replayLiveOrDiePop == 2) {
                                        replayLiveFlag = true;
                                    }
                                }

                                if (!replayLiveFlag) // 죽은돌들을 삭제하는 과정
                                {
                                    for (int i = replayDeleteStack1.getTop() + 1; i > 0; i--) {
                                        int replayPopPosition = replayDeleteStack1.pop();
                                        replay_go_points.get(replayPopPosition).setExistence_stone(false);
                                        replay_go_points.get(replayPopPosition).setStone_position(replayPopPosition);
                                        replay_go_points.get(replayPopPosition).setStone_color(0);
                                        replay_go_points.get(replayPopPosition).setFirstSearch(true);
                                        replay_go_points.get(replayPopPosition).setUpStoneSame(0);
                                        replay_go_points.get(replayPopPosition).setLeftStoneSame(0);
                                        replay_go_points.get(replayPopPosition).setDownStoneSame(0);
                                        replay_go_points.get(replayPopPosition).setRightStoneSame(0);

                                        if (order == 1) {
                                            deathBlackStones++;
                                        } else {
                                            deathwhiteStones++;
                                        }

                                        resetIcon(replayImage, replayPopPosition);
                                        replayGridView.setAdapter(replayAdapter); // 돌이 놓이면 그림을 다시 그려서 업데이트
                                    }
                                }

                                for (int i = 0; i < 19 * 19; i++) // 죽은 돌을 지운후 모든 돌의 검사결과 0으로 초기화
                                {
                                    replay_go_points.get(i).setFirstSearch(true);
                                    replay_go_points.get(i).setUpStoneSame(0);
                                    replay_go_points.get(i).setLeftStoneSame(0);
                                    replay_go_points.get(i).setDownStoneSame(0);
                                    replay_go_points.get(i).setRightStoneSame(0);
                                }

                                for (int i = 0; i < 19 * 19; i++) // 돌 주변 검사결과를 초기화 한다음 남아있는 돌들의 주변을 검사하여 다시 넣어주기
                                {
                                    surroundingSearch(replay_go_points, i);
                                    sideSetting(replay_go_points, i);
                                }

                                replayDeathFlag = 0;
                                replayLiveFlag = false;

                                if (!replayDeleteStack1.empty()) {
                                    for (int i = replayDeleteStack1.getTop() + 1; i > 0; i--) {
                                        replayDeleteStack1.pop();
                                    }
                                }
                            }
                        }

                        String replayText = replayOrder + " 수";
                        turnText.setText(replayText);


                        // 죽은 돌들을 카운트 해줌
                        String whiteCount = "죽은 백돌: " + deathwhiteStones + " 개";
                        String blackCount = "죽은 흑돌: " + deathBlackStones + " 개";
                        deathWhiteCount.setText(whiteCount);
                        deathBlackCount.setText(blackCount);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "복기가 모두 종료되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return convertView;
        }
    }




    // 함수이름 앞에 canPut이 들어가면 착수금지를 위해 만든 함수이다.
    // 착수금지 함수를 따로 만든 이유는 스택이 겹쳐서 오류가 나는것을 방지하기 위함이다
    // 안그래도 코드 길이가 매우 긴데 더 길어졌다. 나중에 최적화 할 수 있는 방법이 생각나면 고쳐보자.
    // canPutTheStone함수는 돌을 놓을 곳 상하좌우에 모두 돌이 있는지 판단하는 함수,
    // 상하좌우에 모두 돌이 있으면 착수금지일 가능성이 있으므로 true를 반환하여 판단에 들어간다.
    public boolean canPutTheStone(ArrayList<Go_point> go_points, int myPosition)
    {
        int color = 0;
        if(order == 1) {
            color = 2;
        }
        else
        {
            color = 1;
        }

        if(myPosition == 0) // left_top
        {
            if(go_points.get(myPosition + 1).isExistence_stone()
                    && go_points.get(myPosition + 19).isExistence_stone())
            {
                if(go_points.get(myPosition + 1).getStone_color() != order
                        && go_points.get(myPosition + 19).getStone_color() != order)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
        }
        else if(myPosition == 18) // right_top
        {
            if(go_points.get(myPosition - 1).isExistence_stone()
                    && go_points.get(myPosition + 19).isExistence_stone())
            {
                if(go_points.get(myPosition - 1).getStone_color() != order
                        && go_points.get(myPosition + 19).getStone_color() != order)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
        }
        else if(myPosition == 342) // left_bottom
        {
            if(go_points.get(myPosition - 19).isExistence_stone()
                    && go_points.get(myPosition + 1).isExistence_stone())
            {
                if(go_points.get(myPosition - 19).getStone_color() != order
                        && go_points.get(myPosition + 1).getStone_color() != order)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
        }
        else if(myPosition == 360) // right_bottom
        {
            if(go_points.get(myPosition - 1).isExistence_stone()
                    && go_points.get(myPosition - 19).isExistence_stone())
            {
                if(go_points.get(myPosition - 1).getStone_color() != order
                        && go_points.get(myPosition - 19).getStone_color() != order)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
        }
        else if (myPosition % 19 == 0 && myPosition != 0 && myPosition != 342) // left
        {
            if(go_points.get(myPosition - 19).isExistence_stone()
                    && go_points.get(myPosition + 1).isExistence_stone()
                    && go_points.get(myPosition + 19).isExistence_stone())
            {
                if(go_points.get(myPosition - 19).getStone_color() != order
                        && go_points.get(myPosition + 1).getStone_color() != order
                        && go_points.get(myPosition + 19).getStone_color() != order)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
        }
        else if (myPosition >= 1 && myPosition <= 17) // top
        {
            if(go_points.get(myPosition - 1).isExistence_stone()
                    && go_points.get(myPosition + 19).isExistence_stone()
                    && go_points.get(myPosition + 1).isExistence_stone())
            {
                if(go_points.get(myPosition - 1).getStone_color() != order
                        && go_points.get(myPosition + 19).getStone_color() != order
                        && go_points.get(myPosition + 1).getStone_color() != order)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
        }
        else if (myPosition % 19 == 18 && myPosition != 18 && myPosition != 360) // right
        {
            if(go_points.get(myPosition - 19).isExistence_stone()
                    && go_points.get(myPosition - 1).isExistence_stone()
                    && go_points.get(myPosition + 19).isExistence_stone())
            {
                if(go_points.get(myPosition - 19).getStone_color() != order
                        && go_points.get(myPosition - 1).getStone_color() != order
                        && go_points.get(myPosition + 19).getStone_color() != order)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
        }
        else if (myPosition >= 343 && myPosition <= 359) // bottom
        {
            if(go_points.get(myPosition - 1).isExistence_stone()
                    && go_points.get(myPosition - 19).isExistence_stone()
                    && go_points.get(myPosition + 1).isExistence_stone())
            {
                if(go_points.get(myPosition - 1).getStone_color() != order
                        && go_points.get(myPosition - 19).getStone_color() != order
                        && go_points.get(myPosition + 1).getStone_color() != order)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
        }
        else
        {
            if(go_points.get(myPosition - 1).isExistence_stone()
                    && go_points.get(myPosition + 19).isExistence_stone()
                    && go_points.get(myPosition + 1).isExistence_stone()
                    && go_points.get(myPosition - 19).isExistence_stone())
            {
                if(go_points.get(myPosition - 1).getStone_color() != order
                        && go_points.get(myPosition + 19).getStone_color() != order
                        && go_points.get(myPosition + 1).getStone_color() != order
                        && go_points.get(myPosition - 19).getStone_color() != order)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
        }

        /* /////// 이 부분으로 하면 사방이 적인 부분만 착수금지 판단
        if(myPosition == 0) // left_top
        {
            if(color == go_points.get(myPosition + 1).getStone_color()
                    && color == go_points.get(myPosition + 19).getStone_color())
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if(myPosition == 18) // right_top
        {
            if(color == go_points.get(myPosition - 1).getStone_color()
                    && color == go_points.get(myPosition + 19).getStone_color())
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if(myPosition == 342) // left_bottom
        {
            if(color == go_points.get(myPosition - 19).getStone_color()
                    && color == go_points.get(myPosition + 1).getStone_color())
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if(myPosition == 360) // right_bottom
        {
            if(color == go_points.get(myPosition - 1).getStone_color()
                    && color == go_points.get(myPosition - 19).getStone_color())
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if (myPosition % 19 == 0 && myPosition != 0 && myPosition != 342) // left
        {
            if(color == go_points.get(myPosition - 19).getStone_color()
                    && color == go_points.get(myPosition + 1).getStone_color()
                    && color == go_points.get(myPosition + 19).getStone_color())
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if (myPosition >= 1 && myPosition <= 17) // top
        {
            if(color == go_points.get(myPosition - 1).getStone_color()
                    && color == go_points.get(myPosition + 19).getStone_color()
                    && color == go_points.get(myPosition + 1).getStone_color())
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if (myPosition % 19 == 18 && myPosition != 18 && myPosition != 360) // right
        {
            if(color == go_points.get(myPosition - 19).getStone_color()
                    && color == go_points.get(myPosition - 1).getStone_color()
                    && color == go_points.get(myPosition + 19).getStone_color())
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if (myPosition >= 343 && myPosition <= 359) // bottom
        {
            if(color == go_points.get(myPosition - 1).getStone_color()
                    && color == go_points.get(myPosition + 19).getStone_color()
                    && color == go_points.get(myPosition + 1).getStone_color())
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            if(color == go_points.get(myPosition - 1).getStone_color()
                    && color == go_points.get(myPosition + 19).getStone_color()
                    && color == go_points.get(myPosition + 1).getStone_color()
                    && color == go_points.get(myPosition - 19).getStone_color())
            {
                return false;
            }
            else
            {
                return true;
            }
        }

         */
    }

    // 벽과 맞닿아 있는 부분은 특수한 경우 이므로 sideSetting 함수를 통해 따로 설정해 준다
    // 벽과 맞닿아 있는 부분은 벽쪽 부분의 StoneSame을 모두 2, 즉 상대돌과 맞닿아 있는 것으로 설정
    // 이렇게 설정 함으로써 벽과 맞닿지 않은 부분 모두 상대돌이 있으면 죽은돌로 판단할 수 있음.
    public void sideSetting(ArrayList<Go_point> go_points, int myPosition)
    {
        if(myPosition == 0) // left_top일경우 위쪽, 왼쪽은 놓이는 돌과 색이 다르게 세팅
        {
            go_points.get(myPosition).setUpStoneSame(2);
            go_points.get(myPosition).setLeftStoneSame(2);
        }
        else if(myPosition == 18) // right_top
        {
            go_points.get(myPosition).setUpStoneSame(2);
            go_points.get(myPosition).setRightStoneSame(2);
        }
        else if(myPosition == 342) // left_bottom
        {
            go_points.get(myPosition).setLeftStoneSame(2);
            go_points.get(myPosition).setDownStoneSame(2);
        }
        else if(myPosition == 360) // right_bottom
        {
            go_points.get(myPosition).setRightStoneSame(2);
            go_points.get(myPosition).setDownStoneSame(2);
        }
        else if (myPosition % 19 == 0 && myPosition != 0 && myPosition != 342) // left
        {
            go_points.get(myPosition).setLeftStoneSame(2);
        }
        else if (myPosition >= 1 && myPosition <= 17) // top
        {
            go_points.get(myPosition).setUpStoneSame(2);
        }
        else if (myPosition % 19 == 18 && myPosition != 18 && myPosition != 360) // right
        {
            go_points.get(myPosition).setRightStoneSame(2);
        }
        else if (myPosition >= 343 && myPosition <= 359) // bottom
        {
            go_points.get(myPosition).setDownStoneSame(2);
        }
    }

    // 놓여있는 돌 주변을 검사하여 주변에 있는 돌이 아군돌인지 상대돌인지 판단하고 정보를 설정
    public void surroundingSearch(ArrayList<Go_point> go_points, int myPosition)
    {
        if(myPosition == 0) // left_top일경우 오른쪽, 아래 만 검사
        {
            // 아래 검사
            if(go_points.get(myPosition + 19).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition + 19).getStone_color())
                {
                    go_points.get(myPosition).setDownStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setDownStoneSame(2);
                }
            }

            // 오른쪽 검사
            if(go_points.get(myPosition + 1).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition + 1).getStone_color())
                {
                    go_points.get(myPosition).setRightStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setRightStoneSame(2);
                }
            }
        }
        else if(myPosition == 18) // right_top일경우 왼쪽, 아래 만 검사
        {
            // 아래 검사
            if(go_points.get(myPosition + 19).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition + 19).getStone_color())
                {
                    go_points.get(myPosition).setDownStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setDownStoneSame(2);
                }
            }

            // 왼쪽 검사
            if(go_points.get(myPosition - 1).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition - 1).getStone_color())
                {
                    go_points.get(myPosition).setLeftStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setLeftStoneSame(2);
                }
            }
        }
        else if(myPosition == 342) // left_bottom일 경우 위쪽, 오른쪽
        {
            // 위쪽 검사
            if(go_points.get(myPosition - 19).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition - 19).getStone_color())
                {
                    go_points.get(myPosition).setUpStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setUpStoneSame(2);
                }
            }

            // 오른쪽 검사
            if(go_points.get(myPosition + 1).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition + 1).getStone_color())
                {
                    go_points.get(myPosition).setRightStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setRightStoneSame(2);
                }
            }
        }
        else if(myPosition == 360) // right_bottom일 경우 위쪽, 왼쪽 검사
        {
            // 위쪽 검사
            if(go_points.get(myPosition - 19).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition - 19).getStone_color())
                {
                    go_points.get(myPosition).setUpStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setUpStoneSame(2);
                }
            }

            // 왼쪽 검사
            if(go_points.get(myPosition - 1).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition - 1).getStone_color())
                {
                    go_points.get(myPosition).setLeftStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setLeftStoneSame(2);
                }
            }
        }
        else if (myPosition % 19 == 0 && myPosition != 0 && myPosition != 342) // left일 경우 위쪽, 아래, 오른쪽만 검사
        {
            // 위쪽
            if(go_points.get(myPosition - 19).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition - 19).getStone_color())
                {
                    go_points.get(myPosition).setUpStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setUpStoneSame(2);
                }
            }
            // 아래쪽
            if(go_points.get(myPosition + 19).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition + 19).getStone_color())
                {
                    go_points.get(myPosition).setDownStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setDownStoneSame(2);
                }
            }
            // 오른쪽
            if(go_points.get(myPosition + 1).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition + 1).getStone_color())
                {
                    go_points.get(myPosition).setRightStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setRightStoneSame(2);
                }
            }
        }
        else if (myPosition >= 1 && myPosition <= 17) // top일 경우 왼쪽, 아래, 오른쪽만 검사
        {
            // 왼쪽
            if(go_points.get(myPosition - 1).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition - 1).getStone_color())
                {
                    go_points.get(myPosition).setLeftStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setLeftStoneSame(2);
                }
            }
            // 아래쪽
            if(go_points.get(myPosition + 19).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition + 19).getStone_color())
                {
                    go_points.get(myPosition).setDownStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setDownStoneSame(2);
                }
            }
            // 오른쪽
            if(go_points.get(myPosition + 1).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition + 1).getStone_color())
                {
                    go_points.get(myPosition).setRightStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setRightStoneSame(2);
                }
            }
        }
        else if (myPosition % 19 == 18 && myPosition != 18 && myPosition != 360) // right일 경우 위쪽, 왼쪽, 아래쪽만 검사
        {
            // 위쪽
            if(go_points.get(myPosition - 19).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition - 19).getStone_color())
                {
                    go_points.get(myPosition).setUpStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setUpStoneSame(2);
                }
            }
            // 왼쪽
            if(go_points.get(myPosition - 1).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition - 1).getStone_color())
                {
                    go_points.get(myPosition).setLeftStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setLeftStoneSame(2);
                }
            }
            // 아래쪽
            if(go_points.get(myPosition + 19).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition + 19).getStone_color())
                {
                    go_points.get(myPosition).setDownStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setDownStoneSame(2);
                }
            }
        }
        else if (myPosition >= 343 && myPosition <= 359) // bottom일 경우 왼쪽, 위쪽, 오른쪽만 검사
        {
            // 위쪽
            if(go_points.get(myPosition - 19).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition - 19).getStone_color())
                {
                    go_points.get(myPosition).setUpStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setUpStoneSame(2);
                }
            }
            // 왼쪽
            if(go_points.get(myPosition - 1).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition - 1).getStone_color())
                {
                    go_points.get(myPosition).setLeftStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setLeftStoneSame(2);
                }
            }
            // 오른쪽
            if(go_points.get(myPosition + 1).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition + 1).getStone_color())
                {
                    go_points.get(myPosition).setRightStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setRightStoneSame(2);
                }
            }
        }
        else
        {
            // 위쪽
            if(go_points.get(myPosition - 19).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition - 19).getStone_color())
                {
                    go_points.get(myPosition).setUpStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setUpStoneSame(2);
                }
            }
            // 왼쪽
            if(go_points.get(myPosition - 1).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition - 1).getStone_color())
                {
                    go_points.get(myPosition).setLeftStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setLeftStoneSame(2);
                }
            }
            // 아래쪽
            if(go_points.get(myPosition + 19).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition + 19).getStone_color())
                {
                    go_points.get(myPosition).setDownStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setDownStoneSame(2);
                }
            }
            // 오른쪽
            if(go_points.get(myPosition + 1).isExistence_stone()) // 착점한 돌 위에
            {
                if(go_points.get(myPosition).getStone_color() == go_points.get(myPosition + 1).getStone_color())
                {
                    go_points.get(myPosition).setRightStoneSame(1);
                }
                else
                {
                    go_points.get(myPosition).setRightStoneSame(2);
                }
            }
        }
    }

    // (착수금지 판단을 위해)놓는 돌 주변에 상대돌이 있나 검사하여 상대돌일 경우 canPutEnemyStack에 push 함.
    public void canPutEnemySearch(ArrayList<Go_point> go_points, int myPosition, int order)
    {
        int before = 0;
        if(order == 1)
        {
            before = 2;
        }
        else
        {
            before = 1;
        }
        if(myPosition == 0) // left_top일경우 오른쪽, 아래 만 검사
        {
            // 아래 검사
            if(go_points.get(myPosition + 19).isExistence_stone() && go_points.get(myPosition + 19).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition + 19));
            }

            // 오른쪽 검사
            if(go_points.get(myPosition + 1).isExistence_stone() && go_points.get(myPosition + 1).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition + 1));
            }
        }
        else if(myPosition == 18) // right_top일경우 왼쪽, 아래 만 검사
        {
            // 아래 검사
            if(go_points.get(myPosition + 19).isExistence_stone() && go_points.get(myPosition + 19).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition + 19));
            }

            // 왼쪽 검사
            if(go_points.get(myPosition - 1).isExistence_stone() && go_points.get(myPosition - 1).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition - 1));
            }
        }
        else if(myPosition == 342) // left_bottom일 경우 위쪽, 오른쪽
        {
            // 위쪽 검사
            if(go_points.get(myPosition - 19).isExistence_stone() && go_points.get(myPosition - 19).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition - 19));
            }

            // 오른쪽 검사
            if(go_points.get(myPosition + 1).isExistence_stone() && go_points.get(myPosition + 1).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition + 1));
            }
        }
        else if(myPosition == 360) // right_bottom일 경우 위쪽, 왼쪽 검사
        {
            // 위쪽 검사
            if(go_points.get(myPosition - 19).isExistence_stone() && go_points.get(myPosition - 19).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition - 19));
            }

            // 왼쪽 검사
            if(go_points.get(myPosition - 1).isExistence_stone() && go_points.get(myPosition - 1).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition - 1));
            }
        }
        else if (myPosition % 19 == 0 && myPosition != 0 && myPosition != 342) // left일 경우 위쪽, 아래, 오른쪽만 검사
        {
            // 위쪽
            if(go_points.get(myPosition - 19).isExistence_stone() && go_points.get(myPosition - 19).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition - 19));
            }
            // 아래쪽
            if(go_points.get(myPosition + 19).isExistence_stone() && go_points.get(myPosition + 19).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition + 19));
            }
            // 오른쪽
            if(go_points.get(myPosition + 1).isExistence_stone() && go_points.get(myPosition + 1).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition + 1));
            }
        }
        else if (myPosition >= 1 && myPosition <= 17) // top일 경우 왼쪽, 아래, 오른쪽만 검사
        {
            // 왼쪽
            if(go_points.get(myPosition - 1).isExistence_stone() && go_points.get(myPosition - 1).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition - 1));
            }
            // 아래쪽
            if(go_points.get(myPosition + 19).isExistence_stone() && go_points.get(myPosition + 19).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition + 19));
            }
            // 오른쪽
            if(go_points.get(myPosition + 1).isExistence_stone() && go_points.get(myPosition + 1).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition + 1));
            }
        }
        else if (myPosition % 19 == 18 && myPosition != 18 && myPosition != 360) // right일 경우 위쪽, 왼쪽, 아래쪽만 검사
        {
            // 위쪽
            if(go_points.get(myPosition - 19).isExistence_stone() && go_points.get(myPosition - 19).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition - 19));
            }
            // 왼쪽
            if(go_points.get(myPosition - 1).isExistence_stone() && go_points.get(myPosition - 1).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition - 1));
            }
            // 아래쪽
            if(go_points.get(myPosition + 19).isExistence_stone() && go_points.get(myPosition + 19).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition + 19));
            }
        }
        else if (myPosition >= 343 && myPosition <= 359) // bottom일 경우 왼쪽, 위쪽, 오른쪽만 검사
        {
            // 위쪽
            if(go_points.get(myPosition - 19).isExistence_stone() && go_points.get(myPosition - 19).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition - 19));
            }
            // 왼쪽
            if(go_points.get(myPosition - 1).isExistence_stone() && go_points.get(myPosition - 1).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition - 1));
            }
            // 오른쪽
            if(go_points.get(myPosition + 1).isExistence_stone() && go_points.get(myPosition + 1).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition + 1));
            }
        }
        else
        {
            // 위쪽
            if(go_points.get(myPosition - 19).isExistence_stone() && go_points.get(myPosition - 19).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition - 19));
            }
            // 왼쪽
            if(go_points.get(myPosition - 1).isExistence_stone() && go_points.get(myPosition - 1).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition - 1));
            }
            // 아래쪽
            if(go_points.get(myPosition + 19).isExistence_stone() && go_points.get(myPosition + 19).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition + 19));
            }
            // 오른쪽
            if(go_points.get(myPosition + 1).isExistence_stone() && go_points.get(myPosition + 1).getStone_color() == before) // 착점한 돌 위에
            {
                canPutEnemyStoneStack.push(go_points.get(myPosition + 1));
            }
        }
    }

    // 놓는 돌 주변에 상대돌이 있나 검사하여 상대돌일 경우 enemyStack에 push 함.
    public void enemySearch(ArrayList<Go_point> go_points, int myPosition, int order, EnemyStoneStack enemyStoneStack)
    {
        if(myPosition == 0) // left_top일경우 오른쪽, 아래 만 검사
        {
            // 아래 검사
            if(go_points.get(myPosition + 19).isExistence_stone() && go_points.get(myPosition + 19).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition + 19));
            }

            // 오른쪽 검사
            if(go_points.get(myPosition + 1).isExistence_stone() && go_points.get(myPosition + 1).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition + 1));
            }
        }
        else if(myPosition == 18) // right_top일경우 왼쪽, 아래 만 검사
        {
            // 아래 검사
            if(go_points.get(myPosition + 19).isExistence_stone() && go_points.get(myPosition + 19).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition + 19));
            }

            // 왼쪽 검사
            if(go_points.get(myPosition - 1).isExistence_stone() && go_points.get(myPosition - 1).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition - 1));
            }
        }
        else if(myPosition == 342) // left_bottom일 경우 위쪽, 오른쪽
        {
            // 위쪽 검사
            if(go_points.get(myPosition - 19).isExistence_stone() && go_points.get(myPosition - 19).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition - 19));
            }

            // 오른쪽 검사
            if(go_points.get(myPosition + 1).isExistence_stone() && go_points.get(myPosition + 1).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition + 1));
            }
        }
        else if(myPosition == 360) // right_bottom일 경우 위쪽, 왼쪽 검사
        {
            // 위쪽 검사
            if(go_points.get(myPosition - 19).isExistence_stone() && go_points.get(myPosition - 19).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition - 19));
            }

            // 왼쪽 검사
            if(go_points.get(myPosition - 1).isExistence_stone() && go_points.get(myPosition - 1).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition - 1));
            }
        }
        else if (myPosition % 19 == 0 && myPosition != 0 && myPosition != 342) // left일 경우 위쪽, 아래, 오른쪽만 검사
        {
            // 위쪽
            if(go_points.get(myPosition - 19).isExistence_stone() && go_points.get(myPosition - 19).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition - 19));
            }
            // 아래쪽
            if(go_points.get(myPosition + 19).isExistence_stone() && go_points.get(myPosition + 19).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition + 19));
            }
            // 오른쪽
            if(go_points.get(myPosition + 1).isExistence_stone() && go_points.get(myPosition + 1).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition + 1));
            }
        }
        else if (myPosition >= 1 && myPosition <= 17) // top일 경우 왼쪽, 아래, 오른쪽만 검사
        {
            // 왼쪽
            if(go_points.get(myPosition - 1).isExistence_stone() && go_points.get(myPosition - 1).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition - 1));
            }
            // 아래쪽
            if(go_points.get(myPosition + 19).isExistence_stone() && go_points.get(myPosition + 19).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition + 19));
            }
            // 오른쪽
            if(go_points.get(myPosition + 1).isExistence_stone() && go_points.get(myPosition + 1).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition + 1));
            }
        }
        else if (myPosition % 19 == 18 && myPosition != 18 && myPosition != 360) // right일 경우 위쪽, 왼쪽, 아래쪽만 검사
        {
            // 위쪽
            if(go_points.get(myPosition - 19).isExistence_stone() && go_points.get(myPosition - 19).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition - 19));
            }
            // 왼쪽
            if(go_points.get(myPosition - 1).isExistence_stone() && go_points.get(myPosition - 1).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition - 1));
            }
            // 아래쪽
            if(go_points.get(myPosition + 19).isExistence_stone() && go_points.get(myPosition + 19).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition + 19));
            }
        }
        else if (myPosition >= 343 && myPosition <= 359) // bottom일 경우 왼쪽, 위쪽, 오른쪽만 검사
        {
            // 위쪽
            if(go_points.get(myPosition - 19).isExistence_stone() && go_points.get(myPosition - 19).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition - 19));
            }
            // 왼쪽
            if(go_points.get(myPosition - 1).isExistence_stone() && go_points.get(myPosition - 1).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition - 1));
            }
            // 오른쪽
            if(go_points.get(myPosition + 1).isExistence_stone() && go_points.get(myPosition + 1).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition + 1));
            }
        }
        else
        {
            // 위쪽
            if(go_points.get(myPosition - 19).isExistence_stone() && go_points.get(myPosition - 19).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition - 19));
            }
            // 왼쪽
            if(go_points.get(myPosition - 1).isExistence_stone() && go_points.get(myPosition - 1).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition - 1));
            }
            // 아래쪽
            if(go_points.get(myPosition + 19).isExistence_stone() && go_points.get(myPosition + 19).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition + 19));
            }
            // 오른쪽
            if(go_points.get(myPosition + 1).isExistence_stone() && go_points.get(myPosition + 1).getStone_color() == order) // 착점한 돌 위에
            {
                enemyStoneStack.push(go_points.get(myPosition + 1));
            }
        }
    }

    // enemyStoneStack 스택에 있는 돌들 하나씩 꺼내서 그 돌과 연결된 모든 돌을 검사
    public void getStone(ArrayList<Go_point> go_points, EnemyStoneStack enemyStoneStack, DeleteStack deleteStack)
    {
        int getPosition = enemyStoneStack.pop().getStone_position();

        deleteStack.push(getPosition);
        go_points.get(getPosition).setFirstSearch(false);
        if(upExamination(go_points, getPosition)) {
            myUpStoneSearch(go_points, getPosition - 19, deleteStack);
        }
        if(leftExamination(go_points, getPosition)) {
            myLeftStoneSearch(go_points, getPosition - 1, deleteStack);
        }
        if(downExamination(go_points, getPosition)) {
            myDownStoneSearch(go_points, getPosition + 19, deleteStack);
        }
        if(rightExamination(go_points, getPosition)) {
            myRightStoneSearch(go_points, getPosition + 1, deleteStack);
        }
    }

    // (착수금지를 위한 함수)canPutEnemyStoneStack 스택에 있는 돌들 하나씩 꺼내서 그 돌과 연결된 모든 돌을 검사
    public void canPutGetStone(ArrayList<Go_point> go_points) // 스택에 있는 돌들 하나씩 꺼내서 검사
    {
        int getPosition = canPutEnemyStoneStack.pop().getStone_position();

        canPutDeleteStack.push(getPosition);
        go_points.get(getPosition).setCanPutFirstSearch(false);
        if(upExamination(go_points, getPosition)) {
            canPutMyUpStoneSearch(go_points, getPosition - 19);
        }
        if(leftExamination(go_points, getPosition)) {
            canPutMyLeftStoneSearch(go_points, getPosition - 1);
        }
        if(downExamination(go_points, getPosition)) {
            canPutMyDownStoneSearch(go_points, getPosition + 19);
        }
        if(rightExamination(go_points, getPosition)) {
            canPutMyRightStoneSearch(go_points, getPosition + 1);
        }

        System.out.println("gd");
    }

    // myUpStoneSearch, myLeftStoneSearch, myRightStoneSearch, myDownStoneSearch 함수는
    // 현재 돌의 위쪽 왼쪽 오른쪽 아래쪽을 검사하여 돌이 있는지, 있다면 아군 돌인지 상대돌인지 검사하여 정보를 저장한다
    // 검사한 돌이 아군돌이면, 즉 연결되어 있다면 일단 모두 deleteStack에 넣어 지울 준비를 한다
    // 나중에 deleteStack을 검사하여 연결된 돌이 모두 죽은 돌이면 deleteStack에 있는 위치의 돌을 모두 지운다.
    // 연결된 모든 돌들을 검사하기 위해 재귀함수를 이용하였다.
    public void myUpStoneSearch(ArrayList<Go_point> go_points, int position, DeleteStack deleteStack)
    {
        if(go_points.get(position).isFirstSearch()) {
            try {
                if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() == order) // 착점한 돌 위에
                {
                    go_points.get(position + 19).setUpStoneSame(1);

                    go_points.get(position).setFirstSearch(false);
                    deleteStack.push(position);
                    if(upExamination(go_points, position)) {
                        myUpStoneSearch(go_points, position - 19, deleteStack);
                    }
                    if(leftExamination(go_points, position)) {
                        myLeftStoneSearch(go_points, position - 1, deleteStack);
                    }
                    if(downExamination(go_points, position)) {
                        myDownStoneSearch(go_points, position + 19, deleteStack);
                    }
                    if(rightExamination(go_points, position)) {
                        myRightStoneSearch(go_points, position + 1, deleteStack);
                    }
                }
                else if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() != order)
                {
                    go_points.get(position + 19).setUpStoneSame(2);
                }
                else if (!go_points.get(position).isExistence_stone())
                {
                    go_points.get(position + 19).setUpStoneSame(0);
                }

            } catch (Exception e) {
                go_points.get(position + 19).setUpStoneSame(2);
                //throw e; //최상위 클래스가 아니라면 무조건 던져주자
            }
        }
    }

    public void myLeftStoneSearch(ArrayList<Go_point> go_points, int position, DeleteStack deleteStack)
    {
        if(go_points.get(position).isFirstSearch()) {
            try {
                if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() == order) // 착점한 돌 위에
                {
                    go_points.get(position + 1).setLeftStoneSame(1);

                    go_points.get(position).setFirstSearch(false);
                    deleteStack.push(position);
                    if(upExamination(go_points, position)) {
                        myUpStoneSearch(go_points, position - 19, deleteStack);
                    }
                    if(leftExamination(go_points, position)) {
                        myLeftStoneSearch(go_points, position - 1, deleteStack);
                    }
                    if(downExamination(go_points, position)) {
                        myDownStoneSearch(go_points, position + 19, deleteStack);
                    }
                    if(rightExamination(go_points, position)) {
                        myRightStoneSearch(go_points, position + 1, deleteStack);
                    }
                }
                else if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() != order)
                {
                    go_points.get(position + 1).setLeftStoneSame(2);
                }
                else if (!go_points.get(position).isExistence_stone())
                {
                    go_points.get(position + 1).setLeftStoneSame(0);
                }

            } catch (Exception e) {
                go_points.get(position + 1).setLeftStoneSame(2);
                //throw e; //최상위 클래스가 아니라면 무조건 던져주자
            }
        }
    }

    public void myDownStoneSearch(ArrayList<Go_point> go_points, int position, DeleteStack deleteStack)
    {
        if(go_points.get(position).isFirstSearch()) {
            try {
                if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() == order) // 착점한 돌 위에
                {
                    go_points.get(position - 19).setDownStoneSame(1);

                    go_points.get(position).setFirstSearch(false);
                    deleteStack.push(position);
                    if(upExamination(go_points, position)) {
                        myUpStoneSearch(go_points, position - 19, deleteStack);
                    }
                    if(leftExamination(go_points, position)) {
                        myLeftStoneSearch(go_points, position - 1, deleteStack);
                    }
                    if(downExamination(go_points, position)) {
                        myDownStoneSearch(go_points, position + 19, deleteStack);
                    }
                    if(rightExamination(go_points, position)) {
                        myRightStoneSearch(go_points, position + 1, deleteStack);
                    }
                }
                else if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() != order)
                {
                    go_points.get(position - 19).setDownStoneSame(2);
                }
                else if (!go_points.get(position).isExistence_stone())
                {
                    go_points.get(position - 19).setDownStoneSame(0);
                }

            } catch (Exception e) {
                go_points.get(position - 19).setDownStoneSame(2);
            }
        }
    }

    public void myRightStoneSearch(ArrayList<Go_point> go_points, int position, DeleteStack deleteStack)
    {
        if(go_points.get(position).isFirstSearch()) {
            try {
                if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() == order) // 착점한 돌 위에
                {
                    go_points.get(position - 1).setRightStoneSame(1);

                    go_points.get(position).setFirstSearch(false);
                    deleteStack.push(position);
                    if(upExamination(go_points, position)) {
                        myUpStoneSearch(go_points, position - 19, deleteStack);
                    }
                    if(leftExamination(go_points, position)) {
                        myLeftStoneSearch(go_points, position - 1, deleteStack);
                    }
                    if(downExamination(go_points, position)) {
                        myDownStoneSearch(go_points, position + 19, deleteStack);
                    }
                    if(rightExamination(go_points, position)) {
                        myRightStoneSearch(go_points, position + 1, deleteStack);
                    }
                }
                else if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() != order)
                {
                    go_points.get(position - 1).setRightStoneSame(2);
                }
                else if (!go_points.get(position).isExistence_stone())
                {
                    go_points.get(position - 1).setRightStoneSame(0);
                }

            } catch (Exception e) {
                go_points.get(position - 1).setRightStoneSame(2);
                //throw e; //최상위 클래스가 아니라면 무조건 던져주자
            }
        }
    }

    // 착수금지 판정을 위한 myUpStoneSearch, myLeftStoneSearch, myRightStoneSearch, myDownStoneSearch 함수
    // 자세히 보면 myUpStoneSearch, myLeftStoneSearch, myRightStoneSearch, myDownStoneSearch과 순서, 즉 order에 들어가는 숫자값이 반대이다.
    // 이는 착수금지는 돌을 아직 실제로 놓았을 때가 아니라서 order가 상대순서로 바뀌지 않아서이다.
    public void canPutMyUpStoneSearch(ArrayList<Go_point> go_points, int position)
    {
        int color = 0;
        if(order == 1) {
            color = 2;
        }
        else
        {
            color = 1;
        }

        if(go_points.get(position).isCanPutFirstSearch()) {
            try {
                if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() == color) // 착점한 돌 위에
                {
                    go_points.get(position + 19).setUpStoneSame(1);

                    go_points.get(position).setCanPutFirstSearch(false);
                    canPutDeleteStack.push(position);
                    if(upExamination(go_points, position)) {
                        canPutMyUpStoneSearch(go_points, position - 19);
                    }
                    if(leftExamination(go_points, position)) {
                        canPutMyLeftStoneSearch(go_points, position - 1);
                    }
                    if(downExamination(go_points, position)) {
                        canPutMyDownStoneSearch(go_points, position + 19);
                    }
                    if(rightExamination(go_points, position)) {
                        canPutMyRightStoneSearch(go_points, position + 1);
                    }
                }
                else if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() != color)
                {
                    go_points.get(position + 19).setUpStoneSame(2);
                }
                else if (!go_points.get(position).isExistence_stone())
                {
                    go_points.get(position + 19).setUpStoneSame(0);
                }

            } catch (Exception e) {
                go_points.get(position + 19).setUpStoneSame(2);
                //throw e; //최상위 클래스가 아니라면 무조건 던져주자
            }
        }
    }

    public void canPutMyLeftStoneSearch(ArrayList<Go_point> go_points, int position)
    {
        int color = 0;
        if(order == 1) {
            color = 2;
        }
        else
        {
            color = 1;
        }

        if(go_points.get(position).isCanPutFirstSearch()) {
            try {
                if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() == color) // 착점한 돌 위에
                {
                    go_points.get(position + 1).setLeftStoneSame(1);

                    go_points.get(position).setCanPutFirstSearch(false);
                    canPutDeleteStack.push(position);
                    if(upExamination(go_points, position)) {
                        canPutMyUpStoneSearch(go_points, position - 19);
                    }
                    if(leftExamination(go_points, position)) {
                        canPutMyLeftStoneSearch(go_points, position - 1);
                    }
                    if(downExamination(go_points, position)) {
                        canPutMyDownStoneSearch(go_points, position + 19);
                    }
                    if(rightExamination(go_points, position)) {
                        canPutMyRightStoneSearch(go_points, position + 1);
                    }
                }
                else if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() != color)
                {
                    go_points.get(position + 1).setLeftStoneSame(2);
                }
                else if (!go_points.get(position).isExistence_stone())
                {
                    go_points.get(position + 1).setLeftStoneSame(0);
                }

            } catch (Exception e) {
                go_points.get(position + 1).setLeftStoneSame(2);
                //throw e; //최상위 클래스가 아니라면 무조건 던져주자
            }
        }
    }

    public void canPutMyDownStoneSearch(ArrayList<Go_point> go_points, int position)
    {
        int color = 0;
        if(order == 1) {
            color = 2;
        }
        else
        {
            color = 1;
        }

        if(go_points.get(position).isCanPutFirstSearch()) {
            try {
                if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() == color) // 착점한 돌 위에
                {
                    go_points.get(position - 19).setDownStoneSame(1);

                    go_points.get(position).setCanPutFirstSearch(false);
                    canPutDeleteStack.push(position);
                    if(upExamination(go_points, position)) {
                        canPutMyUpStoneSearch(go_points, position - 19);
                    }
                    if(leftExamination(go_points, position)) {
                        canPutMyLeftStoneSearch(go_points, position - 1);
                    }
                    if(downExamination(go_points, position)) {
                        canPutMyDownStoneSearch(go_points, position + 19);
                    }
                    if(rightExamination(go_points, position)) {
                        canPutMyRightStoneSearch(go_points, position + 1);
                    }
                }
                else if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() != color)
                {
                    go_points.get(position - 19).setDownStoneSame(2);
                }
                else if (!go_points.get(position).isExistence_stone())
                {
                    go_points.get(position - 19).setDownStoneSame(0);
                }

            } catch (Exception e) {
                go_points.get(position - 19).setDownStoneSame(2);
            }
        }
    }

    public void canPutMyRightStoneSearch(ArrayList<Go_point> go_points, int position)
    {
        int color = 0;
        if(order == 1) {
            color = 2;
        }
        else
        {
            color = 1;
        }

        if(go_points.get(position).isCanPutFirstSearch()) {
            try {
                if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() == color) // 착점한 돌 위에
                {
                    go_points.get(position - 1).setRightStoneSame(1);

                    go_points.get(position).setCanPutFirstSearch(false);
                    canPutDeleteStack.push(position);
                    if(upExamination(go_points, position)) {
                        canPutMyUpStoneSearch(go_points, position - 19);
                    }
                    if(leftExamination(go_points, position)) {
                        canPutMyLeftStoneSearch(go_points, position - 1);
                    }
                    if(downExamination(go_points, position)) {
                        canPutMyDownStoneSearch(go_points, position + 19);
                    }
                    if(rightExamination(go_points, position)) {
                        canPutMyRightStoneSearch(go_points, position + 1);
                    }
                }
                else if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() != color)
                {
                    go_points.get(position - 1).setRightStoneSame(2);
                }
                else if (!go_points.get(position).isExistence_stone())
                {
                    go_points.get(position - 1).setRightStoneSame(0);
                }

            } catch (Exception e) {
                go_points.get(position - 1).setRightStoneSame(2);
                //throw e; //최상위 클래스가 아니라면 무조건 던져주자
            }
        }
    }

    // 바둑판을 그리기 위한 함수
    public void setIcon(int img[])
    {
        for(int i = 0; i < 19*19; i++)
        {
            img[i] = R.drawable.square_icon1;
        }

        img[0] = R.drawable.left_top1;
        img[18] = R.drawable.right_top1;
        img[342] = R.drawable.left_bottom1;
        img[360] = R.drawable.right_bottom1;

        for(int i = 19; i <= 323; i += 19)
        {
            img[i] = R.drawable.left_icon;
        }

        for(int i = 1; i <= 17; i++)
        {
            img[i] = R.drawable.top_icon;
        }

        for(int i = 37; i <= 341; i += 19)
        {
            img[i] = R.drawable.right_icon;
        }

        for(int i = 343; i <= 359; i++)
        {
            img[i] = R.drawable.bottom_icon;
        }

        img[60] = R.drawable.square_point5;
        img[66] = R.drawable.square_point5;
        img[72] = R.drawable.square_point5;
        img[174] = R.drawable.square_point5;
        img[180] = R.drawable.square_point5;
        img[186] = R.drawable.square_point5;
        img[288] = R.drawable.square_point5;
        img[294] = R.drawable.square_point5;
        img[300] = R.drawable.square_point5;
    }

    // 바둑판을 클릭해서 돌을 놓는 함수
    // image[] 배열에 흑돌, 백돌이 놓여진 사진으로 업데이트 해준다
    public void setStone(int img[], int position)
    {
        if(order == 1) {
            if (position == 0) {
                img[position] = R.drawable.left_top_black;
            } else if (position == 18) {
                img[position] = R.drawable.right_top_black;
            } else if (position == 342) {
                img[position] = R.drawable.left_bottom_black;
            } else if (position == 360) {
                img[position] = R.drawable.right_bottom_black;
            } else if (position % 19 == 0 && position != 0 && position != 342) {
                img[position] = R.drawable.left_black1;
            } else if (position >= 1 && position <= 17) {
                img[position] = R.drawable.top_black1;
            } else if (position % 19 == 18 && position != 18 && position != 360) {
                img[position] = R.drawable.right_black1;
            } else if (position >= 343 && position <= 359) {
                img[position] = R.drawable.bottom_black1;
            } else {
                img[position] = R.drawable.square_black1;
            }
        }
        else if(order == 2) {
            if (position == 0) {
                img[position] = R.drawable.left_top_white;
            } else if (position == 18) {
                img[position] = R.drawable.right_top_white;
            } else if (position == 342) {
                img[position] = R.drawable.left_bottom_white;
            } else if (position == 360) {
                img[position] = R.drawable.right_bottom_white;
            } else if (position % 19 == 0 && position != 0 && position != 342) {
                img[position] = R.drawable.left_white1;
            } else if (position >= 1 && position <= 17) {
                img[position] = R.drawable.top_white1;
            } else if (position % 19 == 18 && position != 18 && position != 360) {
                img[position] = R.drawable.right_white1;
            } else if (position >= 343 && position <= 359) {
                img[position] = R.drawable.bottom_white1;
            } else {
                img[position] = R.drawable.square_white;
            }
        }
    }

    // 돌이 죽어서 바둑판을 다시 그릴때 사용하는 함수
    public void resetIcon(int img[], int position)
    {
        if (position == 0) {
            img[position] = R.drawable.left_top1;
        } else if (position == 18) {
            img[position] = R.drawable.right_top1;
        } else if (position == 342) {
            img[position] = R.drawable.left_bottom1;
        } else if (position == 360) {
            img[position] = R.drawable.right_bottom1;
        } else if (position % 19 == 0 && position != 0 && position != 342) {
            img[position] = R.drawable.left_icon;
        } else if (position >= 1 && position <= 17) {
            img[position] = R.drawable.top_icon;
        } else if (position % 19 == 18 && position != 18 && position != 360) {
            img[position] = R.drawable.right_icon;
        } else if (position >= 343 && position <= 359) {
            img[position] = R.drawable.bottom_icon;
        } else if(position == 60 || position == 66 || position == 72 || position == 174 || position == 180
                || position == 186 || position == 288 || position == 294 || position == 300) {
            img[position] = R.drawable.square_point5;
        } else {
            img[position] = R.drawable.square_icon1;
        }
    }

    // myUpStoneSearch, myLeftStoneSearch, myRightStoneSearch, myDownStoneSearch 함수에서
    // 벽면인지 아닌지 검사하는 함수들
    public boolean upExamination(ArrayList<Go_point> go_points, int currentPosition)
    {
        if(currentPosition >= 0 && currentPosition <= 18)
        {
            go_points.get(currentPosition).setUpStoneSame(order);
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean leftExamination(ArrayList<Go_point> go_points, int currentPosition)
    {
        if(currentPosition % 19 == 0)
        {
            go_points.get(currentPosition).setLeftStoneSame(order);
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean downExamination(ArrayList<Go_point> go_points, int currentPosition)
    {
        if(currentPosition >= 342 && currentPosition <= 360)
        {
            go_points.get(currentPosition).setDownStoneSame(order);
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean rightExamination(ArrayList<Go_point> go_points, int currentPosition)
    {
        if(currentPosition % 19 == 18)
        {
            go_points.get(currentPosition).setRightStoneSame(order);
            return false;
        }
        else
        {
            return true;
        }
    }

    // 계가 승패판단 원리
    // 기본적으로 사석은 모두 제거한 상태에서 계가에 들어가야 한다
    // 1. 모든 빈칸을 스택 or 배열에 저장
    // 2. 빈칸을 하나씩 꺼네 주변 빈칸을 모두 탐색
    // 3. 탐색한 빈칸은 카운트, 빈칸이 아니라 돌이있는 경우는 그 돌의 색이 어떤건지 판단후 리턴
    // 4. 리턴된 값으로 흑의 집인지 백의 집인지 판단
    public void emptySearch(ArrayList<Go_point> go_points, DeleteStack emptyStoneStack)
    {
        for(int i = 0; i < 19 * 19; i++)
        {
            if(!go_points.get(i).isExistence_stone())
            {
                emptyStoneStack.push(i);
            }
        }
    }

    // emptyStoneStack에 있는 것을 pop해서 한번도 처음 empty검사하는 것이면 inspectionStack에 push
    // 즉 현재 emptyStoneStack에서 pop된 빈공간과 연결되어있는 모든 빈공간을 검사하면서 inspectionStack에 넣는것
    // 빈공간 검사하면서 만약 주변에 돌이있으면(빈공간의 끝이면) 어떤 색의 돌인지도 저장
    // 나중에 inspectionStack에 있는 돌들을 모두 검사해서 주변 돌들의 색이 모두 같으면 집으로 판정
    public void getEmptyStone(ArrayList<Go_point> go_points, DeleteStack emptyStoneStack, DeleteStack inspectionStack)
    {
        int getPosition = emptyStoneStack.pop();

        if(!go_points.get(getPosition).isEmptySearch()) {
            inspectionStack.push(getPosition);
            go_points.get(getPosition).setEmptySearch(true);

            if(emptyUpExamination(go_points, getPosition)) {
                emptyUpStoneSearch(go_points, getPosition - 19, inspectionStack);
            }
            if(emptyLeftExamination(go_points, getPosition)) {
                emptyLeftStoneSearch(go_points, getPosition - 1, inspectionStack);
            }
            if(emptyDownExamination(go_points, getPosition)) {
                emptyDownStoneSearch(go_points, getPosition + 19, inspectionStack);
            }
            if(emptyRightExamination(go_points, getPosition)) {
                emptyRightStoneSearch(go_points, getPosition + 1, inspectionStack);
            }
        }

    }

    public void emptyUpStoneSearch(ArrayList<Go_point> go_points, int position, DeleteStack inspectionStack)
    {
        if(!go_points.get(position).isEmptySearch()) {
            if (!go_points.get(position).isExistence_stone()) // 빈칸일 경우
            {
                // go_points.get(position + 19).setUpStoneSame(1);
                go_points.get(position).setEmptySearch(true);
                inspectionStack.push(position);
                if(emptyUpExamination(go_points, position)) {
                    emptyUpStoneSearch(go_points, position - 19, inspectionStack);
                }
                if(emptyLeftExamination(go_points, position)) {
                    emptyLeftStoneSearch(go_points, position - 1, inspectionStack);
                }
                if(emptyDownExamination(go_points, position)) {
                    emptyDownStoneSearch(go_points, position + 19, inspectionStack);
                }
                if(emptyRightExamination(go_points, position)) {
                    emptyRightStoneSearch(go_points, position + 1, inspectionStack);
                }
            }
            else if (go_points.get(position).isExistence_stone())
            {
                go_points.get(position + 19).setUpStoneExistence(true);
                go_points.get(position + 19).setUpStoneColor(go_points.get(position).getStone_color());
            }
        }

        /*
        if(!go_points.get(position).isEmptySearch()) {
            try {
                if (!go_points.get(position).isExistence_stone()) // 빈칸일 경우
                {
                    // go_points.get(position + 19).setUpStoneSame(1);
                    go_points.get(position).setEmptySearch(true);
                    inspectionStack.push(position); //////// 여기까지 수정 빈칸이 아닐경우는 값 저장해야함
                    if(upExamination(go_points, position)) {
                        myUpStoneSearch(go_points, position - 19, inspectionStack);
                    }
                    if(leftExamination(go_points, position)) {
                        myLeftStoneSearch(go_points, position - 1, inspectionStack);
                    }
                    if(downExamination(go_points, position)) {
                        myDownStoneSearch(go_points, position + 19, inspectionStack);
                    }
                    if(rightExamination(go_points, position)) {
                        myRightStoneSearch(go_points, position + 1, inspectionStack);
                    }
                }
                else if (go_points.get(position).isExistence_stone())
                {
                    go_points.get(position + 19).setUpStoneExistence(true);
                    go_points.get(position + 19).setUpStoneColor(go_points.get(position).getStone_color());
                }

            } catch (Exception e) {

                //throw e; //최상위 클래스가 아니라면 무조건 던져주자
            }
        }

         */
    }

    public void emptyLeftStoneSearch(ArrayList<Go_point> go_points, int position, DeleteStack inspectionStack)
    {
        if(!go_points.get(position).isEmptySearch()) {
            if (!go_points.get(position).isExistence_stone())
            {
                // go_points.get(position + 1).setLeftStoneSame(1);

                go_points.get(position).setEmptySearch(true);
                inspectionStack.push(position);
                if(emptyUpExamination(go_points, position)) {
                    emptyUpStoneSearch(go_points, position - 19, inspectionStack);
                }
                if(emptyLeftExamination(go_points, position)) {
                    emptyLeftStoneSearch(go_points, position - 1, inspectionStack);
                }
                if(emptyDownExamination(go_points, position)) {
                    emptyDownStoneSearch(go_points, position + 19, inspectionStack);
                }
                if(emptyRightExamination(go_points, position)) {
                    emptyRightStoneSearch(go_points, position + 1, inspectionStack);
                }
            }
            else if (go_points.get(position).isExistence_stone())
            {
                go_points.get(position + 1).setLeftStoneExistence(true);
                go_points.get(position + 1).setLeftStoneColor(go_points.get(position).getStone_color());
            }
        }
    }

    public void emptyDownStoneSearch(ArrayList<Go_point> go_points, int position, DeleteStack inspectionStack)
    {
        if(!go_points.get(position).isEmptySearch()) {
            if (!go_points.get(position).isExistence_stone()) // 착점한 돌 위에
            {
                //go_points.get(position - 19).setDownStoneSame(1);

                go_points.get(position).setEmptySearch(true);
                inspectionStack.push(position);
                if(emptyUpExamination(go_points, position)) {
                    emptyUpStoneSearch(go_points, position - 19, inspectionStack);
                }
                if(emptyLeftExamination(go_points, position)) {
                    emptyLeftStoneSearch(go_points, position - 1, inspectionStack);
                }
                if(emptyDownExamination(go_points, position)) {
                    emptyDownStoneSearch(go_points, position + 19, inspectionStack);
                }
                if(emptyRightExamination(go_points, position)) {
                    emptyRightStoneSearch(go_points, position + 1, inspectionStack);
                }
            }
            else if (go_points.get(position).isExistence_stone())
            {
                go_points.get(position - 19).setDownStoneExistence(true);
                go_points.get(position - 19).setDownStoneColor(go_points.get(position).getStone_color());
            }
        }
    }

    public void emptyRightStoneSearch(ArrayList<Go_point> go_points, int position, DeleteStack inspectionStack)
    {
        if(!go_points.get(position).isEmptySearch()) {
            if (!go_points.get(position).isExistence_stone()) // 착점한 돌 위에
            {
                //go_points.get(position - 1).setRightStoneSame(1);

                go_points.get(position).setEmptySearch(true);
                inspectionStack.push(position);
                if(emptyUpExamination(go_points, position)) {
                    emptyUpStoneSearch(go_points, position - 19, inspectionStack);
                }
                if(emptyLeftExamination(go_points, position)) {
                    emptyLeftStoneSearch(go_points, position - 1, inspectionStack);
                }
                if(emptyDownExamination(go_points, position)) {
                    emptyDownStoneSearch(go_points, position + 19, inspectionStack);
                }
                if(emptyRightExamination(go_points, position)) {
                    emptyRightStoneSearch(go_points, position + 1, inspectionStack);
                }
            }
            else if (go_points.get(position).isExistence_stone())
            {
                go_points.get(position - 1).setRightStoneExistence(true);
                go_points.get(position - 1).setRightStoneColor(go_points.get(position).getStone_color());
            }
        }
    }

    public boolean emptyUpExamination(ArrayList<Go_point> go_points, int currentPosition)
    {
        if(currentPosition >= 0 && currentPosition <= 18)
        {
            //go_points.get(currentPosition).setUpStoneSame(order);
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean emptyLeftExamination(ArrayList<Go_point> go_points, int currentPosition)
    {
        if(currentPosition % 19 == 0)
        {
            //go_points.get(currentPosition).setLeftStoneSame(order);
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean emptyDownExamination(ArrayList<Go_point> go_points, int currentPosition)
    {
        if(currentPosition >= 342 && currentPosition <= 360)
        {
            //go_points.get(currentPosition).setDownStoneSame(order);
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean emptyRightExamination(ArrayList<Go_point> go_points, int currentPosition)
    {
        if(currentPosition % 19 == 18)
        {
            //go_points.get(currentPosition).setRightStoneSame(order);
            return false;
        }
        else
        {
            return true;
        }
    }

    public void countHouse(ArrayList<Go_point> go_points, DeleteStack inspectionStack)
    {
        double count = 0;
        int upColor = 0;
        int leftColor = 0;
        int downColor = 0;
        int rightColor = 0;
        boolean upSame = true;
        boolean leftSame = true;
        boolean downSame = true;
        boolean rightSame = true;

        ArrayList<Integer> inspectionUpList = new ArrayList<>();
        ArrayList<Integer> inspectionLeftList = new ArrayList<>();
        ArrayList<Integer> inspectionDownList = new ArrayList<>();
        ArrayList<Integer> inspectionRightList = new ArrayList<>();

        for(int i = inspectionStack.getTop() + 1; i > 0; i--)
        {
            int position = inspectionStack.pop();

            if(!upExamination(go_points, position) || !leftExamination(go_points, position) || !downExamination(go_points, position)
                    || !rightExamination(go_points, position))
            {
                count += 0.5;
            }
            else
            {
                count += 1;
            }

            if(go_points.get(position).isUpStoneExistence())
            {
                inspectionUpList.add(go_points.get(position).getUpStoneColor());
                //
            }

            if(go_points.get(position).isLeftStoneExistence())
            {
                inspectionLeftList.add(go_points.get(position).getLeftStoneColor());
                //
            }

            if(go_points.get(position).isDownStoneExistence())
            {
                inspectionDownList.add(go_points.get(position).getDownStoneColor());
            }

            if(go_points.get(position).isRightStoneExistence())
            {
                inspectionRightList.add(go_points.get(position).getRightStoneColor());
            }
        }

        if(inspectionUpList.size() != 0)
        {
            if(inspectionUpList.size() == 1)
            {
                upColor = inspectionUpList.get(0);
                //
            }
            else {
                for (int i = 1; i < inspectionUpList.size(); i++)
                {
                    if(inspectionUpList.get(0) != inspectionUpList.get(i))
                    {
                        upSame = false;
                    }
                }

                if(upSame)
                {
                    upColor = inspectionUpList.get(0);
                }
                else
                {
                    upColor = 3; // 3일 경우 어느한쪽에 흑,백이 섞여있다, 즉 집이아니다
                }
            }
        }
        else
        {
            upColor = 0; // 0이면 벽이있는 것으로 간주
        }

        if(inspectionLeftList.size() != 0)
        {
            if(inspectionLeftList.size() == 1)
            {
                leftColor = inspectionLeftList.get(0);
                //
            }
            else {
                for (int i = 1; i < inspectionLeftList.size(); i++)
                {
                    if(inspectionLeftList.get(0) != inspectionLeftList.get(i))
                    {
                        leftSame = false;
                    }
                }

                if(leftSame)
                {
                    leftColor = inspectionLeftList.get(0);
                }
                else
                {
                    leftColor = 3; // 3일 경우 어느한쪽에 흑,백이 섞여있다, 즉 집이아니다
                }
            }
        }
        else
        {
            leftColor = 0; // 0이면 벽이있는 것으로 간주
        }

        if(inspectionDownList.size() != 0)
        {
            if(inspectionDownList.size() == 1)
            {
                downColor = inspectionDownList.get(0);
            }
            else {
                for (int i = 1; i < inspectionDownList.size(); i++)
                {
                    if(inspectionDownList.get(0) != inspectionDownList.get(i))
                    {
                        downSame = false;
                    }
                }

                if(downSame)
                {
                    downColor = inspectionDownList.get(0);
                }
                else
                {
                    downColor = 3; // 3일 경우 어느한쪽에 흑,백이 섞여있다, 즉 집이아니다
                }
            }
        }
        else
        {
            downColor = 0; // 0이면 벽이있는 것으로 간주
        }

        if(inspectionRightList.size() != 0)
        {
            if(inspectionRightList.size() == 1)
            {
                rightColor = inspectionRightList.get(0);
            }
            else {
                for (int i = 1; i < inspectionRightList.size(); i++)
                {
                    if(inspectionRightList.get(0) != inspectionRightList.get(i))
                    {
                        rightSame = false;
                    }
                }

                if(rightSame)
                {
                    rightColor = inspectionRightList.get(0);
                }
                else
                {
                    rightColor = 3; // 3일 경우 어느한쪽에 흑,백이 섞여있다, 즉 집이아니다
                }
            }
        }
        else
        {
            rightColor = 0; // 0이면 벽이있는 것으로 간주
        }

        if((upColor == 0 || upColor == 1) && (leftColor == 0 || leftColor == 1)
                && (downColor == 0 || downColor == 1) && (rightColor == 0 || rightColor == 1))
        {
            blackHouseCount += count;
            //
        }

        if((upColor == 0 || upColor == 2) && (leftColor == 0 || leftColor == 2)
                && (downColor == 0 || downColor == 2) && (rightColor == 0 || rightColor == 2))
        {
            whiteHouseCount += count;
            //
        }
    }

    public void offTimer()
    {
        try {
            lastBlackTimer.cancel();
        } catch (Exception e) {
        }

        try {
            blackTimer.cancel();
        } catch (Exception e) {
        }

        try {
            blackTimer1.cancel();
        } catch (Exception e) {
        }

        try {
            lastWhiteTimer.cancel();
        } catch (Exception e) {
        }

        try {
            whiteTimer.cancel();
        } catch (Exception e) {
        }
    }
}