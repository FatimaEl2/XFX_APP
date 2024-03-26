package com.paradigms.Homework2;

public class FileInfo {
    String Fname;
    String Ftype;
    String Fsize;
    public FileInfo(String name, String type, String size) {
        this.Fname = name;
        this.Ftype = type;
        this.Fsize = size;
    }
    //to string for collections
    public static String toString(FileInfo[] files) {

        String s = "";
        for (int i = 0; i < files.length; i++) {
            s += files[i].toString() + System.lineSeparator() ;
        }
        return s;
    }
    //to string for single file
    public String toString() {
        return "File Name: " + Fname + " File Type: " + Ftype + " File Size: " + Fsize ;
    }
    //getters and setters
    public String getName() {
        return Fname;
    }
    public void setName(String name) {
        this.Fname = name;
    }
    public String getType() {
        return Ftype;
    }
    public void setType(String type) {
        this.Ftype = type;
    }
    public String getSize() {
        return Fsize;
    }
    public void setSize(String size) {
        this.Fsize = size;
    }
}


