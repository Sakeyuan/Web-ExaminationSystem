<template>
    <div v-if="(tableData && tableData.length > 0) || userRealName">
        <div style="padding: 10px 0">
            <el-input style="width: 200px" placeholder="请输入真名搜索" suffix-icon="el-icon-search" class="mr-5"
                v-model="userRealName">
            </el-input>
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        </div>
        <div style="margin: 10px 0">
            <el-button type="danger" @click="showBatchDelById">批量移出班级<i class="el-icon-delete"
                    style="margin-left: 2px"></i>
            </el-button>
            <el-button type="primary" @click="exp">导出所有学生<i class="el-icon-upload2" style="margin-left: 2px"></i>
            </el-button>
        </div>
        <el-table :data="tableData" border :header-cell-class-name="tableTitle"
            @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55">
            </el-table-column>
            <el-table-column prop="studentNumber" label="学号" width="120"></el-table-column>
            <el-table-column prop="user.userRealName" label="真名"></el-table-column>
            <el-table-column prop="myClass.className" label="所属班级" width="100"></el-table-column>
            <el-table-column prop="user.userGender" label="性别" width="50px"></el-table-column>
            <el-table-column prop="user.userEmail" label="邮箱"></el-table-column>
            <el-table-column prop="user.userPhone" label="手机号"></el-table-column>

            <el-table-column prop="operate" label="操作">
                <template slot-scope="scope">
                    <el-popconfirm title="是否移除班级" @confirm="removeStudentFromClass(scope.row.studentId)">
                        <el-button type="danger" slot="reference">移出班级<i class="el-icon-delete"
                                style="margin-left: 2px"></i></el-button>
                    </el-popconfirm>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog title="批量移出班级" :visible.sync="batchDialogVisible" width="30%">
            <span>是否批量移出班级</span>
            <span slot="footer" class="dialog-footer">
                <el-button @click="batchDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="batchRemoveStudentFromClass">确 定</el-button>
            </span>
        </el-dialog>

        <div style="padding: 10px 0">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum"
                :page-sizes="[2, 5, 10, 20]" :page-size="pageSize" layout="total,sizes, prev, pager, next"
                :total="total">
            </el-pagination>
        </div>
    </div>
    <el-empty v-else description="学生为空，可以尝试邀请学生"></el-empty>
</template>
<script>
    import '@/styles/global.css';
    import "@/styles/studentList.css";
    import { UPLOAD_BASE_URL } from "@/utils/constants.js";
    export default {
        name: 'studentList',
        data() {
            return {
                pageNum: 1,
                pageSize: 5,
                userRealName: '',
                tableData: [],
                total: 0,
                tableTitle: 'tableTitle',
                batchDialogVisible: false,
                multipleSelection: []
            }
        },
        watch: {
            userRealName(newVal) {
                if (newVal.trim() === '') {
                    this.load();
                } else {
                    this.handleSearch();
                }
            },
        },
        methods: {
            handleSearch() {
                if (this.userRealName.trim()) {
                    this.load();
                } else {
                    this.userRealName = '';
                    this.load();
                }
            },
            exp() {
                const url = `${UPLOAD_BASE_URL}/teacher/exportStudentInfo?id=${localStorage.getItem("id")}`;
                window.open(url);
            },
            handleSelectionChange(val) {
                this.multipleSelection = val;
            },
            batchRemoveStudentFromClass() {
                this.batchDialogVisible = false;
                let ids = this.multipleSelection.map(v => v.studentId);
                this.$api.studentObj.batchRemoveStudentFromClass(ids).then(res => {
                    if (res.code == 2000) {
                        this.$message.success("批量删除成功");
                    }
                    else {
                        this.$message.error(res.message);
                    }
                    this.load();
                })
            },
            showBatchDelById() {
                if (this.multipleSelection.length == 0) {
                    this.$message("请选择学生");
                    return;
                }
                this.batchDialogVisible = true;
            },
            removeStudentFromClass(studentId) {
                this.$api.studentObj.removeStudentFromClass(studentId).then(res => {
                    if (res.code == 2000) {
                        this.$message.success("移除成功");
                    }
                    else {
                        this.$message.error(res.message);
                    }
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
                    id: localStorage.getItem("id"),
                    name: this.userRealName,
                }
                this.$api.studentObj.getAllStudentInfoPage(data).then(res => {
                    this.tableData = res.data;
                    this.total = res.total;
                })
            },
        },
        mounted() {
            this.load();    // 在组件挂载后加载数据
        }
    }
</script>