package com.yash.coursera.integration.component;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.yash.coursera.integration.components.SFTPComponent;
import com.yash.coursera.integration.helper.FileOpUtils;

@RunWith(MockitoJUnitRunner.class)
public class SFTPComponentTest {

	@InjectMocks
	SFTPComponent sftpComponent;

	@Mock
	private JSch jsch;
	@Mock
	private Session session;
	@Mock
	private ChannelSftp sftpChannel;
	@Mock
	private BufferedInputStream bufferedInputStream;
	@Mock
	private BufferedOutputStream bufferedOutputStream;
	@Mock
	private FileInputStream fileInputStream;
	@Mock
	private OutputStream outPutStream;
	@Mock
	private InputStream inputStream;
	@Mock
	private File fileRemote;
	@Mock
	private ResourceLoader resourceLoader;
	@Mock
	private Resource resource;
	@Mock
	private FileOpUtils fileOpUtils;
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Before
	public void setUp() {
		ReflectionTestUtils.setField(sftpComponent, "getSFTPHost", "sftp.testHost.com");
		ReflectionTestUtils.setField(sftpComponent, "getSFTPUser", "testUser");
		ReflectionTestUtils.setField(sftpComponent, "getSFTPPassword", "testPassword");
		jsch = mock(JSch.class);
		sftpComponent.setJsch(jsch);
	}

	@Test
	public void sholdMoveInboundToLocalViaProcess() {
		SFTPComponent sftpComponentMock = new SFTPComponent();
		SFTPComponent sftpComponentMockTest = Mockito.spy(sftpComponentMock);
		Mockito.doReturn(-1).when(sftpComponentMockTest).downloadFileRemoteToLocal(Mockito.anyString(),
				Mockito.anyString());
		Mockito.doReturn(true).when(sftpComponentMockTest).uploadFileLocalToRemote(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyBoolean());
		assertEquals(true, sftpComponentMockTest.moveInboundToLocalViaProcess(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()));
	}

	@Test
	public void sholdNotMoveInboundToLocalViaProcess_WhenDownloadFail() {
		SFTPComponent sftpComponentMock = new SFTPComponent();
		SFTPComponent sftpComponentMockTest = Mockito.spy(sftpComponentMock);
		Mockito.doReturn(138).when(sftpComponentMockTest).downloadFileRemoteToLocal(Mockito.anyString(),
				Mockito.anyString());
		assertEquals(false, sftpComponentMockTest.moveInboundToLocalViaProcess(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()));
	}

	@Test
	public void sholdNotMoveInboundToLocalViaProcess_WhenUploadFail() {
		SFTPComponent sftpComponentMock = new SFTPComponent();
		SFTPComponent sftpComponentMockTest = Mockito.spy(sftpComponentMock);
		Mockito.doReturn(-1).when(sftpComponentMockTest).downloadFileRemoteToLocal(Mockito.anyString(),
				Mockito.anyString());
		Mockito.doReturn(false).when(sftpComponentMockTest).uploadFileLocalToRemote(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyBoolean());
		assertEquals(false, sftpComponentMockTest.moveInboundToLocalViaProcess(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()));
	}

	@Test
	public void sholdNotMoveInboundToLocalViaProcess_WhenDownloadUploadFail() {
		SFTPComponent sftpComponentMock = new SFTPComponent();
		SFTPComponent sftpComponentMockTest = Mockito.spy(sftpComponentMock);
		Mockito.doReturn(-1).doReturn(138).when(sftpComponentMockTest).downloadFileRemoteToLocal(Mockito.anyString(),
				Mockito.anyString());
		Mockito.doReturn(true).when(sftpComponentMockTest).uploadFileLocalToRemote(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyBoolean());
		assertEquals(false, sftpComponentMockTest.moveInboundToLocalViaProcess(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()));
	}

	@Test
	public void shouldExceptionForDownloadFile_WhenSFTPNotConnected() throws JSchException, SftpException, IOException {
		when(jsch.getSession(Mockito.anyString(), Mockito.anyString())).thenReturn(session);
		doThrow(new JSchException("UNKNOWN HOST EXCEPTION")).when(session).connect();
		doNothing().when(sftpChannel).cd(Mockito.anyString());
		when(resourceLoader.getResource(Mockito.anyString())).thenReturn(resource);
		when(resource.getFile()).thenReturn(getDummyFile()).thenReturn(fileRemote).thenReturn(getDummyFile());
		
		doThrow(new SftpException(0, "No such file.")).when(sftpChannel).get(Mockito.anyString());
		sftpComponent.downloadFileRemoteToLocal("/testRemote/test.txt", "/testLocal");
	}

	@Test
	public void shouldExceptionForDownloadFile_WhenEmptyRemote() throws JSchException, SftpException, IOException {
		when(jsch.getSession(Mockito.anyString(), Mockito.anyString())).thenReturn(session);
		doNothing().when(session).connect();
		when(session.openChannel("sftp")).thenReturn(sftpChannel);
		doNothing().when(sftpChannel).connect();

		doNothing().when(sftpChannel).cd(Mockito.anyString());

		when(resourceLoader.getResource(Mockito.anyString())).thenReturn(resource);
		when(resource.getFile()).thenReturn(getDummyFile()).thenReturn(fileRemote).thenReturn(getDummyFile());
		doThrow(new SftpException(0, "No such file.")).when(sftpChannel).get(Mockito.anyString());
		sftpComponent.downloadFileRemoteToLocal("/testRemote/test.txt", "/testLocal");
	}

	@Test
	public void shouldDownloadFile_WhenDirectyNotExist_FileWrite() throws JSchException, SftpException, IOException {
		when(jsch.getSession(Mockito.anyString(), Mockito.anyString())).thenReturn(session);
		doNothing().when(session).connect();
		when(session.openChannel("sftp")).thenReturn(sftpChannel);
		doNothing().when(sftpChannel).connect();

		doNothing().when(sftpChannel).cd(Mockito.anyString());
		when(resourceLoader.getResource(Mockito.anyString())).thenReturn(resource);
		when(resource.getFile()).thenReturn(getDummyFile()).thenReturn(fileRemote).thenReturn(getDummyFile());

		when(sftpChannel.get(Mockito.anyString())).thenReturn(inputStream);
		when(fileOpUtils.getBufferedInputStream(Mockito.any())).thenReturn(bufferedInputStream);

		when(fileRemote.exists()).thenReturn(false);
		when(fileRemote.mkdir()).thenReturn(true);

		when(fileOpUtils.getFileOutputStream(Mockito.any())).thenReturn(outPutStream);
		when(fileOpUtils.getBufferedOutputStream(Mockito.any())).thenReturn(bufferedOutputStream);
		when(bufferedInputStream.read(Mockito.any())).thenReturn(141).thenReturn(-1);
		doNothing().when(bufferedOutputStream).write(Mockito.any(),Mockito.anyInt(),Mockito.anyInt());
		
		doNothing().when(sftpChannel).rm(Mockito.anyString());

		doNothing().when(sftpChannel).disconnect();
		doNothing().when(session).disconnect();

		sftpComponent.downloadFileRemoteToLocal("/testRemote/test.txt", "/testLocal");
	}
	@Test
	public void shouldDownloadFile_WhenDirectyExist_FileWrite() throws JSchException, SftpException, IOException {
		when(jsch.getSession(Mockito.anyString(), Mockito.anyString())).thenReturn(session);
		doNothing().when(session).connect();
		when(session.openChannel("sftp")).thenReturn(sftpChannel);
		doNothing().when(sftpChannel).connect();
		
		doNothing().when(sftpChannel).cd(Mockito.anyString());
		when(resourceLoader.getResource(Mockito.anyString())).thenReturn(resource);
		when(resource.getFile()).thenReturn(getDummyFile()).thenReturn(fileRemote).thenReturn(getDummyFile());
		
		when(sftpChannel.get(Mockito.anyString())).thenReturn(inputStream);
		when(fileOpUtils.getBufferedInputStream(Mockito.any())).thenReturn(bufferedInputStream);
		
		when(fileRemote.exists()).thenReturn(true);
		
		when(fileOpUtils.getFileOutputStream(Mockito.any())).thenReturn(outPutStream);
		when(fileOpUtils.getBufferedOutputStream(Mockito.any())).thenReturn(bufferedOutputStream);
		when(bufferedInputStream.read(Mockito.any())).thenReturn(141).thenReturn(-1);
		doNothing().when(bufferedOutputStream).write(Mockito.any(),Mockito.anyInt(),Mockito.anyInt());
		
		doNothing().when(sftpChannel).rm(Mockito.anyString());
		
		doNothing().when(sftpChannel).disconnect();
		doNothing().when(session).disconnect();
		
		sftpComponent.downloadFileRemoteToLocal("/testRemote/test.txt", "/testLocal");
	}
	@Test
	public void shouldExceptionForUploadFile_WhenEmptyRemote() throws JSchException, SftpException, IOException {
		when(jsch.getSession(Mockito.anyString(), Mockito.anyString())).thenReturn(session);
		doNothing().when(session).connect();
		when(session.openChannel("sftp")).thenReturn(sftpChannel);
		doNothing().when(sftpChannel).connect();

		doThrow(new SftpException(0, "No such file.")).when(sftpChannel).cd(Mockito.anyString());

		sftpComponent.uploadFileLocalToRemote("/testRemote/test.txt", "/testLocal",false);
	}
	@Test
	public void shouldUploadFileLocalToRemote_WhenPathNotModified() throws JSchException, SftpException, IOException{
		when(jsch.getSession(Mockito.anyString(), Mockito.anyString())).thenReturn(session);
		doNothing().when(session).connect();
		when(session.openChannel("sftp")).thenReturn(sftpChannel);
		doNothing().when(sftpChannel).connect();
		doNothing().when(sftpChannel).cd(Mockito.anyString());
		when(resourceLoader.getResource(Mockito.anyString())).thenReturn(resource);
		when(resource.getFile()).thenReturn(fileRemote);
		when(fileOpUtils.getFileInputStream(fileRemote)).thenReturn(fileInputStream);
		when(fileRemote.exists()).thenReturn(true);
		when(fileRemote.delete()).thenReturn(true);
		sftpComponent.uploadFileLocalToRemote("/testLocal/test.txt", "/testRemote", false);
	}
	@Test
	public void shouldUploadFileLocalToRemote_WhenPathNotModified_FileNotExist() throws JSchException, SftpException, IOException{
		when(jsch.getSession(Mockito.anyString(), Mockito.anyString())).thenReturn(session);
		doNothing().when(session).connect();
		when(session.openChannel("sftp")).thenReturn(sftpChannel);
		doNothing().when(sftpChannel).connect();
		doNothing().when(sftpChannel).cd(Mockito.anyString());
		when(resourceLoader.getResource(Mockito.anyString())).thenReturn(resource);
		when(resource.getFile()).thenReturn(fileRemote);
		when(fileOpUtils.getFileInputStream(fileRemote)).thenReturn(fileInputStream);
		when(fileRemote.exists()).thenReturn(false);
		sftpComponent.uploadFileLocalToRemote("/testLocal/test.txt", "/testRemote", false);
	}
	@Test
	public void shouldUploadFileLocalToRemote_WhenPathNotModified_FileNotDeleted() throws JSchException, SftpException, IOException{
		when(jsch.getSession(Mockito.anyString(), Mockito.anyString())).thenReturn(session);
		doNothing().when(session).connect();
		when(session.openChannel("sftp")).thenReturn(sftpChannel);
		doNothing().when(sftpChannel).connect();
		doNothing().when(sftpChannel).cd(Mockito.anyString());
		when(resourceLoader.getResource(Mockito.anyString())).thenReturn(resource);
		when(resource.getFile()).thenReturn(fileRemote);
		when(fileOpUtils.getFileInputStream(fileRemote)).thenReturn(fileInputStream);
		when(fileRemote.exists()).thenReturn(true);
		when(fileRemote.delete()).thenReturn(false);
		sftpComponent.uploadFileLocalToRemote("/testLocal/test.txt", "/testRemote", false);
	}
	@Test
	public void shouldUploadFileLocalToRemote_WhenPathModified() throws JSchException, SftpException, IOException{
		when(jsch.getSession(Mockito.anyString(), Mockito.anyString())).thenReturn(session);
		doNothing().when(session).connect();
		when(session.openChannel("sftp")).thenReturn(sftpChannel);
		doNothing().when(sftpChannel).connect();
		doNothing().when(sftpChannel).cd(Mockito.anyString());
		when(resourceLoader.getResource(Mockito.anyString())).thenReturn(resource);
		when(resource.getFile()).thenReturn(fileRemote);
		when(fileOpUtils.getFileInputStream(fileRemote)).thenReturn(fileInputStream);
		doNothing().when(sftpChannel).put(Mockito.any(FileInputStream.class), Mockito.anyString());
		when(fileRemote.getName()).thenReturn("test.csv");
		doNothing().when(sftpChannel).rename(Mockito.anyString(),Mockito.anyString());	
		when(fileRemote.exists()).thenReturn(true);
		when(fileRemote.delete()).thenReturn(true);
		
		sftpComponent.uploadFileLocalToRemote("/testLocal/test.txt", "/testRemote", true);
	}
	@Test
	public void shouldUploadFileLocalToRemote_WhenPathModified_FileNotExist() throws JSchException, SftpException, IOException{
		when(jsch.getSession(Mockito.anyString(), Mockito.anyString())).thenReturn(session);
		doNothing().when(session).connect();
		when(session.openChannel("sftp")).thenReturn(sftpChannel);
		doNothing().when(sftpChannel).connect();
		doNothing().when(sftpChannel).cd(Mockito.anyString());
		when(resourceLoader.getResource(Mockito.anyString())).thenReturn(resource);
		when(resource.getFile()).thenReturn(fileRemote);
		when(fileOpUtils.getFileInputStream(fileRemote)).thenReturn(fileInputStream);
		doNothing().when(sftpChannel).put(Mockito.any(FileInputStream.class), Mockito.anyString());
		when(fileRemote.getName()).thenReturn("test.csv");
		doNothing().when(sftpChannel).rename(Mockito.anyString(),Mockito.anyString());	
		when(fileRemote.exists()).thenReturn(false);
		
		sftpComponent.uploadFileLocalToRemote("/testLocal/test.txt", "/testRemote", true);
	}
	@Test
	public void shouldUploadFileLocalToRemote_WhenPathModified_FileNotDeleted() throws JSchException, SftpException, IOException{
		when(jsch.getSession(Mockito.anyString(), Mockito.anyString())).thenReturn(session);
		doNothing().when(session).connect();
		when(session.openChannel("sftp")).thenReturn(sftpChannel);
		doNothing().when(sftpChannel).connect();
		doNothing().when(sftpChannel).cd(Mockito.anyString());
		when(resourceLoader.getResource(Mockito.anyString())).thenReturn(resource);
		when(resource.getFile()).thenReturn(fileRemote);
		when(fileOpUtils.getFileInputStream(fileRemote)).thenReturn(fileInputStream);
		doNothing().when(sftpChannel).put(Mockito.any(FileInputStream.class), Mockito.anyString());
		when(fileRemote.getName()).thenReturn("test.csv");
		doNothing().when(sftpChannel).rename(Mockito.anyString(),Mockito.anyString());	
		when(fileRemote.exists()).thenReturn(true);
		when(fileRemote.delete()).thenReturn(false);
		sftpComponent.uploadFileLocalToRemote("/testLocal/test.txt", "/testRemote", true);
	}

	private File getDummyFile() {
		ClassLoader classLoader = new SFTPComponentTest().getClass().getClassLoader();
		return new File(classLoader.getResource("test.csv").getFile());
	}

}
