package cn.com.farben.gptcoder.operation.platform.plugin.version.facade;

import cn.com.farben.gptcoder.operation.platform.plugin.version.application.service.SSLCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sslCheck")
@Validated
public class SSLCheckController {

    @Autowired
    private SSLCheckService sslCheckService;

    @GetMapping("checkCertificate")
    public ResponseEntity<String> checkSSLCertificateValidity() {

        String url = "https://farchat.farben.com.cn/";
        String warnInfo = sslCheckService.isSSLCertificateValid(url);
        return ResponseEntity.ok(warnInfo);
    }
}
