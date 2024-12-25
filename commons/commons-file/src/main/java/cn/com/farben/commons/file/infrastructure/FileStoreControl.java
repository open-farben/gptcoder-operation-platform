package cn.com.farben.commons.file.infrastructure;

import cn.com.farben.commons.file.infrastructure.enums.FileUseTypeEnum;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 文件存储控制
 */
@Component
public class FileStoreControl {
    /** 文件存储windows总路径 */
    @Value("${commons.config.file_store.total_path.windows}")
    private String windowsPath;

    /** 文件存储linux总路径 */
    @Value("${commons.config.file_store.total_path.linux}")
    private String linuxPath;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Value("${spring.application.name:default}")
    private String appName;

    /**
     * 获取文件存储的绝对路径
     * @param path 相对路径
     * @param useType 使用类型
     * @return 存储绝对路径
     */
    public String getStorePath(String path, FileUseTypeEnum useType) {
        boolean parentEmptyFlg = CharSequenceUtil.isBlank(path) || StrPool.SLASH.equals(path) || StrPool.BACKSLASH.equals(path);
        String totalPath;

        OsInfo osInfo = SystemUtil.getOsInfo();
        if (osInfo.isWindows()) {
            totalPath = windowsPath;
        } else {
            totalPath = linuxPath;
        }
        if (!totalPath.endsWith(StrPool.SLASH)) {
            totalPath += StrPool.SLASH;
        }
        totalPath += useType.getStorePath();
        totalPath += StrPool.SLASH;
        totalPath += appName;
        totalPath += StrPool.SLASH;
        totalPath += activeProfile;
        if (!parentEmptyFlg) {
            totalPath += StrPool.SLASH;
            totalPath += path;
        }

        return FileUtil.normalize(totalPath);
    }
}
