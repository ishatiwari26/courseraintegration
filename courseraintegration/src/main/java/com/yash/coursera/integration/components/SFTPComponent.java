package com.yash.coursera.integration.components;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

@Component
public class SFTPComponent {

	@Value("${sftp.host}")
	private String getSFTPHost;

	@Value("${sftp.user}")
	private String getSFTPUser;

	@Value("${sftp.password}")
	private String getSFTPPassword;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

	private Session session;
	private ChannelSftp sftpChannel;

	private BufferedInputStream bufferedInputStream;
	private BufferedOutputStream bufferedOutputStream;
	private FileInputStream fileInputStream;
	private OutputStream outPutStream;

	private JSch jsch;

	public void setJsch(JSch jsch) {
		this.jsch = jsch;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(SFTPComponent.class);

	public SFTPComponent() {

	}

	public Integer downloadFileRemoteToLocal(String remoteDir, String localDir) {
		Integer readCount = null;
		byte[] buffer = new byte[1024];
		connectToSFTP();
		try {
			String cdDir = remoteDir.substring(0, remoteDir.lastIndexOf("/") + 1);
			sftpChannel.cd(cdDir);
			Resource  resourceRemote = resourceLoader.getResource("file:"+remoteDir);
			File fileRemote = resourceRemote.getFile();
			bufferedInputStream = new BufferedInputStream(sftpChannel.get(fileRemote.getName()));
			
			Resource  resourceLocal = resourceLoader.getResource("file:"+localDir);
			File directory = resourceLocal.getFile();
			if (!directory.exists()) {
				directory.mkdir();
			}
			Resource  resource = resourceLoader.getResource("file:"+localDir + fileRemote.getName());
			File newFile = resource.getFile();

			outPutStream = new FileOutputStream(newFile);

			bufferedOutputStream = new BufferedOutputStream(outPutStream);

			while ((readCount = bufferedInputStream.read(buffer)) > 0) {
				bufferedOutputStream.write(buffer, 0, readCount);
			}
			bufferedInputStream.close();
			bufferedOutputStream.close();
			if (readCount < 0)
				sftpChannel.rm(remoteDir);
		} catch (IOException | SftpException e) {
			LOGGER.error("downoalRemoteToLocal [SFTP file transfer failure from remote to local] :: " + e.getMessage());
			e.printStackTrace();
		}
		disconnectFromSFTP();
		return readCount;
	}

	public boolean uploadFileLocalToRemote(String localDir, String remoteDir, Boolean sftpModifierStatus) {
		long yourmilliseconds = System.currentTimeMillis();
		Date resultdate = new Date(yourmilliseconds);
		boolean isUploaded = false;
		connectToSFTP();
		try {
			sftpChannel.cd(remoteDir);
			Resource  resource = resourceLoader.getResource("file:"+localDir);
			File fileLocal = resource.getFile();
			fileInputStream = new FileInputStream(fileLocal);
			sftpChannel.put(fileInputStream, fileLocal.getName());
			if (sftpModifierStatus) {
				String[] oldFile = fileLocal.getName().toString().split(".csv");
				localDir = oldFile[0].concat("_" + resultdate.toString());
				localDir = localDir.concat(".csv");
				sftpChannel.rename(remoteDir + fileLocal.getName(), remoteDir + localDir);
			}
			fileInputStream.close();
			disconnectFromSFTP();
			fileLocal.setWritable(true);
			if (fileLocal.exists())
				if (fileLocal.delete())
					isUploaded = true;
		} catch (IOException | SftpException e) {
			LOGGER.error("uploadLocalToRemote [SFTP file transfer failure from local to remote] :: " + e.getMessage());
			e.printStackTrace();
		}
		return isUploaded;
	}

	private void connectToSFTP() {
		try {
			session = jsch.getSession(getSFTPUser, getSFTPHost);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(getSFTPPassword);
			session.connect();

			sftpChannel = (ChannelSftp) session.openChannel("sftp");
			sftpChannel.connect();

		} catch (JSchException e) {
			LOGGER.error("SFTP connection failure :: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void disconnectFromSFTP() {
		sftpChannel.disconnect();
		session.disconnect();
	}

	public boolean moveInboundToLocalViaProcess(String remoteInboundDir, String remoteProcessDir, String localDir,
			String fileName, Boolean sftModifierStatus) {
		boolean isMoved = false;
		if (downloadFileRemoteToLocal(remoteInboundDir.concat(fileName), localDir) < 0) {
			if (uploadFileLocalToRemote(localDir.concat(fileName), remoteProcessDir, sftModifierStatus)) {
				if (downloadFileRemoteToLocal(remoteProcessDir.concat(fileName), localDir) < 0) {
					isMoved = true;
				}
			}
		}

		return isMoved;
	}
}
