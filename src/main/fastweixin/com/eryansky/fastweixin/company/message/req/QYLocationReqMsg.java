package com.eryansky.fastweixin.company.message.req;
/**
 *  微信企业号位置消息事件
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYLocationReqMsg extends QYBaseReqMsg {

    private double locationX;
    private double locationY;
    private int scale;
    private String label;

    public QYLocationReqMsg(double locationX, double locationY, int scale, String label) {
        super();
        this.locationX = locationX;
        this.locationY = locationY;
        this.scale = scale;
        this.label = label;
        setMsgType(QYReqType.LOCATION);
    }

    public double getLocationX() {
        return locationX;
    }

    public QYLocationReqMsg setLocationX(double locationX) {
        this.locationX = locationX;
        return this;
    }

    public double getLocationY() {
        return locationY;
    }

    public QYLocationReqMsg setLocationY(double locationY) {
        this.locationY = locationY;
        return this;
    }

    public int getScale() {
        return scale;
    }

    public QYLocationReqMsg setScale(int scale) {
        this.scale = scale;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public QYLocationReqMsg setLabel(String label) {
        this.label = label;
        return this;
    }

    @Override
    public String toString(){
        return "QYLocationReqMsg [location_x=" + locationX
                + ", location_y=" + locationY + ", scale=" + scale
                + ", label=" + label + ", toUserName=" + toUserName + ", fromUserName="
                + fromUserName + ", createTime=" + createTime + ", msgType="
                + msgType + ", msgId=" + msgId + ", agentId=" + agentId + "]";
    }
}
