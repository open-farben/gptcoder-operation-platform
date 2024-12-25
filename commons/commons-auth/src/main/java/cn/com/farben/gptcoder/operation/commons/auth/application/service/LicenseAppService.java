package cn.com.farben.gptcoder.operation.commons.auth.application.service;

import cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.facade.LicenseRepository;
import org.springframework.stereotype.Component;

/**
 *
 * license应用服务<br>
 * 创建时间：2023/9/21<br>
 * @author ltg
 *
 */
@Component
public class LicenseAppService {
    private final LicenseRepository licenseRepository;


    public LicenseAppService(LicenseRepository licenseRepository) {
        this.licenseRepository = licenseRepository;
    }
}
