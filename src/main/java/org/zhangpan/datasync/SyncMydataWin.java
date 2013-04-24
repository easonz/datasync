package org.zhangpan.datasync;
 
import java.awt.AWTException;   
import java.awt.Image;   
import java.awt.SystemTray;   
import java.awt.TrayIcon;   
import java.awt.event.ActionEvent;   
import java.awt.event.ActionListener;   
import java.awt.event.MouseAdapter;   
import java.awt.event.MouseEvent;   
import java.net.URL;   
  
import javax.swing.ImageIcon;   
import javax.swing.JFrame;   
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;   
import javax.swing.ToolTipManager;
import javax.swing.UIManager;   

  
/**  
 *   
 * 创建闪动的托盘图像  
 * @author Everest  
 *  
 */  
public class SyncMydataWin{   
  
    private static final long serialVersionUID = -3115128552716619277L;   
  
    private MainFrame frame;
    private SystemTray sysTray;// 当前操作系统的托盘对象   
    private TrayIcon trayIcon;// 当前对象的托盘   
  
    private ImageIcon icon = null;   
  
    public SyncMydataWin() {   
        init();   
    }   
  
    public URL getRes(String str){   
    	System.out.println(this.getClass().getClassLoader().getResource(""));
    	URL url = this.getClass().getClassLoader().getResource(str);  
        return url; 
    }   
    
    /**  
     * 初始化窗体的方法  
     */  
    public void init() {   
    	
    	frame = new MainFrame();
		createTrayIcon();
    }     
  
    /**  
     * 创建系统托盘的对象 步骤:   
     * 1,获得当前操作系统的托盘对象   
     * 2,创建弹出菜单popupMenu   
     * 3,创建托盘图标icon  
     * 4,创建系统的托盘对象trayIcon  
     */  
    public void createTrayIcon() {   
        icon = new ImageIcon(getRes("image/pic.gif"));// 托盘图标   
        sysTray = SystemTray.getSystemTray();// 获得当前操作系统的托盘对象   
        trayIcon = new TrayIcon(icon.getImage());
        try {
        	// 将托盘添加到操作系统的托盘   
        	sysTray.add(trayIcon);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}  
        
        /** 添加鼠标监听器，当鼠标在托盘图标上双击时，默认显示窗口 */  
        trayIcon.addMouseListener(new MouseAdapter() {   
            public void mouseClicked(MouseEvent e) {   
                if (e.getClickCount() == 2) { // 鼠标双击  
                    frame.setVisible(true); // 显示窗口   
                    frame.toFront();   
                }   
            }   
            @Override  
            public void mouseReleased(MouseEvent e) {  
            	if (e.isPopupTrigger()) {  
            		final JPopupMenu pop = new JPopupMenu();  
            		JMenuItem open = new JMenuItem("打开");   
            		//JMenuItem setting = new JMenuItem("设置");  
            		JMenuItem exit = new JMenuItem("退出");   
            		pop.add(open);   
            		//pop.add(setting);   
            		pop.add(exit);   
                    // 为弹出菜单项添加事件   
            		open.addActionListener(new ActionListener() {   
                        public void actionPerformed(ActionEvent e) {   
                            //frame.setExtendedState(JFrame.NORMAL);   
                            frame.setVisible(true); // 显示窗口   
                            frame.toFront(); 
                        }   
                    });   
                    exit.addActionListener(new ActionListener() {   
                        public void actionPerformed(ActionEvent e) {   
                        	System.out.println("程序退出");
                            System.exit(0);   
                        }   
                    }); 
            		pop.setLocation(e.getX(), e.getY());  
            		pop.setInvoker(pop);  
            		pop.setVisible(true);  
            	}  
            }  
        });
    }   
  
    /**  
     * @param args  
     */  
    public static void main(String[] args) {   
        SwingUtilities.invokeLater(new Runnable() {   
            public void run() {   
				try {
					new SyncMydataWin();   
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					//window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}catch (Throwable e) {
					e.printStackTrace();
					System.exit(0);
				}
            }   
        });   
    }   
  
}