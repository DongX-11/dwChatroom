package dwServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class chatServer {
	
	
	private void SetServer(int port)throws IOException{
		ServerSocket server=new ServerSocket(port);
		//打印出当前创建的服务器端口号
		System.out.println("服务器已创建，端口号："+port);
		
		while(true) {
			Socket socket=server.accept();
			System.out.println("客户机新连接"+socket.getRemoteSocketAddress().toString());
			
			//创建一个线程，加入线程池
			serverExcutePool nExecutor=new serverExcutePool(50,10000);
			nExecutor.DoTask(new taskThread(socket));
		}
	}
	
	
	public static void main(String[] args)throws IOException{
		chatServer cServer=new chatServer();
		cServer.SetServer(3378);
	}
	
}
