package org.zhangpan.datasync.foldermonitor;

import static org.zhangpan.datasync.TaskOperate.*;
import static org.zhangpan.datasync.foldermonitor.FileUtils.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangpan.datasync.db.EventType;
import org.zhangpan.datasync.db.Task;
import org.zhangpan.utils.Constants;

/**
 * 在系统启动的时候扫描本地文件目录与上次系统推出的时候的目录相比较，得出差异结果。
 * 是JNotify的补充，防止用户在退出程序的这段时间对本地目录的修改不能同步到服务器上
 * 
 * @author zhangchao
 * 
 */
public class FolderChecker {

	private static Logger logger = LoggerFactory.getLogger(FolderChecker.class);
	public static final String INFO_FILENAME = "_dirInfo.xml";
	private String rootFolder = "rootDir";
	private File rootDir = null;
	private boolean dbOperate = false;

	static class SingletonHolder {
		static FolderChecker instance = new FolderChecker();
	}

	private FolderChecker() {

	}

	public static FolderChecker getInstance() {
		return SingletonHolder.instance;
	}

	public String getRootFolder() {
		return rootFolder;
	}

	public void setRootFolder(String rootFolder) {
		this.rootFolder = rootFolder;
	}

	/**
	 * 重构本地目标文件夹结构
	 * 
	 * @param dbOperate 是否将改变信息写入数据库
	 * @throws FolderCheckException
	 */
	public void check(boolean dbOperate) throws FolderCheckException {
		try {
			rootDir = new File(rootFolder);
			if (!rootDir.exists()) {
				rootDir.mkdir();
			}
			this.dbOperate = dbOperate;
			logger.info("root dir : " + rootDir.getAbsolutePath());
			buildStructure(rootDir);
		} catch (Exception e) {
			throw new FolderCheckException(e);
		}
	}

	private int buildStructure(File currentDir)
			throws IOException, FolderCheckException {

		int changedSize = 0;
		File infoFile = new File(currentDir.getAbsolutePath()
				+ File.separator + INFO_FILENAME);
		if (!infoFile.exists()) {
			xmlToFile(getEmptyDoc(), infoFile);
			infoFile.createNewFile();
			setFileHidden(infoFile);
		}

		File[] files = currentDir.listFiles();
		if (files.length == 0) {
			return changedSize;
		}
		
		Document doc = fileToXml(infoFile);
		Element rootEl = (Element) doc.selectSingleNode("/nodes");
		// 更新每一个文件的修改信息
		for (File file : files) {
			if (file.isDirectory()) {
				buildStructure(file);
			} else if (file.getName().equals(INFO_FILENAME)) {
				continue;
			}
			Element element = (Element) doc.selectSingleNode("/nodes/node[@name='" + file.getName() + "']");
			changedSize += updateOrAddDoc(rootEl, element, file);
		}

		// 删除无用的信息节点
		changedSize += deleteUnexistNode(rootEl, currentDir);
		// 更新信息文件
		if (changedSize > 0) {
			xmlToFile(doc, infoFile);
		}
		return changedSize;
	}
	
	public String getFileMd5(String path) {
		String md5 = "";
		File targetFile = new File(path);
		if (!targetFile.exists()) {
			try {
				return getMd5(targetFile);
			} catch (FolderCheckException e) {
				e.printStackTrace();
				return "";
			}
		}

		File infoFile = new File(targetFile.getParent() + File.separator + INFO_FILENAME);
		try {
			Document doc = fileToXml(infoFile);
			Element element = (Element) doc.selectSingleNode("/nodes/node[@name='" + targetFile.getName() + "']/md5");
			md5 = element.getText();
		} catch (FolderCheckException e) {
			e.printStackTrace();
			return "";
		}
		return md5;
	}

	private String getRelativePath(File file) {
		return file.getAbsolutePath().substring(rootFolder.length() - 1);
	}

	/**
	 * 给Dom实例更新或添加一个文件的修改信息
	 * 
	 * @param rootEl
	 * @param element
	 * @param file
	 * @throws FolderCheckException
	 */
	private int updateOrAddDoc(Element rootEl, Element element, File file)
			throws FolderCheckException {
		int changedSize = 0;
		if (element == null) {
			element = rootEl.addElement("node");
			element.addAttribute("id", UUID.randomUUID().toString());
			element.addAttribute("name", file.getName());
			element.addElement("md5").addText(getMd5(file));
			element.addElement("lastmodify").addText(String.valueOf(file.lastModified()));

			if (dbOperate) {

				Task task = new Task();
				task.setSrcPath(getRelativePath(file));
				task.setEventType(EventType.add);

				if (file.isDirectory()) {
					task.setFileType(Constants.FOLDER);
					logger.info("add folder node : " + file.getName());
				} else {
					task.setFileType(Constants.FILE);
					logger.info("add file node : " + file.getName());
				}
				add(task);
			}

			changedSize++;
		} else {
			if (Long.valueOf(element.selectSingleNode("lastmodify").getText()) != file
					.lastModified()) {
				element.selectSingleNode("md5").setText(getMd5(file));
				element.selectSingleNode("lastmodify").setText(
						String.valueOf(file.lastModified()));

				if (file.isDirectory()) {
					logger.info("update folder node : " + file.getName());
				} else {
					if (dbOperate) {

						Task task = new Task();
						task.setSrcPath(getRelativePath(file));
						task.setEventType(EventType.modify);
						task.setFileType(Constants.FILE);
						add(task);

						logger.info("update file node : " + file.getName());
					}
				}
				changedSize++;
			}
		}
		return changedSize;
	}

	/**
	 * 清除文件信息节点中已经不存在的节点
	 * 
	 * @param rootEl
	 * @param dir
	 */
	private int deleteUnexistNode(Element rootEl, File dir) {
		int changedSize = 0;
		List<Element> elements = (List<Element>) rootEl.selectNodes("node");
		if (elements.size() == 0) {
			return 0;
		}

		for (Element ele : elements) {
			String name = ele.attributeValue("name");
			if(! new File(dir.getAbsolutePath() + File.separator + name).exists()){
				rootEl.remove(ele);
				changedSize++;
				logger.info("remove file node : " + name);
				if (dbOperate) {

					File targetFile = new File(dir.getAbsolutePath()
							+ File.separatorChar + name);
					Task task = new Task();
					task.setSrcPath(getRelativePath(targetFile));
					task.setEventType(EventType.delete);
					task.setFileType(Constants.FILE);
					add(task);
				}
			}
		}
		return changedSize;
	}

}
