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
    //��ť
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
    
    //��ʷ��¼
    private JScrollPane recordPane;
    
    
    public int imgLength;
    public int point=0;
    public int i=1;
    public Client(){
    	
    	mainJframe=new JFrame("���졪���ͻ���");
        con=mainJframe.getContentPane();
        //showArea=new JTextArea();
        showArea=new JTextPane();//��ʾ����
        showImage=new JTextPane();//��ʾͼƬ
        showRecord=new JTextPane();
        showArea.setEditable(false);
        showImage.setEditable(false);
        showRecord.setEditable(false);
        
        //showArea.setLineWrap(true);
        JSPane=new JScrollPane(showArea);//��Pane����ʾ����
        
        msgText=new JTextField();
        msgText.setColumns(30);
        msgText.addActionListener(this);
        sentBtn=new JButton("������Ϣ");
        picBtn = new JButton("��ͼƬ");
        FileBtn=new JButton("���ļ�");
        RecordBtn=new JButton("��ʷ��¼");
        
        sentBtn.addActionListener((ActionListener) this);
        picBtn.addActionListener((ActionListener) this);
        FileBtn.addActionListener((ActionListener)this);
        RecordBtn.addActionListener((ActionListener)this);
        
        imgField= new JTextField();
//        imgField.setBounds(200,200,30,50);
        
        //�Ϸ��ļ�ֱ�ӻ�ȡ�ļ�·��
        imgField.setColumns(10);
        imgField.setTransferHandler(new TransferHandler()//�Զ����Ϸŵ���
   	        {
   				private static final long serialVersionUID = 1L;
   	            @Override
   	            public boolean importData(JComponent comp, Transferable t) {
   	                try {
   	                    Object o = t.getTransferData(DataFlavor.javaFileListFlavor);//�ö����ʾ��Ҫ���͵�����
   	 
   	                    String filepath = o.toString();
   	                    if (filepath.startsWith("[")) {
   	                        filepath = filepath.substring(1);//ȥ��"["
   	                    }
   	                    if (filepath.endsWith("]")) {
   	                        filepath = filepath.substring(0, filepath.length() - 1);
   	                    }
   	                    filePath=filepath;//��ȡ���ļ�·�����������ܺ�����ʵ��
   	                    System.out.println(filepath);
   	                    imgField.setText(filepath);//��ʾ����Ļ��
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
       
        
        path_label=new JLabel("ͼƬ���ļ�·��");
        img_label=new JLabel("��ʾͼƬ");
        rec_label=new JLabel("��ʷ��¼");
        
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
    	
       
        
        
        //��ʼ��ʱ���ӷ�����
        try {
            socket=new Socket("127.0.0.1",3378);
            input=socket.getInputStream();
            output=socket.getOutputStream();
            //��֤�û���Ϣ
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
                //��ȡ��Ϣ
                if((line=bufferinput.readLine())!=null){
                    System.out.println(line);
                	//showArea.append(line);
                }
                String textLine=bufferinput.readLine();
                //������Ϣ
        	    
                String str;
                if(Login==0)
                	str = JOptionPane.showInputDialog("�����û���");
                else
                	str = JOptionPane.showInputDialog("��������");             
                String strName=str+"\r\n";
   
                output.write(strName.getBytes());
                output.flush();
                Login++;
                
            }
            
            chat();//chat����
            
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

    
    //������Ϣ
    public void sendMsg(String msg) throws IOException {
		msg+="\r\n";
		output.write(msg.getBytes());
		output.flush();
	}
    
    //�����ļ�
    public void sendFile()throws IOException{
    	int lastIndexOf = filePath.lastIndexOf(".");
    	String suffix = filePath.substring(lastIndexOf);
    	//���ͺ�׺��
    	output.write((suffix+"\r\n").getBytes());
    	output.flush();
    	//�����ļ���
    	File source = new File(filePath);
    	FileInputStream fis =  new FileInputStream(source);
    	DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
    	System.out.println("�ļ���СΪ��"+source.length());
    	dos.writeLong(source.length());
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
		System.out.println("�ļ�����ɹ�");
    }
    
    //����ͼƬ
    //Ӧ���������ͼƬ��·��
    //���ص���
    public void sendPic() throws IOException{
    	
    	File source = new File(filePath);
    	FileInputStream fis =  new FileInputStream(source);
    	DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
    	dos.writeLong(source.length());
    	dos.flush();
    	System.out.println("��ͼƬ...");
    	byte[] buf = new byte[1024];
    	long progress = 0;
		int len = 0;
		//���ļ���д���ܵ���	
		while ((len = fis.read(buf,0,buf.length)) != -1){
			dos.write(buf,0,len);
			dos.flush();
			progress+=len;
		}
		System.out.println("�ļ�����ɹ�");
    	
		//֪ͨ����ˣ����ݷ������
    	Graphics g = showImage.getGraphics();
    	BufferedImage image = ImageIO.read(new File(filePath));
		int m = image.getHeight()/20;
		int n = image.getWidth()/20;
		g.clearRect(0, 0, n, m);// �����ͼ�����ĵ�����
        g.drawImage(image,0,0, m, n,imagePane);// ����ָ����С��ͼ��
			
    }
    
    public void actionPerformed(ActionEvent e){
    	  	
    	String cmd = e.getActionCommand();
    	
        String s=msgText.getText();
        if (cmd == "������Ϣ"){
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
        if(cmd == "��ͼƬ"){
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
        if(cmd == "���ļ�") {
        	try {
        		output.write(("file"+"\r\n").getBytes());
        		output.flush();
        		sendFile();
        	}catch(IOException e1) {
        		e1.printStackTrace();
        	}
        }
        if(cmd=="��ʷ��¼") {
        	
            try {
            	Document rec=showRecord.getDocument();
            	File file = new File("E:\\Record.txt");//����һ��file����������ʼ��FileReader
                FileReader reader = new FileReader(file);//����һ��fileReader����������ʼ��BufferedReader
                BufferedReader bReader = new BufferedReader(reader);//newһ��BufferedReader���󣬽��ļ����ݶ�ȡ������
                StringBuilder sb = new StringBuilder();//����һ���ַ������棬���ַ�����Ż�����
                String temp = "";
                while ((temp =bReader.readLine()) != null) {//���ж�ȡ�ļ����ݣ�����ȡ���з���ĩβ�Ŀո�
                    sb.append(temp + "\n");//����ȡ���ַ�����ӻ��з����ۼӴ���ڻ�����
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
        start();//����һ���̶߳�ȡ��ǰ�û�������
        bufferinput=new BufferedReader(new InputStreamReader(input));
        String line;
        //��ȡ��Ϣ
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
	                        // true��ʾ������ԭ�������ݣ����Ǽӵ��ļ��ĺ��档��Ҫ����ԭ�������ݣ�ֱ��ʡ����������ͺ�
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
                	//�жϵ�ǰ��Ϣ�Ƿ��Ƿ���˹ر����ӵ���Ϣ
                	if((line.equals("��������˺���������"))||(line.equals("�������ߣ�"))||line.equals("��������˺��Ѿ����ڵ�¼״̬��")){
//                		System.out.println("Enter");
                		this.stop = true;
                		socket.close();
                		//����ǣ���ֹͣ����
                		System.exit(0);
                		return;
                	}
                }//if
                 else if(line.equals("Pic")) {
              		System.out.println("�ͻ��˻�ȡͼƬ");
              	//����ͻ��˽����ļ���·��
                	File directory = new File("E:\\clientCache");
    				if(!directory.exists()){
    					directory.mkdir();
    				}
                	DataInputStream dis = new DataInputStream(socket.getInputStream());
    				long fileLength = dis.readLong();
    				System.out.println(fileLength);
    				File file = new File(directory.getAbsolutePath()+"//"+this.getName()+".JPG");//�ڿͻ�������1.jpg,2.jpg......
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
    				System.out.println("��Ƭ���ճɹ�");
    				fos.close();
                	Document dos1 = showArea.getDocument();
					dos1.insertString(dos1.getLength(), "ϵͳ��ʾ��ͼƬ�ϴ��ɹ�\r\n",null);
             
             		Graphics g = showImage.getGraphics();
                 	BufferedImage image = ImageIO.read(new File(directory.getAbsolutePath()+"//"+this.getName()+".JPG"));
             		int m = image.getHeight()/20;
             		int n = image.getWidth()/20;
             		g.clearRect(0, 0, n, m);// �����ͼ�����ĵ�����
                    g.drawImage(image,0,0, m, n,imagePane);// ����ָ����С��ͼ��
                    continue;
                    
             	}
                 
                 if(line.equals("file")){
                 	//����ͻ��˽����ļ���·��
                 	File directory = new File("E:\\clientCache");
     				if(!directory.exists()){
     					directory.mkdir();
     				}
     				//��ȡ�ļ���׺��
     				String suffix=bufferinput.readLine();
                 	DataInputStream dis = new DataInputStream(socket.getInputStream());
     				long fileLength = dis.readLong();
     				System.out.println(fileLength);
     				String flag=Integer.toString(i);
     				File file = new File(directory.getAbsolutePath()+"//"+flag+suffix);//�ڿͻ��������ļ�
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
     				System.out.println("�ļ����ճɹ�");
     				fos.close();
     				Document dos2 = showArea.getDocument();
                	try {
						dos2.insertString(dos2.getLength(), "ϵͳ��ʾ���ļ��ϴ��ɹ�\r\n",null);
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
