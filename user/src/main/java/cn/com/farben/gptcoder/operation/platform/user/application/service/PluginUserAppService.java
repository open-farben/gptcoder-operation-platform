package cn.com.farben.gptcoder.operation.platform.user.application.service;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;
import cn.com.farben.commons.errorcode.exception.DataDeleteException;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.commons.errorcode.exception.IncorrectLengthException;
import cn.com.farben.commons.errorcode.exception.OperationNotAllowedException;
import cn.com.farben.commons.web.utils.RsaUtils;
import cn.com.farben.gptcoder.operation.commons.user.dto.CommonOrganMngDTO;
import cn.com.farben.gptcoder.operation.commons.user.dto.DictCodeDTO;
import cn.com.farben.gptcoder.operation.commons.user.enums.SystemDictCodeTypeEnum;
import cn.com.farben.gptcoder.operation.commons.user.utils.CoderClientUtils;
import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.dictionary.application.component.DistributedLockComponent;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.utils.LocalCacheUtils;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.facade.PluginVersionRepository;
import cn.com.farben.gptcoder.operation.platform.user.application.component.EasyExcelComponent;
import cn.com.farben.gptcoder.operation.platform.user.command.AddPluginUserCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.ChangePluginUserStatusCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.ModifyPluginUserCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.PluginUserLoginCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.excel.PluginUserImportData;
import cn.com.farben.gptcoder.operation.platform.user.command.excel.PluginUserUploadResult;
import cn.com.farben.gptcoder.operation.platform.user.domain.PluginUserAggregateRoot;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.PluginUserEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.OrganMngTreeDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.PluginUserDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.PluginUserImportDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.PluginUserLoginDTO;
import cn.com.farben.gptcoder.operation.platform.user.exception.IllegalOrganizationException;
import cn.com.farben.gptcoder.operation.platform.user.exception.UserLoginException;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.RedisConstants;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.UserSystemConstants;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.PluginUserStatusEnum;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.PluginUserRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.service.impl.UserCheck;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.utils.CoderCache;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.utils.CoderServiceUtils;
import cn.com.farben.gptcoder.operation.platform.user.util.PasswordChecker;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 *
 * 插件用户应用服务<br>
 * 创建时间：2023/8/24<br>
 * @author ltg
 *
 */
@Component
public class PluginUserAppService {
    private static final Log logger = LogFactory.get();

    @Value("${coder.token.plugin-user-expire-day}")
    private Long pluginUserExpireDay;

    @Value("${coder.user.plugin-user-default-password}")
    private String defaultPassword;

    private final PluginUserRepository pluginUserRepository;

    private final StringRedisTemplate stringRedisTemplate;

    private final CoderCache coderCache;

    /** 事件发布 */
    private final ApplicationEventPublisher applicationEventPublisher;

    /** 插件版本仓储接口 */
    private final PluginVersionRepository pluginVersionRepository;

    private final EasyExcelComponent excelComponent;

    private final LocalCacheUtils localCache;

    private final OrganMngAppService organMngAppService;

    private final DistributedLockComponent lockComponent;

    private static final String DECRYPT_ACCOUNT_ERROR_MESSAGE = "解密用户账号失败";

    private static final String BATCH_IMPORT_USER_LOCK_SIGN = "system_add_organ_";

    private final UserCheck userCheck;

    public String checkUser(String name) {
        return userCheck.checkUser(name);
    }

    public String unlockUser(String name) {
        userCheck.unlockUser(name,1);
        return "ok";
    }

    /**
     * 插件用户登陆
     * @param loginCommand 登陆命令
     */
    @Transactional(rollbackFor = Exception.class)
    public List<PluginUserLoginDTO> login(PluginUserLoginCommand loginCommand) {
        PluginUserAggregateRoot pluginUserAggregateRoot = new PluginUserAggregateRoot.Builder(pluginUserRepository)
                .stringRedisTemplate(stringRedisTemplate)
                .pluginUserCheck(userCheck)
                .pluginVersionRepository(pluginVersionRepository)
                .applicationEventPublisher(applicationEventPublisher).build();
        return pluginUserAggregateRoot.login(loginCommand, pluginUserExpireDay);
    }

    /**
     * 根据标识码获取插件用户信息
     * @param uuid 标识码
     * @return redis中的用户信息
     */
    public PluginUserDTO getUserInfoByUuid(String uuid) {
        String redisKey = RedisConstants.REDIS_PLUGIN_USER_PREFIX + uuid;

        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(redisKey))) {
            logger.warn("根据标识码[{}]没有获取到对应用户信息", uuid);
            return null;
        }
        String userInfoStr = stringRedisTemplate.opsForValue().get(redisKey);
        PluginUserDTO pluginUserDTO = JSONUtil.toBean(userInfoStr, PluginUserDTO.class);
        String id = pluginUserDTO.getId();
        long count = pluginUserRepository.countUserById(id);
        if (count <= 0) {
            logger.warn("根据id[{}]没有获取到对应用户信息", id);
            return null;
        }
        PluginUserEntity pluginUserEntity = pluginUserRepository.findById(id);
        if (PluginUserStatusEnum.ENABLE != pluginUserEntity.getStatus()) {
            logger.warn("账号[{}]未启用，不能登陆", pluginUserEntity.getAccount());
            return null;
        }
        return pluginUserDTO;
    }

    /**
     * 插件用户登出，从缓存删除key
     * @param uuid 标识码
     * @param account 用户账号
     */
    public void logout(String uuid, String account) {
        String uuidKey = RedisConstants.REDIS_PLUGIN_USER_PREFIX + uuid;
        String userLoginInfoKey = RedisConstants.REDIS_PLUGIN_USER_PREFIX + account;
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(userLoginInfoKey))) {
            throw new DataDeleteException("缓存中不存在对应的账号信息");
        }
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(uuidKey))) {
            // 校验用户是否一致
            String userInfoStr = stringRedisTemplate.opsForValue().get(uuidKey);
            PluginUserDTO pluginUserDTO = JSONUtil.toBean(userInfoStr, PluginUserDTO.class);
            if (!pluginUserDTO.getAccount().equals(account)) {
                throw new DataDeleteException("标识码和账号不一致");
            }
        }
        String userLoginInfoStr = stringRedisTemplate.opsForValue().get(userLoginInfoKey);
        JSONObject redisJo = JSONUtil.parseObj(userLoginInfoStr);
        List<PluginUserLoginDTO> loginList = JSONUtil.toList(redisJo.getJSONArray(RedisConstants.LOGIN_INFO_KEY), PluginUserLoginDTO.class);
        if (CollUtil.isEmpty(loginList)) {
            throw new DataDeleteException("缓存中不存在对应的账号信息");
        }
        // 删除用户登陆信息中对应的uuid的记录
        PluginUserLoginDTO lastSameDto = loginList.stream().filter(
                        dto -> uuid.equalsIgnoreCase(dto.getUuid())).findFirst()
                .orElse(null);
        if (Objects.nonNull(lastSameDto)) {
            loginList.remove(lastSameDto);
            if (CollUtil.isEmpty(loginList)) {
                stringRedisTemplate.delete(userLoginInfoKey);
            } else {
                redisJo.set(RedisConstants.LOGIN_INFO_KEY, loginList);
                stringRedisTemplate.opsForValue().set(userLoginInfoKey, redisJo.toString(), Duration.ofDays(pluginUserExpireDay));
            }
        }
        stringRedisTemplate.delete(uuidKey);
    }

    /**
     * 分页查询插件用户
     * @param searchText 用户账号/姓名/或工号的查询
     * @param organization 用户机构
     * @param status 用户状态
     * @param pageSize 每页数据量
     * @param page 当前页
     * @param user 操作员
     * @return 插件用户信息
     */
    public Page<PluginUserDTO> pageUser(String searchText, String organization, PluginUserStatusEnum status,
                                        long pageSize, long page, String user) {
        List<Integer> organList = listUserAuthorityOrgan(user);
        if (CollUtil.isEmpty(organList)) {
            return new Page<>();
        }
        return pluginUserRepository.pageUser(searchText, organization, status, organList, pageSize, page);
    }

    /**
     * 新增插件用户
     * @param addPluginUserCommand 新增插件用户命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void addUser(AddPluginUserCommand addPluginUserCommand) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        String mobile = addPluginUserCommand.getMobile();
        //电话格式
        if(StringUtils.isNotBlank(mobile) && !CoderClientUtils.checkPhone(mobile)) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0154);
        }
        String organization = addPluginUserCommand.getOrganization();
        //校验组织和职务
        checkOrganization(organization, listUserAuthorityOrgan(operator));
        checkDuty(addPluginUserCommand.getDuty());

        PluginUserAggregateRoot pluginUserAggregateRoot = new PluginUserAggregateRoot.Builder(pluginUserRepository).build();
        PluginUserEntity userEntity = new PluginUserEntity();
        BeanUtils.copyProperties(addPluginUserCommand, userEntity);
        userEntity.setMobile(mobile);
        pluginUserAggregateRoot.addUser(userEntity, operator, defaultPassword);
    }

    /**
     * 修改插件用户信息
     * @param modifyPluginUserCommand 编辑插件用户命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void editUser(ModifyPluginUserCommand modifyPluginUserCommand) {
        PluginUserAggregateRoot pluginUserAggregateRoot = new PluginUserAggregateRoot.Builder(pluginUserRepository)
                .applicationEventPublisher(applicationEventPublisher).build();
        //电话格式
        if(StringUtils.isNotBlank(modifyPluginUserCommand.getMobile()) && !CoderClientUtils.checkPhone(modifyPluginUserCommand.getMobile())) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0154);
        }
        String operator = UserInfoUtils.getUserInfo().getAccount();
        List<Integer> organList = listUserAuthorityOrgan(operator);
        //校验组织和职务
        checkOrganization(modifyPluginUserCommand.getOrganization(), organList);
        checkDuty(modifyPluginUserCommand.getDuty());

        PluginUserEntity userEntity = new PluginUserEntity();
        BeanUtils.copyProperties(modifyPluginUserCommand, userEntity);
        pluginUserAggregateRoot.editUser(userEntity, operator);
    }

    /**
     * 启用插件用户
     * @param changePluginUserStatusCommand 更改插件用户状态命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void enableUser(ChangePluginUserStatusCommand changePluginUserStatusCommand) {
        PluginUserAggregateRoot pluginUserAggregateRoot = new PluginUserAggregateRoot.Builder(pluginUserRepository)
                .applicationEventPublisher(applicationEventPublisher).build();
        String operator = UserInfoUtils.getUserInfo().getAccount();
        pluginUserAggregateRoot.enableUser(changePluginUserStatusCommand.getId(), operator, listUserAuthorityOrgan(operator));
    }

    /**
     * 禁用插件用户
     * @param changePluginUserStatusCommand 更改插件用户状态命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void disableUser(ChangePluginUserStatusCommand changePluginUserStatusCommand) {
        PluginUserAggregateRoot pluginUserAggregateRoot = new PluginUserAggregateRoot.Builder(pluginUserRepository)
                .applicationEventPublisher(applicationEventPublisher).stringRedisTemplate(stringRedisTemplate).build();
        String operator = UserInfoUtils.getUserInfo().getAccount();
        pluginUserAggregateRoot.disableUser(changePluginUserStatusCommand.getId(), operator, listUserAuthorityOrgan(operator));
    }

    /**
     * 删除插件用户
     * @param ids 用户id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(List<String> ids) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        logger.info("操作员[{}]删除插件用户[{}]", operator, ids);
        PluginUserAggregateRoot pluginUserAggregateRoot = new PluginUserAggregateRoot.Builder(pluginUserRepository)
                .stringRedisTemplate(stringRedisTemplate).build();
        List<Integer> organList = listUserAuthorityOrgan(operator);
        for (String id : ids) {
            pluginUserAggregateRoot.removeUser(id, organList);
        }
    }

    /**
     * 重置插件用户密码
     * @param id 用户id
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String id) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        logger.info("操作员[{}]重置插件用户[{}]的密码", operator, id);
        PluginUserAggregateRoot pluginUserAggregateRoot = new PluginUserAggregateRoot.Builder(pluginUserRepository)
                .applicationEventPublisher(applicationEventPublisher).build();
        pluginUserAggregateRoot.resetPassword(id, operator, defaultPassword, listUserAuthorityOrgan(operator));
    }

    /**
     * 插件用户修改密码
     * @param account 用户账号
     * @param password 原密码
     * @param newPassword 新密码
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyPassword(String account, String password, String newPassword) {
        try {
            account = RsaUtils.decryptStr(account, KeyType.PrivateKey);
        } catch (Exception e) {
            logger.error(DECRYPT_ACCOUNT_ERROR_MESSAGE, e);
            throw new UserLoginException(DECRYPT_ACCOUNT_ERROR_MESSAGE);
        }
        try {
            password = RsaUtils.decryptStr(password, KeyType.PrivateKey);
        } catch (Exception e) {
            logger.error("解密原密码失败", e);
            throw new UserLoginException("解密原密码失败");
        }
        try {
            newPassword = RsaUtils.decryptStr(newPassword, KeyType.PrivateKey);
        } catch (Exception e) {
            logger.error("解密新密码失败", e);
            throw new UserLoginException("解密新密码失败");
        }
        if (newPassword.length() < 6 || newPassword.length() > 18) {
            throw new IncorrectLengthException("密码长度为6-18位");
        }
        if (PasswordChecker.isValidPassword(newPassword, account)<2){
            logger.error("检查插件用户密码强度失败");
            throw new UserLoginException(ErrorCodeEnum.USER_ERROR_A0122);
        }
        PluginUserAggregateRoot pluginUserAggregateRoot = new PluginUserAggregateRoot.Builder(pluginUserRepository)
                .applicationEventPublisher(applicationEventPublisher).build();
        pluginUserAggregateRoot.modifyPassword(account, password, newPassword);
    }

    /**
     * 上传文件导入用户信息
     * @param file 文件流
     * @return 导入结果
     */
    @Transactional(rollbackFor = Exception.class)
    public PluginUserImportDTO importUser(MultipartFile file) {
        // 判断文件格式
        String filename = file.getOriginalFilename();
        if(StringUtils.isBlank(filename)) {
            throw new BusinessException(ErrorCodeEnum.EMPTY_UPLOAD_FILE_NAME);
        }
        String suffixName = filename.substring(filename.lastIndexOf("."));
        if (!UserSystemConstants.USER_EXCEL_IMPORT_XLSX.equalsIgnoreCase(suffixName) && !UserSystemConstants.USER_EXCEL_IMPORT_XLS.equalsIgnoreCase(suffixName)) {
            throw new BusinessException(ErrorCodeEnum.ERROR_UPLOAD_FILE_TYPE);
        }

        PluginUserImportDTO importResult = new PluginUserImportDTO();
        String operator = UserInfoUtils.getUserInfo().getAccount();
        String lockResult = null;
        List<Integer> organList = listUserAuthorityOrgan(operator);
        if (CollUtil.isEmpty(organList)) {
            throw new OperationNotAllowedException(UserSystemConstants.NO_DATA_PERMISSION_MESSAGE);
        }
        try {
            lockResult = lockComponent.lockWith(BATCH_IMPORT_USER_LOCK_SIGN, 60);
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream(), 0);
            List<PluginUserImportData> allData = reader.readAll(PluginUserImportData.class);
            if(allData.isEmpty()) {
                throw new BusinessException(ErrorCodeEnum.MUST_IMPORT_ONE_DATA);
            }

            //限制单批次数据量大小
            if(allData.size() > 1000) {
                throw new BusinessException(ErrorCodeEnum.MAX_IMPORT_COUNT_TIP);
            }
            List<String> exportOrganList = allData.stream().map(PluginUserImportData::getOrgan).distinct().toList();
            for (String organ : exportOrganList) {
                checkOrganization(organ, organList);
            }
            //开始处理重复excel数据，以账号为标准
            PluginUserUploadResult result = checkImportData(allData);
            importResult.setTotal(result.getTotal());
            //没有成功的数据，报错提示返回前端
            String cacheKey = UserSystemConstants.USER_EXCEL_IMPORT_CACHE_KEY + UUID.randomUUID();
            List<PluginUserImportData> errorList = result.getErrorList();
            List<String> accountList = result.getAccountList();
            if(result.getSuccess() == 0 || accountList.isEmpty()) {
                importResult.setSuccess(0);
                importResult.setCacheKey(cacheKey);
                //问题数据保存缓存
                coderCache.addCacheData(cacheKey, errorList, 600);
                return importResult;
            }

            //针对初筛成功的数据进一步处理
            List<PluginUserImportData> dataList = result.getDataList();
            int success = result.getSuccess();
            //与数据库数据比对是否有重复
            List<String> existList = pluginUserRepository.findExistAccount(accountList);
            logger.info("当前已存在的账号列表：" + JSONUtil.toJsonStr(existList));
            List<PluginUserImportData> importData = new ArrayList<>(success);
            if(!existList.isEmpty()) {
                //存在重复数据，再过滤一遍
                for(PluginUserImportData item : dataList) {
                    if(existList.contains(item.getAccount())) {
                        item.setErrorMsg("账号已存在");
                        item.setFullOrganization("");
                        errorList.add(item);
                        success--;
                    }else {
                        importData.add(item);
                    }
                }
            }else {
                importData = dataList;
            }
            logger.info("默认密码：" + defaultPassword);
            //当前已启用的用户数量
            long count = pluginUserRepository.countEnabledUser();
            //处理成功数据
            excelComponent.batchImportUser(importData, operator, BCrypt.hashpw(defaultPassword), count);
            if(!errorList.isEmpty()) {
                //问题数据保存缓存
                coderCache.addCacheData(cacheKey, errorList, 18000);
                importResult.setCacheKey(cacheKey);
            }

            importResult.setSuccess(success);
            importResult.setDefaultPwd(defaultPassword);

        }catch (IOException ex) {
            logger.error("导入文件解析文件流失败：{}", ex);
            throw new BusinessException(ErrorCodeEnum.ERROR_READ_EXCEL_FILE);
        }catch (BusinessException ex){
            throw ex;
        }catch (Exception e){
            logger.error("导入异常：", e);
            throw new BusinessException(ErrorCodeEnum.ERROR_IMPORT_PLUGIN_USER);
        }finally {
            if(lockResult != null) {
                lockComponent.release(BATCH_IMPORT_USER_LOCK_SIGN);
            }
        }
        return importResult;
    }

    /**
     * excel导入模板下载
     * @param response 响应流
     */
    public void templateExport(HttpServletResponse response) throws IOException {
        try (InputStream templateStream = excelComponent.getTemplateAsStream(UserSystemConstants.USER_EXCEL_IMPORT_ADDRESS)) {
            ExcelReader reader = ExcelUtil.getReader(templateStream);
            ExcelWriter writer = reader.getWriter();

            Workbook workBook = writer.getWorkbook();
            Sheet sheet0 = workBook.getSheetAt(0);
            String[] stateArr = new String[]{"启用", "禁用"};
            CellRangeAddressList addressList = new CellRangeAddressList(1, 5000, 8, 8);
            DataValidationHelper helper = sheet0.getDataValidationHelper();
            DataValidationConstraint constraint = helper.createExplicitListConstraint(stateArr);
            DataValidation validation = helper.createValidation(constraint, addressList);
            if(validation instanceof XSSFDataValidation) {
                validation.setSuppressDropDownArrow(true);
                validation.setShowErrorBox(true);
            }else {
                validation.setSuppressDropDownArrow(false);
            }
            validation.setShowPromptBox(true);
            validation.createPromptBox("提示","请选择下拉项中的状态值");
            validation.setEmptyCellAllowed(true);
            sheet0.addValidationData(validation);

            Font font = writer.createFont();
            font.setFontName("宋体");
            //填充机构sheet页
            Sheet sheet1 = workBook.getSheetAt(1);
            List<OrganMngTreeDTO> organList = excelComponent.findOrganList();
            //从第一行开始
            int rowNum = 1;
            for(OrganMngTreeDTO item : organList) {
                Row row = sheet1.createRow(rowNum);
                Cell cell = row.createCell(0);
                cell.getCellStyle().setFont(font);
                cell.setCellValue(item.getOrganName());

                Cell cell1 = row.createCell(1);
                cell1.getCellStyle().setFont(font);
                cell1.setCellValue(item.getOrganNo());
                rowNum++;
            }

            //填充职务字典sheet页
            rowNum = 1;
            Sheet sheet2 = workBook.getSheetAt(2);
            List<DictCodeDTO> dictList = excelComponent.findDutyList();
            for(DictCodeDTO item : dictList) {
                Row row = sheet2.createRow(rowNum);
                Cell cell = row.createCell(0);
                cell.getCellStyle().setFont(font);
                cell.setCellValue(item.getKindValue());

                Cell cell1 = row.createCell(1);
                cell1.getCellStyle().setFont(font);
                cell1.setCellValue(item.getKindCode());
                rowNum++;
            }

            // 设置请求头
            response.addHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment;filename=" + new String("importTemplate".getBytes(), StandardCharsets.UTF_8));
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            // 将结果写入，并且关闭IO流
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out, true);
        } catch (Exception e) {
            logger.error("读取文件异常：", e);
            throw new BusinessException(ErrorCodeEnum.ERROR_DOWNLOAD_TEMPLATE);
        }
    }

    /**
     * 下载导入失败的数据集
     * @param response http响应
     * @param key 数据key
     * @throws IOException 抛文件读取异常
     */
    public void exportErrorData(HttpServletResponse response, String key) throws IOException {
        Object obj = coderCache.getCacheData(key);
        if(obj == null) {
            throw new BusinessException(ErrorCodeEnum.NOT_EXIST_INPUT_FILE_TIP);
        }
        List<PluginUserImportData> errorList = (List<PluginUserImportData>) obj;
        try (InputStream templateStream = excelComponent.getTemplateAsStream(UserSystemConstants.USER_EXCEL_ERROR_ADDRESS)) {
            ExcelReader reader = ExcelUtil.getReader(templateStream);
            ExcelWriter writer = reader.getWriter();
            // 设置请求头
            response.addHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment;filename=" + new String("importTemplate".getBytes(), StandardCharsets.UTF_8));
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            writer.addHeaderAlias("sort", "序号");
            writer.addHeaderAlias("account", "账号");
            writer.addHeaderAlias("name", "姓名");
            writer.addHeaderAlias("jobNumber", "工号");
            writer.addHeaderAlias("organ", "机构");
            writer.addHeaderAlias("duty", "职务");
            writer.addHeaderAlias("mobile", "手机号");
            writer.addHeaderAlias("email", "邮箱");
            writer.addHeaderAlias("status", "状态");
            writer.addHeaderAlias("errorMsg", "失败原因");
            writer.setOnlyAlias(false);
            writer.passRows(1);
            logger.info("错误数据：{}", JSONUtil.toJsonStr(errorList));
            // 将结果写入，并且关闭IO流
            ServletOutputStream out = response.getOutputStream();
            writer.write(errorList, false);
            writer.flush(out, true);
        } catch (Exception e) {
            logger.error("读取文件异常：", e);
            throw new BusinessException(ErrorCodeEnum.ERROR_DOWNLOAD_TEMPLATE);
        }
    }

    /**
     * 校验导入excel数据
     * @param allData 导入的数据
     * @return 校验结果
     */
    private PluginUserUploadResult checkImportData(List<PluginUserImportData> allData) {
        PluginUserUploadResult result = new PluginUserUploadResult();
        Set<String> accountSet = new HashSet<>();
        List<PluginUserImportData> dataList = new ArrayList<>();
        List<PluginUserImportData> errorList = new ArrayList<>();
        int total = 0;
        int success = 0;
        for(PluginUserImportData item : allData) {
            total++;
            if(StringUtils.isBlank(checkFormat(item, accountSet))) {
                success++;
                accountSet.add(item.getAccount());
                dataList.add(item);
            }else {
                errorList.add(item);
            }
        }
        result.setDataList(dataList);
        result.setErrorList(errorList);
        result.setSuccess(success);
        result.setAccountList(new ArrayList<>(accountSet));
        result.setTotal(total);
        return result;
    }

    /**
     * 校验必要的格式和账号重复性
     * @param item excel行数据
     * @param accountSet 已有账号
     * @return 错误提示
     */
    private String checkFormat(PluginUserImportData item, Set<String> accountSet) {
        StringBuffer sb = new StringBuffer();
        //校验账号
        CoderServiceUtils.checkImportAccount(item.getAccount(), accountSet, sb);

        //校验姓名
        CoderServiceUtils.checkImportName(item.getName(), sb);

        //校验工号长度
        if(StringUtils.isNotBlank(item.getJobNumber()) && item.getJobNumber().length() > 20){
            sb.append("工号最多为20个字符；");
        }
        //机构必填
        if(StringUtils.isBlank(item.getOrgan())) {
            sb.append("组织机构不能为空；");
        }else {
            //判断是否存在
            CommonOrganMngDTO organMng = localCache.getOrganEntity(item.getOrgan(), ()-> organMngAppService.loadOrganMngByNo(item.getOrgan()));
            if(organMng == null || StringUtils.isBlank(organMng.getId())) {
                sb.append("组织机构不存在；");
            }else {
                item.setFullOrganization(organMng.getFullOrganNo());
            }
        }

        //职务必填
        if(StringUtils.isBlank(item.getDuty())) {
            sb.append("职务不能为空；");
        }else {
            //判断是否存在
            if(StringUtils.isBlank(localCache.getDictValue(SystemDictCodeTypeEnum.JOB_INFO.getCode(), item.getDuty()))) {
                sb.append("职务不存在；");
            }
        }
        //校验手机号
        CoderServiceUtils.checkImportMobile(item.getMobile(), sb);

        //邮箱格式
        CoderServiceUtils.checkImportEmail(item.getEmail(), sb);

        //状态必填
        CoderServiceUtils.checkImportStatus(item.getStatus(), sb);
        item.setErrorMsg(sb.toString());
        return sb.toString();
    }

    private void checkOrganization(String organization, List<Integer> organList) {
        if (CollUtil.isEmpty(organList)) {
            throw new OperationNotAllowedException(UserSystemConstants.NO_DATA_PERMISSION_MESSAGE);
        }
        if (CharSequenceUtil.isNotBlank(organization)) {
            // 检查机构
            CommonOrganMngDTO organMngEntity = organMngAppService.loadOrganMngByNo(organization);
            if (Objects.isNull(organMngEntity) || CharSequenceUtil.isBlank(organMngEntity.getId())) {
                throw new IllegalOrganizationException("机构不存在");
            }
            try {
                Integer iOrganization = Integer.parseInt(organization);
                if (!organList.contains(iOrganization)) {
                    throw new OperationNotAllowedException(UserSystemConstants.NO_DATA_PERMISSION_MESSAGE);
                }
            } catch (NumberFormatException e) {
                throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0400, "机构不正确");
            }
        }
    }

    private void checkDuty(String duty) {
        if (CharSequenceUtil.isNotBlank(duty)) {
            // 检查职务
            String dutyDict = localCache.getDictValue(SystemDictCodeTypeEnum.JOB_INFO.getCode(), duty);
            if (StringUtils.isBlank(dutyDict)) {
                throw new IllegalOrganizationException("职务不存在或有多个");
            }
        }
    }

    public PluginUserAppService(PluginUserRepository pluginUserRepository,
                                StringRedisTemplate stringRedisTemplate, CoderCache coderCache,
                                ApplicationEventPublisher applicationEventPublisher,
                                PluginVersionRepository pluginVersionRepository, EasyExcelComponent excelComponent, LocalCacheUtils localCache,
                                OrganMngAppService organMngAppService,
                                DistributedLockComponent lockComponent, UserCheck userCheck) {
        this.pluginUserRepository = pluginUserRepository;
        this.stringRedisTemplate = stringRedisTemplate;
        this.coderCache = coderCache;
        this.applicationEventPublisher = applicationEventPublisher;
        this.pluginVersionRepository = pluginVersionRepository;
        this.excelComponent = excelComponent;
        this.localCache = localCache;
        this.organMngAppService = organMngAppService;
        this.lockComponent = lockComponent;
        this.userCheck = userCheck;
    }

    /**
     * 查询用户有数据权限的机构
     * @param userAccount 用户账号
     * @return 机构列表
     */
    private List<Integer> listUserAuthorityOrgan(String userAccount) {
        return organMngAppService.findAuthOrganNoList(userAccount);
    }
}
