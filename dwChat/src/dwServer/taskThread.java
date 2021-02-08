package dwServer;
import chatClient.Client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class taskThread extends Thread {
	private Socket client;
	private OutputStream outputStream;
	private userInfo user;
	public int namelist=1;
//	String name = "������"+namelist;
	
	public userInfo getUser() {
		return this.user;
	}
	public taskThread(Socket Client) {
		this.client=Client;
	}
	
	
	public void shutDown()throws IOException{
		System.out.println("Closing client connecton...");
		ManagerThread.rmClient(this);
		client.close();
	}
	
	
	public void sendMessage(String message)throws IOException{
		outputStream.write(("text"+"\r\n").getBytes());
		outputStream.flush();
		message+="\r\n";
		outputStream.write(message.getBytes());
		outputStream.flush();
	}
	
	public void sendPicToClient(File filePath)throws IOException{
		
		outputStream.write(("Pic"+"\r\n").getBytes());
		outputStream.flush();
    	FileInputStream fis =  new FileInputStream(filePath);
    	DataOutputStream dos = new DataOutputStream(client.getOutputStream());
    	dos.writeLong(filePath.length());
    	dos.flush();
    	System.out.println("��ͼƬ...");
    	byte[] buf = new byte[1024];
    	long progress = 0;
		int len = 0;
		//���ļ���д���ܵ���
		System.out.println(fis.available());
		while ((len = fis.read(buf,0,buf.length)) != -1){
			dos.write(buf,0,len);
			dos.flush();
			progress+=len;
		}
		System.out.println(fis.available());
		System.out.println("ͼƬ����ɹ�");
	}
	
	public void sendFileToClient(File filePath)throws IOException{
		//�����ļ�ǰ׺
				outputStream.write(("file"+"\r\n").getBytes());
				outputStream.flush();
				//�����ļ���׺��
				int lastIndexOf = filePath.getName().lastIndexOf(".");
		    	String suffix = filePath.getName().substring(lastIndexOf);
				outputStream.write((suffix+"\r\n").getBytes());
				outputStream.flush();
				//�����ļ�
		    	FileInputStream fis =  new FileInputStream(filePath);
		    	DataOutputStream dos = new DataOutputStream(outputStream);
		    	dos.writeLong(filePath.length());
		    	dos.flush();
		    	System.out.println("���ļ�...");
		    	byte[] buf = new byte[1024];
		    	long progress = 0;
				int len = 0;
				//���ļ���д���ܵ���	
				while ((len = fis.read(buf,0,buf.length)) != -1){
					dos.write(buf,0,len);
					dos.flush();
					progress += len;
				}
				System.out.println("�ļ��ѳɹ�ת����ÿ���ͻ���");
	}
	
	public void chatProcess()throws IOException{
				
		InputStream ins=client.getInputStream();
		outputStream=client.getOutputStream();
		System.out.println(ins.available());
		
		BufferedReader brd=new BufferedReader(new InputStreamReader(ins));
	
		
		System.out.println(ins.available());
		sendMessage("��ӭ�������죬����������û�����");
		String userName=brd.readLine();
		System.out.println(userName);
		sendMessage("���������룺");
		String pwd=brd.readLine();
		System.out.println(pwd);
		user=new userInfo();
		user.setName(userName);
		user.setPw(pwd);
		
		//�������ݿ⣬��֤�û��Ƿ����
		boolean loginState=loginManage.listenLogin(user);
		if(!loginState) {
			//�������������˺���ر�
			sendMessage("��������˺���������");
			this.shutDown();
			return;
		}
		
		boolean loginCheck=ManagerThread.checkIsExist(this);
		if(!loginCheck) {
			sendMessage("��������˺��Ѿ����ڵ�¼״̬��");
			this.shutDown();
			return;
		}
		
		ManagerThread.addClient(this);//��֤�ɹ���������û��������������
		
		String input=brd.readLine();//��ȡ�ͻ��˷�������Ϣ
		
		System.out.print(ins.available());
		
		
		while(!input.equals("bye")) {
			
			if(input.equals("")){
				input=brd.readLine();
				continue;
				}
			
			if(input.equals("text")){
				input=brd.readLine();
				if(input.equals("bye"))
				{
					break;
				}
				System.out.println("��������������:"+input);
				ManagerThread.broadcastMsg(this.user, input);
				input=brd.readLine();
				
			}else if(input.equals("pic")){
				//�������ֵĻ���ΪͼƬ�����ļ�
				
				//����������ͼƬ
				//�����ļ�����·��
				File directory = new File("E:\\serverCache");
				if(!directory.exists()){
					directory.mkdir();
				}
				DataInputStream dis = new DataInputStream(client.getInputStream());
				long fileLength = dis.readLong();
				System.out.println(fileLength);
				File imagefile = new File(directory.getAbsolutePath()+"//"+namelist+".JPG");
				FileOutputStream fos = new FileOutputStream(imagefile);
				byte[] buf=new byte[1024];				
				int len=0;
				long process = 0;
				//�ѹܵ���д���ļ���
				while((len=dis.read(buf,0,buf.length))!=-1) {
					fos.write(buf,0,len);
					fos.flush();
					process+=len;
					if(process == fileLength)
						break;
				}
				System.out.println("�����������ļ��ɹ�");
				namelist++;
				fos.close();
				//����ͼƬ��ÿ���ͻ���
				ManagerThread.broadcastPic(this.user, imagefile);
				input=brd.readLine();
				
			}else if(input.equals("file")) {
				File directory = new File("E:\\serverCache");
				if(!directory.exists()){
					directory.mkdir();
				}
				//��ȡ��׺��
				input=brd.readLine();
				String suffix = input;
				//������������ȡ����
				DataInputStream dis = new DataInputStream(client.getInputStream());
				long fileLength = dis.readLong();
				System.out.println(fileLength);
				File file = new File(directory.getAbsolutePath()+"//"+namelist+suffix);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buf=new byte[1024];				
				int len=0;
				long process = 0;
				//�ѹܵ���д���ļ���
				while((len=dis.read(buf,0,buf.length))!=-1) {
					fos.write(buf,0,len);
					fos.flush();
					process+=len;
					if(process == fileLength)
						break;
				}
				System.out.println("�����������ļ��ɹ�");
				namelist++;
				fos.close();
				ManagerThread.broadcastFile(this.user, file);
				input=brd.readLine();
			}
		}
		sendMessage("�������ߣ�");
		this.shutDown();
	}
		//ʹ�������ж��Ƿ�Ϊ������Ϣ

	
	
	public void run() {
		try {
			chatProcess();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
