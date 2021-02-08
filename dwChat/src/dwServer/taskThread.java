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
//	String name = "服务器"+namelist;
	
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
    	System.out.println("传图片...");
    	byte[] buf = new byte[1024];
    	long progress = 0;
		int len = 0;
		//把文件流写到管道流
		System.out.println(fis.available());
		while ((len = fis.read(buf,0,buf.length)) != -1){
			dos.write(buf,0,len);
			dos.flush();
			progress+=len;
		}
		System.out.println(fis.available());
		System.out.println("图片传输成功");
	}
	
	public void sendFileToClient(File filePath)throws IOException{
		//传输文件前缀
				outputStream.write(("file"+"\r\n").getBytes());
				outputStream.flush();
				//传输文件后缀名
				int lastIndexOf = filePath.getName().lastIndexOf(".");
		    	String suffix = filePath.getName().substring(lastIndexOf);
				outputStream.write((suffix+"\r\n").getBytes());
				outputStream.flush();
				//传输文件
		    	FileInputStream fis =  new FileInputStream(filePath);
		    	DataOutputStream dos = new DataOutputStream(outputStream);
		    	dos.writeLong(filePath.length());
		    	dos.flush();
		    	System.out.println("传文件...");
		    	byte[] buf = new byte[1024];
		    	long progress = 0;
				int len = 0;
				//把文件流写到管道流	
				while ((len = fis.read(buf,0,buf.length)) != -1){
					dos.write(buf,0,len);
					dos.flush();
					progress += len;
				}
				System.out.println("文件已成功转发给每个客户端");
	}
	
	public void chatProcess()throws IOException{
				
		InputStream ins=client.getInputStream();
		outputStream=client.getOutputStream();
		System.out.println(ins.available());
		
		BufferedReader brd=new BufferedReader(new InputStreamReader(ins));
	
		
		System.out.println(ins.available());
		sendMessage("欢迎你来聊天，请输入你的用户名：");
		String userName=brd.readLine();
		System.out.println(userName);
		sendMessage("请输入密码：");
		String pwd=brd.readLine();
		System.out.println(pwd);
		user=new userInfo();
		user.setName(userName);
		user.setPw(pwd);
		
		//调用数据库，验证用户是否存在
		boolean loginState=loginManage.listenLogin(user);
		if(!loginState) {
			//如果不存在这个账号则关闭
			sendMessage("您输入的账号密码有误！");
			this.shutDown();
			return;
		}
		
		boolean loginCheck=ManagerThread.checkIsExist(this);
		if(!loginCheck) {
			sendMessage("您输入的账号已经处于登录状态！");
			this.shutDown();
			return;
		}
		
		ManagerThread.addClient(this);//认证成功，把这个用户加入服务器队列
		
		String input=brd.readLine();//获取客户端发来的信息
		
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
				System.out.println("服务器读到的是:"+input);
				ManagerThread.broadcastMsg(this.user, input);
				input=brd.readLine();
				
			}else if(input.equals("pic")){
				//不是文字的话则为图片或者文件
				
				//服务器接收图片
				//定义文件接收路径
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
				//把管道流写进文件流
				while((len=dis.read(buf,0,buf.length))!=-1) {
					fos.write(buf,0,len);
					fos.flush();
					process+=len;
					if(process == fileLength)
						break;
				}
				System.out.println("服务器接收文件成功");
				namelist++;
				fos.close();
				//传输图片给每个客户端
				ManagerThread.broadcastPic(this.user, imagefile);
				input=brd.readLine();
				
			}else if(input.equals("file")) {
				File directory = new File("E:\\serverCache");
				if(!directory.exists()){
					directory.mkdir();
				}
				//获取后缀名
				input=brd.readLine();
				String suffix = input;
				//创建输入流读取数据
				DataInputStream dis = new DataInputStream(client.getInputStream());
				long fileLength = dis.readLong();
				System.out.println(fileLength);
				File file = new File(directory.getAbsolutePath()+"//"+namelist+suffix);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buf=new byte[1024];				
				int len=0;
				long process = 0;
				//把管道流写进文件流
				while((len=dis.read(buf,0,buf.length))!=-1) {
					fos.write(buf,0,len);
					fos.flush();
					process+=len;
					if(process == fileLength)
						break;
				}
				System.out.println("服务器接收文件成功");
				namelist++;
				fos.close();
				ManagerThread.broadcastFile(this.user, file);
				input=brd.readLine();
			}
		}
		sendMessage("您已下线！");
		this.shutDown();
	}
		//使用正则化判断是否为文字信息

	
	
	public void run() {
		try {
			chatProcess();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
