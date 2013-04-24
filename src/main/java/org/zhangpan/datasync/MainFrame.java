package org.zhangpan.datasync;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.zhangpan.utils.ApplicationConfigs;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1379963724699883220L;

	private JTextField pcsTokenTxt;
	private JTextField localPathTxt;
	private JTextField serverPathTxt;
	private JButton localPathSelectButton;
	private JRadioButton manualSyncRadio;
	private JRadioButton autoSyncRadio;
	private ButtonGroup syncTypeGroup;

	private JButton syncButton;
	private JButton comfirmButton;
	private JButton cancelButton;

	private ApplicationConfigs config = new ApplicationConfigs();
	private final static String ACCESS_TOCKEN = "accessToken";
	private final static String SERVER_ROOT = "appRoot";
	private final static String AUTO_SYNC = "autoSync";
	private final static String LOCAL_ROOT = "localRoot";
	MainLogic mainLogic = null;

	/**
	 * 构造函数
	 * 
	 */
	public MainFrame() {

		mainLogic = new MainLogic();
		mainLogic.init();

		initFrame();

		// 点击窗口右上角的关闭按钮关闭窗口,退出程序
		// this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		// 点击窗口右上角的关闭按钮关闭窗口,退出程序
		this.addWindowListener(new WindowAdapter() {
			// 实现了一个WindowAdapter的匿名类,并将它注册为窗口事件的监听器.
			public void windowClosing(WindowEvent e) {
				System.out.println("程序最小化.");
				// System.exit(0);
			}
		});

		// 设置程序感观
		// setupLookAndFeel();
	}

	private void initFrame() {
		// 设置窗口标题
		this.setTitle("个人数据同步工具");

		ImageIcon icon = new ImageIcon(getRsc("image/8.png"));
		this.setIconImage(icon.getImage());

		setSizeAndCentralizeMe(480, 320);

		initMenuBar();
		initContent();
		initConfig();

		// this.pack();
		this.setVisible(false);
	}

	private void initConfig() {
		pcsTokenTxt.setText(config.getProperty(ACCESS_TOCKEN));
		localPathTxt.setText(config.getProperty(LOCAL_ROOT));
		serverPathTxt.setText(config.getProperty(SERVER_ROOT));
		boolean autoSync = Boolean.valueOf(config.getProperty(AUTO_SYNC));
		mainLogic.setAutoSync(autoSync);
		autoSyncRadio.setSelected(autoSync);
		manualSyncRadio.setSelected(!autoSync);
		if (autoSync) {
			startSync();
		}
	}

	private void saveConfig() {
		config.setValue(ACCESS_TOCKEN, pcsTokenTxt.getText());
		config.setValue(LOCAL_ROOT, localPathTxt.getText());
		config.setValue(SERVER_ROOT, serverPathTxt.getText());
		config.setValue(AUTO_SYNC, String.valueOf(autoSyncRadio.isSelected()));
	}

	private void initMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu menu = new JMenu("菜单");
		JMenu helpMenu = new JMenu("帮助");

		JMenuItem file = new JMenuItem("保存    ");
		JMenuItem exit = new JMenuItem("退出    ");
		JMenuItem about = new JMenuItem("关于DataSync     ");

		file.setSize(70, 30);

		file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("保存 click ...");
				JOptionPane.showMessageDialog(MainFrame.this, "保存成功，需要重启应用以实用新的配置",
						"提示", JOptionPane.WARNING_MESSAGE);
				MainFrame.this.saveConfig();
			}
		});
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("退出 click ...");
				if (JOptionPane.showConfirmDialog(MainFrame.this, "您确定要退出吗？", "标题",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					stopSync();
					System.exit(0);
				}
			}
		});
		about.addActionListener(new AboutAction(this));

		menu.add(file);
		menu.add(exit);
		helpMenu.add(about);

		menuBar.add(menu);
		menuBar.add(helpMenu);
		this.setJMenuBar(menuBar);
	}

	private void initContent() {

		JPanel panel = new JPanel();
		panel.setBounds(10, 22, 300, 150);
		this.getContentPane().add(panel);
		panel.setLayout(null);
		panel.setBorder(new TitledBorder("系统配置"));

		JLabel pscTokenLabel = new JLabel("PCSToken");
		JLabel serverPathLabel = new JLabel("服务器路径");
		JLabel localPathLabel = new JLabel("本地路径");
		pcsTokenTxt = new JTextField(".EHUINJKNjfdkajioNjkhjkdahuiohbfdan");
		serverPathTxt = new JTextField("/App/Datasync/");
		localPathTxt = new JTextField("E:/test");
		localPathSelectButton = new JButton("选择");

		pscTokenLabel.setBounds(30, 30, 80, 20);
		pcsTokenTxt.setBounds(120, 30, 250, 20);
		serverPathLabel.setBounds(30, 60, 80, 20);
		serverPathTxt.setBounds(120, 60, 250, 20);
		serverPathTxt.setEditable(false);
		localPathLabel.setBounds(30, 90, 80, 20);
		localPathTxt.setBounds(120, 90, 250, 20);
		localPathSelectButton.setBounds(380, 90, 60, 20);
		localPathSelectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser fd = new JFileChooser();
				fd.setToolTipText("选择本地文件路径");
				fd.setCurrentDirectory(new File(MainFrame.this.localPathTxt
						.getText()));
				// fd.setSelectedFile(new
				// File(MainFrame.this.localPathTxt.getText()));
				fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				// fd.showOpenDialog(null);
				fd.showDialog(MainFrame.this, "选择本地路径");
				File f = fd.getSelectedFile();
				if (f != null) {
					MainFrame.this.localPathTxt.setText(f.getAbsolutePath() + File.separator);
					// System.out.println(f.getName());
				}
			}
		});

		panel.add(pscTokenLabel);
		panel.add(pcsTokenTxt);
		panel.add(serverPathLabel);
		panel.add(serverPathTxt);
		panel.add(localPathLabel);
		panel.add(localPathTxt);
		panel.add(localPathSelectButton);

		JLabel syncTypeLabel = new JLabel("同步方式");
		syncTypeLabel.setBounds(30, 120, 250, 20);
		syncTypeGroup = new ButtonGroup();
		manualSyncRadio = new JRadioButton("手动同步");
		manualSyncRadio.setBounds(120, 120, 100, 20);
		manualSyncRadio.setSelected(true);
		autoSyncRadio = new JRadioButton("自动同步");
		autoSyncRadio.setBounds(250, 120, 100, 20);
		syncTypeGroup.add(autoSyncRadio);
		syncTypeGroup.add(manualSyncRadio);
		panel.add(syncTypeLabel);
		panel.add(autoSyncRadio);
		panel.add(manualSyncRadio);

		syncButton = new JButton("立即同步");
		comfirmButton = new JButton("保存配置");
		cancelButton = new JButton("取消");
		syncButton.setBounds(70, 160, 100, 20);
		comfirmButton.setBounds(180, 160, 100, 20);
		cancelButton.setBounds(290, 160, 100, 20);
		panel.add(syncButton);
		panel.add(comfirmButton);
		panel.add(cancelButton);

		syncButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startSync();
				boolean isDone = false;
				while(!isDone){
					try{
						tryGetLock();
					}catch(DataSyncException e1){
						try {
							Thread.sleep(100);
						} catch (InterruptedException e2) {
							e2.printStackTrace();
						}
						continue;
					}
					isDone = true;
				}
				JOptionPane.showMessageDialog(MainFrame.this, "同步成功", "提示",
						JOptionPane.WARNING_MESSAGE);
				System.out.println("sync button click.");
			}
		});

		comfirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainFrame.this.saveConfig();
				JOptionPane.showMessageDialog(MainFrame.this, "保存成功，需要重启应用以实用新的配置",
						"提示", JOptionPane.WARNING_MESSAGE);
				System.out.println("confirm button click.");
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainFrame.this.setVisible(false);
				System.out.println("cancel button click.");
			}
		});
	}
	
	public void tryGetLock() {
		mainLogic.tryGetLock();
	}

	public void startSync() {
		try {
			mainLogic.startSync();
		} catch (DataSyncException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "提示",
					JOptionPane.WARNING_MESSAGE);
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void stopSync() {
		try {
			mainLogic.stopSync();
		} catch (DataSyncException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "提示",
					JOptionPane.WARNING_MESSAGE);
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private String getRsc(String resc) {
		URL path = Thread.currentThread().getContextClassLoader()
				.getResource(resc);
		return path.getPath();
	}

	// 设置程序大小并定位程序在屏幕正中
	private void setSizeAndCentralizeMe(int width, int height) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(width, height);
		this.setLocation(screenSize.width / 2 - width / 2, screenSize.height
				/ 2 - height / 2);
	}

	// 设置程序感观
	private void setupLookAndFeel() {
		// 取得系统当前可用感观数组
		UIManager.LookAndFeelInfo[] arr = UIManager.getInstalledLookAndFeels();

		Random random = new Random();
		String strLookFeel = arr[random.nextInt(arr.length)].getClassName();

		try {
			UIManager.setLookAndFeel(strLookFeel);
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			System.out.println("Can't Set Lookandfeel Style to " + strLookFeel);
		}
	}

	/**
	 * 菜单关于窗口
	 * 
	 * @author YAOWENHAO
	 * @since 2011-12-21
	 * @version 1.0
	 */
	static class AboutAction extends AbstractAction {

		/** 字段注释 */
		private static final long serialVersionUID = -1097396738396411124L;

		public static String ABOUNT_CONTENT = "<p style=\"text-align:left;\">欢迎使用<span style=\"\" color:=\"E53333\"><strong>DataSyync</strong></span></p>"
				+ "<p style=\"text-align:left;\">当前版本：v1.0.0;发布日期：2013.04.15</strong></p>"
				+ "<p style=\"text-align:left;\">....</p>"
				+ "<p style=\"text-align:left;\">Copyright (C) 2013 By zhhangpan.</p>";

		JFrame parentsFrame;
		URL img = MainFrame.class.getResource("image/8.png"); //$NON-NLS-1$
		String imagesrc = "<img src=" + img + " width=\"50\" height=\"50\">"; //$NON-NLS-1$ //$NON-NLS-2$
		String message = ABOUNT_CONTENT;

		protected AboutAction(JFrame frame) {
			this.parentsFrame = frame;
		}

		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(parentsFrame,
					"<html><center>" + message + "</center><br></html>", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					"关于DataSync", //$NON-NLS-1$
					JOptionPane.DEFAULT_OPTION);

		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			// SwingUtilities
			// 里有invokeLater和invokeAndWait方法
			public void run() {
				new MainFrame();
			}
		});
	}
}