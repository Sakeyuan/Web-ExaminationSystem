<template>
    <div v-if="tableData && tableData.length > 0">
        <div style="padding: 10px 0">
            <el-input style="width: 200px" placeholder="请输入试卷名称" suffix-icon="el-icon-search" class="mr-5"
                v-model="paperName" @keyup.enter.native="handleSearch">
            </el-input>
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        </div>
        <div style="margin: 10px 0">
            <el-button type="danger" @click="showBatchDelById">批量删除试卷<i class="el-icon-delete"
                    style="margin-left: 2px"></i>
            </el-button>
        </div>
        <el-table :data="tableData" :header-cell-class-name="tableTitle" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55">
            </el-table-column>
            <el-table-column prop="paper.paperName" label="试卷名称"></el-table-column>
            <el-table-column prop="paper.paperCreateStamp" label="创建时间"></el-table-column>
            <el-table-column prop="paper.paperTotalTime" label="考试总时长">
                <template slot-scope="scope">
                    {{ paperTotalTimeInHours[scope.$index] }}
                </template>
            </el-table-column>

            <el-table-column label="发布的班级">
                <template slot-scope="scope">
                    <el-tooltip effect="dark">
                        <div>
                            <span v-for="classItem in scope.row.classList.slice(0, 2)" :key="classItem.classId">
                                {{ classItem.className }}
                            </span>
                            <span v-if="scope.row.classList.length > 2">...</span>
                        </div>
                        <div slot="content">
                            {{ scope.row.classList.map(c => c.className).join(', ') }}
                        </div>
                    </el-tooltip>
                </template>
            </el-table-column>
            <el-table-column prop="operate" label="操作">
                <template slot-scope="scope">
                    <el-button type="primary" size="mini" class="mr-2" @click="getPaperDetail(scope.row.paper.paperId)">
                        详情
                    </el-button>
                    <el-popconfirm confirm-button-text='确定' cancel-button-text='取消' icon="el-icon-info" icon-color="red"
                        title="是否删除该题目？" @confirm="deletePaperById(scope.row.paper.paperId)">
                        <el-button slot="reference" type="danger" size="mini">删除</el-button>
                    </el-popconfirm>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog title="批量删除试卷" :visible.sync="batchDialogVisible" width="30%">
            <span>是否批量删除试卷</span>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="batchDelById">确 定</el-button>
            </span>
        </el-dialog>

        <div style="padding: 10px 0">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum"
                :page-sizes="[2, 5, 10, 20]" :page-size="pageSize" layout="total,sizes, prev, pager, next"
                :total="total">
            </el-pagination>
        </div>
    </div>
    <el-empty v-else description="看看是否可以发布试卷"></el-empty>
</template>
<script>
    import '@/styles/global.css';
    import "@/styles/studentList.css";

    export default {
        name: 'UserListView',
        data() {
            return {
                pageNum: 1,
                pageSize: 10,
                paperName: '',
                tableData: [],
                total: 0,
                tableTitle: 'tableTitle',
                oneDialogVisible: false,
                batchDialogVisible: false,
                multipleSelection: [],
                classList: [],
            }
        },
        computed: {
            // 计算属性：将 paperTotalTime 转换为小时
            paperTotalTimeInHours() {
                return this.tableData.map(item => {
                    // 将分钟转换为小时
                    const hours = Math.floor(item.paper.paperTotalTime / 60);
                    // 计算剩余的分钟数
                    const minutes = item.paper.paperTotalTime % 60;
                    // 返回格式化后的字符串
                    return `${hours}小时${minutes}分钟`;
                });
            },
        },
        watch: {
            paperName(newVal) {
                if (newVal.trim() === '') {
                    this.load();
                } else {
                    this.handleSearch();
                }
            },
        },
        methods: {
            getPaperDetail(paperId) {
                this.$router.push({
                    path: "/teacher/paperDetail",
                    query: {
                        paperId: paperId,
                    }
                });
            },
            handleSearch() {
                if (this.paperName.trim()) {
                    this.load();
                } else {
                    this.paperName = '';
                    this.load();
                }
            },
            handleSelectionChange(val) {
                this.multipleSelection = val;
            },
            batchDelById() {
                this.batchDialogVisible = false;
                let ids = this.multipleSelection.map(v => v.paperId) //把对象数组转为id数组
                this.$api.paperObj.batchDelById(ids).then(res => {
                    if (res.code == 2000) {
                        this.$message.success("删除成功");
                    }
                    else {
                        this.$message.error(res.message);
                    }
                    this.load();
                })
            },
            showDeleteDialog(userId) {
                // 设置要删除的用户 ID，并显示删除对话框
                this.userIdToDelete = userId;
                this.oneDialogVisible = true;
            },
            showBatchDelById() {
                this.batchDialogVisible = true;
            },
            deletePaperById(id) {
                // 发送删除请求
                this.$api.paperObj.deletePaperById(id).then(res => {
                    if (res.code == 2000) {
                        this.$message.success("删除成功");
                    }
                    else {
                        this.$message.error(res.message);
                    }
                    this.oneDialogVisible = false;
                    this.userIdToDelete = 0;
                    this.load();
                })
            },
            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.load();
            },
            handleCurrentChange(pageNum) {
                this.pageNum = pageNum;
                this.load();
            },
            load() {
                const data = {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                    id: parseInt(localStorage.getItem('id')),
                    name: this.paperName,
                }
                this.$api.paperObj.getAllPaperListByTeacherId(data).then(res => {
                    if (res.code == 2000) {
                        this.tableData = res.data;
                        this.total = res.total;
                    }
                    else {
                        this.$message({
                            message: res.message,
                            type: 'error',
                        })
                    }
                })
            },
        },
        mounted() {
            this.load(); // 在组件挂载后加载数据
        }
    }
</script>