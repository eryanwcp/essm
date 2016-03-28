/*
 * Copyright (c) 2012 Lei Hu. All rights reserved.
 * Lei Hu PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package test.common.mail.receiver;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import com.eryansky.common.mail.entity.Account;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AccountTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	public void testGetMail() {
		Map<String, String> cases = new HashMap<String, String>();
		cases.put("aa@gmail.com", "aa@gmail.com"); // 简单的正确性测试
		cases.put("AA@Gmail.com", "aa@gmail.com"); // 保存为小写的测试
		cases.put("aaaa", null); // 错误测试

		Account test = null;
		
		for (Map.Entry<String, String>item : cases.entrySet()) {
			try {
				test = new Account();
				test.setMailAddress(item.getKey());
				
			} catch (Exception e) {
				
			}
			
			assertEquals(item.getValue(), test.getMailAddress());
		}
	}

	@Test
	public void testSetMail() {
		Map<String, Boolean> cases = new HashMap<String, Boolean>();
		
		cases.put("xx@gmail.com", true);
		cases.put("asdasdas", false);
		cases.put("asd@a.com@b.com", false);
		
		for (Map.Entry<String, Boolean>item : cases.entrySet()) {
			testMailHelper(item.getKey(), item.getValue());
		}
	}

	/**
	 * @param mail 邮箱地址
	 * @param expected 期望的结果
	 */
	private void testMailHelper(String mail, boolean expected) {
		Account test = new Account();

		if (expected) {
			try {
				test.setMailAddress(mail);
			} catch (Exception e) {
				fail(mail + " : 邮箱是正确的，但判断错误");
			}
		} else {
			boolean flag = false;
			try {
				test.setMailAddress(mail);
			} catch (IllegalArgumentException e) {
				flag = true;
			} catch (Exception e) {
				fail(mail + " 错误邮箱，但发生其他错误");
			}
			if (!flag) {
				fail(mail + " 错误邮箱，但认为是正确的");
			}
		}
	}
}
