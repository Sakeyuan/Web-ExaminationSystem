<template>
    <div>
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
        <el-table :data="tableData" :header-cell-class-name="tableTitle" @selection-change="handleSelectionChange"
            border>
            <el-table-column type="selection" width="55">
            </el-table-column>
            <el-table-column prop="paper.paperName" label="试卷名称" width="250px"></el-table-column>
            <el-table-column prop="paper.paperCreateStamp" label="创建时间" width="100px"></el-table-column align="center">
            <el-table-column prop="paper.paperTotalTime" width="100px" label="考试总时长" align="center">
                <template slot-scope="scope">
                    {{ paperTotalTimeInHours[scope.$index] }}
                </template>
            </el-table-column>
            <el-table-column prop="paper.paperTotalTime" width="100px" label="试卷是否发布" align="center">
                <template slot-scope="scope">
                    <el-tag type="success" v-if="scope.row.paper.released">是</el-tag>
                    <el-tag type="info" v-else>否</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="发布的班级" width="120px" align="center">
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
                    <el-popconfirm confirm-button-text='确定' cancel-button-text='取消' icon="el-icon-info" icon-color="red"
                        title="是否发布该题目？" @confirm="setPaperReleased(scope.row.paper.paperId)">
                        <el-button slot="reference" type="primary" size="mini" :disabled="scope.row.paper.released"
                            class="mr-5">发布</el-button>
                    </el-popconfirm>

                    <el-button type="primary" size="mini" class="ml-2" @click="getPaperDetail(scope.row.paper.paperId)">
                        详情
                    </el-button>
                    <el-button type="primary" size="mini" class="mr-5" @click="paperAddClass(scope.row.paper.paperId)">
                        添加班级
                    </el-button>
                    <el-popconfirm confirm-button-text='确定' cancel-button-text='取消' icon="el-icon-info" icon-color="red"
                        title="是否删除该题目？" @confirm="deletePaperById(scope.row.paper.paperId)" class="ml-2">
                        <el-button slot="reference" type="danger" size="mini">删除</el-button>
                    </el-popconfirm>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog title="批量删除试卷" :visible.sync="batchDialogVisible" width="30%">
            <span>是否批量删除试卷</span>
            <span slot="footer" class="dialog-footer">
                <el-button @click="batchDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="batchDelById">确 定</el-button>
            </span>
        </el-dialog>

        <el-dialog title="添加班级" :visible.sync="addClassDialogVisible" width="50%" :before-close="handleClose">
            <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">全选
            </el-checkbox>
            <div style="margin: 15px 0;"></div>
            <el-checkbox-group v-model="checkedClass" @change="handleCheckedChange">
                <el-checkbox v-for="classItem in classList" :label="classItem.className" :key="classItem.classId"
                    :disabled="classItem.isPublished">{{ classItem.className }}</el-checkbox>
            </el-checkbox-group>
            <span slot="footer" class="dialog-footer">
                <el-button @click="addClassDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="addPaperClass">确 定</el-button>
            </span>
        </el-dialog>

        <div style="padding: 10px 0">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum"
                :page-sizes="[2, 5, 10, 20]" :page-size="pageSize" layout="total,sizes, prev, pager, next"
                :total="total">
            </el-pagination>
        </div>
    </div>
</template>
<script>
    import '@/styles/global.css';
    import "@/styles/studentList.css";

    export default {
        name: 'UserListView',
        data() {
            return {
                pageNum: 1,
                pageSize: 5,
                paperName: '',
                tableData: [],
                total: 0,
                tableTitle: 'tableTitle',
                oneDialogVisible: false,
                batchDialogVisible: false,
                multipleSelection: [],
                classList: [],
                addClassDialogVisible: false,
                isIndeterminate: true,
                checkAll: false,
                checkedClass: [],
                addPaperId: 0,
            }
        },
        computed: {
            paperTotalTimeInHours() {
                return this.tableData.map(item => {
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
            setPaperReleased(paperId) {
                this.$api.paperObj.setPaperReleased(paperId).then(res => {
                    if (res.code === 2000) {
                        this.$message.success("发布成功");
                        this.load();
                    } else {
                        this.$message.error("发布失败");
                    }
                })
            },
            addPaperClass() {
                if (this.addPaperId == 0) {
                    this.$message.warn("请选择试卷");
                    return;
                }
                const data = {
                    paperId: this.addPaperId,
                    classList: this.checkedClass,
                }
                this.$api.paperObj.addPaperClass(data).then(res => {
                    if (res.code === 2000) {
                        this.$message.success("添加成功");
                        this.addClassDialogVisible = false;
                        this.load();
                    }
                    else {
                        this.$message.error("添加失败");
                    }
                })
            },
            handleClose(done) {
                done();
            },
            getAllClassByTeacherId() {
                this.$api.classObj.getAllClassByTeacherId(localStorage.getItem("id")).then(res => {
                    if (res.code == 2000) {
                        this.classList = res.data.map(classItem => {
                            // 添加一个属性 isPublished，表示该班级是否已发布
                            classItem.isPublished = this.tableData.some(paper => {
                                return paper.classList.some(c => c.classId === classItem.classId) && paper.paper.paperId === this.addPaperId;
                            });
                            return classItem;
                        });
                        console.log(JSON.stringify(this.classList))
                    }
                    else {
                        this.$message.error(res.message)
                    }
                })
            },
            handleCheckedChange(value) {
                let checkedCount = value.length;
                this.checkAll = checkedCount === this.classList.length;
                this.isIndeterminate = checkedCount > 0 && checkedCount < this.classList.length;
                this.checkedClass = value;
            },
            handleCheckAllChange(val) {
                if (val) {
                    this.checkedClass = this.classList.map(classItem => classItem.className);
                }
                else {
                    this.checkedClass = [];
                }
                this.isIndeterminate = false;
            },
            paperAddClass(paperId) {
                this.addClassDialogVisible = true;
                this.addPaperId = paperId;
                this.getAllClassByTeacherId();
            },
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