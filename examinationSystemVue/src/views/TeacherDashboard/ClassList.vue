<template>
    <div>
        <div style="padding: 10px 0">
            <el-input style="width: 200px" placeholder="请输入班级名称" suffix-icon="el-icon-search" class="mr-5"
                v-model="className">
            </el-input>
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        </div>
        <div style="margin: 10px 0; display: flex; justify-content: space-between;">
            <el-button type="danger" @click="showBatchDelById">
                批量解散班级<i class="el-icon-delete" style="margin-left: 2px"></i>
            </el-button>
            <el-button type="primary" @click="openCreateClassDialog">
                创建班级<i class="el-icon-plus" style="margin-left: 2px"></i>
            </el-button>
        </div>

        <el-table :data="tableData" border :header-cell-class-name="tableTitle"
            @selection-change="handleSelectionChange" empty-text="暂无数据">
            <el-table-column type="selection" width="40px"></el-table-column>
            <el-table-column prop="className" label="班级名称" width="200px"></el-table-column>
            <el-table-column prop="classCode" label="班级代码" width="270px"></el-table-column>
            <el-table-column prop="studentNumbers" label="班级人数" width="80px"></el-table-column>

            <el-table-column prop="operate" label="操作">
                <template slot-scope="scope">
                    <el-button type="primary" @click="inviteStudent(scope.row.classId,scope.row.className)">
                        邀请学生加入班级<i class="el-icon-plus" style="margin-left: 2px"></i>
                    </el-button>
                    <el-button type="primary" @click="exportClassStudent(scope.row.classId)">
                        导出班级学生<i class="el-icon-upload2" style="margin-left: 2px"></i>
                    </el-button>
                    <el-popconfirm title="是否解散该班级班级" style="margin-left: 5px" @confirm="removeClass(scope.row.classId)">
                        <el-button type="danger" slot="reference">解散班级<i class="el-icon-delete"></i></el-button>
                    </el-popconfirm>
                </template>
            </el-table-column>
        </el-table>


        <el-dialog title="创建班级" :visible.sync="createClassDialogVisible" style="width: 800px">
            <el-form :model="form">
                <el-form-item label="班级名称" :label-width="formLabelWidth">
                    <el-input v-model="form.className" autocomplete="off" style="width: 200px" placeholder="请输入班级名称"
                        @keyup.enter.native="createClassByName">
                    </el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="createClassDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="createClassByName">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="邀请学生加入班级" :visible.sync="inviteStudentDialogVisible" style="width: 900px">
            <el-form :model="form">
                <el-form-item label="学生学号" :label-width="formLabelWidth">
                    <el-input v-model="form.studentNumber" autocomplete="off" style="width: 200px"
                        placeholder="请输入学生学号">
                    </el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer"
                style="display: flex; justify-content: space-between; align-items: center;">
                <el-upload ref="upload" style="display: flex; align-items: center;" :show-file-list="false"
                    :action="uploadUrl" :before-upload="beforeUpload" :auto-upload="true"
                    :on-success="loadStudentNumber">
                    <el-button type="primary">批量邀请<i class="el-icon-upload el-icon--right"></i></el-button>
                </el-upload>
                <div>
                    <el-button type="primary" @click="inviteStudentByNumber">确 定</el-button>
                    <el-button @click="inviteStudentDialogVisible = false">取 消</el-button>
                </div>
            </div>
        </el-dialog>

        <el-dialog title="批量解散班级" :visible.sync="batchDialogVisible" width="30%">
            <span>是否批量解散班级</span>
            <span slot="footer" class="dialog-footer">
                <el-button @click="batchDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="batchRemoveClass">确 定</el-button>
            </span>
        </el-dialog>
        <div style="padding: 10px 0">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum"
                :page-sizes="[2, 5, 10, 20]" :page-size="pageSize" layout="total,sizes, prev, pager, next"
                :total="total">
            </el-pagination>
        </div>
        <el-drawer title="邀请学生" :visible.sync="drawer" :direction="direction" :before-close="handleClose" size="50%"
            modal-append-to-body>
            <div style="text-align: left;margin-left: 10px;">
                <el-transfer style="text-align: left; display: inline-block" v-model="value" filterable
                    :titles="['姓名-学号', '邀请名单']" :format="{noChecked: '${total}',hasChecked: '${checked}/${total}'}"
                    @right-check-change="handleChange" :data="transferData">
                    <span slot-scope="{ option }">{{ option.label }}</span>
                </el-transfer>
            </div>
            <div style="margin-left: 250px; margin-top:100px">
                <el-button type="primary" @click="invite">邀请名单加入{{rowClassName}}</el-button>
            </div>
        </el-drawer>
    </div>

</template>
<script>
    import '@/styles/global.css';
    import "@/styles/studentList.css";
    import { UPLOAD_BASE_URL } from "@/utils/constants.js";
    export default {
        name: 'ClassList',
        data() {
            return {
                drawer: false,
                direction: 'rtl',
                inviteStudentDialogVisible: false,
                formLabelWidth: '70px',
                dialogVisible: false,
                pageNum: 1,
                pageSize: 5,
                className: '',
                rowClassName: '',
                classId: 0,
                tableData: [],
                total: 0,
                tableTitle: 'tableTitle',
                userIdToDelete: 0,
                batchDialogVisible: false,
                createClassDialogVisible: false,
                uploadUrl: '',
                needInviteStudent: [],
                form: {
                    className: '',
                    studentNumber: '',
                },
                transferData: [],
                value: [],
                renderFunc(h, option) {
                    return <span>{option.key} - {option.label}</span>;
                }
            }
        },
        created() {
            this.uploadUrl = `${UPLOAD_BASE_URL}/teacher/importStudentInfo`;
        },
        methods: {
            exportClassStudent(classId) {
                const url = `${UPLOAD_BASE_URL}/teacher/exportClassStudent?classId=${classId}`;
                window.open(url);
            },
            invite() {
                const data = {
                    classId: this.classId,
                    data: this.needInviteStudent,
                }
                this.$api.teacherObj.invite(data).then(res => {
                    if (this.needInviteStudent.length == 0) {
                        this.$message.error("请选择需要邀请的学生");
                        return;
                    }
                    if (res.code == 2000) {
                        this.needInviteStudent = [];
                        this.drawer = false;
                        this.$message.success("邀请成功");
                    }
                    else {
                        this.$message.warn(res.message);
                    }
                })
            },
            handleChange(value) {
                this.needInviteStudent = value.map(index => this.transferData[index].label);
            },
            loadStudentNumber(response, file, fileList) {
                this.needInviteStudent = [];
                this.transferData = [];
                if (response.code == 2000) {
                    this.inviteStudentDialogVisible = false;
                    this.drawer = true;
                    for (let i = 0; i <= response.total - 1; i++) {
                        this.transferData.push({
                            key: i,
                            label: `${response.data[i].name + "-" + response.data[i].number}`,
                        });
                    }
                    this.$message({
                        message: "上传成功",
                        type: 'success'
                    });
                }
                else {
                    this.$message({
                        message: response.message,
                        type: 'error'
                    });
                }
            },
            beforeUpload(file) {
                const isExcel = /\.(xlsx|xls)$/i.test(file.name);
                if (!isExcel) {
                    this.$message.error('请上传Excel文件');
                }
                this.needInviteStudent = [];
                return isExcel;
            },
            inviteStudent(classId, rowClassName) {
                this.inviteStudentDialogVisible = true;
                this.rowClassName = rowClassName;
                this.classId = classId;
            },
            inviteStudentByNumber() {
                if (this.form.studentNumber == '') {
                    this.$message({
                        message: "请输入学生学号", 
                        type: 'error'       
                    });
                    return;
                }
                const data = {
                    classId: this.classId,
                    studentNumber: this.form.studentNumber
                }
                this.$api.teacherObj.singleInvite(data).then(res => {
                    if (res.code == 2000) {
                        this.$message({
                            message: "邀请成功",
                            type: 'success'
                        });
                    }
                    else {
                        this.$message({
                            message: res.message,
                            type: 'error'
                        });
                    }
                })
            },
            createClassByName() {
                const data = {
                    className: this.form.className,
                }
                this.$api.classObj.createClassByName(data).then(res => {
                    if (this.form.className == '') {
                        this.$message.error("请输入班级名称");
                        return;
                    }
                    if (res.code == 2000) {
                        this.$message.success("创建成功");
                        this.createClassDialogVisible = false;
                        this.load();
                    }
                    else {
                        this.$message.error(res.message);
                    }
                })

            },
            handleClose(done) {
                this.transferData = [];
                done();
            },
            openCreateClassDialog() {
                this.createClassDialogVisible = true; 
            },
            exp() {
                window.open(this.uploadUrl + "/teacher/exportStudentInfo")
            },
            handleSelectionChange(val) {
                this.multipleSelection = val;
            },
            batchRemoveClass() {
                this.batchDialogVisible = false;
                let ids = this.multipleSelection.map(v => v.classId) //把对象数组转为id数组
                this.$api.classObj.batchRemoveClass(ids).then(res => {
                    if (res.code == 2000) {
                        this.$message.success("解散成功");
                    }
                    else {
                        this.$message.error(res.message);
                    }
                    this.load();
                })
            },
            showBatchDelById() {
                this.batchDialogVisible = true;

            },
            removeClass(classId) {
                this.$api.classObj.removeClass(classId).then(res => {
                    if (res.code == 2000) {
                        this.$message.success("解散成功");
                    }
                    else {
                        this.$message.error(res.message);
                    }
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
                    className: this.className,
                    id: localStorage.getItem("id"),
                }
                this.$api.classObj.getAllClassByIdPage(data).then(res => {
                    this.tableData = res.data;
                    this.total = res.total;
                })
            },
            handleSearch() {
                if (this.className.trim()) {
                    this.load();
                } else {
                    this.className = '';
                    this.load();
                }
            },
        },
        mounted() {
            this.load(); // 在组件挂载后加载数据
        },
        watch: {
            className(newVal) {
                if (newVal.trim() === '') {
                    this.load();
                } else {
                    this.handleSearch();
                }
            },
        },
    }
</script>

<style scoped>
    .transfer-footer {
        margin-left: 20px;
        padding: 6px 5px;
    }
</style>