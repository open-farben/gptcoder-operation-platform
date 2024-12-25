package cn.com.farben.gptcoder.operation.platform.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelSheetDataDTO {

    /** 第一列数据 */
    /*@ExcelProperty(index = 1)*/
    private String colOne;

    /** 第二列数据 */
    /*@ExcelProperty(index = 2)*/
    private String colTwo;
}
