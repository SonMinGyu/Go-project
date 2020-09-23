package org.application.go.Model;

public class Omok_Go_point {
    private boolean existenceStone;
    private int stoneColor;
    private int position;
    private boolean firstSearch;

    public boolean isFirstSearch() {
        return firstSearch;
    }

    public void setFirstSearch(boolean firstSearch) {
        this.firstSearch = firstSearch;
    }

    public boolean isExistenceStone() {
        return existenceStone;
    }

    public void setExistenceStone(boolean existenceStone) {
        this.existenceStone = existenceStone;
    }

    public int getStoneColor() {
        return stoneColor;
    }

    public void setStoneColor(int stoneColor) {
        this.stoneColor = stoneColor;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
