package org.application.go.Model;

import java.util.ArrayList;

public class GameModel {
    private String gameTitle;
    private String gameType;
    private String hostUid;
    private String participantUid;
    private int hostColor; // hostColor와 participantColor는 기력이 같은 경우만 사용, 기력이 다르면 기력 낮은쪽이 흑돌
    private int participantColor;
    private String hostLevel;
    private String participantLevel;
    private int numberOfUsers;
    private String gameKey;
    private String hostName;
    private String participantName;
    private int order;
    private boolean start;
    private ArrayList<Go_point> go_points;
    private int deathBlackStones;
    private int deathWhiteStones;
    private String blackTimer;
    private String whiteTimer;
    private boolean finish;

    public boolean getFinish() {
        return finish;
    }

    public void setFinish(boolean getFinish) {
        finish = getFinish;
    }

    public String getBlackTimer() {
        return blackTimer;
    }

    public void setBlackTimer(String blackTimer) {
        this.blackTimer = blackTimer;
    }

    public String getWhiteTimer() {
        return whiteTimer;
    }

    public void setWhiteTimer(String whiteTimer) {
        this.whiteTimer = whiteTimer;
    }

    public int getDeathBlackStones() {
        return deathBlackStones;
    }

    public void setDeathBlackStones(int deathBlackStones) {
        this.deathBlackStones = deathBlackStones;
    }

    public int getDeathWhiteStones() {
        return deathWhiteStones;
    }

    public void setDeathWhiteStones(int deathWhiteStones) {
        this.deathWhiteStones = deathWhiteStones;
    }

    public ArrayList<Go_point> getGo_points() {
        return go_points;
    }

    public void setGo_points(ArrayList<Go_point> go_points) {
        this.go_points = go_points;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getParticipantLevel() {
        return participantLevel;
    }

    public void setParticipantLevel(String participantLevel) {
        this.participantLevel = participantLevel;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

    public String getHostLevel() {
        return hostLevel;
    }

    public void setHostLevel(String hostLevel) {
        this.hostLevel = hostLevel;
    }

    public String getGameKey() {
        return gameKey;
    }

    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getHostUid() {
        return hostUid;
    }

    public void setHostUid(String hostUid) {
        this.hostUid = hostUid;
    }

    public String getParticipantUid() {
        return participantUid;
    }

    public void setParticipantUid(String participantUid) {
        this.participantUid = participantUid;
    }

    public int getHostColor() {
        return hostColor;
    }

    public void setHostColor(int hostColor) {
        this.hostColor = hostColor;
    }

    public int getParticipantColor() {
        return participantColor;
    }

    public void setParticipantColor(int participantColor) {
        this.participantColor = participantColor;
    }
}
