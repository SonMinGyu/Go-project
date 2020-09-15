package org.application.go;

public class Queue {

    // 큐 배열은 front와 rear 그리고 maxSize를 가진다.
    private int front;
    private int rear;
    private int maxSize;
    private int[] queueArray;

    // 큐 배열 생성
    public Queue(int maxSize){

        this.front = 0;
        this.rear = -1;
        this.maxSize = maxSize;
        this.queueArray = new int[maxSize];
    }

    public int getFront() {
        return front;
    }

    public void setFront(int front) {
        this.front = front;
    }

    public int getRear() {
        return rear;
    }

    public void setRear(int rear) {
        this.rear = rear;
    }

    // 큐가 비어있는지 확인
    public boolean empty(){
        return (front == rear+1);
    }

    // 큐가 꽉 찼는지 확인
    public boolean full(){
        return (rear == maxSize-1);
    }

    // 큐 rear에 데이터 등록
    public void insert(int item){

        if(full()) throw new ArrayIndexOutOfBoundsException();

        queueArray[++rear] = item;
    }

    // 큐에서 front 데이터 조회
    public int peek(){

        if(empty()) throw new ArrayIndexOutOfBoundsException();

        return queueArray[front];
    }

    // 큐에서 front 데이터 제거
    public int remove(){

        int item = peek();
        front++;
        return item;
    }
}
