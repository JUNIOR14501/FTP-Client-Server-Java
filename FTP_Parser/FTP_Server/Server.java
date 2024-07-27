import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class Server{
	private ServerSocket ss;
	private Socket s;
	private Socket dataSocket;
	private static Scanner ins;

	private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет

	private static String logServer;
    private static String passServer;

	private static String ip;
    private static String log;
    private static String pass;

	private static String status = "0";
	private static HashMap<Integer, String> studentsHash;

	//Управляющий порт
    private static final String CMD_PORT =  "21";
    //Порт передачи данных
    private static final String DATA_PORT =  "20";
	//Дополнительный порт для пассивного режима
	private static final String PASSIVE_PORT =  "2024";
	public Server(){
			try {

				status = auth();
				if (s.isConnected()){
					System.out.println("221 Пользователь подключился.");
				}
				
				while (status.equals("0")){
					out.write(status + "\n");
					out.flush();
					System.out.println(" 400 Пользователь не прошел проверку пароля и логина.");
					if (s != null){
						s.close();
					}
					if (ss != null){
						ss.close();
					}
					if (dataSocket  != null){
	
						dataSocket.close();
					}
					status = auth();
					System.out.println("Статус: " + status);
					
				}
				
				out.write(status + "\n");
				out.flush();
				System.out.println("223 Пользователь прошел проверку пароля и логина.");
				
				//sendFile();

				//Заполняем Map из файла
				studentsHash = JsonToMap();
				System.out.println(studentsHash.toString());
				
				//Смотрим какое действие хочет юзер
				String ch = "0";
				String ans;
				while (!ch.equals("5")){
					ch = in.readLine();
					System.out.println(studentsHash.toString());
					switch (ch) {
						case "1":
							ans = getStudents();

							out.write(ans + "\n");
							out.flush();
							break;
						case "2":
							String idForSearch = in.readLine();
							ans = getNameStu(idForSearch);

							out.write(ans + "\n");
							out.flush();
							break;
						case "3":
							String name = in.readLine();
							ans = addStu(name);

							out.write(ans + "\n");
							out.flush();
							break;
						case "4":
							String idForDel = in.readLine();
							ans = delStu(idForDel);

							out.write(ans + "\n");
							out.flush();
							break;
						case "5":
							ss.close();
							break;
						default:
							out.write("404 Пункт отсутствует в меню" + "\n");
							out.flush();
							break;
					}
				}
				
				ss.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	private String delStu(String idForDel) {
		try {

			if (studentsHash.containsKey(Integer.parseInt(idForDel))){
				studentsHash.remove(Integer.parseInt(idForDel));
				return "211 Студент с id: " + idForDel + " успешно удален";
			} else{
				return "412 Студент с id: " + idForDel + " не существует";
			}

		} catch (Exception e) {
			return "412 Студент с id: " + idForDel + " не существует";
		}
		
	}

	private String addStu(String name) {
		int id = (int) (Math.random() * 60000);
		if (studentsHash.containsKey(id)){
			id = (int) (Math.random() * 6000000) + 60001;
		}
		studentsHash.put(id,name);
		return "258 Студент с именем: " + name + " добавлен с id: "  + id;
	}

	private String getNameStu(String idForSearch) {
		try{

			if (studentsHash.containsKey(Integer.parseInt(idForSearch))){
				return "291 Студент с id "+ idForSearch + ": " + studentsHash.get(Integer.parseInt(idForSearch));
			} else {
				return "404 Студента с id " + idForSearch +" не существует";
			}

		} catch (Exception e) {
			return "404 Студент с id: " + idForSearch + " не существует";
		}
	}

	private HashMap<Integer, String> JsonToMap() throws FileNotFoundException {
		HashMap<Integer, String> stu = new HashMap<>();
		Scanner scanner = new Scanner(new File("data.json"));
		while (scanner.hasNextLine()) {
			String s = scanner.nextLine();
			if ((!s.contains("}")) && !(s.contains("{")) 
			&& !(s.contains("[")) && !(s.contains("]"))){
                s.replace("\"", "");
                String[] v = s.split(":");
                String id = v[1].replace(",", "").replaceAll(" ", "");

				s = scanner.nextLine();
				s.replace("\"", "");
				String[] c = s.split(":");
				String name = c[1].replaceAll("\"", "").replaceAll(" ", "");
                
                //System.out.println(id + name);
				stu.put(Integer.parseInt(id), name);
			}
		}
		scanner.close();
		return stu;
	}

	private String getStudents() {
		ArrayList<String> values = new ArrayList<>(studentsHash.values());
		Collections.sort(values);
		String s = "257 Список студентов по именам (в алфавитном порядке): " + values.toString();
       	return s;
	}

	private String auth() throws IOException {
		ss = new ServerSocket(Integer.parseInt(CMD_PORT));
		System.out.println("|Log| Сервер прослушивает порт " + CMD_PORT + ".");

		
		//1 
		s = ss.accept();

		InetAddress initAdr = s.getInetAddress();

		System.out.println("|Log| Приняли запрос на подключение.");
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

		String ftpMode = in.readLine();
		if (ftpMode.equals("PASV")){
			System.out.println("|Log| Клиент работает в пассивном режиме.");
			out.write(PASSIVE_PORT + "\n");
			out.flush();
			System.out.println("|Log| Передали порт " + PASSIVE_PORT +" сервера для подключения клиента.");
			
			//Закрываем первое соединение для PASV
			ss.close();
			s.close();
			//Закрываем Второе соединение на PASSIVE_PORT порту
			ss = new ServerSocket(Integer.parseInt(PASSIVE_PORT));
			System.out.println("|Log| Сервер прослушивает порт " + PASSIVE_PORT + ".");
			s = ss.accept();
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			out.write("ACK" + "\n");
			out.flush();

			System.out.println("|Log| Клиент успешно подключился к серверу.");

		} else{
			System.out.println("|Log| Клиент таботает в активном режиме.");
			String dataPortClient;
			dataPortClient = in.readLine();
			System.out.println("|Log| Приняли порт данных клиента: " + dataPortClient);

			//2
			out.write("ACK" + "\n");
			out.flush();
			System.out.println("|Log| Отправили подтверждение клиенту, пытаемся установить соединение с портом " + dataPortClient + " клиента.");
			
			//3
			dataSocket = new Socket(initAdr, Integer.parseInt(dataPortClient));
			in = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()));
			if (dataSocket.isConnected()){
				System.out.println("|Log| Соединение с клиентом "+ initAdr +" успешно установлено на порту " + dataPortClient + ".");
			} else {
				System.out.println("|Log| Соединение с клиентом не установлено.");
			}
		}

		


		log = in.readLine();
		System.out.println("Логин пользователя: " + log);

		pass = in.readLine();
		System.out.println("Пароль пользователя: " +pass);

		ip = in.readLine();
		System.out.println("Пользователь подключился на ip:  " +ip);


		if ((logServer.equals(log)) && (passServer.equals(pass))){
			return "1";
		} else {
			return "0";
		}
	}

	public void sendFile() {
		byte b[] = new byte[2002]; 
			
		try {
			FileInputStream fi;
			OutputStream os;
			fi = new FileInputStream("data.json");
			
			int countB = 0;
			int count;
			while((count=fi.read(b))!=-1){
				
				for(int i=0; i<count;i++){
				countB++;
				}
			}   

			byte a[] = new byte[countB]; 
			
			for (int i = 0; i< countB; i++){
				a[i] = b[i];
			}
			
			os = s.getOutputStream();
			os.write(a, 0, a.length);
			
			fi.close();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String args[]) throws Exception{
		ins = new Scanner(System.in);
        System.out.print("Установите логин для подключения: ");
        logServer = ins.nextLine();
		System.out.print("Установите пароль для подключения: ");
        passServer = ins.nextLine();
		System.out.print("|Log| Для доступа к серверу, используйте введенные логин и пароль. ");
		new Server();
	}
}