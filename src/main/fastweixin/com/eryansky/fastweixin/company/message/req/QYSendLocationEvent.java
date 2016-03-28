package com.eryansky.fastweixin.company.message.req;
/**
 *
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class QYSendLocationEvent extends QYMenuEvent {

    private String locationX;
    private String locationY;
    private int scale;
    private String label;
    private String poiname;

    public QYSendLocationEvent(String eventKey, String locationX, String locationY, int scale, String label, String poiname) {
        super(eventKey);
        this.locationX = locationX;
        this.locationY = locationY;
        this.scale = scale;
        this.label = label;
        this.poiname = poiname;
    }

    public String getLocationX() {
        return locationX;
    }

    public QYSendLocationEvent setLocationX(String locationX) {
        this.locationX = locationX;
        return this;
    }

    public String getLocationY() {
        return locationY;
    }

    public QYSendLocationEvent setLocationY(String locationY) {
        this.locationY = locationY;
        return this;
    }

    public int getScale() {
        return scale;
    }

    public QYSendLocationEvent setScale(int scale) {
        this.scale = scale;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public QYSendLocationEvent setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getPoiname() {
        return poiname;
    }

    public QYSendLocationEvent setPoiname(String poiname) {
        this.poiname = poiname;
        return this;
    }

    @Override
    public String toString(){
        return "QYMenuEvent [locationX=" + locationX + ", locationY=" + locationY
                + ", scale=" + scale + ", label=" + label + ", poiname=" + poiname
                + ", eventKey=" + getEventKey()
                + ", toUserName=" + toUserName + ", fromUserName="
                + fromUserName + ", createTime=" + createTime + ", msgType="
                + msgType + ", event=" + event + ", agentId=" + agentId + "]";
    }
}
