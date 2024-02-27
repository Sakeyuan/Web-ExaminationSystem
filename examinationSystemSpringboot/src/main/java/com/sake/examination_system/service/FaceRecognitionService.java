package com.sake.examination_system.service;

import java.io.IOException;

public interface FaceRecognitionService {
    Boolean recognizeFaceWithOpenCV(String imagePath);

    String recognizeFaceWithBaiDu(String imagePath) throws Exception;

    Boolean compareFaces(String img1, String img2) throws Exception;

    String faceMatch(String base64Image1, String base64Image2) throws IOException;

    Boolean recognizeFace(String filePath) throws Exception;
}
