import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private ServerSocket sss;
    private Socket dataSocket;

    private Socket cmdSocket;

    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет
    private static Scanner ins;

    private static String ip;
    private static String log;
    private static String pass;

    private static String answerConn = "0";

    //CONNECT_MODE = true --- Пассивный режим
    //CONNECT_MODE = false --- Активный режим
    private static Boolean connMode;

    //Управляющий порт
    private static final String CMD_PORT =  "1026";
    //Порт передачи данных
    private static final String DATA_PORT =  "1027";


    public void connClient() {
        try{
            auth();
            while (answerConn.equals("0")){
                System.out.println("495 Подключение не удалось. Внимательно проверьте данные для входа. \n");
                if (cmdSocket != null){
                    cmdSocket.close();
                }
                if (sss != null){
                    sss.close();
                }
				if (dataSocket != null){
                    dataSocket.close();
                }

                auth();

            }
            //ins.close();
            
            //getFile();
            //System.out.println("Файл скачан успешно.");

            System.out.flush();
            String ch = "0";
            while (!ch.equals("5")){

                ch = menu();
                out.write(ch + "\n");
                out.flush();

                if (ch.equals("2")){
                    System.out.println("Введите id для поиска: ");
                    String id = ins.nextLine();
                    out.write(id + "\n");
                    out.flush();
                }

                if (ch.equals("3")){
                    System.out.println("Введите имя для студента: ");
                    String name = ins.nextLine();
                    out.write(name + "\n");
                    out.flush();
                }
                
                if (ch.equals("4")){
                    System.out.println("Введите id для удаления: ");
                    String id = ins.nextLine();
                    out.write(id + "\n");
                    out.flush();
                }

                String ans = in.readLine();
                System.out.println("\n" +ans+"\n");
            }

            

            cmdSocket.close();
            in.close();
            out.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private String menu() {
        System.out.println("\nМеню");
        System.out.println("1) Получение списка студентов по имени");
        System.out.println("2) Получение информации о студенте по id");
        System.out.println("3) Добавление студента (id уникален и генерируется автоматически)");
        System.out.println("4) Удаление студента по id");
        System.out.println("5) Завершение работы");

        System.out.println("Выберите действие: ");
        String ch = ins.nextLine();
        return ch;
    }

    @SuppressWarnings("resource")
    public void auth() throws NumberFormatException, UnknownHostException, IOException{
        
        
        ins = new Scanner(System.in);
        System.out.print("Введите свой логин: ");
        log = ins.nextLine();
        
        System.out.print("Введите свой пароль: ");
        pass = ins.nextLine();
        
        System.out.print("Введите адресс подключения (Для локальной машины 127.0.0.1): ");
        ip = ins.nextLine();
        
        System.out.print("Выберите режим работы клиента (1-пассивный, 0-активный): ");
        String s = ins.nextLine();

        if (s.equals("1")) connMode = true;
        else if (s.endsWith("0")) connMode = false;
        else {
            System.out.print("|Log| Выбран режим по умолчанию");
            connMode = false;
        }

        if (connMode){
            System.out.println("|Log| Выбран пассивный режим работы клиента");
        } else{
            System.out.println("|Log| Выбран активный режим работы клиента");
        }
        
        //1 Отправили порт данных
        cmdSocket = new Socket(ip, Integer.parseInt("21"));
        in = new BufferedReader(new InputStreamReader(cmdSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(cmdSocket.getOutputStream()));
        
        //CONNECT_MODE = true --- Пассивный режим
         //CONNECT_MODE = false --- Активный режим
        if (connMode){
            out.write("PASV" + "\n");
            out.flush();
            System.out.println("|Log| Отправили команду PASV на сервер.");

            String passivePort = in.readLine();
            System.out.println("|Log| Получили порт "+ passivePort +" для подключения к серверу.");
            
            //Закрыли соединение и переподключаемся по новому порту от сервера
            cmdSocket.close();
            cmdSocket = new Socket(ip, Integer.parseInt(passivePort));


            in = new BufferedReader(new InputStreamReader(cmdSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(cmdSocket.getOutputStream()));
            in.readLine();
            System.out.println("|Log| Клиент успешно подключился к серверу по порту " + passivePort +".");
        } else{

            out.write("ACTI" + "\n");
            out.flush();

            out.write(DATA_PORT + "\n");
            out.flush();
            System.out.println("|Log| Отправили порт данных клиента: " + DATA_PORT);
            
            //2
            String ans2 = in.readLine();
            if (ans2.equals("ACK")){
                System.out.println("|Log| Сервер принял порт клиента для передачи данных.");
            } else {
                System.out.println("|Log| Сервер не принял порт клиента для передачи данных.");
            }

            //3 Прослушиваем порт данных
            sss = new ServerSocket(Integer.parseInt(DATA_PORT));
            dataSocket = sss.accept();
            System.out.println("|Log| Сервер подключился к клиенту");

            in = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()));
        }
        


        out.write(log + "\n");
        out.flush();
        //System.out.println("note: логин отправлен");
        out.write(pass + "\n");
        out.flush();
        //System.out.println("note: пароль отправлен");
        out.write(ip + "\n");
        out.flush();
        //System.out.println("note: ip отправлен");

        answerConn = in.readLine();
    }

    private void getFile() {
        try {
            InputStream is;
            FileOutputStream fs;
            byte []b = new byte[2002];
            is = dataSocket.getInputStream();
            int countB = 0;
            int count;
            while((count=is.read(b))!=-1){
            
                for(int i=0; i<count;i++){
                    countB++;
                }
            }   

            byte a[] = new byte[countB]; 

            for (int i = 0; i< countB; i++){
                a[i] = b[i];
            }

            fs = new FileOutputStream("FTP_Client\\data.json");
            fs.write(a, 0, a.length);

            is.close();
            fs.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }

    public static void main(String[] args) {
        Client cl = new Client();
        cl.connClient();
    }
} 