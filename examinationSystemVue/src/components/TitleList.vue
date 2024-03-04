<template>
    <div v-if="(tableData && tableData.length > 0) || titleName">
        <div style="padding: 10px 0">
            <el-select v-model="titleTypeFilter" placeholder="题型筛选" class="mr-5">
                <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
            </el-select>
            <el-input style="width: 200px" placeholder="请输入题目名称" suffix-icon="el-icon-search" class="mr-5"
                v-model="titleName">
            </el-input>
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            <el-button type="primary" icon="el-icon-refresh" @click="reset">重置</el-button>
        </div>
        <div style="margin: 10px 0">
            <el-button type="danger" @click="showBatchDelById" v-if="isShowBatchButton">批量删除题目<i class="el-icon-delete"
                    style="margin-left: 2px"></i>
            </el-button>
        </div>

        <el-table :data="tableData" border :header-cell-class-name="tableTitle"
            @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55"></el-table-column>
            <el-table-column prop="titleType" label="题目类型" width="80px"></el-table-column>
            <el-table-column prop="titleContent.scores" label="题目分数" width="50px"></el-table-column>
            <el-table-column prop="titleCreateStamp" label="创建时间" width="120px"></el-table-column>
            <el-table-column prop="titleContent.name" label="题干"></el-table-column>
            <el-table-column prop="operate" label="操作">
                <template slot-scope="scope">
                    <el-button class="ml-5" slot="reference" type="primary" size="mini" v-if="isShowDetailButton"
                        @click="showTitleDetail(scope.row)">详情
                    </el-button>
                    <el-popconfirm confirm-button-text='确定' cancel-button-text='取消' icon="el-icon-info" icon-color="red"
                        title="是否删除该题目？" @confirm="deleteTitleById(scope.row.titleId)">
                        <el-button class="ml-5" slot="reference" type="danger" size="mini" v-if="isShowDelButton">删除
                        </el-button>
                    </el-popconfirm>

                </template>
            </el-table-column>
        </el-table>

        <el-dialog title="批量删除题目" :visible.sync="batchDialogVisible" width="30%">
            <span>是否批量删除题目</span>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="batchDeleteTitleById">确 定</el-button>
            </span>
        </el-dialog>

        <div style="padding: 10px 0">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum"
                :page-sizes="[2, 5, 10, 20]" :page-size="pageSize" layout="total,sizes, prev, pager, next"
                :total="total">
            </el-pagination>
        </div>

        <el-dialog :visible.sync="dialogVisible" title="题目详情" width="50%">
            <div v-if="selectedQuestion">
                <p class="mb-10"><strong>题目类型：</strong> {{ selectedQuestion.titleType }}</p>
                <p class="mb-20" style="line-height: 1.5"><strong>题目内容：</strong> {{ selectedQuestion.titleContent.name
                    }}</p>
                <strong
                    v-if="selectedQuestion.titleType === '单选题' || selectedQuestion.titleType === '多选题' ">选项：</strong>
                <div v-if="selectedQuestion.titleType === '单选题'" class="select-style">
                    <el-radio-group class="custom-radio-group">
                        <el-radio v-for="(option, optionIndex) in selectedQuestion.titleContent.selectInput"
                            :key="optionIndex" :label="option.select" class="custom-radio">
                            {{ option.select }}. {{ option.content }}
                        </el-radio>
                    </el-radio-group>
                </div>
                <div v-else-if="selectedQuestion.titleType === '多选题'" class="select-style">
                    <el-checkbox-group class="custom-checkbox-group" v-model="checkList">
                        <el-checkbox v-for="(option, optionIndex) in selectedQuestion.titleContent.selectInput"
                            :key="optionIndex" :label="option.select" class="custom-checkbox">
                            {{ option.select }}. {{ option.content }}
                        </el-checkbox>
                    </el-checkbox-group>
                </div>
                <p class="mb-10"><strong>题目分数：</strong> {{ selectedQuestion.titleContent.scores }}</p>
                <p class="mb-10" style="line-height: 1.5"><strong>标准答案：</strong>
                    {{ selectedQuestion.titleType === '判断题' ? (selectedQuestion.titleContent.answer ? '正确' : '错误') :
                    selectedQuestion.titleContent.answer }}
                </p>
                <div>
                </div>
            </div>
            <div v-else>
                <p>未选择题目</p>
            </div>
        </el-dialog>
    </div>
    <el-empty v-else description="尝试创建自己的题库"></el-empty>
</template>
<script>
    import '@/styles/global.css';
    import "@/styles/studentList.css";
    import "@/styles/exam.css";

    export default {
        name: 'ClassList',
        data() {
            return {
                checkList: [],
                options: [{
                    value: '1',
                    label: '无'
                }],
                pageNum: 1,
                pageSize: 5,
                titleName: '',
                titleTypeFilter: '',
                tableData: [],
                total: 0,
                tableTitle: 'tableTitle',
                userIdToDelete: 0,
                batchDialogVisible: false,
                multipleSelection: [],
                isShowBatchButton: true,
                isShowDelButton: true,
                isShowDetailButton: true,
                dialogVisible: false,  // 对话框可见性
                selectedQuestion: null  // 选中的题目
            }
        },
        methods: {
            showTitleDetail(question) {
                this.selectedQuestion = question;
                this.dialogVisible = true;
            },
            reset() {
                this.titleName = '';
                this.titleTypeFilter = '';
            },
            showDetailButton(parameters) {
                this.isShowDetailButton = parameters;
            },
            showBatchButton(parameters) {
                this.isShowBatchButton = parameters;
            },
            showDelButton(parameters) {
                this.isShowDelButton = parameters;
            },
            exp() {
                window.open("http://localhost:9090/teacher/exportStudentInfo")
            },
            handleSelectionChange(val) {
                this.multipleSelection = val;
            },
            batchDeleteTitleById() {
                this.batchDialogVisible = false;
                let ids = this.multipleSelection.map(v => v.titleId)         //把对象数组转为id数组
                this.$api.titleObj.batchDeleteTitleById(ids).then(res => {
                    if (res.code == 2000) {
                        this.$message.success("删除成功");
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
            deleteTitleById(id) {
                // 发送删除请求
                this.$api.titleObj.deleteTitleById(id).then(res => {
                    if (res.code == 2000) {
                        this.$message.success("删除成功");
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
                    id: parseInt(localStorage.getItem('id')),
                    name: this.titleName,
                    titleType: this.titleTypeFilter,
                }
                this.$api.titleObj.getAllTitlesByTeacherId(data).then(res => {
                    this.tableData = res.data;
                    this.total = res.total;
                })
            },
            handleSearch() {
                if (this.titleName.trim()) {
                    this.load();                // 如果搜索框有内容，执行搜索
                } else {
                    this.titleName = '';     // 清空搜索框
                    this.load();                // 如果搜索框为空，加载数据
                }
            },
            loadAllTitle() {
                this.$api.titleObj.getAllTitleType().then(res => {
                    if (res.code == 2000) {
                        res.data.forEach(item => {
                            this.options.push({ value: item, label: item });
                        });
                    }
                });
            }
        },
        mounted() {
            this.loadAllTitle();
            this.load();
        },
        watch: {
            titleName(newVal) {
                if (newVal.trim() === '') {
                    this.load(); // 如果搜索框为空，加载数据
                } else {
                    this.handleSearch(); // 执行搜索操作
                }
            },
            titleTypeFilter(newVal) {
                if (newVal.trim() === '') {
                    this.load(); // 如果搜索框为空，加载数据
                } else {
                    this.handleSearch(); // 执行搜索操作
                }
            },
        },
    }
</script>

<style>
    .select-style {
        margin-left: 75px;
        margin-bottom: 10px;
    }
</style>