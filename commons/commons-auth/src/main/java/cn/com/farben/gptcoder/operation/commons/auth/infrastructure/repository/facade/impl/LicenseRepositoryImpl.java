package cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.gptcoder.operation.commons.auth.domain.entity.LicenseEntity;
import cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.facade.LicenseRepository;
import cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.mapper.LicenseMapper;
import cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.po.LicensePO;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Objects;

import static cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.po.table.LicenseTableDef.LICENSE;
import static com.mybatisflex.core.query.QueryMethods.max;

/**
 *
 * license仓储实现<br>
 * 创建时间：2023/9/21<br>
 * @author ltg
 *
 */
@Repository
public class LicenseRepositoryImpl implements LicenseRepository {
    /** license DB服务 */
    private final LicenseMapper licenseMapper;
    private static final Log logger = LogFactory.get();

    @Override
    public String getLicense() {
        logger.info("获取license");
        return QueryChain.of(licenseMapper).select(LICENSE.LICENSE_).where(
                LICENSE.CREATE_TIME.eq(
                        QueryChain.of(licenseMapper).from(LICENSE).select(max(LICENSE.CREATE_TIME)))
                ).objAs(String.class);
    }

    @Override
    public long countByLicense(String lic) {
        Objects.requireNonNull(lic, "授权码不能为空");
        logger.info("查询license:[{}]数量", lic);
        return QueryChain.of(licenseMapper).where(LICENSE.LICENSE_.eq(lic)).count();
    }

    @Override
    public void storeLicense(LicenseEntity entity) {
        Objects.requireNonNull(entity, "授权码实体不能为空");
        logger.info("存储license: [{}]", entity);
        LicensePO licensePO = new LicensePO();
        CommonAssemblerUtil.assemblerEntityToPO(entity, licensePO);
        licenseMapper.insertSelectiveWithPk(licensePO);
    }

    @Override
    public LocalDateTime getLastAuthDate() {
        return QueryChain.of(licenseMapper).select(max(LICENSE.CREATE_TIME)).objAs(LocalDateTime.class);
    }

    @Override
    public long countEnabledUser() {
        return licenseMapper.countEnabledUser();
    }

    public LicenseRepositoryImpl(LicenseMapper licenseMapper) {
        this.licenseMapper = licenseMapper;
    }
}
