package org.techtown.sit2;

public class Sit_table {
    private int sit;
    private int count;


    public Sit_table(){
        this.setsit(sit);
        this.setcount(count);
    }

    public int getsit() {
        return sit;
    }

    public int getcount() {
        return count;
    }

    public void setsit(int sit) {
        this.sit = sit;
    }

    public void setcount(int count) {
        this.count = count;
    }

    
}
