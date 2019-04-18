package com.yash.coursera.integration.components;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Component
public class SFTPComponent {
	
	@Value("${SFTPHOST}")
	private String getSFTPHost;

	@Value("${SFTPUSER}")
	private String getSFTPUser;

	@Value("${SFTPPASS}")
	private String getSFTPPassword;
	
	private JSch jsch;

	public void setJsch(JSch jsch) {
		this.jsch = jsch;
	}

	private Session session;
	private Channel channel;
	private ChannelSftp sftpChannel;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SFTPComponent.class);

	public SFTPComponent() {

	}

	public Integer downloadFileRemoteToLocal(String remoteDir, String localDir) {
		Integer readCount = null;
		byte[] buffer = new byte[1024];
		BufferedInputStream bufferedInputStream;
		connectToSFTP();
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
			LOGGER.error("downoalRemoteToLocal [SFTP file transfer failure from remote to local] :: "+ e.getMessage());
			e.printStackTrace();
		}
		disconnectFromSFTP();
		return readCount;
	}

	public boolean uploadFileLocalToRemote(String localDir, String remoteDir) {
		boolean isUploaded = false;
		FileInputStream fileInputStream = null;
		connectToSFTP();
		try {
			sftpChannel.cd(remoteDir);
			File fileLocal = new File(localDir);
			fileInputStream = new FileInputStream(fileLocal);
			sftpChannel.put(fileInputStream, fileLocal.getName());
			fileInputStream.close();
			if (fileLocal.delete())
				isUploaded = true;
		} catch (Exception e) {
			LOGGER.error("uploadLocalToRemote [SFTP file transfer failure from local to remote] :: "+ e.getMessage());
			e.printStackTrace();
		}
		disconnectFromSFTP();
		return isUploaded;
	}
	private void connectToSFTP() {
		try {			
			session = jsch.getSession(getSFTPUser, getSFTPHost);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(getSFTPPassword);
			session.connect();

			channel = session.openChannel("sftp");
			channel.connect();
			sftpChannel = (ChannelSftp) channel;

		} catch (JSchException e) {
			LOGGER.error("SFTP connection failure :: "+ e.getMessage());
			e.printStackTrace();
		}
	}

	private void disconnectFromSFTP() {
		sftpChannel.disconnect();
		channel.disconnect();
		session.disconnect();
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
