package com.sake.examination_system.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.sake.examination_system.entity.DTO.PaperDTO;
import com.sake.examination_system.util.SakeUtil;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

@Data
public class Paper {
    private int paperId;

    private String paperName;
    private Long paperTotalTime;

    private String  paperContent;

    private int paperScore;

    private Timestamp paperCreateStamp;

    private boolean isAllowCheck;

    private boolean isReleased;

    private int teacherId;

    public String getPaperCreateStamp() {
        return SakeUtil.getFormatTime(paperCreateStamp);
    }

    public Object getPaperContent() {
        Gson gson = new Gson();
        if(this.paperContent.contains("name")){
            // 使用 Gson 将 JSON 字符串解析为 JSON 对象
            Object jsonObject = new Gson().fromJson(this.paperContent, Object.class);
            // 将 JSON 对象转换为 JSON 字符串
            return new Gson().toJson(jsonObject);
        }

        // 创建一个 Type 对象，用于保存泛型信息
        Type listType = new TypeToken<List<PaperDTO.SubheadingData>>(){}.getType();
        try {
            // 使用 Gson 将 JSON 字符串转换为对象列表
            return gson.fromJson(paperContent, listType);
        } catch (Exception e) {
            e.printStackTrace();
            // 处理异常或返回默认值
            return Collections.emptyList();
        }
    }

    public void addPaperContent(String content) {
        this.paperContent += content;
        this.paperContent += ",";
    }

    public void addPaperContentLeftBracket() {
        this.paperContent += "{";
    }

    public void addPaperContentRightBracket() {
        this.paperContent += "}";
    }
    public void addPaperContentBracket(String str){
        this.paperContent += str;
    }

    public void removeContentLastStr(){
        if (this.paperContent != null && this.paperContent.length() > 0) {
            this.paperContent = this.paperContent.substring(0, this.paperContent.length() - 1);
        }
    }

    public void clearPaperContent(){
        if(this.paperContent != null && !this.paperContent.equals("")){
            this.paperContent = "";
        }
    }
}
