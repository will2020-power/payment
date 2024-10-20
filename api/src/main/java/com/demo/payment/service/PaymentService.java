package com.demo.payment.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipayLogger;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.demo.payment.common.Result;
import com.demo.payment.config.AliPayConfig;
import com.demo.payment.config.WechatPayConfig;
import com.demo.payment.model.TradeStatus;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.PrintWriter;

import static com.alipay.api.AlipayConstants.CHARSET_UTF8;

@Slf4j
@Service
public class PaymentService {

    @Autowired
    private AliPayConfig aliPayConfig;

    @Autowired
    private WechatPayConfig wechatPayConfig;

    AlipayClient alipayClient = null;

    //初始化后赋值
    @PostConstruct
    public void init() {
        alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", aliPayConfig.getAppId(), aliPayConfig.getPrivateKey(), "json", "UTF-8", aliPayConfig.getAlipayPublicKey(), "RSA2");
        AlipayLogger.setNeedEnableLogger(false);
    }

    public String payWechat(String orderNo) {
        String privateKeyPath = String.format("%s%s%s", wechatPayConfig.getCertPath(), File.separator, "apiclient_key.pem");
        Config config =
                new RSAAutoCertificateConfig.Builder()
                        .merchantId(wechatPayConfig.getMerchantId())
                        .privateKeyFromPath(privateKeyPath)
                        .merchantSerialNumber(wechatPayConfig.getMerchantSerialNumber())
                        .apiV3Key(wechatPayConfig.getApiV3Key())
                        .build();

        NativePayService service = new NativePayService.Builder().config(config).build();

        // request.setXxx(val)设置所需参数，具体参数可见Request定义
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(2);
        request.setAmount(amount);
        request.setAppid(wechatPayConfig.getAppId());
        request.setMchid(wechatPayConfig.getMerchantId());
        request.setDescription("商品");
        request.setNotifyUrl("https://notify_url");
        request.setOutTradeNo(orderNo);
        // 调用下单方法，得到应答
        com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse response = service.prepay(request);
        String codeUrl = response.getCodeUrl();
        // 使用微信扫描 code_url 对应的二维码，即可体验Native支付
        log.info(response.getCodeUrl());

        return codeUrl;
    }

    public String payAlipay(String orderNo) throws Exception{
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderNo);
        bizContent.put("total_amount", 0.02);
        bizContent.put("subject", "商品");
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        bizContent.put("qr_pay_mode", 4);
        request.setNotifyUrl("");

        request.setBizContent(bizContent.toString());
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        if (response.isSuccess()) {
            String body = response.getBody();
            log.info(body);
            return body;
        } else {
            System.out.println("调用失败");
            return "";
        }
    }

    public TradeStatus queryWechat(String orderNo){
        String privateKeyPath = String.format("%s%s%s", wechatPayConfig.getCertPath(), File.separator, "apiclient_key.pem");
        Config config =
                new RSAAutoCertificateConfig.Builder()
                        .merchantId(wechatPayConfig.getMerchantId())
                        .privateKeyFromPath(privateKeyPath)
                        .merchantSerialNumber(wechatPayConfig.getMerchantSerialNumber())
                        .apiV3Key(wechatPayConfig.getApiV3Key())
                        .build();

        NativePayService service = new NativePayService.Builder().config(config).build();


        QueryOrderByOutTradeNoRequest outTrderNo = new QueryOrderByOutTradeNoRequest();

        outTrderNo.setMchid(wechatPayConfig.getMerchantId());
        outTrderNo.setOutTradeNo(orderNo);

        Transaction result = service.queryOrderByOutTradeNo(outTrderNo);

        log.info("微信查询结果[{}]", JSON.toJSONString(result));

        TradeStatus status = new TradeStatus();
        status.setTradeStatus(result.getTradeState().toString().toLowerCase());
        return status;
    }

    public TradeStatus queryAlipay(String orderNo) throws Exception{
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderNo);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        TradeStatus status = new TradeStatus();

        if (response.isSuccess()) {
            log.info("支付宝查询结果[{}]", response.getBody());
            if ("WAIT_BUYER_PAY".equalsIgnoreCase(response.getTradeStatus())) {
                status.setTradeStatus("wait_buyer_pay");
                return status;
            }
        } else {
            if ("ACQ.TRADE_NOT_EXIST".equalsIgnoreCase(response.getSubCode())) {
                status.setTradeStatus("trade_not_exist");
            } else {
                status.setTradeStatus("wait_buyer_pay");
            }
        }

        return status;
    }

}
