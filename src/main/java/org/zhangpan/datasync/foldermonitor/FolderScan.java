package org.zhangpan.datasync.foldermonitor;

import static org.zhangpan.datasync.foldermonitor.FileUtils.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 在系统启动的时候扫描本地文件目录与服务器文件目录相比较，得出差异结果。
 * 是JNotify的补充，防止用户在退出程序的这段时间对本地目录的修改不能同步到服务器上
 * 
 * @author zhangchao
 * 
 */
public class FolderScan {

	private static Logger logger = LoggerFactory.getLogger(FolderScan.class);
	public static final String INFO_FILENAME = "_dirInfo.xml";
	private String rootDirUrl = "rootDir";
	private File rootDir = null;

	public FolderScan() {
		rootDir = new File(rootDirUrl);
		if (!rootDir.exists()) {
			rootDir.mkdir();
		}
	}

	public void init() throws FolderCheckException {
		try {
			System.out.println(rootDir.getAbsolutePath());
			logger.info(rootDir.getAbsolutePath());
			initDir(rootDir);
		} catch (Exception e) {
			throw new FolderCheckException(e);
		}
	}

	private int initDir(File currentDir) throws IOException, FolderCheckException {

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
				initDir(file);
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
			logger.info("add file node : " + file.getName());
			changedSize++;
		} else {
			if (Long.valueOf(element.selectSingleNode("lastmodify").getText()) != file
					.lastModified()) {
				element.selectSingleNode("md5").setText(getMd5(file));
				element.selectSingleNode("lastmodify").setText(
						String.valueOf(file.lastModified()));
				logger.info("update file node : " + file.getName());
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

		logger.info("dir path : " + dir.getAbsolutePath());
		for (Element ele : elements) {
			String name = ele.attributeValue("name");
			if(! new File(dir.getAbsolutePath() + File.separator + name).exists()){
				rootEl.remove(ele);
				changedSize++;
				logger.info("remove file node : " + name);
			}
		}
		return changedSize;
	}

}
