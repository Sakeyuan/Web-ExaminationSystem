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
                                <el-button class="mt-10" type="primary" size="small" @click="startExam(item)">
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
        <el-dialog :visible.sync="showExamDialog" title="注意事项" width="60%" center>
            <div style="text-align: center; padding: 20px;">
                <p style="font-size: 16px;">请在考试开始前仔细阅读以下注意事项：</p>
                <ul style="list-style-type: none; padding: 0; margin-bottom: 20px;">
                    <li style="font-size: 14px; margin-bottom: 10px;">请确保在稳定的网络环境下参加考试。</li>
                    <li style="font-size: 14px; margin-bottom: 10px;">禁止使用任何形式的作弊工具。</li>
                    <li style="font-size: 14px; margin-bottom: 10px;">请在规定时间内完成考试。</li>
                    <li style="font-size: 14px; margin-bottom: 10px;">不要随意离开考试界面，以免影响考试顺利进行。</li>
                </ul>
                <div>
                    <el-icon name="information" style="font-size: 30px; color: #409EFF;"></el-icon>
                    <p style="font-size: 14px; margin-top: 10px;">如果有任何疑问，请联系考务人员。</p>
                </div>
            </div>
            <div slot="footer" class="dialog-footer" style="text-align: center;">
                <el-button @click="showExamDialog = false" size="medium">取消</el-button>
                <el-button @click="goToExam" type="primary" size="medium">确定</el-button>
            </div>
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
                showExamDialog: false,
                pageNum: 1,
                pageSize: 6,
                total: 0,
                papers: [],
                index: 0,
                dialogVisible: false,
                item: null,
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
            startExam(item) {
                this.showExamDialog = true;
                this.item = item;
            },
            goToExam() {
                this.$router.push("/exam");
                this.showExamDialog = false;
                this.dialogVisible = true;
                this.$nextTick(() => {
                    this.$refs.camera.setShowUploadImgBtn(false);
                });
                const itemString = JSON.stringify(this.item);
                const parsedItem = JSON.parse(itemString);
                localStorage.setItem('paperItem', itemString);
                localStorage.setItem('examRemainingTime', parsedItem.paperTotalTime);
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
                            this.$message.error(res.message);
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