package com.javacook.parfehh.util.string;

/**
 * Created by vollmer on 12.09.16.
 */
public class NamerMain {

    public static void main(String[] args) {
        Namer namer = new Namer();
        System.out.println(namer.createUniqueName("Ich bin 38 Jahre alt."));
        System.out.println(namer.createUniqueName("Ich bin 38 Jahre alt."));
        System.out.println(namer.createUniqueName("Ich bin 38 Jahre alt und dieser Satz ist lang."));
        System.out.println(namer.createUniqueName("Ich bin 38 Jahre alt und dieser Satz ist lang."));
        System.out.println(namer.createUniqueName("Ich bin 38 Jahre alt und dieser Satz ist länger."));
    }

}