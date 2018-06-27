package com.eryansky.fastweixin.message;


import com.eryansky.fastweixin.message.util.MessageBuilder;
import com.eryansky.fastweixin.util.StrUtil;

public class CustomMsg extends BaseMsg {

    private String kfAccount;

    public CustomMsg(){

    }

    public CustomMsg(String kfAccount) {
        this.kfAccount = kfAccount;
    }

    public String getKfAccount() {
        return kfAccount;
    }

    public void setKfAccount(String kfAccount) {
        this.kfAccount = kfAccount;
    }

    @Override
    public String toXml() {
        MessageBuilder mb = new MessageBuilder(super.toXml());
        mb.addData("MsgType", RespType.KF);
        //可以不指定客服
        if(StrUtil.isNotBlank(kfAccount)) {
            mb.append("<TransInfo>\n");
            mb.addData("KfAccount", kfAccount);
            mb.append("</TransInfo>\n");
        }
        mb.surroundWith("xml");
        return mb.toString();
    }

}