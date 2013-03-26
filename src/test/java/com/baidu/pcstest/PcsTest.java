package com.baidu.pcstest;

import java.io.File;
import java.util.List;

import com.baidu.pcs.PcsClient;
import com.baidu.pcs.PcsFileEntry;
import com.baidu.pcs.PcsUploadResult;
import com.baidu.pcs.exception.PcsException;

public class PcsTest {
	//private static String accessToken = "3.c2d0e04c99d5f0ec11d36cb0e782ec54.2592000.1334322972.1175746697-238347";
	private static String accessToken = "3.e87dfe1d23d49c016f14158faad519e2.2592000.1360397434.3355604315-238347";
	//private static String appRoot = "/apps/pcstest_oauth/";
	private static String appRoot = "/apps/pcstest_oauth/";
	private static String localFileName = "README.rst";
	private static String serverFileName = localFileName + "pcs_serverside";

	public static void main(String[] args) throws PcsException {
		PcsClient pcsClient = new PcsClient(accessToken, appRoot);
		System.out.println(pcsClient.quota());

		// list
		for (PcsFileEntry entity : pcsClient.list(appRoot)) {
			if (entity.getServerFilename().indexOf(localFileName) != -1) {
				// delete
				pcsClient.delete(entity.getPath() );
				System.out.println("[delete]" + entity.getServerFilename());
			} else {
				System.out.println(entity.getServerFilename());
			}
		}

		pcsClient.mkdir(appRoot + serverFileName + ".dir2");

		// upload
		System.out.println("uploading ...");
		PcsUploadResult rst = pcsClient.uploadFile(localFileName, appRoot, serverFileName);
		
		System.out.println(rst);
		
		// check if it's in file list
		for (PcsFileEntry entity : pcsClient.list(appRoot)) {
			if (entity.getServerFilename().equals(serverFileName))
				System.out.println("upload file in file List now!");
		}

		// download
		System.out.println("downloading ...");
		pcsClient.downloadToFile(appRoot + serverFileName, localFileName + ".download");

		// copy
		System.out.println("copy ...");
		pcsClient.copy(appRoot + serverFileName, appRoot + serverFileName + ".copy");

		// move
		System.out.println("move ...");
		pcsClient.move(appRoot + serverFileName, appRoot + serverFileName + ".move");
		
		//search
		for (PcsFileEntry entity : pcsClient.search(appRoot, serverFileName, true)) {
			System.out.println("searched list:" + entity.getPath());
		}
		assertInList(serverFileName+".copy",  pcsClient.search(appRoot, serverFileName, true));
		assertInList(serverFileName+".move",  pcsClient.search(appRoot, serverFileName, true));
		
		// clean up
		new File(localFileName + ".download").delete();
		
	}
	

	static void assertInList(String fileName, List<PcsFileEntry> lst){
		System.out.println("assertInList:" + fileName);
		boolean found = false;
		for (PcsFileEntry entity : lst) {
			if (entity.getServerFilename().indexOf(fileName) != -1) {
				found = true;
//				System.out.println("true " + entity.getServerFilename());
			}
		}
		if (!found) {
		    throw new AssertionError();
		}
	}
}
