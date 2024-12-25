package cn.com.farben.commons.file.infrastructure;

import java.util.List;

/**
 * 文件信息常量类
 */
public class FileInfoConstants {
    /** 文件类型长度限制 */
    public static final int FILE_TYPE_MAX_LENGTH = 20;

    /** 文件名长度限制 */
    public static final int FILE_NAME_MAX_LENGTH = 150;

    /** 文件路径长度限制 */
    public static final int FILE_PATH_MAX_LENGTH = 255;

    /** 文件上传后在该时间内还未入库，将被清除 */
    public static final int CLEAN_HOUR = 25;

    /** 操作文件重试次数 */
    public static final int FILE_HANDLE_TRY_COUNT = 3;

    /** 操作文件重试间隔(毫秒) */
    public static final long FILE_HANDLE_TRY_TIME = 200L;

    public static final String FILE_EXISTS_MESSAGE = "文件[{}]已存在";

    public static final String STORE_FILE_FAIL_MESSAGE = "存储文件出错";

    /** 一次最多允许上传的文件数 */
    public static final int FILE_EACH_UPLOAD_LIMIT = 20;

    /** 临时文件存储路径 */
    public static final String FILE_TMP_DIR = "/tmp";

    /** 根据id跟新数据的条件 */
    public static final String ID_UPDATE_CONDITION = "id = {0}";

    /** 上传文件夹的文件类型 */
    public static final String UPLOAD_FOLDER_TYPE = "zip";

    /** 父id列名 */
    public static final String PARENT_ID_COLUMN = "parent_id";

    /** 文件信息同步的redis key */
    public static final String FILE_SYNC_REDIS_KEY = "file_sync";

    /** 临时文件信息同步的redis key */
    public static final String TMP_FILE_SYNC_REDIS_KEY = "tmp_file_sync";

    /** 搜索时的文件数限制 */
    public static final int FILE_SEARCH_LIMIT = 200;

    /** 支持的代码类型 */
    public static final List<String> CODE_LIST = List.of("cpp", "hpp", "cc", "hh", "cxx", "hxx", "go",
            "java", "kt", "js", "mjs", "ts", "tsx", "php", "proto", "py", "rst", "rb", "rs", "scala",
            "swift", "md", "tex", "html", "sol", "cs", "cob", "cpy", "lua", "pl", "pm", "c", "h");

    public static final String NOT_CODE_FILE_MESSAGE = "[{}]不是代码文件，过滤掉";

    private FileInfoConstants() {
        throw new IllegalStateException("常量类不允许实例化");
    }
}
