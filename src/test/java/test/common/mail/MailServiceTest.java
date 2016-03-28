/*
 * Copyright (c) 2012 Lei Hu. All rights reserved.
 * Lei Hu PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package test.common.mail;

import com.eryansky.common.mail.config.IMAPServerConfig;
import com.eryansky.common.mail.config.ServerConfig;
import com.eryansky.common.mail.entity.Account;
import com.eryansky.common.mail.exception.NotSupportedException;
import com.eryansky.common.mail.support.AbstractMonitor;
import org.junit.*;

/**
 * @author L <qiyuan4f@gmail.com>
 * @version 1.0 <2012-10-24 00:14>
 */
public class MailServiceTest {
	Account account;
	AbstractMonitor abstractMonitor;

	/**
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 综合测试
	 */
	@Test
	public void testService() throws InterruptedException, NotSupportedException {

		ServerConfig server;

		account = new Account();
		server = new IMAPServerConfig();
		server.setAddress("imap.163.com");
		account.setReceiverServer(server);
		account.setMailAddress("");
		account.setUsername("");
		account.setPassword("");
		account.setWebMailServerUrl("");
	}
}
