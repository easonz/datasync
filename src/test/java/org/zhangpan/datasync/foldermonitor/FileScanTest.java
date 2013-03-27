package org.zhangpan.datasync.foldermonitor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zhangpan.datasync.foldermonitor.FolderMonitor;

public class FileScanTest {

	@Before
	public void setUp() {
		
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testFileScan() {
		try {
			FolderMonitor.getInstance().setRootFolder("d:/test");
			FolderMonitor.getInstance().startMonitor();
			/* System.in.read(); */
			Thread.sleep(100000);
			FolderMonitor.getInstance().stopMonitor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public final static void main(String[] argv) {
/*		FolderScan fileScan = new FolderScan();
		try {
			fileScan.init();
		} catch (FolderCheckException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}
}
