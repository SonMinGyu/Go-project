package org.application.go.Model;

import android.widget.Button;

public class Go_point {
    boolean existence_stone;
    int stone_color; // 0이면 없는것 1면 black, 2면 white
    int stone_position;
    int upStoneSame; // 0이면 위에돌 없음, 1이면 위에돌 색같음, 2이면 색다름
    int downStoneSame;
    int leftStoneSame;
    int rightStoneSame;
    boolean firstSearch;
    boolean canPutFirstSearch;

    public Go_point()
    {
        this.existence_stone = false;
        this.stone_color = 0;
        this.stone_position = -1;
        this.upStoneSame = 0;
        this.downStoneSame = 0;
        this.leftStoneSame = 0;
        this.rightStoneSame = 0;
        this.firstSearch = true;
        this.canPutFirstSearch = true;
    }

    public boolean isCanPutFirstSearch() {
        return canPutFirstSearch;
    }

    public void setCanPutFirstSearch(boolean canPutFirstSearch) {
        this.canPutFirstSearch = canPutFirstSearch;
    }

    public boolean isFirstSearch() {
        return firstSearch;
    }

    public void setFirstSearch(boolean firstSearch) {
        this.firstSearch = firstSearch;
    }

    public boolean isExistence_stone() {
        return existence_stone;
    }

    public void setExistence_stone(boolean existence_stone) {
        this.existence_stone = existence_stone;
    }

    public int getStone_color() {
        return stone_color;
    }

    public void setStone_color(int stone_color) {
        this.stone_color = stone_color;
    }

    public int getStone_position() {
        return stone_position;
    }

    public void setStone_position(int stone_position) {
        this.stone_position = stone_position;
    }

    public int getUpStoneSame() {
        return upStoneSame;
    }

    public void setUpStoneSame(int upStoneSame) {
        this.upStoneSame = upStoneSame;
    }

    public int getDownStoneSame() {
        return downStoneSame;
    }

    public void setDownStoneSame(int downStoneSame) {
        this.downStoneSame = downStoneSame;
    }

    public int getLeftStoneSame() {
        return leftStoneSame;
    }

    public void setLeftStoneSame(int leftStoneSame) {
        this.leftStoneSame = leftStoneSame;
    }

    public int getRightStoneSame() {
        return rightStoneSame;
    }

    public void setRightStoneSame(int rightStoneSame) {
        this.rightStoneSame = rightStoneSame;
    }
}
