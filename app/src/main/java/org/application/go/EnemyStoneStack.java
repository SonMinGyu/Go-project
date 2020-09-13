package org.application.go;

import org.application.go.Model.Go_point;

public class EnemyStoneStack {

    private int top;
    private int maxSize;
    private Go_point[] stackArray;

    // 배열 스택 생성,  스택의 최대 크기로 생성
    public EnemyStoneStack(int maxSize) {

        this.maxSize = maxSize;
        this.stackArray = new Go_point[maxSize];
        this.top = -1;
    }

    // 스택이 비어있는지 체크
    public boolean empty(){
        return (top == -1);
    }

    // 스택이 꽉찼는지 체크
    public boolean full(){
        return (top == maxSize-1);
    }

    // 스택에 item 입력
    public void push(Go_point item){

        if(full()) throw new ArrayIndexOutOfBoundsException((top+1)+">=" + maxSize);

        stackArray[++top] = item;
    }

    // 스택의 가장 위의 데이터 반환
    public Go_point peek(){

        if(empty()) throw new ArrayIndexOutOfBoundsException(top);

        return stackArray[top];
    }

    // 스택의 가장 위의 데이터 제거
    public Go_point pop(){

        Go_point item = peek();

        top--;

        return item;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }
}
