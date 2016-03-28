/*
 * Copyright (c) 2012 Lei Hu. All rights reserved.
 * Lei Hu PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package test.common.mail.receiver;

import com.beust.jcommander.internal.Lists;
import com.eryansky.common.mail.config.POP3ServerConfig;
import com.eryansky.common.mail.config.ServerConfig;
import com.eryansky.common.mail.entity.Account;
import com.eryansky.common.mail.receiver.Receiver;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.eryansky.modules.mail.service.ContactGroupManager;
import com.eryansky.modules.mail.service.EmailManager;
import com.eryansky.modules.mail.service.InboxManager;
import com.eryansky.modules.mail.service.MailAccountManager;
import com.eryansky.modules.mail.service.MailContactManager;
import com.eryansky.modules.mail.service.ReceiveInfoManager;
import com.eryansky.modules.mail.utils.MailUtils;
import org.junit.*;
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
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
		"classpath:applicationContext-mybatis.xml",
		"classpath:applicationContext-task.xml",
		"classpath:applicationContext-ehcache.xml" })
public class POP3ReceiverTest {
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
		server.setAddress("mail.jx.tobacco.gov.cn");
		account.setReceiverServer(server);
		account.setMailAddress("chenqq@jx.tobacco.gov.cn");
		account.setUsername("chenqq");
		account.setPassword("123456");
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
		for (Entry<Account, String> item : cases.entrySet()) {
			Receiver receiver = null;
			try {
				receiver = Receiver.make(item.getKey());
				receiver.open();
				System.out.println(receiver.getFolder().getNewMessageCount());;
				Message[] list = receiver.search(Receiver.SearchType.ALL);
				System.out.println(list.length);
				Map<String,List<String>> data = Maps.newHashMap();
				Set<String> sets = Sets.newHashSet();
				for(Message message:list){
					MimeMessage mimeMessage = (MimeMessage)message;
//					System.out.println(MailUtils.getSubject(mimeMessage));
//					System.out.println(receiver.getUID(mimeMessage));
					InternetAddress sender = MailUtils.getFrom(mimeMessage);
//					System.out.println(sender.getAddress());

					Map<String,String> map = Maps.newHashMap();
					map.put("title",MailUtils.getSubject(mimeMessage));
					String uuid = receiver.getUID(mimeMessage);
					List<String> titles = Lists.newArrayList();
					if(Collections3.isEmpty(titles)){
						titles = Lists.newArrayList();
					}
					titles.add(MailUtils.getSubject(mimeMessage));
					data.put(uuid.toUpperCase(),titles);
					sets.add(uuid.toUpperCase());

//					if(headCharset == null){
//						subject = new String(subject.getBytes("iso8859-1"),"gbk");
//					}
//					System.out.println(subject);

//					MimeMessageParser parser = new MimeMessageParser((MimeMessage) message).parse();
//					System.out.println(mimeMessage.getSubject());
//					System.out.println(MailUtils.getSubject(mimeMessage));
//					System.out.println(MailUtils.decodeWord(MailUtils.getSubject(mimeMessage)));
//					System.out.println(MailUtils.getSubject2(mimeMessage));


				}
				System.out.println(sets.size());
				System.out.println(data.size());
				System.out.println(JsonMapper.getInstance().toJson(sets));
				System.out.println(JsonMapper.getInstance().toJson(data));
			} catch (Exception e) {
				e.printStackTrace();
				fail(item.getValue());
			} finally {
				receiver.close();
			}
		}
	}

	private static Pattern encodeStringPattern = Pattern.compile("=\\?(.+)\\?(B|Q)\\?(.+)\\?=", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private final String[] CHARTSET_HEADER = new String[] { "Subject", "From", "To", "Cc", "Delivered-To" };


}
