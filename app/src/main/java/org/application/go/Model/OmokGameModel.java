package org.application.go.Model;

import java.util.ArrayList;

public class OmokGameModel {
    private String gameTitle;
    private String gameType;
    private String hostUid;
    private String participantUid;
    private int hostColor; // hostColor와 participantColor는 기력이 같은 경우만 사용, 기력이 다르면 기력 낮은쪽이 흑돌
    private int participantColor;
    private int numberOfUsers;
    private String gameKey;
    private String hostName;
    private String participantName;
    private int order;
    private boolean start;
    private ArrayList<Omok_Go_point> omok_go_points;
    private boolean finish;

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

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

    public String getGameKey() {
        return gameKey;
    }

    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public ArrayList<Omok_Go_point> getOmok_go_points() {
        return omok_go_points;
    }

    public void setOmok_go_points(ArrayList<Omok_Go_point> omok_go_points) {
        this.omok_go_points = omok_go_points;
    }

    public boolean getFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }
}
