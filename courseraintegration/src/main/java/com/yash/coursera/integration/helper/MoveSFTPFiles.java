package com.yash.coursera.integration.helper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Component
public class MoveSFTPFiles {
	private String host;
	private String user;
	private String password;

	private JSch jsch;
	private Session session;
	private Channel channel;
	private ChannelSftp sftpChannel;

	public MoveSFTPFiles() {

	}

	public MoveSFTPFiles(String host, String user, String password) {
		this.host = host;
		this.user = user;
		this.password = password;
	}

	private void connect() {
		try {
			jsch = new JSch();
			session = jsch.getSession(user, host);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.connect();

			channel = session.openChannel("sftp");
			channel.connect();
			sftpChannel = (ChannelSftp) channel;

		} catch (JSchException e) {
			e.printStackTrace();
		}
	}

	private void disconnect() {
		sftpChannel.disconnect();
		channel.disconnect();
		session.disconnect();
	}

	public Integer downloadFileRemoteToLocal(String remoteDir, String localDir) {
		Integer readCount = null;
		byte[] buffer = new byte[1024];
		BufferedInputStream bufferedInputStream;
		connect();
		try {
			String cdDir = remoteDir.substring(0, remoteDir.lastIndexOf("/") + 1);
			sftpChannel.cd(cdDir);
			File fileRemote = new File(remoteDir);
			bufferedInputStream = new BufferedInputStream(sftpChannel.get(fileRemote.getName()));
			File directory = new File(localDir);
			if (!directory.exists()) {
				directory.mkdir();
			}
			File newFile = new File(localDir + "/" + fileRemote.getName());
			OutputStream os = new FileOutputStream(newFile);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(os);
			while ((readCount = bufferedInputStream.read(buffer)) > 0) {
				bufferedOutputStream.write(buffer, 0, readCount);
			}
			bufferedInputStream.close();
			bufferedOutputStream.close();
			if (readCount < 0)
				sftpChannel.rm(remoteDir);
		} catch (Exception e) {
			e.printStackTrace();
		}
		disconnect();
		return readCount;
	}

	public boolean uploadFileLocalToRemote(String localDir, String remoteDir) {
		boolean isUploaded = false;
		FileInputStream fileInputStream = null;
		connect();
		try {
			sftpChannel.cd(remoteDir);
			File fileLocal = new File(localDir);
			fileInputStream = new FileInputStream(fileLocal);
			sftpChannel.put(fileInputStream, fileLocal.getName());
			fileInputStream.close();
			if (fileLocal.delete())
				isUploaded = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		disconnect();
		return isUploaded;
	}

	/*public boolean moveSFTFileInboundToProcess(String strInboundFilePath, String strProcessFilePath,
			String strLocalPath) {
		boolean isMoved = false;
		byte[] buffer = new byte[1024];
		BufferedInputStream bis;
		FileInputStream fis = null;
		connect();
		try {
			
			 * ################# Copy File from Inbound Folder to Local Folder
			 * #################
			 
			String cdDir = strInboundFilePath.substring(0, strInboundFilePath.lastIndexOf("/") + 1);
			sftpChannel.cd(cdDir);

			File fileInbound = new File(strInboundFilePath);
			bis = new BufferedInputStream(sftpChannel.get(fileInbound.getName()));

			File newFileLocal = new File(strLocalPath + "/" + fileInbound.getName());

			// Download file
			OutputStream os = new FileOutputStream(newFileLocal);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			int readCount;
			while ((readCount = bis.read(buffer)) > 0) {
				bos.write(buffer, 0, readCount);
			}
			bis.close();
			bos.close();
			sftpChannel.rm(strInboundFilePath);

			
			 * ################# Copy File from Local Folder to Process Folder
			 * #################
			 
			sftpChannel.cd(strProcessFilePath);

			// Upload file
			File fileLocal = new File(strLocalPath + fileInbound.getName());
			fis = new FileInputStream(fileLocal);
			sftpChannel.put(fis, fileLocal.getName());

			fis.close();
			if (fileLocal.delete())
				isMoved = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		disconnect();
		return isMoved;
	}
*/
	/*public static void main(String[] args) {
		String SFTPHOST = "sftp.c4b-integration.com";

		String SFTPUSER = "john_deere_client";

		String SFTPPASS = "Sot@]}_HRHX5";

		String SFTPWORKINGDIR = "/uploads/dev/Inbound/";
		String SFTPPROCESSDIR = "/uploads/dev/Process/";

		String localPath = "D:/temp/";

		MoveSFTPFiles ftp = new MoveSFTPFiles(SFTPHOST, SFTPUSER, SFTPPASS);

		ftp.downloadFileRemoteToLocal(SFTPWORKINGDIR + "inbound.csv", localPath);

		ftp.uploadFileLocalToRemote(localPath + "inbound.csv", SFTPPROCESSDIR);

		// ftp.moveFiles(SFTPWORKINGDIR + "inbound.csv", SFTPPROCESSDIR);

		
		 * boolean isMoved = ftp.moveSFTFileInboundToProcess(SFTPWORKINGDIR,
		 * SFTPPROCESSDIR, localPath); System.out.println(isMoved);
		 

	}
*/
	
}
