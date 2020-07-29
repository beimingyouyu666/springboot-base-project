package com.yangk.baseproject.controller;

import com.yangk.baseproject.common.annotation.IdempotentCache;
import com.yangk.baseproject.common.config.BoyProperties;
import com.yangk.baseproject.domain.dto.ParcelShelfDTO;
import com.yangk.baseproject.domain.request.RequestMsg;
import com.yangk.baseproject.domain.response.ResponseMsg;
import com.yangk.baseproject.remote.ParcelShelfRemote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description 测试
 * @Author yangkun
 * @Date 2020/6/29
 * @Version 1.0
 * @blame yangkun
 */
@RestController
@RequestMapping("test")
@Slf4j
public class TestController {

    @Autowired
    private ParcelShelfRemote parcelShelfRemote;
    @Autowired
    private HttpServletRequest request;
    @Value("${server.port}")
    private String port;
    @Autowired
    private BoyProperties boyProperties;

    @RequestMapping("getConfig")
    public String getConfig(){
        log.info("ceshi de s d f");
        log.info("ceshi de s d f");
        log.info("ceshi de s d f");
        return boyProperties.getName();
    }

    @RequestMapping("test")
    public String test() {
        log.error("=============================main error");
        log.info("=============================main info");
        log.warn("=============================main warn");
        return "test";
    }

    @RequestMapping("testIdempotent1")
    @IdempotentCache(cacheKey = "testIdempotent",uuidNames = {"method","requestId"})
    public ResponseMsg testIdempotent1(@RequestBody RequestMsg requestMsg) {
        System.out.println("testIdempotent" + requestMsg.getMethod() + requestMsg.getRequestId());
        return ResponseMsg.buildSuccessMsg();
    }

    @RequestMapping("testIdempotent2")
    @IdempotentCache(cacheKey = "testIdempotent")
    public ResponseMsg testIdempotent2(String requestId) {
        System.out.println("testIdempotent" + requestId);
        return ResponseMsg.buildSuccessMsg();
    }

    @RequestMapping("testRemote")
    public ResponseMsg testRemote(HttpServletRequest request) {
        ParcelShelfDTO parcelShelfDTO = new ParcelShelfDTO();
        parcelShelfDTO.setFpxTrackingNo("200001913100");
        parcelShelfDTO.setWarehouseNumber("USSFOA");
        parcelShelfDTO.setWarehouseCode("test55");
        return parcelShelfRemote.parcelShelf(parcelShelfDTO);
    }

    @RequestMapping("setSession")
    public String setSession(String msg) {
        request.getSession().setAttribute("testSession", msg);
        System.out.println("setSession ok," + "port:" + port);
        return "setSession ok," + "port:" + port;
    }

    @RequestMapping("getSession")
    public String getSession() {
        String testSession = (String) request.getSession().getAttribute("testSession");
        System.out.println("getSession ok,testSession:" + testSession + "port:" + port);
        return "getSession ok,testSession:" + testSession + "port:" + port;
    }
}
