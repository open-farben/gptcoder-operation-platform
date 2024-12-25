package cn.com.farben.gptcoder.operation.platform.model.facade;

import cn.com.farben.commons.web.ResultData;
import cn.com.farben.gptcoder.operation.platform.model.application.service.ModelAppService;
import cn.com.farben.gptcoder.operation.platform.model.command.ChangeModelParamCommand;
import cn.com.farben.gptcoder.operation.platform.model.command.ChangeModelStatusCommand;
import cn.com.farben.gptcoder.operation.platform.model.dto.ModelManagerDTO;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/model")
@Validated
public class ModelController {
    private static final Log logger = LogFactory.get();

    private final ModelAppService modelAppService;

    @GetMapping(value = "/model_index")
    public ResultData<String> modelIndex() {
//        return "model_index , success !!!";
        String str = "model_index , success !!!";
        return new ResultData.Builder<String>().ok().data(str).build();


    }

    /**
     * 查询系统支持的模型
     * @return 系统支持的模型
     */
    @RequestMapping("getSupportModels")
    public ResultData<ModelManagerDTO> getSupportModels() {
        logger.info("查询系统支持的模型");
        return new ResultData.Builder<ModelManagerDTO>().ok().data(modelAppService.getSupportModels()).build();
    }

    /**
     * 启用模型
     * @param changeModelStatusCommand 改变模型状态命令
     * @return 操作结果
     */
    @PostMapping("/enableModel")
    public ResultData<Void> enableModel(@RequestBody @Valid ChangeModelStatusCommand changeModelStatusCommand) {
        logger.info("启用模型：[{}]", changeModelStatusCommand);
        modelAppService.enableModel(changeModelStatusCommand);
        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 禁用模型
     * @param changeModelStatusCommand 改变模型状态命令
     * @return 操作结果
     */
    @PostMapping("/disableModel")
    public ResultData<Void> disableModel(@RequestBody @Valid ChangeModelStatusCommand changeModelStatusCommand) {
        logger.info("禁用模型：[{}]", changeModelStatusCommand);
        modelAppService.disableModel(changeModelStatusCommand);
        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 修改模型参数
     * @param changeModelParamCommand 修改模型参数命令
     * @return 操作结果
     */
    @PostMapping("/modifyModelParam")
    public ResultData<Void> modifyModelParam(@RequestBody @Valid ChangeModelParamCommand changeModelParamCommand) {
        logger.info("修改模型参数：[{}]", changeModelParamCommand);
        modelAppService.modifyModelParam(changeModelParamCommand);
        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 刷新缓存
     * @return 操作结果
     */
    @PostMapping("/refreshCache")
    public ResultData<Void> refreshCache() {
        logger.info("刷新模型缓存");
        modelAppService.refreshCache();
        return new ResultData.Builder<Void>().ok().build();
    }

    public ModelController(ModelAppService modelAppService) {
        this.modelAppService = modelAppService;
    }
}
