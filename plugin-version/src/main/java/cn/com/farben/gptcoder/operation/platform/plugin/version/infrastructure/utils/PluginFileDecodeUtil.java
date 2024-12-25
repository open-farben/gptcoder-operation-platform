package cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.utils;

import cn.com.farben.gptcoder.operation.platform.plugin.version.exception.PluginFileException;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.PluginFileDecode;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathConstants;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 插件文件解析工具类
 */
public class PluginFileDecodeUtil {
    private static final String JETBRAINS_JAR_NAME = "gptcoder-jetbrains-extension";

    private static final String VSCODE_VERSION_FILE_NAME = "extension/package.json";

    private static final String VSCODE_CHANGE_FILE_NAME = "extension/CHANGELOG.md";

    private static final String VERSION_PATTERN = "^v(?!\\.)(\\d+(\\.\\d+)+)(?![\\d\\.])$";

    private static final String ERROR_FILE_MESSSAGE = "插件文件不正确，未获取到正确信息";

    private static final Log logger = LogFactory.get();

    /**
     * 解析vscode插件文件
     * @param inputStream 文件流
     * @return 解析结果
     */
    public static PluginFileDecode decodeVscode(FileInputStream inputStream) {
        Objects.requireNonNull(inputStream, "插件文件不能为空");
        // vscode版本读取package.json，日志读取changelog.md文件
        StringBuilder packageBuilder = new StringBuilder();
        String changelog = null;
        String version = null;
        try(inputStream) {
            ZipInputStream zin = new ZipInputStream(inputStream);
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                if (ze.isDirectory()) {
                    continue;
                }
                if (VSCODE_VERSION_FILE_NAME.equals(ze.getName())) {
                    // 读取版本信息
                    readTop2000(zin).forEach(packageBuilder::append);
                    if (!JSONUtil.isTypeJSONObject(packageBuilder.toString())) {
                        throw new PluginFileException("无法读取版本信息，请确认包是否正确以及文件是否过长");
                    }
                    JSONObject packageJo = JSONUtil.parseObj(packageBuilder.toString());
                    version = packageJo.getStr("version");
                    if (CharSequenceUtil.isBlank(version)) {
                        throw new PluginFileException("无法读取版本信息，请确认包是否正确");
                    }
                }

                if (VSCODE_CHANGE_FILE_NAME.equals(ze.getName())) {
                    // 读取change log
                    changelog = readVsCodeChange(readTop2000(zin));
                }
            }
            zin.closeEntry();

            return new PluginFileDecode(version, changelog);
        } catch (IOException e) {
            logger.error("解析vscode插件文件出错", e);
            throw new PluginFileException("解析vscode插件文件出错");
        }
    }

    /**
     * 解析JetBrains插件文件
     * @param inputStream 文件流
     * @return 解析结果
     */
    public static PluginFileDecode decodeJetBrains(FileInputStream inputStream) {
        Objects.requireNonNull(inputStream, "插件文件不能为空");
        try(inputStream) {
            ZipInputStream topStream = new ZipInputStream(inputStream);
            ZipInputStream containsStream = findContainsEntry(topStream, JETBRAINS_JAR_NAME);
            if (Objects.isNull(containsStream)) {
                throw new PluginFileException("无法读取插件文件");
            }
            ZipInputStream jarStream = new ZipInputStream(containsStream);
            Objects.requireNonNull(jarStream, "JetBrains插件文件不正确，未获取到正确信息");
            ZipInputStream pluginXmlStream = findEntry(jarStream, "META-INF/plugin.xml");
            String str = readStream(pluginXmlStream);
            Document document = XmlUtil.parseXml(str);
            Object version = XmlUtil.getByXPath("//idea-plugin/version", document, XPathConstants.STRING);
            Object changeNotes = XmlUtil.getByXPath("//idea-plugin/change-notes", document, XPathConstants.STRING);

            return new PluginFileDecode(version == null ? null : version.toString(), changeNotes == null ? null : changeNotes.toString());
        } catch (IOException e) {
            logger.error("解析JetBrains插件文件出错", e);
            throw new PluginFileException("无法读取插件文件");
        }
    }

    private static String readStream(ZipInputStream stream) throws IOException {
        InputStreamReader reader = null;
        if (stream != null) {
            reader = new InputStreamReader(stream);
        }
        BufferedReader bufferedReader = null;
        if (reader != null) {
            bufferedReader = new BufferedReader(reader);
        }
        String str = null;
        StringBuilder stringBuilder = new StringBuilder();
        while (bufferedReader == null || (str = bufferedReader.readLine()) != null) {
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }

    /**
     * 精准查找
     * @param in 输入流
     * @param name 名称
     * @return 查找到的流
     */
    private static ZipInputStream findEntry(ZipInputStream in, String name) {
        ZipEntry entry = null;
        while (true) {
            try {
                if ((entry = in.getNextEntry()) == null) break;
            } catch (IOException e) {
                logger.error(ERROR_FILE_MESSSAGE);
                throw new PluginFileException(ERROR_FILE_MESSSAGE);
            }
            if (entry.getName().equals(name)) {
                return in;
            }
        }
        return null;
    }

    /**
     * 包含查找
     * @param in 输入流
     * @param name 名称
     * @return 查找到的流
     */
    private static ZipInputStream findContainsEntry(ZipInputStream in, String name) {
        ZipEntry entry = null;
        while (true) {
            try {
                if ((entry = in.getNextEntry()) == null) break;
            } catch (IOException e) {
                logger.error(ERROR_FILE_MESSSAGE);
                throw new PluginFileException(ERROR_FILE_MESSSAGE);
            }
            if (entry.getName().contains(name)) {
                return in;
            }
        }
        return null;
    }

    /**
     * 读取文件内容，最多2000行
     * @param zin 输入流
     * @return 按行记录的文件文本内容
     */
    private static List<String> readTop2000(ZipInputStream zin) {
        List<String> resultList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(zin, StandardCharsets.UTF_8));
        String line;
        while (true) {
            try {
                if ((line = br.readLine()) == null || resultList.size() >= 2000) break;
            } catch (IOException e) {
                logger.error("读取插件文件信息出错", e);
                throw new PluginFileException(ERROR_FILE_MESSSAGE);
            }
            resultList.add(line);
        }
        return resultList;
    }

    /**
     * 读取vscode当前版本的更改日志
     * @param lineList 更改日志内容
     * @return 当前版本更改内容
     */
    private static String readVsCodeChange(List<String> lineList) {
        if (CollectionUtil.isEmpty(lineList)) {
            return null;
        }
        int index = 0;
        boolean versionFindFlg = false;
        StringBuilder resultBuilder = new StringBuilder();
        for (String str : lineList) {
            if (index == 0 || CharSequenceUtil.isEmpty(str)) {
                // 第一行和空行不要
                index++;
                continue;
            }
            if (str.startsWith("v") && str.split(" ").length == 2) {
                // 可能是记录版本号的
                String versionStr = str.split(" ")[0];
                Pattern regex = Pattern.compile(VERSION_PATTERN);
                Matcher matcher = regex.matcher(versionStr);
                if (matcher.matches()) {
                    // 是版本号，只读取第一个版本号的内容
                    if (versionFindFlg) {
                        break;
                    } else {
                        versionFindFlg = true;
                    }
                }
            }
            resultBuilder.append(str);
            index++;
        }
        return resultBuilder.toString();
    }

    private PluginFileDecodeUtil() {
        throw new IllegalStateException("工具类不允许实例化");
    }
}
