package dwServer;

import java.util.HashMap;
import java.util.Map;

public class loginManage {
	//管理登录用户信息
	//使用Map数据结构来存放用户信息
	private static Map<String,userInfo>usersDataBase=new HashMap<String,userInfo>();
	//使用for循环生成用户添加数据进入usersDataBase
	static {//存储用户信息
		for(int i=1;i<11;i++) {
			userInfo uinfo=new userInfo();
			uinfo.setName("user"+i);
			uinfo.setPw("pw"+i);
			usersDataBase.put(uinfo.getName(),uinfo);
		}
	}
	//进行验证
	public static boolean listenLogin(userInfo user) {
		if(usersDataBase.containsKey(user.getName())) {
			userInfo correctUserInfo=usersDataBase.get(user.getName());
			String correctUserPw=correctUserInfo.getPW();
			if(correctUserPw.equals(user.getPW())) {
				return true;
			}
		}
		System.out.println(user.getName()+"登录失败！");
		return false;
	}
}
