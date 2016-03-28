package test;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;

import com.eryansky.common.utils.collections.Collections3;

/**
 *   测试.
 */
public class Test {

	public static void main(String[] args) {

        String s = "1";
        String[] ss = s.split(",");
        for(String sss:ss){
            System.out.println(sss);
        }

        File dir = new File("e:\\");
        System.out.println(dir.isDirectory());
        dir.deleteOnExit();

        String path = "2014\\system\\notice\\1\\12\\_6633663531316535613137346466666530626461376533313261383138303266_10月.xls";
        System.out.println(StringUtils.substringBeforeLast(path, File.separator));

        List<String> list1 = Lists.newArrayList();
        List<String> list2 = Lists.newArrayList();
        list1.add("01");
        list1.add("00");
        list1.add("0011");
        int min = 0;
        Iterator<String> iterator = list1.iterator();
        while (iterator.hasNext()){
            String str = iterator.next();
            System.out.println(str);
            if(min==0){
                min = str.length();
                list2.add(str);
            }else if(str.length() <=min){
                list2.add(str);
            }

        }
        System.out.println(JsonMapper.getInstance().toJson(list2));



//        System.out.println(new NullPointerException("空指针一次").getClass().getSimpleName());
//        System.out.println(new NullPointerException("空指针一次").getClass().getName());
//        List<String> ids = null;
//		if (!Collections3.isEmpty(ids)) {
//			for (Long id : ids) {
//				System.out.println(id);
//			}
//		}
//
//
//		try {
//			Validate.notBlank("", "queryString不能为空");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
		
		
	}

}
