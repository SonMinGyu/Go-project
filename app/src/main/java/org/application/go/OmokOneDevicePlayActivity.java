package org.application.go;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.application.go.Model.Go_point;
import org.application.go.Model.Omok_Go_point;

import java.util.ArrayList;
import java.util.List;

public class OmokOneDevicePlayActivity extends AppCompatActivity {

    GridView omokGridView;
    OmokOneDevicePlayActivity.omokMyAdapter omokAdapter;

    List<Omok_Go_point> omok_go_points;
    int omokImage[] = new int[19*19];

    Button omokStartButton;
    TextView omokTurnText;

    boolean omokIsStart = false;
    int omokOrder = 1;

    int verticalCount = 0;
    int horizontalCount = 0;
    int leftDiagonalCount = 0;
    int rightDiagonalCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omok_one_device_play);

        omokStartButton = (Button) findViewById(R.id.omok_startButton);
        omokTurnText = (TextView) findViewById(R.id.omok_turn_textView);

        omok_go_points = new ArrayList<>();

        initArray();
        setIcon(omokImage);

        omokAdapter = new OmokOneDevicePlayActivity.omokMyAdapter(getApplicationContext(), R.layout.item_square, omokImage);
        omokGridView = (GridView) findViewById(R.id.omok_gridView);
        omokGridView.setAdapter(omokAdapter);

        omokStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                omokIsStart = true;
                omokStartButton.setClickable(false);
            }
        });
    }

    class omokMyAdapter extends BaseAdapter {
        Context context;
        int layout;
        int img[];
        LayoutInflater inf;

        public omokMyAdapter(Context context, int layout, int[] img) {
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

                    if(!omokIsStart) // 대국시작을 누르지 않으면 클릭불가(시작안됨)
                    {
                        return;
                    }

                    /*
                    deleteStack = new DeleteStack(19*19);
                    deleteStack1 = new DeleteStack(19*19);
                    liveOrDie = new DeleteStack(19*19);
                    enemyStoneStack = new EnemyStoneStack(4);

                    canPutDeleteStack = new DeleteStack(19*19);
                    canPutLiveOrDie = new DeleteStack(19*19);
                    canPutEnemyStoneStack = new EnemyStoneStack(4);
                    booleanStack = new DeleteStack(4);

                     */

                    if(omok_go_points.get(position).isExistenceStone()) // 돌이 있는 곳에 돌을 놓을 경우
                    {
                        Toast.makeText(getApplicationContext(),"돌이 있는 곳에는 놓을 수 없습니다!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(omokOrder == 1)
                    {
                        omok_go_points.get(position).setExistenceStone(true);
                        omok_go_points.get(position).setStoneColor(omokOrder);
                        omok_go_points.get(position).setPosition(position);
                        setStone(omokImage, position);
                        iv.setImageResource(omokImage[position]);
                        omokOrder = 2;
                    }
                    else if(omokOrder == 2)
                    {
                        omok_go_points.get(position).setExistenceStone(true);
                        omok_go_points.get(position).setStoneColor(omokOrder);
                        omok_go_points.get(position).setPosition(position);
                        setStone(omokImage, position);
                        iv.setImageResource(omokImage[position]);
                        omokOrder = 1;
                    }

                    upDownStoneSearch(position);
                    leftRightStoneSearch(position);
                    upleftDownrightStoneSearch(position);
                    uprightDownleftStoneSearch(position);

                    for(int i = 0; i < 19*19; i++)
                    {
                        omok_go_points.get(i).setFirstSearch(true);
                    }

                    if(omokOrder == 1) {
                        omokTurnText.setText("흑돌 턴 입니다");
                    }
                    else
                    {
                        omokTurnText.setText("백돌 턴 입니다");
                    }

                    if(verticalCount == 5 || horizontalCount == 5 || leftDiagonalCount == 5 || rightDiagonalCount == 5)
                    {
                        if(omokOrder == 2) {
                            //Toast.makeText(getApplicationContext(), "흑돌 승리하셨습니다!", Toast.LENGTH_SHORT).show();
                            final LinearLayout linearLayout = (LinearLayout) View.inflate(OmokOneDevicePlayActivity.this, R.layout.activity_black_win, null);
                            final AlertDialog.Builder customDialog = new AlertDialog.Builder(OmokOneDevicePlayActivity.this);
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
                            omokIsStart = false;
                        }
                        else
                        {
                            //Toast.makeText(getApplicationContext(), "백돌 승리하셨습니다!", Toast.LENGTH_SHORT).show();
                            final LinearLayout linearLayout = (LinearLayout) View.inflate(OmokOneDevicePlayActivity.this, R.layout.activity_white_win, null);
                            final AlertDialog.Builder customDialog = new AlertDialog.Builder(OmokOneDevicePlayActivity.this);
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
                            omokIsStart = false;
                        }
                    }

                    System.out.println("mainmainmain verti  " + Integer.toString(verticalCount));
                    System.out.println("mainmainmain hor  " + Integer.toString(horizontalCount));
                    System.out.println("mainmainmain left  " + Integer.toString(leftDiagonalCount));
                    System.out.println("mainmainmain right  " + Integer.toString(rightDiagonalCount));

                    verticalCount = 0;
                    horizontalCount = 0;
                    leftDiagonalCount = 0;
                    rightDiagonalCount = 0;
                }
            });

            return convertView;
        }
    }

    public void initArray()
    {
        for(int i = 0; i < 19 * 19; i++)
        {
            Omok_Go_point omok_go_point = new Omok_Go_point();

            omok_go_point.setExistenceStone(false);
            omok_go_point.setStoneColor(0);
            omok_go_point.setPosition(i);
            omok_go_points.add(omok_go_point);
        }
    }

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

    public void setStone(int img[], int position)
    {
        if(omokOrder == 1) {
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
        else if(omokOrder == 2) {
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

    public void upDownStoneSearch(int position)
    {
        verticalCount++;
        omok_go_points.get(position).setFirstSearch(false);
        if(upExamination(position)) {
            myUpStoneSearch(position - 19);
        }
        if(downExamination(position)) {
            myDownStoneSearch(position + 19);
        }
    }

    public void leftRightStoneSearch(int position)
    {
        horizontalCount++;
        omok_go_points.get(position).setFirstSearch(false);
        if(leftExamination(position)) {
            myLeftStoneSearch(position - 1);
        }
        if(rightExamination(position)) {
            myRightStoneSearch(position + 1);
        }
    }

    public void upleftDownrightStoneSearch(int position)
    {
        leftDiagonalCount++;
        omok_go_points.get(position).setFirstSearch(false);
        if(upExamination(position) && leftExamination(position)) {
            //myLeftStoneSearch(position - 1);
            myUpleftStoneSearch(position - 20);
        }
        if(downExamination(position) && rightExamination(position)) {
            //myRightStoneSearch(position + 1);
            myDownrightStoneSearch(position + 20);
        }
    }

    public void uprightDownleftStoneSearch(int position)
    {
        rightDiagonalCount++;
        omok_go_points.get(position).setFirstSearch(false);
        if(upExamination(position) && rightExamination(position)) {
            myUprightStoneSearch(position - 18);
        }
        if(downExamination(position) && leftExamination(position)) {
            myDownleftStoneSearch(position + 18);
        }
    }

    public void myDownleftStoneSearch(int position)
    {
        if(omok_go_points.get(position).isFirstSearch()) {
            try {
                if (omok_go_points.get(position).isExistenceStone() && omok_go_points.get(position).getStoneColor() != omokOrder) // 착점한 돌 위에
                {
                    rightDiagonalCount++;
                    omok_go_points.get(position).setFirstSearch(false);
                    if(downExamination(position) && leftExamination(position)) {
                        myDownleftStoneSearch(position + 18);
                    }
                }
                else if (omok_go_points.get(position).isExistenceStone() && omok_go_points.get(position).getStoneColor() == omokOrder)
                {
                    ;
                }
                else if (!omok_go_points.get(position).isExistenceStone())
                {
                    ;
                }

            } catch (Exception e) {
                //throw e; //최상위 클래스가 아니라면 무조건 던져주자
            }
        }
    }

    public void myUprightStoneSearch(int position)
    {
        if(omok_go_points.get(position).isFirstSearch()) {
            try {
                if (omok_go_points.get(position).isExistenceStone() && omok_go_points.get(position).getStoneColor() != omokOrder) // 착점한 돌 위에
                {
                    rightDiagonalCount++;
                    omok_go_points.get(position).setFirstSearch(false);
                    if(upExamination(position) && rightExamination(position)) {
                        myUprightStoneSearch(position - 18);
                    }
                }
                else if (omok_go_points.get(position).isExistenceStone() && omok_go_points.get(position).getStoneColor() == omokOrder)
                {
                    ;
                }
                else if (!omok_go_points.get(position).isExistenceStone())
                {
                    ;
                }

            } catch (Exception e) {
                //throw e; //최상위 클래스가 아니라면 무조건 던져주자
            }
        }
    }

    public void myDownrightStoneSearch(int position)
    {
        if(omok_go_points.get(position).isFirstSearch()) {
            try {
                if (omok_go_points.get(position).isExistenceStone() && omok_go_points.get(position).getStoneColor() != omokOrder) // 착점한 돌 위에
                {
                    leftDiagonalCount++;
                    omok_go_points.get(position).setFirstSearch(false);
                    if(downExamination(position) && rightExamination(position)) {
                        myDownrightStoneSearch(position + 20);
                    }
                }
                else if (omok_go_points.get(position).isExistenceStone() && omok_go_points.get(position).getStoneColor() == omokOrder)
                {
                    ;
                }
                else if (!omok_go_points.get(position).isExistenceStone())
                {
                    ;
                }

            } catch (Exception e) {
                //throw e; //최상위 클래스가 아니라면 무조건 던져주자
            }
        }
    }

    public void myUpleftStoneSearch(int position)
    {
        if(omok_go_points.get(position).isFirstSearch()) {
            try {
                if (omok_go_points.get(position).isExistenceStone() && omok_go_points.get(position).getStoneColor() != omokOrder) // 착점한 돌 위에
                {
                    leftDiagonalCount++;
                    omok_go_points.get(position).setFirstSearch(false);
                    if(upExamination(position) && leftExamination(position)) {
                        myUpleftStoneSearch(position - 20);
                    }
                }
                else if (omok_go_points.get(position).isExistenceStone() && omok_go_points.get(position).getStoneColor() == omokOrder)
                {
                    ;
                }
                else if (!omok_go_points.get(position).isExistenceStone())
                {
                    ;
                }

            } catch (Exception e) {
                //throw e; //최상위 클래스가 아니라면 무조건 던져주자
            }
        }
    }

    public void myUpStoneSearch(int position)
    {
        if(omok_go_points.get(position).isFirstSearch()) {
            try {
                if (omok_go_points.get(position).isExistenceStone() && omok_go_points.get(position).getStoneColor() != omokOrder) // 착점한 돌 위에
                {
                    verticalCount++;
                    omok_go_points.get(position).setFirstSearch(false);
                    if(upExamination(position)) {
                        myUpStoneSearch(position - 19);
                    }
                    /*
                    if(leftExamination(position)) {
                        myLeftStoneSearch(position - 1);
                    }
                    if(downExamination(position)) {
                        myDownStoneSearch(position + 19);
                    }
                    if(rightExamination(position)) {
                        myRightStoneSearch(position + 1);
                    }

                     */
                }
                else if (omok_go_points.get(position).isExistenceStone() && omok_go_points.get(position).getStoneColor() == omokOrder)
                {
                    ;
                }
                else if (!omok_go_points.get(position).isExistenceStone())
                {
                    ;
                }

            } catch (Exception e) {
                //throw e; //최상위 클래스가 아니라면 무조건 던져주자
            }
        }
    }

    public void myLeftStoneSearch(int position)
    {
        if(omok_go_points.get(position).isFirstSearch()) {
            try {
                if (omok_go_points.get(position).isExistenceStone() && omok_go_points.get(position).getStoneColor() != omokOrder) // 착점한 돌 위에
                {
                    horizontalCount++;
                    omok_go_points.get(position).setFirstSearch(false);
                    if(leftExamination(position)) {
                        myLeftStoneSearch(position - 1);
                    }
                }
                else if (omok_go_points.get(position).isExistenceStone() && omok_go_points.get(position).getStoneColor() == omokOrder)
                {
                    ;
                }
                else if (!omok_go_points.get(position).isExistenceStone())
                {
                    ;
                }

            } catch (Exception e) {
                //throw e; //최상위 클래스가 아니라면 무조건 던져주자
            }
        }
    }

    public void myDownStoneSearch(int position)
    {
        if(omok_go_points.get(position).isFirstSearch()) {
            try {
                if (omok_go_points.get(position).isExistenceStone() && omok_go_points.get(position).getStoneColor() != omokOrder) // 착점한 돌 위에
                {
                    verticalCount++;
                    omok_go_points.get(position).setFirstSearch(false);
                    if(downExamination(position)) {
                        myDownStoneSearch(position + 19);
                    }
                }
                else if (omok_go_points.get(position).isExistenceStone() && omok_go_points.get(position).getStoneColor() == omokOrder)
                {
                    ;
                }
                else if (!omok_go_points.get(position).isExistenceStone())
                {
                    ;
                }

            } catch (Exception e) {
                //throw e; //최상위 클래스가 아니라면 무조건 던져주자
            }
        }
    }

    public void myRightStoneSearch(int position)
    {
        if(omok_go_points.get(position).isFirstSearch()) {
            try {
                if (omok_go_points.get(position).isExistenceStone() && omok_go_points.get(position).getStoneColor() != omokOrder) // 착점한 돌 위에
                {
                    horizontalCount++;
                    omok_go_points.get(position).setFirstSearch(false);
                    if(rightExamination(position)) {
                        myRightStoneSearch(position + 1);
                    }
                }
                else if (omok_go_points.get(position).isExistenceStone() && omok_go_points.get(position).getStoneColor() == omokOrder)
                {
                    ;
                }
                else if (!omok_go_points.get(position).isExistenceStone())
                {
                    ;
                }

            } catch (Exception e) {
                //throw e; //최상위 클래스가 아니라면 무조건 던져주자
            }
        }
    }

    public boolean upExamination(int currentPosition)
    {
        if(currentPosition >= 0 && currentPosition <= 18)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean leftExamination(int currentPosition)
    {
        if(currentPosition % 19 == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean downExamination(int currentPosition)
    {
        if(currentPosition >= 342 && currentPosition <= 360)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean rightExamination(int currentPosition)
    {
        if(currentPosition % 19 == 18)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}