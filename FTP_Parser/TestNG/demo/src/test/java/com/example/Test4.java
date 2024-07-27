package com.example;


import org.testng.annotations.Test;

import java.io.IOException;
import java.net.UnknownHostException;



public class Test4 {
    private static final String IP = "127.0.0.1";
    private static final String LOGIN = "pop";
    private static final String PASS = "1234";
    
    //Клиент отправляет данные для авторизации и если успешно - отключается через ввод цифры 5 
    //Метод проверки работы активного режими
  
    //Получение информации о стуеднте по id
    @Test(timeOut = 200)
    public void GetStudentInfoTest() throws NumberFormatException, UnknownHostException, IOException{
        Client cl = new Client();
        //В данном примере, используем id 2 для поска
        cl.SetIdS("2");
        cl.ConnClient(LOGIN,PASS, IP,"1", "2");
        System.out.println(cl.ans);
    }

  
}
