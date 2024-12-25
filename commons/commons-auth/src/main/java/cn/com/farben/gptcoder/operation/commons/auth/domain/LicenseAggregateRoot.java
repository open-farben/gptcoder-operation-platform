package cn.com.farben.gptcoder.operation.commons.auth.domain;

import cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.facade.LicenseRepository;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import lombok.Getter;

/**
 *
 * 授权码聚合根<br>
 * 创建时间：2023/10/8<br>
 * @author ltg
 *
 */
@Getter
public class LicenseAggregateRoot {
    private static final Log logger = LogFactory.get();

    /** 授权码仓储接口 */
    private final LicenseRepository licenseRepository;

    public static class Builder {
        /** 授权码仓储接口 */
        private final LicenseRepository licenseRepository;

        public Builder(LicenseRepository licenseRepository) {
            this.licenseRepository = licenseRepository;
        }

        public LicenseAggregateRoot build() {
            return new LicenseAggregateRoot(this);
        }
    }

    private LicenseAggregateRoot(LicenseAggregateRoot.Builder builder) {
        this.licenseRepository = builder.licenseRepository;
    }
}
