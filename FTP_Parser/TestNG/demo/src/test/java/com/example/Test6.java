package com.example;


import org.testng.annotations.Test;

import java.io.IOException;
import java.net.UnknownHostException;



public class Test6 {
    private static final String IP = "127.0.0.1";
    private static final String LOGIN = "pop";
    private static final String PASS = "1234";
    
    //Проверка удаления студента из списка
    @Test(timeOut = 200)
    public void DeleteStudent() throws NumberFormatException, UnknownHostException, IOException{
        Client cl = new Client();
        //В данном примере, используем id 2 для удаления
        cl.SetIdForDelete("2");
        cl.ConnClient(LOGIN,PASS, IP,"1", "4");
        System.out.println(cl.ans);
    }
}
