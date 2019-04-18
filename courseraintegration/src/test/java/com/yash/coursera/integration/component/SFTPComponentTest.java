package com.yash.coursera.integration.component;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.yash.coursera.integration.components.SFTPComponent;

@RunWith(MockitoJUnitRunner.class)
public class SFTPComponentTest {

	@InjectMocks
	SFTPComponent sftpComponent;
	@Mock
	private JSch jsch;
	@Mock
	private Session session;
	@Mock
	private Channel channel;
	@Mock
	private ChannelSftp sftpChannel;

	@Before
	public void setUp() {
		ReflectionTestUtils.setField(sftpComponent, "getSFTPHost", "sftp.testHost.com");
		ReflectionTestUtils.setField(sftpComponent, "getSFTPUser", "testUser");
		ReflectionTestUtils.setField(sftpComponent, "getSFTPPassword", "testPassword");
		jsch = mock(JSch.class);
		sftpComponent.setJsch(jsch);
	}

	@Test
	public void shouldDownloadFileFromRemoteToLocal() throws IOException, SftpException, JSchException {
		/*BufferedInputStream bufferedInputStream = mock(BufferedInputStream.class);
		BufferedOutputStream bufferedOutputStream = mock(BufferedOutputStream.class);
		InputStream inputStream = mock(InputStream.class);
		OutputStream outputStream = mock(OutputStream.class);
		when(jsch.getSession(Mockito.anyString(), Mockito.anyString())).thenReturn(session);
		doNothing().when(session).connect();
		when(session.openChannel("sftp")).thenReturn(channel);
		doNothing().when(channel).connect();
		when(channel.getOutputStream()).thenReturn(outputStream);
		when(sftpChannel.get(Mockito.anyString())).thenReturn(bufferedInputStream);
		when(bufferedInputStream.read(Mockito.any())).thenReturn(-1);

		sftpComponent.downloadFileRemoteToLocal("/testRemote/test.txt", "/testLocal");*/
	}

}
