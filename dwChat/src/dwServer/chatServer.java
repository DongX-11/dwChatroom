package dwServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class chatServer {
	
	
	private void SetServer(int port)throws IOException{
		ServerSocket server=new ServerSocket(port);
		//��ӡ����ǰ�����ķ������˿ں�
		System.out.println("�������Ѵ������˿ںţ�"+port);
		
		while(true) {
			Socket socket=server.accept();
			System.out.println("�ͻ���������"+socket.getRemoteSocketAddress().toString());
			
			//����һ���̣߳������̳߳�
			serverExcutePool nExecutor=new serverExcutePool(50,10000);
			nExecutor.DoTask(new taskThread(socket));
		}
	}
	
	
	public static void main(String[] args)throws IOException{
		chatServer cServer=new chatServer();
		cServer.SetServer(3378);
	}
	
}
