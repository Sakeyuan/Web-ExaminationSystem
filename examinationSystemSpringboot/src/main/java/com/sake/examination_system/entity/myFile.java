package com.sake.examination_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("file")
public class myFile {
    @TableField("file_id")
    @TableId(type = IdType.AUTO)
    private int fileId;

    @TableField("file_name")
    private String fileName;

    @TableField("file_type")
    private String fileType;

    @TableField("file_size")
    private Long fileSize;

    @TableField("file_url")
    private String fileUrl;

    @TableField("file_md5")
    private String fileMd5;

    @TableField("file_is_delete")
    private boolean fileIsDelete;

    @TableField("file_enable")
    private boolean fileEnable;
}
