package org.zhangpan.datasync;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class DataSyncWin extends JFrame {
	private static final long serialVersionUID = 1379963724699883220L;

	/**
	 * 构造函数
	 * 
	 */
	public DataSyncWin() {

		initFrame();

		// 点击窗口右上角的关闭按钮关闭窗口,退出程序
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 点击窗口右上角的关闭按钮关闭窗口,退出程序
		this.addWindowListener(new WindowAdapter() {
			// 实现了一个WindowAdapter的匿名类,并将它注册为窗口事件的监听器.
			public void windowClosing(WindowEvent e) {
				System.out.println("程序退出.");
				System.exit(0);
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
		// initToolBar();
		initContent();

		this.pack();
		// 显示窗口
		this.setVisible(true);
	}

	private void initMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu menu = new JMenu("菜单");
		JMenu helpMenu = new JMenu("帮助");

		JMenuItem file = new JMenuItem("文件");
		JMenuItem help = new JMenuItem("帮助");
		file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("文件 click ...");
			}
		});
		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("帮助 click ...");
			}
		});

		menu.add(file);
		menu.add(help);

		menuBar.add(menu);
		menuBar.add(helpMenu);
		this.setJMenuBar(menuBar);
	}

	private void initToolBar() {
		JToolBar toolbar = new JToolBar();
		JButton button1 = new JButton("前一张");
		JButton button2 = new JButton("后一张");
		toolbar.add(button1);
		toolbar.add(button2);
		// 添加toolbar到主面板中
		JPanel contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(toolbar, BorderLayout.NORTH);
	}

	private void initContent() {
		final JLabel directoryLabel = new JLabel("本地路径");
		this.add(directoryLabel, BorderLayout.NORTH);

		JFileChooser fileChooser = new JFileChooser(".");
		fileChooser.setControlButtonsAreShown(false);
		this.add(fileChooser, BorderLayout.CENTER);

		// Create ActionListener
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				JFileChooser theFileChooser = (JFileChooser) actionEvent
						.getSource();
				String command = actionEvent.getActionCommand();
				if (command.equals(JFileChooser.APPROVE_SELECTION)) {
					File selectedFile = theFileChooser.getSelectedFile();
					directoryLabel.setText(selectedFile.getParent());
				} else if (command.equals(JFileChooser.CANCEL_SELECTION)) {
					directoryLabel.setText(" ");
				}
			}
		};
		fileChooser.addActionListener(actionListener);
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

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			// SwingUtilities
			// 里有invokeLater和invokeAndWait方法
			public void run() {
				new DataSyncWin();
			}
		});
	}
}