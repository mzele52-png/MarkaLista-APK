package com.marek.markalista;

public class Product {
    public String name;
    public String quantity;
    public boolean done;
    public boolean newByVoice;

    public Product(String name) {
        this.name = name;
        this.quantity = "";
        this.done = false;
        this.newByVoice = false;
    }

    public Product(String name, String quantity, boolean done, boolean newByVoice) {
        this.name = name;
        this.quantity = quantity;
        this.done = done;
        this.newByVoice = newByVoice;
    }
}
