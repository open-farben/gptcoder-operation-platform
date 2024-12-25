package cn.com.farben.gptcoder.operation.platform.dictionary.facade;

import cn.com.farben.commons.web.ResultData;
import cn.com.farben.gptcoder.operation.commons.user.dto.DictCodeDTO;
import cn.com.farben.gptcoder.operation.commons.user.dto.DictCodeInfoDTO;
import cn.com.farben.gptcoder.operation.platform.dictionary.application.service.DictCodeAppService;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.AddDictCodeCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.DictIdCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.DisableDictCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.EditDictCodeCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.PrimaryIdCommand;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统字典配置
 * @author wuanhui
 */
@RestController
@RequestMapping("/dict/option")
public class DictCodeController {

    private static final Log logger = LogFactory.get();

    private final DictCodeAppService dictCodeAppService;

    public DictCodeController(DictCodeAppService dictCodeAppService) {
        this.dictCodeAppService = dictCodeAppService;
    }

    /**
     * 查询字典配置明细
     * @param command 参数
     * @return 请求结果
     */
    @GetMapping("/detail")
    public ResultData<DictCodeInfoDTO> findDictCodeDetail(@Valid PrimaryIdCommand command) {
        return new ResultData.Builder<DictCodeInfoDTO>().ok().data(dictCodeAppService.dictCodeDetail(command)).build();
    }

    /**
     * 查询字典配置明细
     * @param dictCode 参数
     * @return 请求结果
     */
    @GetMapping("/item")
    public ResultData<List<DictCodeDTO>> findDictOptionItem(@RequestParam(name = "dictCode") String dictCode) {
        return new ResultData.Builder<List<DictCodeDTO>>().ok().data(dictCodeAppService.findDictByCode(dictCode)).build();
    }

    /**
     * 查询字典配置明细（优先查缓存）
     * @param dictCode 参数
     * @return 请求结果
     */
    @GetMapping("/cache")
    public ResultData<List<DictCodeDTO>> findDictOptionCacheItem(@RequestParam(name = "dictCode") String dictCode) {
        return new ResultData.Builder<List<DictCodeDTO>>().ok().data(dictCodeAppService.findDictOptionCacheItem(dictCode)).build();
    }

    /**
     * 新增用户
     * @param command 新增用户命令
     * @return 操作结果
     */
    @PostMapping("/add")
    public ResultData<Boolean> addDict(@RequestBody AddDictCodeCommand command) {
        logger.info("---------- 新增字典参数 -----------：{}", command);
        return new ResultData.Builder<Boolean>().ok().data(dictCodeAppService.addDictCode(command)).build();
    }

    /**
     * 新增用户
     * @param command 新增用户命令
     * @return 操作结果
     */
    @PostMapping("/edit")
    public ResultData<Boolean> editDict(@RequestBody EditDictCodeCommand command) {
        logger.info("---------- 修改字典参数 -----------：{}", command);
        return new ResultData.Builder<Boolean>().ok().data(dictCodeAppService.editDictCode(command)).build();
    }

    /**
     * 删除字典
     * @param command 删除字典参数
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    public ResultData<Boolean> deleteDict(@RequestBody @Valid DictIdCommand command) {
        return new ResultData.Builder<Boolean>().ok().data(dictCodeAppService.deleteDictCode(command)).build();
    }

    /**
     * 禁用、启用字典
     * @param command 禁用、启用字典参数
     * @return 操作结果
     */
    @PostMapping("/disable")
    public ResultData<Boolean> disableDict(@RequestBody @Valid DisableDictCommand command) {
        return new ResultData.Builder<Boolean>().ok().data(dictCodeAppService.disableDictCode(command)).build();
    }

}
