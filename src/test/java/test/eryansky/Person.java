package test.eryansky;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * User: 尔演&Eryan eryanwcp@gmail.com
 * Date: 13-9-11 下午2:27
 */
@JsonFilter(" ")
public class Person implements Serializable {

    public Person(){

    }
    private Date date;

    @JsonFormat(pattern = "yyyy-MM-dd EEEE", timezone = "GMT+08:00")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}