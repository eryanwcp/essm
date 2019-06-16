/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eryansky.j2cache;

import com.eryansky.j2cache.util.FstJSONSerializer;
import com.eryansky.j2cache.util.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;

/**
 * 命令消息封装
 * 格式：
 * 第1个字节为命令代码，长度1 [OPT]
 * 第2、3个字节为region长度，长度2 [R_LEN]
 * 第4、N 为 region 值，长度为 [R_LEN]
 * 第N+1、N+2 为 key 长度，长度2 [K_LEN]
 * 第N+3、M为 key值，长度为 [K_LEN]
 * 
 * @author Winter Lau(javayou@gmail.com)
 */
public class Command implements java.io.Serializable{

	private final static Logger logger = LoggerFactory.getLogger(Command.class);


	public final static byte OPT_JOIN 	   = 0x01;	//加入集群
	public final static byte OPT_EVICT_KEY = 0x02; 	//删除缓存
	public final static byte OPT_CLEAR_KEY = 0x03; 	//清除缓存
	public final static byte OPT_QUIT 	   = 0x04;	//退出集群
	
	private int src;
	private int operator;
	private String region;
	private String[] keys;

	private static Serializer serializer;

	static {
		serializer = new FstJSONSerializer(null);
	}

	public static int genRandomSrc() {
		long ct = System.currentTimeMillis();
		Random rnd_seed = new Random(ct);
		return (int)(rnd_seed.nextInt(10000) * 1000 + ct % 1000);
	}

	public Command(){}//just for json deserialize , dont remove it.

	public Command(byte o, String r, String...keys){
		this.operator = o;
		this.region = r;
		this.keys = keys;
	}

	public static Command join() {
		return new Command(OPT_JOIN, null);
	}

	public static Command quit() {
		return new Command(OPT_QUIT, null);
	}

	public String json() {
		try {
			return new String(serializer.serialize(this));
		} catch (IOException e) {
			logger.warn("Failed to json j2cache command", e);
		}
		return null;
	}

	public static Command parse(String json) {
		try {
			return (Command) serializer.deserialize(json.getBytes());
		} catch (IOException e) {
			logger.warn("Failed to parse j2cache command: {}", json, e);
		}
		return null;
	}

	public int getOperator() {
		return operator;
	}

	public String getRegion() {
		return region;
	}

	public String[] getKeys() {
		return keys;
	}

	public int getSrc() {
		return src;
	}

    public void setSrc(int src) {
        this.src = src;
    }

    public void setOperator(int operator) {
        this.operator = operator;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    @Override
	public String toString(){
		return json();
	}

}
