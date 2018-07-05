package com.gglabs.threads;

/**
 * Created by GG on 06/12/2017.
 */

public class VarArgsExample {

    static void display(String... value){
        System.out.println("Display method invoked:");
        for (String s : value) System.out.println(value);
    }

}
