package cn.loock.jdproxy.controller;

import cn.loock.jdproxy.bean.Config;
import cn.loock.jdproxy.bean.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ZhiyuanG
 * Date: 2018/7/20.
 * Time: 下午9:38
 */
@RestController
public class RootController extends BaseController {

    @Autowired
    private Config config;

    @RequestMapping("/")
    public ResponseResult root() {
        Map<String, String> result = new HashMap<>();
        result.put("version", config.getVersion());
        return new ResponseResult(result);
    }
}
