package chatClient;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class Client extends Thread implements ActionListener {
	
	JTextPane showArea;
	JTextPane showImage;
	JTextPane showRecord;
	
    JTextField msgText;
    JTextField imgField;
    JTextField recField;
    
    JFrame mainJframe;
    //按钮
    JButton sentBtn;
    JButton picBtn;
    JButton FileBtn;
    JButton RecordBtn;
    
    JScrollPane JSPane;
    JPanel pane;
    JPanel imgPane;
    JPanel recPane;
    Container con;
    JLabel path_label;
    JLabel img_label;
    JLabel rec_label;
    //Thread thread=null;
    //Socket connectToServer;
    //DataInputStream inFromServer;
    //DataOutputStream outToServer;
	
 
	private InputStream input;
    private OutputStream output;
    private BufferedReader bufferinput;
    private Socket socket;
    private boolean stop = false;
    private String s_input;
    public String filePath;
    
    private FileInputStream fis;
    private FileOutputStream fos;
    
    private JScrollPane imagePane;
    private PrintWriter pw;
    
    //历史记录
    private JScrollPane recordPane;
    
    
    public int imgLength;
    public int point=0;
    public int i=1;
    public Client(){
    	
    	mainJframe=new JFrame("聊天——客户端");
        con=mainJframe.getContentPane();
        //showArea=new JTextArea();
        showArea=new JTextPane();//显示文字
        showImage=new JTextPane();//显示图片
        showRecord=new JTextPane();
        showArea.setEditable(false);
        showImage.setEditable(false);
        showRecord.setEditable(false);
        
        //showArea.setLineWrap(true);
        JSPane=new JScrollPane(showArea);//在Pane中显示文字
        
        msgText=new JTextField();
        msgText.setColumns(30);
        msgText.addActionListener(this);
        sentBtn=new JButton("发送消息");
        picBtn = new JButton("发图片");
        FileBtn=new JButton("发文件");
        RecordBtn=new JButton("历史记录");
        
        sentBtn.addActionListener((ActionListener) this);
        picBtn.addActionListener((ActionListener) this);
        FileBtn.addActionListener((ActionListener)this);
        RecordBtn.addActionListener((ActionListener)this);
        
        imgField= new JTextField();
//        imgField.setBounds(200,200,30,50);
        
        //拖放文件直接获取文件路径
        imgField.setColumns(10);
        imgField.setTransferHandler(new TransferHandler()//自定义拖放的类
   	        {
   				private static final long serialVersionUID = 1L;
   	            @Override
   	            public boolean importData(JComponent comp, Transferable t) {
   	                try {
   	                    Object o = t.getTransferData(DataFlavor.javaFileListFlavor);//该对象表示将要传送的数据
   	 
   	                    String filepath = o.toString();
   	                    if (filepath.startsWith("[")) {
   	                        filepath = filepath.substring(1);//去掉"["
   	                    }
   	                    if (filepath.endsWith("]")) {
   	                        filepath = filepath.substring(0, filepath.length() - 1);
   	                    }
   	                    filePath=filepath;//获取到文件路径，用作加密函数的实参
   	                    System.out.println(filepath);
   	                    imgField.setText(filepath);//显示在屏幕上
   	                    return true;
   	                }
   	                catch (Exception e) {
   	                    e.printStackTrace();
   	                }
   	                return false;
   	            }
   	            @Override
   	            public boolean canImport(JComponent comp, DataFlavor[] flavors) {
   	                for (int i = 0; i < flavors.length; i++) {
   	                    if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
   	                        return true;
   	                    }
   	                }
   	                return false;
   	            }
   	        });
        
        
        pane=new JPanel();
        pane.setLayout(new FlowLayout());
        pane.add(msgText);
        pane.add(sentBtn);
       
        
        path_label=new JLabel("图片或文件路径");
        img_label=new JLabel("显示图片");
        rec_label=new JLabel("历史记录");
        
        JPanel leftpanel = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
//        imgPane=new JPanel();
//        imgPane.setLayout(new FlowLayout());
        imagePane = new JScrollPane(showImage);
        leftpanel.setLayout(gridBagLayout);
        leftpanel.add(picBtn, new GridBagConstraints(0, 0, 1, 1, 1, 1, 
        	    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        leftpanel.add(FileBtn, new GridBagConstraints(0, 1, 1, 1, 1, 1, 
        	    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        leftpanel.add(path_label, new GridBagConstraints(0, 2, 1, 1, 1, 1, 
        	    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        leftpanel.add(imgField, new GridBagConstraints(0, 3, 1, 1, 1, 1, 
        	    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        leftpanel.add(img_label, new GridBagConstraints(0, 4, 1, 1, 1, 1, 
        	    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        leftpanel.add(imagePane, new GridBagConstraints(0, 5, 1, 1, 10,20 , 
        	    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        
        JPanel rightpanel = new JPanel();
//        GridBagLayout gridBagLayout_right = new GridBagLayout();
        rightpanel.setLayout(gridBagLayout);
        recordPane=new JScrollPane(showRecord);
       
        rightpanel.add(RecordBtn, new GridBagConstraints(0, 0, 1, 1, 1, 1, 
        	    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        rightpanel.add(rec_label, new GridBagConstraints(0, 1, 1, 1, 1, 1, 
        	    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        rightpanel.add(recordPane, new GridBagConstraints(0, 2, 1, 1, 100, 100, 
        	    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        
        
        con.add(JSPane, BorderLayout.CENTER);
        con.add(pane, BorderLayout.SOUTH);
        con.add(leftpanel,BorderLayout.WEST);
        con.add(rightpanel,BorderLayout.EAST);
        
        mainJframe.setSize (600 ,500);
        mainJframe.setVisible (true);
        mainJframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
       
        
        
        //初始化时连接服务器
        try {
            socket=new Socket("127.0.0.1",3378);
            input=socket.getInputStream();
            output=socket.getOutputStream();
            //验证用户信息
            login();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static void main(String[] args){
        Client client=new Client();
    }

    public void login(){
        try {
            int Login=0;
            bufferinput=new BufferedReader(new InputStreamReader(input));
            String line;
            while(Login<2){
                //获取消息
                if((line=bufferinput.readLine())!=null){
                    System.out.println(line);
                	//showArea.append(line);
                }
                String textLine=bufferinput.readLine();
                //发送消息
        	    
                String str;
                if(Login==0)
                	str = JOptionPane.showInputDialog("输入用户名");
                else
                	str = JOptionPane.showInputDialog("输入密码");             
                String strName=str+"\r\n";
   
                output.write(strName.getBytes());
                output.flush();
                Login++;
                
            }
            
            chat();//chat调用
            
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(socket!=null){
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    
    //发送消息
    public void sendMsg(String msg) throws IOException {
		msg+="\r\n";
		output.write(msg.getBytes());
		output.flush();
	}
    
    //发送文件
    public void sendFile()throws IOException{
    	int lastIndexOf = filePath.lastIndexOf(".");
    	String suffix = filePath.substring(lastIndexOf);
    	//发送后缀名
    	output.write((suffix+"\r\n").getBytes());
    	output.flush();
    	//创建文件流
    	File source = new File(filePath);
    	FileInputStream fis =  new FileInputStream(source);
    	DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
    	System.out.println("文件大小为："+source.length());
    	dos.writeLong(source.length());
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
		System.out.println("文件传输成功");
    }
    
    //发送图片
    //应该输入的是图片的路径
    //返回的是
    public void sendPic() throws IOException{
    	
    	File source = new File(filePath);
    	FileInputStream fis =  new FileInputStream(source);
    	DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
    	dos.writeLong(source.length());
    	dos.flush();
    	System.out.println("传图片...");
    	byte[] buf = new byte[1024];
    	long progress = 0;
		int len = 0;
		//把文件流写到管道流	
		while ((len = fis.read(buf,0,buf.length)) != -1){
			dos.write(buf,0,len);
			dos.flush();
			progress+=len;
		}
		System.out.println("文件传输成功");
    	
		//通知服务端，数据发送完毕
    	Graphics g = showImage.getGraphics();
    	BufferedImage image = ImageIO.read(new File(filePath));
		int m = image.getHeight()/20;
		int n = image.getWidth()/20;
		g.clearRect(0, 0, n, m);// 清除绘图上下文的内容
        g.drawImage(image,0,0, m, n,imagePane);// 绘制指定大小的图像
			
    }
    
    public void actionPerformed(ActionEvent e){
    	  	
    	String cmd = e.getActionCommand();
    	
        String s=msgText.getText();
        if (cmd == "发送消息"){
           String text = msgText.getText();
           msgText.setText("");
     	   try {
     		   output.write(("text"+"\r\n").getBytes());
     		   output.flush();
     		   sendMsg(text);
		} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
		   }   
        }
        if(cmd == "发图片"){
        	try {
        		output.write(("pic"+"\r\n").getBytes());
        		output.flush();
//        		output.write((filePath).getBytes());
//        		output.flush();
				sendPic();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        if(cmd == "发文件") {
        	try {
        		output.write(("file"+"\r\n").getBytes());
        		output.flush();
        		sendFile();
        	}catch(IOException e1) {
        		e1.printStackTrace();
        	}
        }
        if(cmd=="历史记录") {
        	
            try {
            	Document rec=showRecord.getDocument();
            	File file = new File("E:\\Record.txt");//定义一个file对象，用来初始化FileReader
                FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
                BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
                StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
                String temp = "";
                while ((temp =bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
                    sb.append(temp + "\n");//将读取的字符串添加换行符后累加存放在缓存中
                    System.out.println(temp);
                }
				bReader.close();
				String record=sb.toString();
				rec.insertString(rec.getLength(), record+"\r\n",null);
			} catch (IOException | BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
    }


    
    public void chat() {
        start();//开启一个线程读取当前用户的输入
        bufferinput=new BufferedReader(new InputStreamReader(input));
        String line;
        //获取消息
        try {
            while(true){
            
         	line=bufferinput.readLine();
         	
                 if(line!=null&&line.equals("text")){
                	 line=bufferinput.readLine();
                   //System.out.println(line);
                	 //showArea.append(line+"\r\n");
                	Document dos = showArea.getDocument();
                	try {
						dos.insertString(dos.getLength(), line+"\r\n",null);
	                	String s=dos.getText(point,dos.getLength()-point);
	                	point=dos.getLength();
	                	FileWriter fwriter = null;
	                	
	                	try {
	                        // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
	                        fwriter = new FileWriter("E://record.txt", true);
	                        fwriter.write(s);
	                    } catch (IOException ex) {
	                        ex.printStackTrace();
	                    } finally {
	                        try {
	                            fwriter.flush();
	                            fwriter.close();
//	                            continue;
	                        } catch (IOException ex) {
	                            ex.printStackTrace();
	                        }
	                    }
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}            	              	
                	//判断当前信息是否是服务端关闭连接的信息
                	if((line.equals("您输入的账号密码有误！"))||(line.equals("您已下线！"))||line.equals("您输入的账号已经处于登录状态！")){
//                		System.out.println("Enter");
                		this.stop = true;
                		socket.close();
                		//如果是，则停止程序
                		System.exit(0);
                		return;
                	}
                }//if
                 else if(line.equals("Pic")) {
              		System.out.println("客户端获取图片");
              	//定义客户端接收文件的路径
                	File directory = new File("E:\\clientCache");
    				if(!directory.exists()){
    					directory.mkdir();
    				}
                	DataInputStream dis = new DataInputStream(socket.getInputStream());
    				long fileLength = dis.readLong();
    				System.out.println(fileLength);
    				File file = new File(directory.getAbsolutePath()+"//"+this.getName()+".JPG");//在客户端生成1.jpg,2.jpg......
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
    				System.out.println("照片接收成功");
    				fos.close();
                	Document dos1 = showArea.getDocument();
					dos1.insertString(dos1.getLength(), "系统提示：图片上传成功\r\n",null);
             
             		Graphics g = showImage.getGraphics();
                 	BufferedImage image = ImageIO.read(new File(directory.getAbsolutePath()+"//"+this.getName()+".JPG"));
             		int m = image.getHeight()/20;
             		int n = image.getWidth()/20;
             		g.clearRect(0, 0, n, m);// 清除绘图上下文的内容
                    g.drawImage(image,0,0, m, n,imagePane);// 绘制指定大小的图像
                    continue;
                    
             	}
                 
                 if(line.equals("file")){
                 	//定义客户端接收文件的路径
                 	File directory = new File("E:\\clientCache");
     				if(!directory.exists()){
     					directory.mkdir();
     				}
     				//获取文件后缀名
     				String suffix=bufferinput.readLine();
                 	DataInputStream dis = new DataInputStream(socket.getInputStream());
     				long fileLength = dis.readLong();
     				System.out.println(fileLength);
     				String flag=Integer.toString(i);
     				File file = new File(directory.getAbsolutePath()+"//"+flag+suffix);//在客户端生成文件
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
     				System.out.println("文件接收成功");
     				fos.close();
     				Document dos2 = showArea.getDocument();
                	try {
						dos2.insertString(dos2.getLength(), "系统提示：文件上传成功\r\n",null);
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                 	i++;
     				continue;
                  }
                 
            }//while
            

            
            
        }catch (IOException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
            if(socket!=null){
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    
    
    public void run() {
        while(true){   
        	
        }
    }
}
