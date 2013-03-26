package org.zhangpan.datasync.foldermonitor;

import net.contentobjects.jnotify.JNotifyListener;

/**
 * 
 * @author zhangchao
 * 
 */
public class Listener implements JNotifyListener {

	public void fileRenamed(final int wd, final String rootPath,
			final String oldName, final String newName) {
		System.out.println("JNotifyTest.fileRenamed() : wd #" + wd + " root = "
				+ rootPath + ", " + oldName + " -> " + newName);
	}

	public void fileModified(final int wd, final String rootPath,
			final String name) {
		System.out.println("JNotifyTest.fileModified() : wd #" + wd
				+ " root = " + rootPath + ", " + name);
	}

	public void fileDeleted(final int wd, final String rootPath,
			final String name) {
		System.out.println("JNotifyTest.fileDeleted() : wd #" + wd + " root = "
				+ rootPath + ", " + name);
	}

	public void fileCreated(final int wd, final String rootPath,
			final String name) {
		System.out.println("JNotifyTest.fileCreated() : wd #" + wd + " root = "
				+ rootPath + ", " + name);
	}
}
