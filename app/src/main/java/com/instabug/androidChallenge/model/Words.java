package com.instabug.androidChallenge.model;

public class Words {
    int id ,count;
    String word;

    public Words() {
    }

    public Words(int id, String word, int count) {
        this.id = id;
        this.word = word;
        this.count = count;
    }

    public Words(  String word, int count) {
         this.word = word;
        this.count = count;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
