//package cn.com.farben.commons.file.facade;
//
//import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
//import cn.com.farben.commons.errorcode.exception.OperationNotAllowedException;
//import cn.com.farben.commons.file.application.service.IFileInfoService;
//import cn.com.farben.commons.file.command.ChangeFolderCommand;
//import cn.com.farben.commons.file.command.CreateFolderCommand;
//import cn.com.farben.commons.file.domain.entity.FileInfoEntity;
//import cn.com.farben.commons.file.dto.FileInfoDTO;
//import cn.com.farben.commons.file.infrastructure.enums.FileUseTypeEnum;
//import cn.com.farben.commons.file.job.FileClearJob;
//import cn.com.farben.commons.web.bo.UserInfoBO;
//import cn.com.farben.commons.web.constants.MvcConstants;
//import cn.com.farben.commons.web.ResultData;
//import cn.com.farben.commons.web.utils.UserThreadLocal;
//import cn.hutool.core.lang.tree.Tree;
//import cn.hutool.core.text.CharSequenceUtil;
//import cn.hutool.log.Log;
//import cn.hutool.log.LogFactory;
//import com.mybatisflex.core.paginate.Page;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.Max;
//import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
//@RestController
//@RequestMapping("/files")
//@Validated
//public class FileInfoController {
//    private static final Log logger = LogFactory.get();
//
//    private final IFileInfoService fileInfoService;
//
//    private final FileClearJob fileClearJob;
//
//    /**
//     * 创建目录
//     * @param createFolderCommand 创建目录命令
//     * @return 目录id
//     */
//    @PostMapping("/createFolder")
//    public ResultData<String> createFolder(@RequestBody @Valid CreateFolderCommand createFolderCommand) {
//        logger.info("创建目录:[{}]", createFolderCommand);
//        String user = getUser();
//        return new ResultData.Builder<String>().ok().data(fileInfoService.createFolder(createFolderCommand.getFolderName(), createFolderCommand.getUseType(),
//                createFolderCommand.getParentPath(), user)).build();
//    }
//
//    /**
//     * 上传文件
//     * @return 文件id
//     */
//    @PostMapping("/uploadFile")
//    public ResultData<String> uploadFile(@RequestParam("file") @NotNull(message = "必须上传文件") MultipartFile multipartFile,
//                                         @RequestParam("useType") @NotNull(message = "使用类型不能为空") FileUseTypeEnum useType,
//                                         @RequestParam(name = "fileName", required = false) String fileName,
//                                         @RequestParam(name = "parentPath", required = false) String parentPath) {
//        String user = getUser();
//        if (CharSequenceUtil.isBlank(fileName)) {
//            return new ResultData.Builder<String>().ok().data(fileInfoService.storeFile(multipartFile, useType, parentPath, user)).build();
//        } else {
//            return new ResultData.Builder<String>().ok().data(fileInfoService.storeFile(multipartFile, fileName, useType, parentPath, user)).build();
//        }
//    }
//
//    /**
//     * 下载文件
//     */
//    @GetMapping("downloadFile")
//    public void downloadFile(@RequestParam("id") String id, HttpServletResponse response) {
//        fileInfoService.downloadFile(id, response);
//    }
//
//    /**
//     * 根据id获取文件信息
//     * @param id 文件id
//     * @return 文件信息实体
//     */
//    @GetMapping("getInfoById")
//    public ResultData<FileInfoDTO> getInfoById(@RequestParam("id") String id) {
//        FileInfoEntity fileInfoEntity = fileInfoService.getById(id);
//        FileInfoDTO dto = new FileInfoDTO();
//        CommonAssemblerUtil.assemblerEntityToDTO(fileInfoEntity, dto);
//        return new ResultData.Builder<FileInfoDTO>().ok().data(dto).build();
//    }
//
//    /**
//     * 删除文件信息
//     * @param ids 文件id列表
//     * @return 操作结果
//     */
//    @DeleteMapping("removeByIds")
//    public ResultData<Void> removeByIds(@RequestParam("ids") List<String> ids) {
//        String user = getUser();
//        fileInfoService.removeByIds(ids, user);
//        return new ResultData.Builder<Void>().ok().build();
//    }
//
//    /**
//     * 替换文件
//     * @param multipartFile 新文件
//     * @param fileId 文件id
//     * @param fileName 文件名
//     * @return 操作结果
//     */
//    @PostMapping("/replaceFile")
//    public ResultData<Void> replaceFile(@RequestParam("file") @NotNull(message = "必须上传文件") MultipartFile multipartFile,
//                                          @RequestParam("fileId") @NotBlank(message = "文件id不能为空") String fileId,
//                                          @RequestParam("fileName") @NotBlank(message = "文件名不能为空") String fileName) {
//        logger.info("替换文件，文件id:[{}]，文件名:[{}]", fileId, fileName);
//        String user = getUser();
//        fileInfoService.replaceFile(multipartFile, fileId, fileName, user);
//        return new ResultData.Builder<Void>().ok().build();
//    }
//
//    /**
//     * 上传多个文件
//     * @param files 文件数组
//     * @param useType 使用类型
//     * @param parentPath 上级目录
//     * @return 文件名和对应id的map
//     */
//    @PostMapping("/uploadFiles")
//    public ResultData<Map<String, String>> uploadFiles(@RequestParam("files") @NotNull(message = "必须上传文件") MultipartFile[] files,
//                                                       @RequestParam("useType") @NotNull(message = "使用类型不能为空") FileUseTypeEnum useType,
//                                                       @RequestParam("parentPath") @NotBlank(message = "上级目录不能为空") String parentPath) {
//        logger.info("上传多个文件，useType:[{}]，parentPath:[{}]", useType, parentPath);
//        String user = getUser();
//        return new ResultData.Builder<Map<String, String>>().ok().data(fileInfoService.storeFiles(files, useType, parentPath, user)).build();
//    }
//
//    /**
//     * 修改目录名
//     * @param changeFolderCommand 修改目录名命令
//     * @return 操作结果
//     */
//    @PostMapping("/changeFolderName")
//    public ResultData<Void> changeFolderName(@RequestBody @Valid ChangeFolderCommand changeFolderCommand) {
//        logger.info("修改目录名:[{}]", changeFolderCommand);
//        String user = getUser();
//        fileInfoService.changeFolderName(changeFolderCommand.getId(), changeFolderCommand.getName(), user);
//        return new ResultData.Builder<Void>().ok().build();
//    }
//
//    /**
//     * 下载多个文件
//     */
//    @GetMapping("downloadFiles")
//    public void downloadFiles(@RequestParam("idList") List<String> idList, HttpServletResponse response) {
//        fileInfoService.downloadFiles(idList, response);
//    }
//
//    /**
//     * 根据id获取下级目录、文件的信息
//     * @param id 文件id
//     * @param name 子文件或目录名
//     * @param pageSize 每页数据量
//     * @param pageNo 当前页
//     * @return 文件信息实体列表
//     */
//    @GetMapping("pageChildrens")
//    public ResultData<Page<FileInfoDTO>> pageChildrens(@RequestParam("id") @NotBlank(message = "目录id不能为空") String id,
//                                                        @RequestParam(value = "name", required = false) String name,
//                                                        @RequestParam("pageSize") @Min(value = 1, message = "不能小于1") @Max(value = MvcConstants.MAX_PAGE_SIZE, message = "不能大于" + MvcConstants.MAX_PAGE_SIZE) long pageSize,
//                                                        @RequestParam("pageNo") @Min(value = 1, message = "不能小于1") long pageNo) {
//        return new ResultData.Builder<Page<FileInfoDTO>>().ok().data(fileInfoService.pageChildrens(id, name, pageSize, pageNo)).build();
//    }
//
//    /**
//     * 以压缩包形式上传文件夹，文件名就是目录名
//     * @param file 压缩包文件
//     * @param useType 使用类型
//     * @param parentPath 上级目录
//     * @return 文件信息列表
//     */
//    @PostMapping("/uploadZipFile")
//    public ResultData<List<FileInfoDTO>> uploadZipFile(@RequestParam("file") @NotNull(message = "必须上传文件") MultipartFile file,
//                                                       @RequestParam("useType") @NotNull(message = "使用类型不能为空") FileUseTypeEnum useType,
//                                                       @RequestParam("parentPath") @NotBlank(message = "上级目录不能为空") String parentPath) {
//        logger.info("以压缩包形式上传文件夹，useType:[{}]，parentPath:[{}]", useType, parentPath);
//        String user = getUser();
//        return new ResultData.Builder<List<FileInfoDTO>>().ok().data(fileInfoService.uploadZipFile(file, useType, parentPath, user)).build();
//    }
//
//    /**
//     * 强制删除目录
//     * @param id 目录id
//     * @return 操作结果
//     */
//    @DeleteMapping("forceRemoveFolder")
//    public ResultData<Void> forceRemoveFolder(@RequestParam("id") String id) {
//        String user = getUser();
//        fileInfoService.forceRemoveFolder(id, user);
//        return new ResultData.Builder<Void>().ok().build();
//    }
//
//    /**
//     * 查询所有子孙的文件信息
//     * @param id 文件id
//     * @return 文件信息实体
//     */
//    @GetMapping("treeDescendant")
//    public ResultData<List<Tree<String>>> treeDescendant(@RequestParam("id") String id) {
//        return new ResultData.Builder<List<Tree<String>>>().ok().data(fileInfoService.treeDescendant(id)).build();
//    }
//
//    /**
//     * 手动触发文件清理任务
//     * @return 操作结果
//     */
//    @PostMapping("/manualClearJob")
//    public ResultData<Void> manualClearJob() {
//        logger.info("手动触发文件清理任务");
//        fileClearJob.clearUselessFilesTask();
//        return new ResultData.Builder<Void>().ok().build();
//    }
//
//    /**
//     * 获取文件内容
//     * @param id 文件id
//     * @param path 文件路径
//     * @param charset 字符集，默认utf-8
//     * @return 按行返回文件内容
//     */
//    @GetMapping("listFileContent")
//    public ResultData<List<String>> listFileContent(@RequestParam(value = "id", required = false) String id,
//                                                    @RequestParam(value = "path", required = false) String path,
//                                                    @RequestParam(value = "charset" ,required = false) String charset) {
//        return new ResultData.Builder<List<String>>().ok().data(fileInfoService.listFileContent(id, path, charset)).build();
//    }
//
//    /**
//     * 查询子文件和子目录
//     * @param id 文件id
//     * @return 文件信息实体
//     */
//    @GetMapping("listChildrens")
//    public ResultData<List<FileInfoDTO>> listChildrens(@RequestParam("id") String id) {
//        List<FileInfoEntity> fileInfoEntities = fileInfoService.listChildrens(id);
//        return new ResultData.Builder<List<FileInfoDTO>>().ok().data(CommonAssemblerUtil.assemblerEntityListToDTOList(fileInfoEntities, FileInfoDTO.class)).build();
//    }
//
//    /**
//     * 根据文件名搜索指定目录下的文件信息
//     * @param id 目录id
//     * @param fileName 文件名
//     * @return 文件信息实体
//     */
//    @GetMapping("treeSearch")
//    public ResultData<List<Tree<String>>> treeSearch(@RequestParam(value = "id") String id,
//                                                     @RequestParam(value = "fileName") String fileName) {
//        return new ResultData.Builder<List<Tree<String>>>().ok().data(fileInfoService.treeSearch(id, fileName)).build();
//    }
//
//    /**
//     * 以压缩包形式上传代码文件夹，文件名就是目录名
//     * @param file 压缩包文件
//     * @param useType 使用类型
//     * @param parentPath 上级目录
//     * @return 文件信息列表
//     */
//    @PostMapping("/uploadCodeZip")
//    public ResultData<List<FileInfoDTO>> uploadCodeZip(@RequestParam("file") @NotNull(message = "必须上传文件") MultipartFile file,
//                                                       @RequestParam("useType") @NotNull(message = "使用类型不能为空") FileUseTypeEnum useType,
//                                                       @RequestParam("parentPath") @NotBlank(message = "上级目录不能为空") String parentPath) {
//        logger.info("以压缩包形式上传代码文件夹，useType:[{}]，parentPath:[{}]", useType, parentPath);
//        String user = getUser();
//        return new ResultData.Builder<List<FileInfoDTO>>().ok().data(fileInfoService.uploadCodeZip(file, useType, parentPath, user)).build();
//    }
//
//    public FileInfoController(IFileInfoService fileInfoService, FileClearJob fileClearJob) {
//        this.fileInfoService = fileInfoService;
//        this.fileClearJob = fileClearJob;
//    }
//
//    private String getUser() {
//        UserInfoBO userInfoBO = UserThreadLocal.get();
//        if (Objects.isNull(userInfoBO) || CharSequenceUtil.isBlank(userInfoBO.getAccount())) {
//            throw new OperationNotAllowedException("未获取到用户信息");
//        }
//        return userInfoBO.getAccount();
//    }
//}
