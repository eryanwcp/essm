package com.eryansky.fastweixin.company.message.req;
/**
 *  微信企业号地理位置上报事件
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class QYLocationEvent extends QYBaseEvent {

    private double latitude;
    private double longitude;
    private double precision;

    public QYLocationEvent(double latitude, double longitude, double precision) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.precision = precision;
        setEvent(QYEventType.LOCATION);
    }

    public double getLatitude() {
        return latitude;
    }

    public QYLocationEvent setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public QYLocationEvent setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getPrecision() {
        return precision;
    }

    public QYLocationEvent setPrecision(double precision) {
        this.precision = precision;
        return this;
    }

    @Override
    public String toString(){
        return "QYLocationEvent [latitude=" + latitude + ", longitude" + longitude
                + ", precision=" + precision + ", toUserName=" + toUserName + ", fromUserName="
                + fromUserName + ", createTime=" + createTime + ", msgType="
                + msgType + ", event=" + event + ", agentId=" + agentId + "]";
    }
}
