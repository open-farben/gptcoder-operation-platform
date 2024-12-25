package cn.com.farben.commons.file.application.service;

import cn.com.farben.commons.file.domain.entity.FileInfoEntity;
import cn.com.farben.commons.file.dto.FileInfoDTO;
import cn.com.farben.commons.file.infrastructure.enums.FileUseTypeEnum;
import cn.com.farben.commons.web.constants.MvcConstants;
import cn.hutool.core.lang.tree.Tree;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

/**
 * 文件信息应用服务<br>
 * 可以上传文件、查看文件、下载文件、修改文件等<br>
 * 事务要保持一致时，请在调用的方法上加上@Transactional(rollbackFor = Exception.class)
 */
public interface IFileInfoService {
    /**
     * 创建文件夹，会根据使用类型自动加上前缀路径
     * @param folderName 文件夹名
     * @param useType 使用类型
     * @param parentPath 上级目录
     * @param operator 操作人
     * @return 文件id
     */
//    String createFolder(@NotBlank String folderName, @NonNull FileUseTypeEnum useType, String parentPath, @NotBlank String operator);

    /**
     * 上传文件到指定路径，会根据使用类型自动加上前缀路径
     * @param multipartFile 用户上传的文件
     * @param useType 使用类型
     * @param parentPath 上级目录
     * @param operator 操作人员
     * @return 文件id
     */
    String storeFile(@NonNull MultipartFile multipartFile, @NonNull FileUseTypeEnum useType, String parentPath,
                     @NotBlank String operator);

    /**
     * 上传文件到指定路径，并指定文件名，会根据使用类型自动加上前缀路径
     * @param multipartFile 用户上传的文件
     * @param fileName 文件名
     * @param useType 使用类型
     * @param parentPath 上级目录
     * @param operator 操作人员
     * @return 文件id
     */
    String storeFile(@NonNull MultipartFile multipartFile, @NotBlank String fileName, @NonNull FileUseTypeEnum useType,
                     String parentPath, @NotBlank String operator);

    /**
     * 存储文件
     * @param fileName 文件名
     * @param useType 使用类型
     * @param parentPath 上级目录
     * @param operator 操作人员
     * @return 文件id
     */
    String storeFile(@NotBlank String fileName, @NonNull FileUseTypeEnum useType,
                     String parentPath, @NotBlank String operator);

    /**
     * 根据id下载文件
     * @param id 主键
     */
    void downloadFile(String id, HttpServletResponse response);

    /**
     * 根据id获取文件信息
     * @param id 文件id
     * @return 文件信息实体
     */
    FileInfoEntity getById(String id);

    /**
     * 根据id删除指定文件信息
     * @param idList 要删除的文件id列表
     */
    void removeByIds(List<String> idList, String operator);

    /**
     * 上传多个文件到指定路径，会根据使用类型自动加上前缀路径
     * @param files 用户上传的文件数组
     * @param useType 使用类型
     * @param parentPath 上级目录
     * @param operator 操作人员
     * @return 文件id map，key：文件名，value：文件id
     */
    Map<String, String> storeFiles(@NonNull MultipartFile[] files, @NonNull FileUseTypeEnum useType, @NotBlank String parentPath,
                                   @NotBlank String operator);

    /**
     * 上传临时文件到指定路径，并指定文件名，会根据使用类型自动加上前缀路径
     * @param multipartFile 用户上传的文件
     * @param fileName 文件名
     * @param useType 使用类型
     * @param operator 操作人员
     * @return 临时文件名
     */
    String storeTmpFile(@NonNull MultipartFile multipartFile, @NotBlank String fileName, @NonNull FileUseTypeEnum useType,
                        @NotBlank String operator);

    /**
     * 获取临时文件的文件流
     * @param fileName 文件名
     * @param useType 使用类型
     * @return 文件流
     */
    FileInputStream getTmpFileStream(@NotBlank String fileName, @NonNull FileUseTypeEnum useType);

    /**
     * 删除临时文件
     * @param fileName 文件名
     * @param useType 使用类型
     * @param operator 操作员
     */
    void removeTmpFile(@NotBlank String fileName, @NonNull FileUseTypeEnum useType, @NotBlank String operator);

    /**
     * 获取文件数量
     * @param fileName 文件名
     * @param useType 使用类型
     * @param parentPath 父目录
     * @return 文件数量
     */
    long countFile(@NotBlank String fileName, @NonNull FileUseTypeEnum useType, String parentPath);

    /**
     * 将临时文件正式转储
     * @param tmpFileName 临时文件名
     * @param officialName 正式文件名
     * @param parentPath 父目录
     * @param useType 使用类型
     * @param operator 操作员
     * @return 文件id
     */
    String transferTmpFile(@NotBlank String tmpFileName, @NotBlank String officialName, String parentPath,
                        @NonNull FileUseTypeEnum useType, @NotBlank String operator);

}
