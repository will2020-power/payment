package com.demo.payment.api;

import com.demo.payment.common.Result;
import com.demo.payment.service.PaymentService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;

import static com.alipay.api.AlipayConstants.CHARSET_UTF8;

@Slf4j
@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;


    @GetMapping("/pay/wechat/{orderNo}")
    public void payWechat(@PathVariable("orderNo") String orderNo, HttpServletResponse httpServletResponse) throws Exception {
        String codeUrl = paymentService.payWechat(orderNo);

        int width = 200;
        int height = 200;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(codeUrl, BarcodeFormat.QR_CODE, width, height);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0 : 0xFFFFFF);
            }
        }

        httpServletResponse.setContentType("image/png");
        ImageIO.write(bufferedImage, "png", httpServletResponse.getOutputStream());
    }

    @GetMapping("/pay/alipay/{orderNo}")
    public void payAlipay(@PathVariable("orderNo") String orderNo, HttpServletResponse httpServletResponse) throws Exception {
        String body = paymentService.payAlipay(orderNo);
        httpServletResponse.setContentType("text/html;charset=" + CHARSET_UTF8);
        PrintWriter out = httpServletResponse.getWriter();
        out.write(body);
        out.flush();
        out.close();
    }

    @GetMapping("/pay/query/wechat/{orderNo}")
    public Result queryWechat(@PathVariable("orderNo") String orderNo) throws Exception {
        return Result.success(paymentService.queryWechat(orderNo));
    }

    @GetMapping("/pay/query/alipay/{orderNo}")
    public Result queryAlipay(@PathVariable("orderNo") String orderNo) throws Exception {
        return Result.success(paymentService.queryAlipay(orderNo));
    }


}
