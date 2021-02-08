package dwServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ManagerThread {
	
	//使用ArrayList保存线程处理的对象
	private static ArrayList<taskThread> taskThreadList=new ArrayList<taskThread>();
	
	private ManagerThread() {}
	
	public static void broadcastMsg(userInfo sender,String message)throws IOException{
		message=sender.getName()+":"+message;//获取用户名
		for(int i=0;i<taskThreadList.size();i++) {
			taskThread sThread=taskThreadList.get(i);
			sThread.sendMessage(message);
			
		}
	}
	
	public static void broadcastPic(userInfo sender,File file)throws IOException {
		String response=sender.getName()+":发送图片";
		for(int i=0;i<taskThreadList.size();i++) {
			taskThread sThread=taskThreadList.get(i);
			sThread.sendPicToClient(file);
		}
	}
	
	public static void broadcastFile(userInfo sender,File file)throws IOException {
		String response=sender.getName()+":发送文件";
		for(int i=0;i<taskThreadList.size();i++) {
			taskThread sThread=taskThreadList.get(i);
			sThread.sendFileToClient(file);
		}
	}
	
	//将客户申请创建的线程任务加入队列
	public static void addClient(taskThread sThread)throws IOException{
		taskThreadList.add(sThread);//将线程加入队列
		broadcastMsg(sThread.getUser(),"用户上线");	
	}
	
	public static void rmClient(taskThread sThread)throws IOException{
		broadcastMsg(sThread.getUser(),"用户已下线");
		taskThreadList.remove(sThread);
	}
	
	public static boolean checkIsExist(taskThread sThread)throws IOException{
		for(int i=0;i<taskThreadList.size();i++) {
			if(sThread.getUser().getName().equals(taskThreadList.get(i).getUser().getName())) {
				return false;
			}
		}
		return true;//判断当前要登录的用户是否已经登录
	}
}
