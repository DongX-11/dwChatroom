package dwServer;

import java.util.HashMap;
import java.util.Map;

public class loginManage {
	//�����¼�û���Ϣ
	//ʹ��Map���ݽṹ������û���Ϣ
	private static Map<String,userInfo>usersDataBase=new HashMap<String,userInfo>();
	//ʹ��forѭ�������û�������ݽ���usersDataBase
	static {//�洢�û���Ϣ
		for(int i=1;i<11;i++) {
			userInfo uinfo=new userInfo();
			uinfo.setName("user"+i);
			uinfo.setPw("pw"+i);
			usersDataBase.put(uinfo.getName(),uinfo);
		}
	}
	//������֤
	public static boolean listenLogin(userInfo user) {
		if(usersDataBase.containsKey(user.getName())) {
			userInfo correctUserInfo=usersDataBase.get(user.getName());
			String correctUserPw=correctUserInfo.getPW();
			if(correctUserPw.equals(user.getPW())) {
				return true;
			}
		}
		System.out.println(user.getName()+"��¼ʧ�ܣ�");
		return false;
	}
}
