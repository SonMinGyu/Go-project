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

    // 계가와 승패판단을 위한 선언
    boolean emptySearch;
    boolean upStoneExistence;
    boolean leftStoneExistence;
    boolean downStoneExistence;
    boolean rightStoneExistence;
    int upStoneColor;
    int leftStoneColor;
    int downStoneColor;
    int rightStoneColor;

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
        this.emptySearch = false;
        this.upStoneExistence = false;
        this.leftStoneExistence = false;
        this.downStoneExistence = false;
        this.rightStoneExistence = false;
        this.upStoneColor = 0;
        this.leftStoneColor = 0;
        this.downStoneColor = 0;
        this.rightStoneColor = 0;
    }

    public boolean isUpStoneExistence() {
        return upStoneExistence;
    }

    public void setUpStoneExistence(boolean upStoneExistence) {
        this.upStoneExistence = upStoneExistence;
    }

    public boolean isLeftStoneExistence() {
        return leftStoneExistence;
    }

    public void setLeftStoneExistence(boolean leftStoneExistence) {
        this.leftStoneExistence = leftStoneExistence;
    }

    public boolean isDownStoneExistence() {
        return downStoneExistence;
    }

    public void setDownStoneExistence(boolean downStoneExistence) {
        this.downStoneExistence = downStoneExistence;
    }

    public boolean isRightStoneExistence() {
        return rightStoneExistence;
    }

    public void setRightStoneExistence(boolean rightStoneExistence) {
        this.rightStoneExistence = rightStoneExistence;
    }

    public int getUpStoneColor() {
        return upStoneColor;
    }

    public void setUpStoneColor(int upStoneColor) {
        this.upStoneColor = upStoneColor;
    }

    public int getLeftStoneColor() {
        return leftStoneColor;
    }

    public void setLeftStoneColor(int leftStoneColor) {
        this.leftStoneColor = leftStoneColor;
    }

    public int getDownStoneColor() {
        return downStoneColor;
    }

    public void setDownStoneColor(int downStoneColor) {
        this.downStoneColor = downStoneColor;
    }

    public int getRightStoneColor() {
        return rightStoneColor;
    }

    public void setRightStoneColor(int rightStoneColor) {
        this.rightStoneColor = rightStoneColor;
    }

    public boolean isEmptySearch() {
        return emptySearch;
    }

    public void setEmptySearch(boolean emptySearch) {
        this.emptySearch = emptySearch;
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
