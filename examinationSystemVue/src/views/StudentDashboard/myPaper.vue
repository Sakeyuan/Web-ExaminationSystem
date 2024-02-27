<template>
    <div>
        <div v-if="papers && papers.length > 0">
            <div class="paper-list">
                <el-row :gutter="5">
                    <el-col :span="8" v-for="item in papers" :key="item.paperId">
                        <el-card class="mb-10" shadow="hover">
                            <div class="card-content">
                                <i class="el-icon-document"></i>
                                <h3 class="paper-name-h">{{ item.paperName }}</h3>
                                <p class="mt-10">分数: {{ item.paperScore }} 分</p>
                                <p class="mt-5">总时长: {{ paperTotalTimeInHours[papers.indexOf(item)] }}</p>
                                <el-button class="mt-10" type="primary" size="small" @click="goToExam(item)">
                                    开始考试</el-button>
                            </div>
                        </el-card>
                    </el-col>
                </el-row>
            </div>
            <div class="pagination">
                <el-pagination @size-change="handleSizeChange" @current-change="handlePageChange"
                    :current-page="pageNum" :page-size="pageSize" layout="total, prev, pager, next, jumper"
                    :total="total" :background="true">
                </el-pagination>
            </div>
        </div>
        <div v-else>
            <el-empty description="空空如也" class="empty-message"></el-empty>
        </div>
        <el-dialog title="人脸识别" :visible.sync="dialogVisible" width="50%" :before-close="handleClose">
            <Camera ref="camera" @sendFile="uploadFile"></Camera>
        </el-dialog>
    </div>
</template>
<script>
    import '@/styles/studentPaper.css';
    import Camera from "@/components/camera.vue";
    export default {
        name: 'myPapers',
        components: {
            Camera
        },
        data() {
            return {
                pageNum: 1,
                pageSize: 6,
                total: 0,
                papers: [],
                index: 0,
                dialogVisible: false,
            }
        },
        computed: {
            paperTotalTimeInHours() {
                return this.papers.map((item, index) => {
                    const hours = Math.floor(item.paperTotalTime / 60);
                    const minutes = item.paperTotalTime % 60;
                    return `${hours}小时${minutes}分钟`;
                });
            },
        },
        methods: {
            handleClose(done) {
                this.$refs.camera.clearVideoCamera();
                this.dialogVisible = false;
                if (done) {
                    done();
                }
            },
            goToExam(item) {
                // this.dialogVisible = true;
                // this.$nextTick(() => {
                //     this.$refs.camera.setShowUploadImgBtn(false);
                // });

                const itemString = JSON.stringify(item);
                const parsedItem = JSON.parse(itemString);
                localStorage.setItem('paperItem', itemString);
                localStorage.setItem('examRemainingTime', parsedItem.paperTotalTime);
                this.$router.push("/exam");
            },
            uploadFile(file) {
                this.file = file;
                if (this.file) {
                    const loadingInstance = this.$loading({
                        text: '正在识别中...',
                        background: 'rgba(0, 0, 0, 0.7)',
                        spinner: 'el-icon-loading',
                        lock: true,
                    });
                    const formData = new FormData();
                    formData.append('file', this.file);
                    const studentData = {
                        id: parseInt(localStorage.getItem("id")),
                        examRemainingTime: localStorage.getItem("examRemainingTime")
                    }
                    formData.append('data', new Blob([JSON.stringify(studentData)], { type: 'application/json' }));
                    this.$api.uploadObj.faceRecognition(formData).then(res => {
                        if (res.code == 2000) {
                            this.handleClose();
                            loadingInstance.$loading({
                                text: '识别成功正在加载试卷...',
                                background: 'rgba(0, 0, 0, 0.7)',
                                spinner: 'el-icon-loading',
                                lock: true,
                            });
                            setTimeout(() => {
                                this.$router.push("/exam");
                                loadingInstance.close();
                            }, 1000);
                        }
                        else {
                            loadingInstance.close();
                            localStorage.removeItem('paperItem');
                            localStorage.removeItem('examRemainingTime');
                            this.$message(res.message);
                        }
                    }).catch(error => {
                        localStorage.removeItem('paperItem');
                        localStorage.removeItem('examRemainingTime');
                        loadingInstance.close();
                        this.$message.error('识别失败');
                    }).finally(() => {
                        loadingInstance.close();
                    });
                }
                else {
                    this.$message.error("请拍照上传");
                }
            },
            async getUnFinishPaperByStudentId() {
                const data = {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                    id: localStorage.getItem('id'),
                };

                const res = await this.$api.paperObj.getUnFinishPaperByStudentId(data);
                if (res.code === 2000) {
                    this.papers = res.data;
                    this.total = res.total;
                }
            },
            async handlePageChange(newPage) {
                this.pageNum = newPage;
                await this.getUnFinishPaperByStudentId();
            },
            async handleSizeChange(newSize) {
                this.pageSize = newSize;
                await this.getUnFinishPaperByStudentId();
            },
        },
        mounted() {
            this.getUnFinishPaperByStudentId();
        },

    }
</script>

<style scoped>

</style>