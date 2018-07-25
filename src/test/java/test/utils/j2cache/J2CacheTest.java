/**
 * Copyright (c) 2014-2018 http://www.jfit.com.cn
 * <p>
 * 江西省锦峰软件科技有限公司
 */
package test.utils.j2cache;


import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.j2cache.CacheChannel;
import com.eryansky.j2cache.J2Cache;

import java.util.Calendar;
import java.util.Date;

/**
 * @author 温春平 jfwencp@jx.tobacco.gov.cn
 * @date 2018-07-23 
 */
public class J2CacheTest {

    public static void main(String[] args) {
        String region = "cache1";
        CacheChannel channel = J2Cache.getChannel();
        channel.set(region,"userid","t1",false);
        System.out.println(channel.get(region,"userid"));

        channel.clear(region);
        Date d1 = Calendar.getInstance().getTime();
        for(int i=0;i<10;i++){
            channel.set(region,"i"+i,i,false);
        }

        for(int i=0;i<10;i++){
            System.out.println(channel.get(region,"i"+i));
        }
        Date d2 = Calendar.getInstance().getTime();
        System.out.println(d2.getTime() - d1.getTime());
        System.out.println(JsonMapper.toJsonString(channel.keys("cache1")));
    }
}
