package tss.information;

import javax.persistence.Column;

public class PaperResponseStruct {
    private String pid;
    private String begin;
    private String end;
    private String last;
    private String count;
    private String papername;
    private boolean isauto;
    private String[] qid;
    private String[] score;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }


    public String getBegin() { return begin; }

    public void setBegin(String begin) { this.begin = begin;}


    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }


    public String getLast(){return last;}

    public void setLast(String last){this.last = last;}


    public  String getCount(){
        return  count;
    }

    public void setCount(String count){
        this.count = count;
    }


    public String getPapername(){return papername;}

    public void setPapername(String papername){this.papername = papername;}


    public boolean getIsauto(){return isauto;}

    public void setIsauto(boolean isauto){this.isauto = isauto;}


    public String[] getQid() {return qid; }

    public void setQid(String[] qid) {this.qid = qid; }

    public String[] getScore() {return score; }

    public void setScore(String[] score) {this.score = score; }
}
