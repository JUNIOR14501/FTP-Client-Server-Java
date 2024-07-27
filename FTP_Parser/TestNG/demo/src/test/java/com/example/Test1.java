package com.example;


import org.testng.annotations.Test;

import java.io.IOException;
import java.net.UnknownHostException;



public class Test1 {
    private static final String IP = "127.0.0.1";
    private static final String LOGIN = "pop";
    private static final String PASS = "1234";
    
    //Клиент отправляет данные для авторизации и если успешно - отключается через ввод цифры 5 
    //Метод проверки работы активного режими
    @Test
    public void AuthActiveModeTest() throws NumberFormatException, UnknownHostException, IOException{
        Client cl = new Client();
        cl.ConnClient(LOGIN, PASS, IP,"1", "5");
    }
}
