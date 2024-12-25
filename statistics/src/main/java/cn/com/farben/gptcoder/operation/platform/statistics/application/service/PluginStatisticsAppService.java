package cn.com.farben.gptcoder.operation.platform.statistics.application.service;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.gptcoder.operation.platform.statistics.command.PluginFunctionStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.PluginUsageStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.PluginFunctionStatisticsDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.PluginUsageStatisticsDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.exception.ExportDataException;
import cn.com.farben.gptcoder.operation.platform.statistics.exception.IncorrectDateException;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.enums.OrderEnum;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.PluginFunStateRepository;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.PluginUsageStateRepository;
import cn.com.farben.gptcoder.operation.platform.user.application.component.AccountOrganComponent;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/**
 *
 * 插件使用记录应用服务<br>
 * 创建时间：2023/11/2<br>
 * @author ltg
 *
 */
@Component
public class PluginStatisticsAppService {
    private final PluginUsageStateRepository pluginUsageStateRepository;

    private final PluginFunStateRepository pluginFunStateRepository;

    private final AccountOrganComponent accountOrganComponent;

    private static final Log logger = LogFactory.get();

    /**
     * 分页查询插件使用记录
     * @param startDay 查询起始日期，格式：yyyy-MM-dd
     * @param endDay 查询截止日期，格式：yyyy-MM-dd
     * @param fullOrganNo 用户机构
     * @param orderStr 排序字段
     * @param orderBy 排序类型(ASC、DESC)
     * @param searchText 查询文本，可根据账号、姓名、工号进行模糊查询
     * @param pageSize 每页数据量
     * @param pageNo 当前页
     * @param operator 操作员
     * @return 插件用户使用情况统计结果DTO,按照最近使用时间倒序排序
     */
    public Page<PluginUsageStatisticsDTO> pagePluginUsageStatistics(String startDay, String endDay, String fullOrganNo,
                                                                    String orderStr, OrderEnum orderBy, String searchText,
                                                                    long pageSize, long pageNo, String operator) {
        checkParam(startDay, endDay, orderStr, orderBy, searchText);
        PluginUsageStatCommand command = new PluginUsageStatCommand();
        accountOrganComponent.filterUserAuth(command, operator);
        command.setEndDay(endDay);
        command.setFullOrganNo(fullOrganNo);
        command.setOrderStr(orderStr);
        command.setPageNo(pageNo);
        command.setPageSize(pageSize);
        command.setStartDay(startDay);
        command.setOrderBy(orderBy);
        command.setSearchText(searchText);
        return pluginUsageStateRepository.pagePluginUsageStatistics(command);
    }

    /**
     * 导出插件使用信息
     * @param startDay 查询起始日期，格式：yyyy-MM-dd
     * @param endDay 查询截止日期，格式：yyyy-MM-dd
     * @param fullOrganNo 用户机构
     * @param orderStr 排序字段
     * @param orderBy 排序类型(ASC、DESC)
     * @param searchText 查询文本，可根据账号、姓名、工号进行模糊查询
     * @param operator 操作员
     */
    public void exportPluginUsage(String startDay, String endDay, String fullOrganNo, String orderStr,
                                  OrderEnum orderBy, String searchText, HttpServletResponse response, String operator) {
        checkParam(startDay, endDay, orderStr, orderBy, searchText);
        PluginUsageStatCommand command = new PluginUsageStatCommand();
        accountOrganComponent.filterUserAuth(command, operator);
        command.setStartDay(startDay);
        command.setOrderBy(orderBy);
        command.setSearchText(searchText);
        command.setEndDay(endDay);
        command.setFullOrganNo(fullOrganNo);
        command.setOrderStr(orderStr);
        List<PluginUsageStatisticsDTO> dataList = pluginUsageStateRepository.listPluginUsageStatistics(command);
        if (CollUtil.isEmpty(dataList)) {
            throw new ExportDataException("没有数据导出");
        }
        int index = 1;
        for (PluginUsageStatisticsDTO item : dataList) {
            item.setSort(index++);
        }
        String resourceLocation = "classpath:templates/userUsageTemplate.xlsx";
        ExcelReader reader = ExcelUtil.getReader(ResourceUtil.getStream(resourceLocation));
        ExcelWriter writer = reader.getWriter();
        ServletOutputStream out = null;
        try (writer) {
            CellStyle style = writer.getWorkbook().createCellStyle();
            style.setDataFormat(writer.getWorkbook().createDataFormat().getFormat("0.00%"));
            style.setBorderBottom(BorderStyle.THIN);

            writer.setOnlyAlias(true);
            writer.writeCellValue(0, 0, startDay + "至" + endDay, false);
            writer.passRows(2);
            writer.write(dataList, false);
            for (int i = 2; i <= index; i++) {
                writer.getCell(7, i).setCellStyle(style);
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=pluginUsage.xlsx");
            out = response.getOutputStream();
            writer.flush(out, true);
        } catch (Exception e) {
            logger.error("导出插件使用信息出错", e);
            throw new ExportDataException("导出插件使用信息出错");
        } finally {
            if (Objects.nonNull(out)) {
                IoUtil.close(out);
            }
        }
    }

    /**
     * 分页查询插件功能使用统计
     * @param startDay 查询起始日期，格式：yyyy-MM-dd
     * @param endDay 查询截止日期，格式：yyyy-MM-dd
     * @param fullOrganNo 用户机构
     * @param orderStr 排序字段
     * @param orderBy 排序类型(ASC、DESC)
     * @param searchText 查询文本，可根据账号、姓名、工号进行模糊查询
     * @param pageSize 每页数据量
     * @param pageNo 当前页
     * @param operator 操作员
     * @return 插件用户使用情况统计结果DTO,按照最近使用时间倒序排序
     */
    public Page<PluginFunctionStatisticsDTO> pagePluginFunctionStatistics(String startDay, String endDay, String fullOrganNo,
                                                                           String orderStr, OrderEnum orderBy, String searchText,
                                                                        long pageSize, long pageNo, String operator) {
        checkParam(startDay, endDay, orderStr, orderBy, searchText);
        //过滤权限
        PluginFunctionStatCommand command = new PluginFunctionStatCommand();
        accountOrganComponent.filterUserAuth(command, operator);
        command.setPageNo(pageNo);
        command.setPageSize(pageSize);
        command.setStartDay(startDay);
        command.setEndDay(endDay);
        command.setFullOrganNo(fullOrganNo);
        command.setOrderStr(orderStr);
        command.setOrderBy(orderBy);
        command.setSearchText(searchText);
        return pluginFunStateRepository.pagePluginFunctionStatistics(command);
    }

    /**
     * 导出插件功能使用统计
     * @param startDay 查询起始日期，格式：yyyy-MM-dd
     * @param endDay 查询截止日期，格式：yyyy-MM-dd
     * @param fullOrganNo 用户机构
     * @param orderStr 排序字段
     * @param orderBy 排序类型(ASC、DESC)
     * @param searchText 查询文本，可根据账号、姓名、工号进行模糊查询
     * @param operator 操作员
     */
    public void exportPluginFunction(String startDay, String endDay, String orderStr, String fullOrganNo,
                                  OrderEnum orderBy, String searchText, HttpServletResponse response, String operator) {
        checkParam(startDay, endDay, orderStr, orderBy, searchText);
        //过滤权限
        PluginFunctionStatCommand command = new PluginFunctionStatCommand();
        accountOrganComponent.filterUserAuth(command, operator);
        command.setStartDay(startDay);
        command.setEndDay(endDay);
        command.setFullOrganNo(fullOrganNo);
        command.setOrderStr(orderStr);
        command.setOrderBy(orderBy);
        command.setSearchText(searchText);
        List<PluginFunctionStatisticsDTO> dataList = pluginFunStateRepository.listPluginFunctionStatistics(command);
        if (CollUtil.isEmpty(dataList)) {
            throw new ExportDataException("没有数据导出");
        }
        int index = 1;
        for (PluginFunctionStatisticsDTO item : dataList) {
            item.setSort(index++);
        }
        String resourceLocation = "classpath:templates/pluginFunctionTemplate.xlsx";
        ExcelReader reader = ExcelUtil.getReader(ResourceUtil.getStream(resourceLocation));
        ExcelWriter writer = reader.getWriter();
        ServletOutputStream out = null;
        try (writer) {
            writer.setOnlyAlias(true);
            writer.writeCellValue(0, 0, startDay + "至" + endDay, false);
            writer.passRows(2);
            writer.write(dataList, false);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=pluginFunction.xlsx");
            out = response.getOutputStream();
            writer.flush(out, true);
        } catch (Exception e) {
            logger.error("导出插件功能使用统计出错", e);
            throw new ExportDataException("导出插件功能使用统计出错");
        } finally {
            if (Objects.nonNull(out)) {
                IoUtil.close(out);
            }
        }
    }

    public PluginStatisticsAppService(PluginUsageStateRepository pluginUsageStateRepository, PluginFunStateRepository pluginFunStateRepository,
                                      AccountOrganComponent accountOrganComponent) {
        this.pluginUsageStateRepository = pluginUsageStateRepository;
        this.pluginFunStateRepository = pluginFunStateRepository;
        this.accountOrganComponent = accountOrganComponent;
    }

    private void checkParam(String startDay, String endDay, String orderStr,
                            OrderEnum orderBy, String searchText) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();
        if (CharSequenceUtil.isEmpty(startDay)) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0400, "查询起始日期不能为空");
        }
        if (!CharSequenceUtil.isEmpty(startDay)) {
            // 校验开始时间
            try {
                start = formatter.parse(startDay,  LocalDate::from);
            } catch (DateTimeParseException e) {
                throw new IncorrectDateException("查询起始日期不正确，正确格式：yyyy-MM-dd");
            }
        }

        if (CharSequenceUtil.isEmpty(endDay)) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0400, "查询截止日期不能为空");
        }
        if (!CharSequenceUtil.isEmpty(endDay)) {
            // 校验结束时间
            try {
                end = formatter.parse(endDay,  LocalDate::from);
            } catch (DateTimeParseException e) {
                throw new IncorrectDateException("查询截止日期不正确，正确格式：yyyy-MM-dd");
            }
        }
        if (end.isAfter(LocalDate.now())) {
            throw new IncorrectDateException("查询截止日期不能是未来日期");
        }
        if (end.isBefore(start)) {
            throw new IncorrectDateException("查询截止日期不能小于查询起始日期");
        }
        if (ChronoUnit.DAYS.between(start, end) > 90) {
            throw new IncorrectDateException("查询日期间隔不能大于90天");
        }
        if (CharSequenceUtil.isNotBlank(searchText) && searchText.length() < 3) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0400, "查询文本至少要三个字符");
        }
        if (CharSequenceUtil.isNotBlank(orderStr) && Objects.isNull(orderBy)) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0400, "未指定排序方式");
        }
    }
}
