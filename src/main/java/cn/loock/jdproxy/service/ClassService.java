package cn.loock.jdproxy.service;

import cn.loock.jdproxy.exception.ErrorMessageUtil;
import cn.loock.jdproxy.exception.IllegalRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: ZhiyuanG
 * Date: 2018/7/21.
 * Time: 下午12:01
 */
@Service
public class ClassService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassService.class);

    public Object getInstance(String className) {
        Class<?> aClass;
        try {
            aClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            LOGGER.error("className:" + className + "not found", e);
            throw new IllegalRequestException(ErrorMessageUtil.JD_CLIENT_REQUEST_CREATE_ERROR);
        }

        try {
            return aClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("getInstance by className:" + className + "error", e);
            throw new IllegalRequestException(ErrorMessageUtil.JD_CLIENT_REQUEST_CREATE_ERROR);
        }
    }
}
