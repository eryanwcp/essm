/*
 * Copyright (c) 2012 Lei Hu. All rights reserved.
 * Lei Hu PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package test.common.mail.receiver;

import com.eryansky.common.mail.config.IMAPServerConfig;
import com.eryansky.common.mail.config.ServerConfig;
import com.eryansky.common.mail.entity.Account;
import com.eryansky.common.mail.receiver.Receiver;
import org.junit.*;

import javax.mail.Message;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static org.junit.Assert.fail;

/**
 * @author L <qiyuan4f@gmail.com>
 * @version 1.0 <2012-10-24 00:14>
 */
public class IMAPReceiverTest {
	private static Map<Account, String> cases = new HashMap<Account, String>();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Account account = null;
		ServerConfig server;
		
		//163 SSL
//		account = new Account();
//		server = new IMAPServerConfig();
//		server.setAddress("imap.ccntgrid.org");
//		server.setEncryptionType(ServerConfig.EncryptionType.SSL);
//		account.setReceiverServer(server);
//		account.setMailAddress("L@ccntgrid.org");
//		account.setUsername("L");
//		account.setPassword("");
//		cases.put(account, "不支持IMAP SSL协议");
		
		// 163
		account = new Account();
		server = new IMAPServerConfig();
		server.setAddress("imap.qq.com");
		server.setEncryptionType(ServerConfig.EncryptionType.SSL);
		server.setPort(993);
		account.setReceiverServer(server);
		account.setMailAddress("1036416764@qq.com");
		account.setUsername("1036416764");
		account.setPassword("5736165wcp");
		cases.put(account, "不支持IMAP协议");

	}

	/**
	 * 测试
	 */
	@Test
	public void testFetch() {
		for (Entry<Account, String> item : cases.entrySet()) {
			Receiver receiver = null;
			try {
				receiver = Receiver.make(item.getKey());
				receiver.open();
				Message[] list = receiver.search(Receiver.SearchType.UNREAD);
				System.out.println(list.length);
				for(Message message:list){
					System.out.println(message.getSubject());
				}
			} catch (Exception e) {
				fail(item.getValue());
			} finally {
				if (receiver != null) {
					receiver.close();
				}
			}
		}
	}
}
