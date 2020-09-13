package org.application.go;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.application.go.Model.Go_point;

import java.util.ArrayList;

//// 바둑판에 화점 업데이트 필요, 바둑돌 놓을때 ui 업데이트 필요
public class MainActivity extends AppCompatActivity {

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

    EnemyStoneStack canPutEnemyStoneStack;
    DeleteStack canPutDeleteStack;
    DeleteStack canPutLiveOrDie;
    DeleteStack booleanStack;
    int canPutDeathFlag = 0; // 0은 아무것도아님, 1은 죽은돌, 2는 산돌, 연결되어 있는 돌 다 검사해서 활로가 없으면 liveOrDie 스택에 1주고 있으면 2줌
    boolean canPutLiveFlag = false;

    TextView turnText;
    TextView deathWhiteCount;
    TextView deathBlackCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        turnText = (TextView) findViewById(R.id.main_turn_textView);
        deathWhiteCount = (TextView) findViewById(R.id.main_black_score);
        deathBlackCount = (TextView) findViewById(R.id.main_white_score);

        setIcon();

        go_points = new ArrayList<>();

        for(int i = 0; i < 19*19; i++)
        {
            Go_point go_point = new Go_point();
            go_point.setStone_position(i);
            go_points.add(go_point);
            sideSetting(go_points, i);
        }

        if(order == 1) {
            turnText.setText("흑돌 턴 입니다");
        }
        else
        {
            turnText.setText("백돌 턴 입니다");
        }

        deathWhiteCount.setText("죽은 백돌: " + deathwhiteStones + " 개");
        deathBlackCount.setText("죽은 흑돌: " + deathBlackStones + " 개");

        adapter = new MyAdapter(getApplicationContext(), R.layout.item_square, image);

        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(adapter);

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
                        setStone(position);
                        iv.setImageResource(image[position]);
                        order = 2;
                    }
                    else if(order == 2)
                    {
                        go_points.get(position).setExistence_stone(true);
                        go_points.get(position).setStone_color(order);
                        go_points.get(position).setStone_position(position);
                        surroundingSearch(go_points, position);
                        setStone(position);
                        iv.setImageResource(image[position]);
                        order = 1;
                    }

                    for(int i = 0; i < 19*19; i++) // 돌을 놓은 후 돌들의 주변 상황을 최신 결과로 업데이트
                    {
                        surroundingSearch(go_points, i);
                    }

                    enemySearch(go_points, position, order); // 착점 주변에 상대돌 있나 찾는 함수
                    if(!enemyStoneStack.empty())
                    {
                        for (int j = enemyStoneStack.getTop() + 1; j > 0; j--)
                        {
                            // 상대돌이 있으면 getStone함수에서 그 돌을 pop해서 연결된 돌들을 모두 deleteStack에 push한다
                            getStone(go_points);

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

                                    resetIcon(popPosition);
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
                    deathWhiteCount.setText("죽은 백돌: " + deathwhiteStones + " 개");
                    deathBlackCount.setText("죽은 흑돌: " + deathBlackStones + " 개");
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
                return false;
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
                return false;
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
                return false;
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
                return false;
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
                return false;
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
                return false;
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
                return false;
            }
            else
            {
                return true;
            }
        }
        else if (myPosition >= 343 && myPosition <= 359) // bottom
        {
            if(go_points.get(myPosition - 1).isExistence_stone()
                    && go_points.get(myPosition + 19).isExistence_stone()
                    && go_points.get(myPosition + 1).isExistence_stone())
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
            if(go_points.get(myPosition - 1).isExistence_stone()
                    && go_points.get(myPosition + 19).isExistence_stone()
                    && go_points.get(myPosition + 1).isExistence_stone()
                    && go_points.get(myPosition - 19).isExistence_stone())
            {
                return false;
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
    public void enemySearch(ArrayList<Go_point> go_points, int myPosition, int order)
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
    public void getStone(ArrayList<Go_point> go_points)
    {
        int getPosition = enemyStoneStack.pop().getStone_position();

        System.out.println("mainmainmain222" + Integer.toString(getPosition));

        deleteStack.push(getPosition);
        go_points.get(getPosition).setFirstSearch(false);
        if(upExamination(go_points, getPosition)) {
            myUpStoneSearch(go_points, getPosition - 19);
        }
        if(leftExamination(go_points, getPosition)) {
            myLeftStoneSearch(go_points, getPosition - 1);
        }
        if(downExamination(go_points, getPosition)) {
            myDownStoneSearch(go_points, getPosition + 19);
        }
        if(rightExamination(go_points, getPosition)) {
            myRightStoneSearch(go_points, getPosition + 1);
        }

        System.out.println("gd");
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
    public void myUpStoneSearch(ArrayList<Go_point> go_points, int position)
    {
        if(go_points.get(position).isFirstSearch()) {
            try {
                if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() == order) // 착점한 돌 위에
                {
                    go_points.get(position + 19).setUpStoneSame(1);

                    go_points.get(position).setFirstSearch(false);
                    deleteStack.push(position);
                    if(upExamination(go_points, position)) {
                        myUpStoneSearch(go_points, position - 19);
                    }
                    if(leftExamination(go_points, position)) {
                        myLeftStoneSearch(go_points, position - 1);
                    }
                    if(downExamination(go_points, position)) {
                        myDownStoneSearch(go_points, position + 19);
                    }
                    if(rightExamination(go_points, position)) {
                        myRightStoneSearch(go_points, position + 1);
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

    public void myLeftStoneSearch(ArrayList<Go_point> go_points, int position)
    {
        if(go_points.get(position).isFirstSearch()) {
            try {
                if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() == order) // 착점한 돌 위에
                {
                    go_points.get(position + 1).setLeftStoneSame(1);

                    go_points.get(position).setFirstSearch(false);
                    deleteStack.push(position);
                    if(upExamination(go_points, position)) {
                        myUpStoneSearch(go_points, position - 19);
                    }
                    if(leftExamination(go_points, position)) {
                        myLeftStoneSearch(go_points, position - 1);
                    }
                    if(downExamination(go_points, position)) {
                        myDownStoneSearch(go_points, position + 19);
                    }
                    if(rightExamination(go_points, position)) {
                        myRightStoneSearch(go_points, position + 1);
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

    public void myDownStoneSearch(ArrayList<Go_point> go_points, int position)
    {
        if(go_points.get(position).isFirstSearch()) {
            try {
                if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() == order) // 착점한 돌 위에
                {
                    go_points.get(position - 19).setDownStoneSame(1);

                    go_points.get(position).setFirstSearch(false);
                    deleteStack.push(position);
                    if(upExamination(go_points, position)) {
                        myUpStoneSearch(go_points, position - 19);
                    }
                    if(leftExamination(go_points, position)) {
                        myLeftStoneSearch(go_points, position - 1);
                    }
                    if(downExamination(go_points, position)) {
                        myDownStoneSearch(go_points, position + 19);
                    }
                    if(rightExamination(go_points, position)) {
                        myRightStoneSearch(go_points, position + 1);
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

    public void myRightStoneSearch(ArrayList<Go_point> go_points, int position)
    {
        if(go_points.get(position).isFirstSearch()) {
            try {
                if (go_points.get(position).isExistence_stone() && go_points.get(position).getStone_color() == order) // 착점한 돌 위에
                {
                    go_points.get(position - 1).setRightStoneSame(1);

                    go_points.get(position).setFirstSearch(false);
                    deleteStack.push(position);
                    if(upExamination(go_points, position)) {
                        myUpStoneSearch(go_points, position - 19);
                    }
                    if(leftExamination(go_points, position)) {
                        myLeftStoneSearch(go_points, position - 1);
                    }
                    if(downExamination(go_points, position)) {
                        myDownStoneSearch(go_points, position + 19);
                    }
                    if(rightExamination(go_points, position)) {
                        myRightStoneSearch(go_points, position + 1);
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
    public void setIcon()
    {
        for(int i = 0; i < 19*19; i++)
        {
            image[i] = R.drawable.square_icon1;
        }

        image[0] = R.drawable.left_top1;
        image[18] = R.drawable.right_top1;
        image[342] = R.drawable.left_bottom1;
        image[360] = R.drawable.right_bottom1;

        for(int i = 19; i <= 323; i += 19)
        {
            image[i] = R.drawable.left_icon;
        }

        for(int i = 1; i <= 17; i++)
        {
            image[i] = R.drawable.top_icon;
        }

        for(int i = 37; i <= 341; i += 19)
        {
            image[i] = R.drawable.right_icon;
        }

        for(int i = 343; i <= 359; i++)
        {
            image[i] = R.drawable.bottom_icon;
        }
    }

    // 바둑판을 클릭해서 돌을 놓는 함수
    // image[] 배열에 흑돌, 백돌이 놓여진 사진으로 업데이트 해준다
    public void setStone(int position)
    {
        if(order == 1) {
            if (position == 0) {
                image[position] = R.drawable.left_top_black;
            } else if (position == 18) {
                image[position] = R.drawable.right_top_black;
            } else if (position == 342) {
                image[position] = R.drawable.left_bottom_black;
            } else if (position == 360) {
                image[position] = R.drawable.right_bottom_black;
            } else if (position % 19 == 0 && position != 0 && position != 342) {
                image[position] = R.drawable.left_black;
            } else if (position >= 1 && position <= 17) {
                image[position] = R.drawable.top_black;
            } else if (position % 19 == 18 && position != 18 && position != 360) {
                image[position] = R.drawable.right_black;
            } else if (position >= 343 && position <= 359) {
                image[position] = R.drawable.bottom_black;
            } else {
                image[position] = R.drawable.square_black;
            }
        }
        else if(order == 2) {
            if (position == 0) {
                image[position] = R.drawable.left_top_white;
            } else if (position == 18) {
                image[position] = R.drawable.right_top_white;
            } else if (position == 342) {
                image[position] = R.drawable.left_bottom_white;
            } else if (position == 360) {
                image[position] = R.drawable.right_bottom_white;
            } else if (position % 19 == 0 && position != 0 && position != 342) {
                image[position] = R.drawable.left_white;
            } else if (position >= 1 && position <= 17) {
                image[position] = R.drawable.top_white;
            } else if (position % 19 == 18 && position != 18 && position != 360) {
                image[position] = R.drawable.right_white;
            } else if (position >= 343 && position <= 359) {
                image[position] = R.drawable.bottom_white;
            } else {
                image[position] = R.drawable.square_white;
            }
        }
    }

    // 돌이 죽어서 바둑판을 다시 그릴때 사용하는 함수
    public void resetIcon(int position)
    {
        if (position == 0) {
            image[position] = R.drawable.left_top1;
        } else if (position == 18) {
            image[position] = R.drawable.right_top1;
        } else if (position == 342) {
            image[position] = R.drawable.left_bottom1;
        } else if (position == 360) {
            image[position] = R.drawable.right_bottom1;
        } else if (position % 19 == 0 && position != 0 && position != 342) {
            image[position] = R.drawable.left_icon;
        } else if (position >= 1 && position <= 17) {
            image[position] = R.drawable.top_icon;
        } else if (position % 19 == 18 && position != 18 && position != 360) {
            image[position] = R.drawable.right_icon;
        } else if (position >= 343 && position <= 359) {
            image[position] = R.drawable.bottom_icon;
        } else {
            image[position] = R.drawable.square_icon1;
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

}