package org.zhangpan.datasync.test;

import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zhangpan.datasync.DataSyncException;
import org.zhangpan.datasync.MainLogic;

public class MainLogicTest {

	MainLogic mainLogic = null;

	@Before
	public void setUp() {
		mainLogic = new MainLogic();
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testManualSync() {
		mainLogic.setAutoSync(false);
		testMain();

	}

	// @Test
	public void testAutoSync() {
		mainLogic.setAutoSync(true);
		testMain();
	}

	private void testMain() {

		mainLogic.init();

		try {

			Scanner sc = new Scanner(System.in);
			while (true) {

				try {

					int i = sc.nextInt();

					switch (i) {
					case 1:
						mainLogic.startSync();
						break;
					case 2:
						mainLogic.stopSync();
						break;
					default:
						break;
					}
				} catch (DataSyncException e1) {
					// e1.printStackTrace();
				}

			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
