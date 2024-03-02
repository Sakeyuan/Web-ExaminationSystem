package com.sake.examination_system.util;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.xmlbeans.impl.xb.ltgfmt.Code;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.beans.factory.annotation.Value;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
public class SakeUtil {
    private String ip2regionPath = System.getProperty("user.dir") + "/files/ip/";;
    public static final String API_KEY = "oHpW0v4CSOZeXQfixMZ5NZUX";
    public static final String SECRET_KEY = "zqZCq5pJpuKBOeLezOZrxt5rxTJcNXkK";

    public static String getClientIpByString(String ip) {
        SakeUtil instance = new SakeUtil();
        return instance.changeIpToAddress(ip);
    }

    public static String getApiKey(){
        return API_KEY;
    }

    public static String getApiSecretKey(){
        return SECRET_KEY;
    }

    public static String generateVerificationCode() {
        // 设置验证码位数
        int codeLength = 6;

        // 随机数生成器
        Random random = new Random();

        // 生成验证码
        StringBuilder verificationCode = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            verificationCode.append(random.nextInt(10));
        }

        return verificationCode.toString();
    }

    public static String getAccessToken(String apiKey, String apiSecretKey) throws IOException {
        String tokenUrl = "https://aip.baidubce.com/oauth/2.0/token";
        String grantType = "client_credentials";

        URL url = new URL(tokenUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // 设置请求方法为 POST
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // 构建请求参数
        String params = "grant_type=" + grantType +
                "&client_id=" + apiKey +
                "&client_secret=" + apiSecretKey;

        // 将参数写入请求体
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = params.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // 发送请求并获取响应
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // 解析 JSON 响应，提取访问令牌
            String json = response.toString();
            // 这里你可能需要使用 JSON 解析库来提取 accessToken
            // 例如，使用 JSONObject 或 Gson
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(json, JsonObject.class);

            if (jsonResponse.has("access_token")) {
                return jsonResponse.get("access_token").getAsString();
            } else {
                System.err.println("Failed to extract access token from JSON response.");
                return null;
            }
        } finally {
            connection.disconnect();
        }
    }

    public static String imageToBase64(String imagePath) throws Exception {
        Path path = Paths.get(imagePath);
        byte[] imageBytes = Files.readAllBytes(path);

        // 使用 Base64 编码

        return Base64.getEncoder().encodeToString(imageBytes);
    }



    public static String generateRandomKey(String id) {
        // 使用UUID生成随机的唯一标识
        String uuid = UUID.randomUUID().toString().replace("-", "");

        // 获取当前时间戳
        long timestamp = System.currentTimeMillis();

        // 拼接id、uuid和时间戳来生成最终的key
        String key = id + "-" + uuid + "-" + timestamp;

        return key;
    }

    private Searcher getSearcher() throws IOException {
        //获取地址下的库数据
        byte[] bytes = Searcher.loadContentFromFile(ip2regionPath + "ip2region.xdb");
        return Searcher.newWithBuffer(bytes);
    }

    private String changeIpToAddress(String ip){
        //获取searcher
        Searcher searcher = null;
        try {
            searcher = getSearcher();
            return searcher.search(ip);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }finally {
            try {
                if (Objects.nonNull(searcher)){
                    searcher.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private String getIp(HttpServletRequest request){
        try {
            String realIp = request.getHeader("X-Real-IP");
            String forwardedIp = request.getHeader("X-Forwarded-For");
            String clientIp = request.getHeader("Proxy-Client-IP");
            String proxyIp = request.getHeader("WL-Proxy-Client-IP");
            String httpClientIp = request.getHeader("HTTP_CLIENT_IP");
            String xForwardedIp = request.getHeader("HTTP_X_FORWARDED_FOR");
            String remoteAddress = InetAddress.getLocalHost().getHostAddress();

            if (forwardedIp != null && !"unKnown".equalsIgnoreCase(forwardedIp)) {
                // 多次反向代理后会有多个Ip值，第一个Ip才是真实Ip
                int index = forwardedIp.indexOf(",");
                if (index != -1) {
                    return forwardedIp.substring(0, index);
                } else {
                    return forwardedIp;
                }
            }

            return (realIp != null && !"unKnown".equalsIgnoreCase(realIp)) ? realIp :
                    (clientIp != null && !"unKnown".equalsIgnoreCase(clientIp)) ? clientIp :
                            (proxyIp != null && !"unKnown".equalsIgnoreCase(proxyIp)) ? proxyIp :
                                    (httpClientIp != null && !"unKnown".equalsIgnoreCase(httpClientIp)) ? httpClientIp :
                                            (xForwardedIp != null && !"unKnown".equalsIgnoreCase(xForwardedIp)) ? xForwardedIp : remoteAddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static MyResponseEntity<String> getIpAddress(HttpServletRequest request){
        SakeUtil instance = new SakeUtil();
        String ipData = instance.changeIpToAddress(instance.getIp(request));
        String[] ipParts = ipData.split("\\|");
        // 获取城市信息
        String province = ipParts.length >= 3 && !ipParts[2].equals("0") ? ipParts[2] : "未知";
        return new MyResponseEntity<>(CodeNums.SUCCESS,"SUCCESS",province);
    }
    public static String getSecretKey() {
        return "0123456789";
    }

    public static String generateRandomClassCode() {
        // 定义班级代码的可能字符
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        int codeLength = 6; // 班级代码的长度

        // 使用 Random 类生成随机数
        Random random = new Random();

        StringBuilder classCodeBuilder = new StringBuilder();

        // 生成班级代码
        for (int i = 0; i < codeLength; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            classCodeBuilder.append(randomChar);
        }

        return classCodeBuilder.toString();
    }

    public static Integer parseAuthorization(HttpServletRequest httpServletRequest){
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        String token = authorizationHeader.substring(7); // 7 是 "Bearer " 的长度
        return Integer.valueOf(JWT.decode(token).getAudience().get(0));
    }

    public static String generateFileUid() {
        return UUID.randomUUID().toString();
    }

    public static String getToken(int id,String sign){
        return JWT.create()
                .withAudience(String.valueOf(id))  //将userId作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(),12)) //12小时后到期
                .sign(Algorithm.HMAC256(sign));  //sign作为密钥
    }

    public static String getRefreshToken(int id,String sign){
        return JWT.create()
                .withAudience(String.valueOf(id))  //将userId作为载荷
                .withExpiresAt(DateUtil.offsetMonth(new Date(),2)) //2个月后到期
                .sign(Algorithm.HMAC256(sign));  //sign作为密钥
    }

    public static String generateMD5(String str) {
        try {
            // 创建 MessageDigest 实例，指定算法为 MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 将输入字符串转换为字节数组
            byte[] bytes = md.digest(str.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b & 0xff));
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileMD5Code(String filePath) throws Exception {
        byte[] buffer = new byte[1024];
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        try (InputStream fis = new FileInputStream(filePath)) {
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                md5Digest.update(buffer, 0, bytesRead);
            }
        }
        byte[] digest = md5Digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int USERNAME_LENGTH = 8; // 指定用户名长度
    public static String generateUsername() {
        SecureRandom random = new SecureRandom();
        StringBuilder username = new StringBuilder(USERNAME_LENGTH);

        for (int i = 0; i < USERNAME_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            username.append(CHARACTERS.charAt(randomIndex));
        }
        return username.toString();
    }

    public static String getFormatTime(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(timestamp);
    }

    // 辅助方法：将 Timestamp 格式化为精确到分钟的字符串
    public static String formatToMinutes(Timestamp timestamp) {
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return localDateTime.format(formatter);
    }
}
