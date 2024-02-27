<template>
    <div>
        <div v-if="papers && papers.length > 0">
            <div class="paper-list">
                <el-row :gutter="5">
                    <el-col :span="8" v-for="item in papers" :key="item.paperId">
                        <el-card class="mb-10" shadow="hover">
                            <div class="card-content">
                                <i class="el-icon-document"></i>
                                <h3>{{ item.paperName }}</h3>
                                <p class="mt-2">考试时长: {{ item.paperTotalTime }} 分钟</p>
                                <p class="mt-2">试卷总分: {{ item.paperScore }} 分</p>
                                <p v-if="item.paperStartTime && item.paperEndTime" class="mt-2">
                                    时间范围: {{ item.paperStartTime }} 至 {{ item.paperEndTime }}
                                </p>
                                <div class="mt-5">
                                    <span>
                                        是否已经批改:
                                        <div v-if="item.studentPaper.correct" style="display: inline">
                                            <el-tag type="success">
                                                已批改
                                            </el-tag>
                                            <div>
                                                得分:
                                                <el-tag v-if="item.studentPaper.scores > 60" type="success">
                                                    {{item.studentPaper.scores}}
                                                </el-tag>
                                                <el-tag v-else type="danger">
                                                    {{item.studentPaper.scores}}
                                                </el-tag>
                                            </div>
                                        </div>
                                        <div v-else style="display: inline">
                                            <el-tag type="info" style="margin-bottom: 4px">未批改</el-tag><br><br>
                                        </div>
                                    </span>
                                </div>
                                <el-button type="primary" size="small"
                                    @click="getPaperDetailed(item.paperId,item.paperName,item.studentPaper.correct)"
                                    class="mt-10" :disabled="!item.allowCheck">详情
                                </el-button>
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
    </div>
</template>

<script>
    import '@/styles/studentPaper.css';
    import '@/styles/global.css';
    export default {
        name: 'myPapers',
        data() {
            return {
                pageNum: 1,
                pageSize: 6,
                total: 0,
                papers: [],
                studentPapers: [],
            }
        },
        methods: {
            findStudentPaperByPaperId(paperId) {
                return this.studentPapers.find(studentPaper => studentPaper.paperId === paperId);
            },
            handlePaper() {
                if (this.papers == null) {
                    console.log("papers data is empty");
                    return;
                }
                this.papers.forEach(paper => {
                    const studentPaperObj = this.findStudentPaperByPaperId(paper.paperId);
                    console.log("studentPaperObj :" + JSON.stringify(studentPaperObj));
                    paper.studentPaper = studentPaperObj;
                });
            },
            getPaperDetailed(paperId, paperName, isCorrect) {
                console.log(isCorrect)
                if (!isCorrect) {
                    this.$alert('试卷未批改，无法查看', paperName, {
                        confirmButtonText: '确定',
                    });
                }
                else {
                    this.$router.push({
                        path: "/paperDetailed",
                        query: {
                            paperId: paperId,
                            studentId: parseInt(localStorage.getItem('id'))
                        }
                    });
                }
            },
            async getFinishPaperByStudentId() {
                try {
                    const data = {
                        pageNum: this.pageNum,
                        pageSize: this.pageSize,
                        id: localStorage.getItem('id'),
                    };

                    const res = await this.$api.paperObj.getFinishPaperByStudentId(data);
                    if (res.code === 2000) {
                        this.papers = res.data;
                        this.total = res.total;
                        this.studentPapers = res.other;
                        this.handlePaper();
                    }
                } catch (err) {
                    this.$message.error(err.message);
                }
            },
            async handlePageChange(newPage) {
                this.pageNum = newPage;
                await this.getFinishPaperByStudentId();
            },
            async handleSizeChange(newSize) {
                this.pageSize = newSize;
                await this.getFinishPaperByStudentId();
            },
        },
        mounted() {
            this.getFinishPaperByStudentId();
        },
    }
</script>