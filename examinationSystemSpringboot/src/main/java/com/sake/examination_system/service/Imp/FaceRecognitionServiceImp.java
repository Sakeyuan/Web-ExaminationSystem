package com.sake.examination_system.service.Imp;

import com.google.gson.Gson;
import com.sake.examination_system.entity.FaceMatchResult;
import com.sake.examination_system.service.FaceRecognitionService;

import com.sake.examination_system.util.SakeUtil;
import org.springframework.stereotype.Service;

import java.io.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


@Service
public class FaceRecognitionServiceImp implements FaceRecognitionService {
    //private static CascadeClassifier faceDetector;

//    @Override
//    public Boolean recognizeFaceWithOpenCV(String imagePath) {
//        if (faceDetector == null) {
//            initializeOpenCV();
//        }
//        try {
//            // 读取图像文件为字节数组
//            byte[] imageData = readImageFile(imagePath);
//
//            // 将字节数组转换为 OpenCV Mat 对象
//            Mat image = Imgcodecs.imdecode(new MatOfByte(imageData), Imgcodecs.IMREAD_UNCHANGED);
//
//            // 进行人脸检测
//            MatOfRect faces = new MatOfRect();
//            faceDetector.detectMultiScale(image, faces);
//
//            // 返回是否检测到人脸
//            return faces.toArray().length > 0;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    @Override
    public String recognizeFaceWithBaiDu(String imagePath) throws Exception {
        String accessToken = SakeUtil.getAccessToken(SakeUtil.getApiKey(), SakeUtil.getApiSecretKey());
        if (accessToken == null) {
            return "accessToken 获取失败";
        }
        String apiUrl = "https://aip.baidubce.com/rest/2.0/face/v3/detect?access_token=" + accessToken;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 设置请求方法为 POST
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        // 构建请求体
        String base64Image = SakeUtil.imageToBase64(imagePath);
        String jsonBody = "{\"image\":\"" + base64Image + "\",\"image_type\":\"BASE64\"}";
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        // 发送请求并获取响应
        return getRespondToString(connection);
    }

    private String getRespondToString(HttpURLConnection connection) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }

    @Override
    public String faceMatch(String base64Image1, String base64Image2) throws IOException {
        String accessToken = SakeUtil.getAccessToken(SakeUtil.getApiKey(), SakeUtil.getApiSecretKey());
        if (accessToken == null) {
            return "accessToken 获取失败";
        }

        // 构建人脸比对的 API 地址，包含访问令牌
        String apiUrl = "https://aip.baidubce.com/rest/2.0/face/v3/match?access_token=" + accessToken;

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // 设置请求方法为 POST
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // 设置请求头
        connection.setRequestProperty("Content-Type", "application/json");

        // 构建请求体
        String jsonBody = "[{\"image\":\"" + base64Image1 + "\",\"image_type\":\"BASE64\"},{\"image\":\"" + base64Image2 + "\",\"image_type\":\"BASE64\"}]";
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // 发送请求并获取响应
        return getRespondToString(connection);
    }

    @Override
    public Boolean recognizeFace(String filePath) throws Exception {
        String FaceDetectedResultString = recognizeFaceWithBaiDu(filePath);
        Gson gson = new Gson();
        FaceMatchResult res =  gson.fromJson(FaceDetectedResultString, FaceMatchResult.class);
        if(res.getError_code() == 110 || res.getError_code() == 111){
            //token失效了
            recognizeFace(filePath);
        }
        return res.getError_code() == 0 && res.getResult().getFace_num() == 1;
    }

    @Override
        public Boolean compareFaces(String img1, String img2) throws Exception {
            String FaceMatchResultString = faceMatch(SakeUtil.imageToBase64(img1),SakeUtil.imageToBase64(img2));
            Gson gson = new Gson();
            FaceMatchResult res =  gson.fromJson(FaceMatchResultString, FaceMatchResult.class);
            if(res.getError_code() == 110 || res.getError_code() == 111){
                //token失效了

                compareFaces(img1,img2);
            }
            return res.getResult().getScore() > 80;
        }

    private byte[] readImageFile(String imagePath) throws IOException {
        File file = new File(imagePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        fileInputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

//    private static synchronized void initializeOpenCV() {
//
//        // 使用正斜杠作为路径分隔符
//        String dir = System.getProperty("user.dir").replace("\\", "/");
//
//        // 构建本机库文件路径
//        String filePath = "/dll/" + (System.getProperty("java.vm.name").contains("64") ? "x64" : "x86") + "/opencv_java343.dll";
//        String path = dir + filePath;
//
//        // 检查本机库文件是否存在
//        File file = new File(path);
//        if (!file.exists()) {
//            throw new RuntimeException("找不到动态库: " + path);
//        }
//        // 加载本机库
//        System.out.println("Loading OpenCV library...");
//        try {
//            System.load(path);
//        } catch (UnsatisfiedLinkError e) {
//            throw new RuntimeException("动态库加载失败: " + path);
//        }
//
//        String casPath = System.getProperty("user.dir").concat("/haarcascades/haarcascade_frontalface_alt.xml");
//
//        faceDetector = new CascadeClassifier(casPath);
//
//        if (faceDetector.empty()) {
//            throw new RuntimeException("检测器初始化失败: " + casPath);
//        }
//    }
}
