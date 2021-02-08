package dwServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ManagerThread {
	
	//ʹ��ArrayList�����̴߳���Ķ���
	private static ArrayList<taskThread> taskThreadList=new ArrayList<taskThread>();
	
	private ManagerThread() {}
	
	public static void broadcastMsg(userInfo sender,String message)throws IOException{
		message=sender.getName()+":"+message;//��ȡ�û���
		for(int i=0;i<taskThreadList.size();i++) {
			taskThread sThread=taskThreadList.get(i);
			sThread.sendMessage(message);
			
		}
	}
	
	public static void broadcastPic(userInfo sender,File file)throws IOException {
		String response=sender.getName()+":����ͼƬ";
		for(int i=0;i<taskThreadList.size();i++) {
			taskThread sThread=taskThreadList.get(i);
			sThread.sendPicToClient(file);
		}
	}
	
	public static void broadcastFile(userInfo sender,File file)throws IOException {
		String response=sender.getName()+":�����ļ�";
		for(int i=0;i<taskThreadList.size();i++) {
			taskThread sThread=taskThreadList.get(i);
			sThread.sendFileToClient(file);
		}
	}
	
	//���ͻ����봴�����߳�����������
	public static void addClient(taskThread sThread)throws IOException{
		taskThreadList.add(sThread);//���̼߳������
		broadcastMsg(sThread.getUser(),"�û�����");	
	}
	
	public static void rmClient(taskThread sThread)throws IOException{
		broadcastMsg(sThread.getUser(),"�û�������");
		taskThreadList.remove(sThread);
	}
	
	public static boolean checkIsExist(taskThread sThread)throws IOException{
		for(int i=0;i<taskThreadList.size();i++) {
			if(sThread.getUser().getName().equals(taskThreadList.get(i).getUser().getName())) {
				return false;
			}
		}
		return true;//�жϵ�ǰҪ��¼���û��Ƿ��Ѿ���¼
	}
}
