/*
 * Copyright (c) 2012 Lei Hu. All rights reserved.
 * Lei Hu PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package test.common.mail.receiver;

import com.eryansky.common.mail.config.POP3ServerConfig;
import com.eryansky.common.mail.config.ServerConfig;
import com.eryansky.common.mail.entity.Account;
import com.eryansky.common.mail.receiver.Receiver;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.mail.entity.MailAccount;
import com.eryansky.modules.mail.service.ContactGroupManager;
import com.eryansky.modules.mail.service.EmailManager;
import com.eryansky.modules.mail.service.InboxManager;
import com.eryansky.modules.mail.service.MailAccountManager;
import com.eryansky.modules.mail.service.MailContactManager;
import com.eryansky.modules.mail.service.ReceiveInfoManager;
import com.eryansky.modules.mail.utils.MailUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
//        "classpath:applicationContext-quartz.xml",
		"classpath:applicationContext-ehcache.xml" })
public class ReceiverTest {
	private static Map<Account, String> cases = new HashMap<Account, String>();

	@Autowired
	private EmailManager emailManager;
	@Autowired
	private ReceiveInfoManager receiveInfoManager;
	@Autowired
	private MailContactManager mailContactManager;
	@Autowired
	private ContactGroupManager contactGroupManager;
	@Autowired
	private InboxManager inboxManager;
	@Autowired
	private MailAccountManager mailAccountManager;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Account account = null;
		ServerConfig server;
		//163 SSL
//		account = new Account();
//		server = new POP3ServerConfig();
//		server.setAddress("pop.ccntgrid.org");
//		server.setEncryptionType(ServerConfig.EncryptionType.SSL);
//		account.setReceiverServer(server);
//		account.setMailAddress("L@ccntgrid.org");
//		account.setUsername("L");
//		account.setPassword("");
//		cases.put(account, "不支持POP3 SSL协议");
		
		// 163
		account = new Account();
		server = new POP3ServerConfig();
		server.setAddress("pop.163.com");
		account.setReceiverServer(server);
		account.setMailAddress("eryanwcp@163.com");
		account.setUsername("eryanwcp");
		account.setPassword("5736165wcp");
		cases.put(account, "不支持POP3协议");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFetch() {
		MailAccount mailAccount = mailAccountManager.getById("7e9e9350e8cb4af1ac3a7c6672c44c02");
		Receiver receiver = null;
		try {
			receiver = Receiver.make(mailAccount.toAccount());
			boolean open = receiver.open();
			System.out.println(mailAccount.getMailAddress()+" open:"+open);
			Message[] list = receiver.search(Receiver.SearchType.ALL);
			System.out.println("size:"+list.length);
			System.out.println(list.length);
			for(Message message:list){
				MimeMessage mimeMessage = (MimeMessage)message;

				try {
//				System.out.println(MailUtils.getSubject(mimeMessage));
					StringBuffer content = new StringBuffer(30);
					MailUtils.getMailTextContent(mimeMessage, content);
//				System.out.println(content);

					List<InternetAddress> rAddress = MailUtils.getRecipientAddress(mimeMessage, Message.RecipientType.TO);
					if(Collections3.isNotEmpty(rAddress)){
                        for(InternetAddress rAddres:rAddress){
                            System.out.println(MailUtils.decode(rAddres.getAddress()));
                        }
                    }
					rAddress = MailUtils.getRecipientAddress(mimeMessage, Message.RecipientType.CC);
					if(Collections3.isNotEmpty(rAddress)){
                        for(InternetAddress rAddres:rAddress){
                            System.out.println(MailUtils.decode(rAddres.getAddress()));
                        }
                    }
					rAddress = MailUtils.getRecipientAddress(mimeMessage, Message.RecipientType.BCC);
					if(Collections3.isNotEmpty(rAddress)){
                        for(InternetAddress rAddres:rAddress){
                            System.out.println(MailUtils.decode(rAddres.getAddress()));
                        }
                    }

					System.out.println(MailUtils.getFrom(mimeMessage).getAddress());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				receiver.close();
			} catch (Exception e) {
			}
		}
	}

	private static Pattern encodeStringPattern = Pattern.compile("=\\?(.+)\\?(B|Q)\\?(.+)\\?=", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private final String[] CHARTSET_HEADER = new String[] { "Subject", "From", "To", "Cc", "Delivered-To" };


}
