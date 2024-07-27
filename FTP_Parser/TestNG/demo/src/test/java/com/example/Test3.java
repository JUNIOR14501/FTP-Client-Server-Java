package com.example;


import org.testng.annotations.Test;

import java.io.IOException;
import java.net.UnknownHostException;



public class Test3 {
    private static final String IP = "127.0.0.1";
    private static final String LOGIN = "pop";
    private static final String PASS = "1234";
    

    //Вывод списка имен студентов(Метод работает 500ms поскольку данные отправляются множество раз и сервер может выдать ошибку переполнения буфера)
    @Test(timeOut = 200)
    public void GetStudentsTest() throws NumberFormatException, UnknownHostException, IOException{
        Client cl = new Client();
        cl.ConnClient(LOGIN,PASS, IP,"1", "1");
        String ans = cl.ans;
        System.out.println(ans);
    }

   
}
