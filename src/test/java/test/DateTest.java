package test;

import com.eryansky.common.utils.MoneyFormat;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import test.eryansky.Person;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * User: 尔演&Eryan eryanwcp@gmail.com
 * Date: 13-9-10 下午8:38
 */
public class DateTest {
    public static void main(String[] args) throws Exception {
        Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse("2013-09-11");
        System.out.println(JsonMapper.nonDefaultMapper().toJson(d1));
        Person p = new Person();
        p.setDate(d1);
        System.out.println(JsonMapper.nonDefaultMapper().toJson(p));
    }

}
