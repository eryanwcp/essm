package com.eryansky.server.impl;

import java.util.*;

import javax.jws.WebService;

import com.eryansky.common.utils.DateUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.modules.notice._enum.MessageReceiveObjectType;
import com.eryansky.modules.notice._enum.ReceiveObjectType;
import com.eryansky.modules.notice.service.NoticeService;
import com.eryansky.modules.sys.mapper.Organ;
import com.eryansky.modules.sys.service.OrganService;
import com.eryansky.modules.sys.service.SystemService;
import com.eryansky.server.IApiWebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.modules.notice.utils.MessageUtils;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.service.UserService;
import com.eryansky.server.result.WSResult;


/**
 * 外部API接口实现类
 */
//serviceName与portName属性指明WSDL中的名称, endpointInterface属性指向Interface定义类.
@WebService(serviceName = "ApiWebService", portName = "ApiWebServicePort", endpointInterface = "com.eryansky.server.IApiWebService")
public class ApiWebServiceImpl implements IApiWebService {

    private static Logger logger = LoggerFactory.getLogger(ApiWebServiceImpl.class);

    private UserService userService = SpringContextHolder.getBean(UserService.class);
    private OrganService organService = SpringContextHolder.getBean(OrganService.class);
    private NoticeService noticeService = SpringContextHolder.getBean(NoticeService.class);


    @Override
    public WSResult sendMessage(String data) {
        logger.debug(data);
        try {
            Map<String, Object> map = JsonMapper.getInstance().fromJson(data, HashMap.class);
            if(map == null){
                logger.error("请求参数格式错误:" + data);
                return WSResult.buildResult(WSResult.class, WSResult.PARAMETER_ERROR, "请求参数格式错误");
            }

            String appId = (String) map.get("appId");
            String serviceId = (String) map.get("serviceId");
            String senderId = (String) map.get("senderId");
            String content = (String) map.get("content");
            String linkUrl = (String) map.get("linkUrl");
            String date = (String) map.get("date");
            Date sendTime = DateUtils.parseDate(date);
            List<String> receiveIds = (List) map.get("receiveIds");
            String receiveType = (String) map.get("receiveType");
            List<String> tipType = (List) map.get("tipType");
            String _receiveType = receiveType;
            try {
                checkOptional(appId, "appId");
                checkOptional(serviceId, "serviceId");
                checkOptional(content, "content");
                if (Collections3.isEmpty(receiveIds)) {
                    return WSResult.buildResult(WSResult.class, WSResult.PARAMETER_ERROR, "请求参数[receiveIds]不能为空");
                }
                if(StringUtils.isBlank(_receiveType) || !MessageReceiveObjectType.Organ.getValue().equals(_receiveType)){
                    _receiveType = MessageReceiveObjectType.User.getValue();
                }
            } catch (Exception e) {//2.判断必填参数
                logger.error(e.getMessage());
                return WSResult.buildResult(WSResult.class, WSResult.PARAMETER_ERROR, e.getMessage());
            }



            //发送者
            User senderUser = null;
            if (StringUtils.isNotBlank(senderId)) {
                senderUser = userService.getUserByLoginName(senderId);
                if (senderUser == null) {
                    return WSResult.buildResult(WSResult.class, WSResult.IMAGE_ERROR, "senderId:" + senderId + "，无相关账号信息");
                }
            }
            if (senderUser == null) {
                senderUser = userService.getSuperUser();
            }


            List<String> receiveObjectIds = new ArrayList<String>();
            for (String localLoginName : receiveIds) {
                User recevieUser = userService.getUserByLoginName(localLoginName);
                if (recevieUser == null) {
                    logger.error("账号[" + localLoginName + "]，无相关账号信息");
                    return WSResult.buildResult(WSResult.class, WSResult.IMAGE_ERROR, "账号[" + localLoginName + "]，无相关账号信息");
                }
                receiveObjectIds.add(recevieUser.getId());
            }


            //微信发送消息
            try {
                MessageUtils.sendMessage(appId, senderUser.getId(), content, linkUrl, _receiveType, receiveObjectIds,sendTime);
                return WSResult.buildResult(WSResult.class, WSResult.SUCCESS, "消息发送成功");
            } catch (Exception e) {
                return WSResult.buildResult(WSResult.class, WSResult.IMAGE_ERROR, "消息发送失败");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return WSResult.buildDefaultErrorResult(WSResult.class);
        }
    }



    /*
     * 校验参数是否为null和 ""
     */
    public void checkOptional(String optionalValue, String optionalName) throws Exception {
        if (StringUtils.isBlank(optionalValue)) {
            throw new Exception("请求参数[" + optionalName + "]不能为空");
        }
    }



    @Override
    public WSResult sendNotice(String data) {
        logger.debug(data);
        try {
            Map<String, Object> map = JsonMapper.getInstance().fromJson(data, HashMap.class);
            if(map == null){
                logger.error("请求参数格式错误:" + data);
                return WSResult.buildResult(WSResult.class, WSResult.PARAMETER_ERROR, "请求参数格式错误");
            }

            String appId = (String) map.get("appId");
            String serviceId = (String) map.get("serviceId");
            String senderId = (String) map.get("senderId");
            String type = (String) map.get("type");
            String title = (String) map.get("title");
            String content = (String) map.get("content");
            String date = (String) map.get("date");
            Date sendTime = DateUtils.parseDate(date);
            String receiveType = (String) map.get("receiveType");
            List<String> receiveIds = (List) map.get("receiveIds");
            List<String> tipType = (List) map.get("tipType");
            String _receiveType = receiveType;
            try {
                checkOptional(appId, "appId");
                checkOptional(serviceId, "serviceId");
                checkOptional(senderId, "senderId");
                checkOptional(title, "title");
                checkOptional(content, "content");
                if (Collections3.isEmpty(receiveIds)) {
                    return WSResult.buildResult(WSResult.class, WSResult.PARAMETER_ERROR, "请求参数[receiveIds]不能为空");
                }
                if(StringUtils.isBlank(_receiveType) || ReceiveObjectType.Organ.toString().equalsIgnoreCase(_receiveType)){
                    _receiveType = MessageReceiveObjectType.Organ.getValue();
                }
            } catch (Exception e) {//2.判断必填参数
                logger.error(e.getMessage());
                return WSResult.buildResult(WSResult.class, WSResult.PARAMETER_ERROR, e.getMessage());
            }

            //发送者
            User senderUser = null;
            if (StringUtils.isNotBlank(senderId)) {
                senderUser = userService.getUserByLoginName(senderId);
                if (senderUser == null) {
                    return WSResult.buildResult(WSResult.class, WSResult.IMAGE_ERROR, "senderId:" + senderId + "，应用集成平台无相关账号映射信息");
                }
            }
            if (senderUser == null) {
                senderUser = userService.getSuperUser();
            }

            List<String> organIds = new ArrayList<String>();
            for (String companyCode : receiveIds) {
                Organ company = organService.getByCode(companyCode);
                if (company == null) {
                    logger.error("机构[" + companyCode + "]，应用集成平台无相关映射信息");
                    return WSResult.buildResult(WSResult.class, WSResult.IMAGE_ERROR, "机构[" + companyCode + "]，应用集成平台无相关映射信息");
                }
                List<String> organList = organService.findDepartmentAndGroupOrganIdsByCompanyId(company.getId());
                if(Collections3.isNotEmpty(organList)){
                    organIds.addAll(organList);
                }
                if(!organIds.contains(company.getId())){
                    organIds.add(company.getId());
                }
            }

            //发送
            try {
                noticeService.sendToOrganNotice(appId, type,title, content, sendTime, senderUser.getId(),senderUser.getDefaultOrganId(),organIds);
                return WSResult.buildResult(WSResult.class, WSResult.SUCCESS, "通知发送成功");
            } catch (Exception e) {
                return WSResult.buildResult(WSResult.class, WSResult.IMAGE_ERROR, "通知发送失败");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return WSResult.buildDefaultErrorResult(WSResult.class);
        }
    }

}
