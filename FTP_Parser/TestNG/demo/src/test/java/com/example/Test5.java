package com.example;


import org.testng.annotations.Test;

import java.io.IOException;
import java.net.UnknownHostException;


public class Test5 {
    private static final String IP = "127.0.0.1";
    private static final String LOGIN = "pop";
    private static final String PASS = "1234";
    
   
    //Проверка добавления студента 
    @Test(timeOut = 200)
    public void AddNewStudent() throws NumberFormatException, UnknownHostException, IOException{
        Client cl = new Client();
        cl.SetName("Alex");
        cl.ConnClient(LOGIN,PASS, IP,"1", "3");
        System.out.println(cl.ans);
    }

    
}
