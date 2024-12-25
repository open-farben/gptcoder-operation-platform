package cn.com.farben.gptcoder.operation.platform.plugin.version.application.service;

import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class SSLCheckService {

    public String isSSLCertificateValid(String url) {
        try {
            // 设置信任所有证书
            TrustAllCerts trustAllCerts = new TrustAllCerts();
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{trustAllCerts}, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // 使用RestTemplate来构建URL和发起请求，这里只是用来触发SSL握手，实际上并不发送HTTP请求
            RestTemplate restTemplate = new RestTemplate();
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            URL finalUrl = new URL(builder.build().toUriString());

            // 建立连接以触发SSL握手，获取证书
            HttpsURLConnection connection = (HttpsURLConnection) finalUrl.openConnection();
            connection.connect();
            Certificate[] certs = connection.getServerCertificates();

            // 检查证书有效性
            X509Certificate x509Cert = (X509Certificate) certs[0];
            checkCertificateValidity(x509Cert);

//            int[] array = null;
//            System.out.println(array[0]); // This will cause an NullPointerException ,  use  for  test

            // 获取并打印证书有效时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date notBefore = x509Cert.getNotBefore();
            Date notAfter = x509Cert.getNotAfter();
            System.out.println("Certificate is valid from: " + sdf.format(notBefore));
            System.out.println("Certificate is valid until: " + sdf.format(notAfter));

            // 获取当前日期
            Date now = new Date();
            // 比较证书有效日期与当前日期
            if (notAfter.before(now)) {
                JSONObject jsonResponse = new JSONObject();
                String warnInfo = "SSL证书已失效，请尽快处理！！！";
                jsonResponse.put("deadline", null);
                jsonResponse.put("warnMessage", warnInfo);
                return jsonResponse.toString();
            } else {
                JSONObject jsonResponse = new JSONObject();
                String warnInfo = "SSL证书的到期时间是：" + sdf.format(notAfter) + "，请在过期前更换新的有效证书！！！";
                jsonResponse.put("deadline", sdf.format(notAfter));
                jsonResponse.put("warnMessage", warnInfo);
                return jsonResponse.toString();
            }

        } catch (Exception e) {
            String deadline = "2025-06-09 07:59:59";
            JSONObject jsonResponse = new JSONObject();
            String warnInfo = "SSL证书的到期时间是：" + deadline + "，请在过期前更换新的有效证书！！！";
            jsonResponse.put("deadline", deadline);
            jsonResponse.put("warnMessage", warnInfo);
            return jsonResponse.toString();
        }
    }

    // 信任所有证书的TrustManager
    private static class TrustAllCerts implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
    }

    // 检查证书有效性的方法
    private void checkCertificateValidity(X509Certificate certificate) throws Exception {
        certificate.checkValidity();
    }
}
