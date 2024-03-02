<template>
    <div class="camera_outer">
        <div class="camera-grid">
            <video id="videoCamera" :width="videoWidth" :height="videoHeight" autoplay></video>
            <img :src="imgSrc" v-if="imgSrc" alt="拍摄照片">
        </div>
        <canvas id="canvasCamera" style="display:none;" :width="videoWidth" :height="videoHeight"></canvas>
        <div style="display: flex;justify-content: space-between;align-items: center; margin-top: 30px">
            <el-button type="primary" @click="setImage">拍照</el-button>
            <el-upload ref="upload" style="display: flex; align-items: center;" :show-file-list="false"
                class="avatar-uploader" action="" :before-upload="beforeAvatarUpload" :auto-upload="false"
                :on-change="picturePreview" v-if="showUploadImgBtn">
                <el-button type="primary">上传照片</el-button>
            </el-upload>
            <el-button type="primary" @click="getCompetence">打开摄像头</el-button>
            <el-button type="primary" @click="submitUpload">确认上传</el-button>
        </div>
    </div>
</template>

<script>
    import { UPLOAD_BASE_URL } from "@/utils/constants"
    export default {
        created() {
            this.uploadUrl = `${UPLOAD_BASE_URL}/file/upload`;
        },
        data() {
            return {
                uploadUrl: '',
                videoWidth: 400,
                videoHeight: 300,
                imgSrc: '',
                file: '',
                thisCancas: null,
                thisContext: null,
                thisVideo: null,
                mediaStream: null,
                showUploadImgBtn: true,
            }
        },
        methods: {
            setShowUploadImgBtn(parameter) {
                this.showUploadImgBtn = parameter;
            },
            submitUpload() {
                if (this.file == null) {
                    console.log("Camera this.file is null" + this.file);
                }
                this.$emit("sendFile", this.file);
            },
            picturePreview(file, fileList) {
                const reader = new FileReader();
                reader.onload = (event) => {
                    this.imgSrc = event.target.result; // 将图像DataURL设置为imgSrc，以便在页面上预览照片
                };
                reader.readAsDataURL(file.raw); // 将上传的文件转换为DataURL
                this.file = file.raw;
            },
            clearVideoCamera() {
                this.imgSrc = ''; // 清空 imgSrc 数据
                this.file = '';
                const video = document.getElementById('videoCamera');
                video.srcObject = null; // 清除视频流
                this.stopNavigator(); // 停止摄像头
            },
            beforeAvatarUpload(file) {
                const isJPG = file.type === 'image/jpeg';
                const isLt2M = file.size / 1024 / 1024 < 2;
                if (!isJPG) {
                    this.$message.error('上传头像图片只能是 JPG 格式!');
                }
                if (!isLt2M) {
                    this.$message.error('上传头像图片大小不能超过 2MB!');
                }
                return isJPG && isLt2M;
            },
            getCompetence() {
                // 获取摄像头权限
                const constraints = { audio: false, video: { width: this.videoWidth, height: this.videoHeight } };
                navigator.mediaDevices.getUserMedia(constraints)
                    .then(stream => {
                        this.mediaStream = stream;
                        const video = document.getElementById('videoCamera');
                        if ('srcObject' in video) {
                            video.srcObject = stream;
                        } else {
                            video.src = window.URL.createObjectURL(stream);
                        }
                        video.onloadedmetadata = () => video.play();
                    })
                    .catch(err => {
                        this.$message.warning('无法访问摄像头');
                        console.error('无法访问摄像头:', err);
                    });
            },

            setImage() {
                if (!this.mediaStream) {
                    this.$message.warning('请先打开摄像头');
                    return;
                }
                const canvas = document.getElementById('canvasCamera');
                const video = document.getElementById('videoCamera');
                const context = canvas.getContext('2d');
                context.drawImage(video, 0, 0, this.videoWidth, this.videoHeight);
                canvas.toBlob((blob) => {
                    const file = new File([blob], 'photo.png', { type: 'image/png' });
                    this.file = file;
                    this.imgSrc = URL.createObjectURL(blob);
                }, 'image/png');
            },
            stopNavigator() {
                if (this.mediaStream) {
                    const tracks = this.mediaStream.getTracks();
                    tracks.forEach(track => track.stop());
                    this.mediaStream = null; // 将媒体流设为 null
                }
            }
        },
        destroyed() {
            this.stopNavigator();
        },

    }

</script>

<style scoped>
    .camera_outer {
        position: relative;
        overflow: hidden;
    }

    video,
    canvas {
        transform: scaleX(-1);
    }

    /* 按钮样式 */
    .btn_camera {
        position: absolute;
        bottom: 10px;
        left: 50%;
        transform: translateX(-50%);
        height: 40px;
        line-height: 40px;
        padding: 0 15px;
        background-color: #409EFF;
        color: #FFFFFF;
        border: none;
        border-radius: 4px;
        cursor: pointer;
    }

    /* 上传按钮样式 */
    .el-button {
        margin: 0 10px;
    }

    .camera-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        /* 将内容分为两列 */
        gap: 20px;
        /* 间隔 */
    }

    video,
    img {
        width: 100%;
        /* 让视频和图片填满它们的容器 */
        height: auto;
        /* 高度自适应 */
    }
</style>