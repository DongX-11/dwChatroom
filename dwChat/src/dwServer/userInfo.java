package dwServer;

public class userInfo {
	private String userName;
	private String password;
	private String loginTime;
	private String portName;
	
	
	public void setName(String UserName) {
		this.userName=UserName;
	}
	
	public String getName() {
		return userName;
	}
	
	public void setPw(String passWord) {
		this.password=passWord;
	}
	
	public String getPW() {
		return password;
	}
}
