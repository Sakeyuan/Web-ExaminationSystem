<template>
    <div v-if="classList && classList.length > 0">
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm"
            :label-position="labelPosition">
            <el-form-item label="试卷名称" prop="name" label-width="80px">
                <el-input v-model="ruleForm.name" type="textarea" autosize placeholder="请输入试卷名称"></el-input>
            </el-form-item>

            <el-form-item label="是否允许考后查看" prop="isAllowCheck" label-width="140px">
                <el-switch style="display: block;margin-top: 3px;" v-model="ruleForm.isAllowCheck"
                    active-color="#13ce66" inactive-color="#ff4949" active-text="是" inactive-text="否" active-value='1'
                    inactive-value='0'>
                </el-switch>
            </el-form-item>

            <el-form-item label="时间设置(分钟)" prop="examTotalTime" class="mt-30" label-width="120px">
                <div class="ml-5">
                    <el-slider v-model="ruleForm.examTotalTime" show-input :min="0" :max="500" :step="10" show-stops>
                    </el-slider>
                </div>
            </el-form-item>

            <el-form-item label="班级列表" prop="checkedClass" label-width="80px">
                <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">全选
                </el-checkbox>
                <div style="margin: 15px 0;"></div>
                <el-checkbox-group v-model="ruleForm.checkedClass" @change="handleCheckedChange">
                    <el-checkbox v-for="classItem in classList" :label="classItem.className" :key="classItem.classId">{{
                        classItem.className }}</el-checkbox>
                </el-checkbox-group>
            </el-form-item>
            <el-form-item v-for="(subheading, index) in ruleForm.subheadings" :label="'标题' + (index + 1)"
                :key="subheading.key" :prop="'subheadings.' + index + '.value'"
                :rules="{required: true, message: '标题不能为空', trigger: 'blur'}"
                style="margin-left: 80px;margin-top: 30px;" label-width="80px">
                <el-row type="flex" align="middle">
                    <el-col :span="18">
                        <el-input type="textarea" autosize v-model="subheading.value"></el-input>
                    </el-col>
                    <el-col :span="6">
                        <el-button @click.prevent="getTitleList(subheading)" class="ml-5">添加题目</el-button>
                        <el-button @click.prevent="removeSubheading(subheading)" class="ml-5">删除</el-button>
                    </el-col>
                </el-row>
                <div class="shadow-box mt-30" v-if="subheading.hasCardData">
                    <div v-for="(item, i) in subheading.cardData" :key="i" class="title-item">
                        <div class="title-div">
                            <div>
                                题目{{ i + 1 }}<span class="colon-separator">:</span> {{ item.titleContent.name }}
                                <template v-if="item.titleType === '判断题'">
                                    <span class="judge-separator">( )</span>
                                </template>
                                <template v-if="item.titleType === '填空题'">
                                    <span class="judge-separator"></span>
                                </template>
                            </div>
                            <div class="text item answer-div">
                                <div class="select-input-container">
                                    <template v-if="item.titleType === '单选题' || item.titleType === '多选题'">
                                        <div v-for="(input, index) in item.titleContent.selectInput" :key="index"
                                            class="select-item">
                                            <div>
                                                {{ input.select }}<span class="dot-separator">. </span>{{
                                                input.content}}
                                            </div>
                                        </div>
                                    </template>
                                    <div v-else-if="item.titleType === '简答题'">
                                        <div class="mt-5">
                                            <textarea rows="5" cols="50" placeholder="请输入答案"></textarea>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </el-form-item>
            <el-form-item class="mt-30">
                <el-button @click="addSubheading" type="primary">添加标题</el-button>
                <el-button type="primary" @click="submitForm('ruleForm')">立即创建</el-button>
                <el-button @click="resetForm('ruleForm')">重置</el-button>
            </el-form-item>
        </el-form>
        <el-dialog title="题目列表" :visible.sync="dialogTableVisible" width="800px" top="10vh" :center="true">
            <div style="padding: 10px 0">
                <el-select v-model="titleTypeFilter" placeholder="题型筛选" class="mr-5">
                    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                </el-select>
                <el-input style="width: 200px" placeholder="请输入题目名称" suffix-icon="el-icon-search" class="mr-5"
                    v-model="titleName">
                </el-input>
                <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
                <el-button type="primary" icon="el-icon-refresh" @click="reset">清空</el-button>
            </div>
            <el-table :data="tableData" border :header-cell-class-name="tableTitle"
                @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55"></el-table-column>
                <el-table-column prop="titleId" label="ID" width="50"></el-table-column>
                <el-table-column prop="titleType" label="题目类型" width="60px"></el-table-column>
                <el-table-column prop="titleContent.name" label="题干"></el-table-column>
            </el-table>
            <div style="padding: 10px 0">
                <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    :current-page="pageNum" :page-sizes="[2, 5, 10, 20]" :page-size="pageSize"
                    layout="total,sizes, prev, pager, next" :total="total">
                </el-pagination>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogTableVisible = false">取 消</el-button>
                <el-button type="primary" @click="getTitleById">确 定</el-button>
            </span>
        </el-dialog>
    </div>
    <el-empty v-else description="请先创建班级"></el-empty>
</template>
<script>
    import "@/styles/createTitle.css";
    import "@/styles/global.css";
    import "@/styles/releasePaper.css";
    import TitleList from "@/components/TitleList.vue";
    export default {
        components: {
            TitleList,
        },
        data() {
            var validateExamTotalTime = (rule, value, callback) => {
                if (value < 1 || value > 500) {
                    return callback(new Error('时间长度在 1 到 500 个分钟'));
                } else {
                    return callback();
                }
            };
            return {
                labelPosition: 'left',
                options: [{
                    value: '1',
                    label: '无'
                }],
                titleTypeFilter: '',
                currentSubheadingIndex: -1,
                dialogTableVisible: false,
                pageNum: 1,
                pageSize: 5,
                total: 0,
                titleName: '',
                tableTitle: 'tableTitle',
                tableData: [],
                multipleSelection: [],
                classList: [],
                checkAll: false,
                isIndeterminate: true,
                subheadingTmp: {},
                ruleForm: {
                    name: '',
                    examTotalTime: 0,
                    checkedClass: [],
                    isAllowCheck: '0',
                    subheadings: [
                        {
                            value: '',
                            key: Date.now(),
                            hasCardData: false,
                            cardData: [],
                        },
                    ]
                },
                paperData: [],
                rules: {
                    name: [
                        { required: true, message: '请输入题目名称', trigger: 'blur' },
                        { min: 1, max: 500, message: '长度在 1 到 500 个字符', trigger: 'blur' }
                    ],
                    isAllowCheck: [{
                        required: true,
                    }],
                    examTotalTime: [
                        { required: true, trigger: 'blur' },
                        { validator: validateExamTotalTime, trigger: 'blur' }
                    ],
                    checkedClass: [
                        { type: 'array', required: true, message: '请至少选择一个班级', trigger: 'blur' }
                    ],
                }
            };
        },
        mounted() {
            this.getAllClassByTeacherId();
            this.loadAllTitle();
        },
        methods: {
            reset() {
                this.titleName = '';
                this.titleTypeFilter = '';
            },
            loadAllTitle() {
                this.$api.titleObj.getAllTitleType().then(res => {
                    if (res.code == 2000) {
                        res.data.forEach(item => {
                            this.options.push({ value: item, label: item });
                        });
                    }
                });
            },
            getTitleById() {
                this.currentSubheadingIndex = this.ruleForm.subheadings.indexOf(this.subheadingTmp);
                const selectedIds = this.multipleSelection.map(item => item.titleId);
                const existingCardData = this.ruleForm.subheadings[this.currentSubheadingIndex].cardData;
                const existingIds = existingCardData.map(existingTitle => existingTitle.titleId);
                const filteredIds = selectedIds.filter(id => !existingIds.includes(id));
                if (filteredIds.length === 0) {
                    this.$message("题目已经存在")
                    return;
                }
                this.$api.titleObj.getTitleById(filteredIds).then(res => {
                    if (res.code === 2000) {
                        this.dialogTableVisible = false;
                        if (this.currentSubheadingIndex !== -1) {
                            this.ruleForm.subheadings[this.currentSubheadingIndex].hasCardData = true;
                            this.ruleForm.subheadings[this.currentSubheadingIndex].cardData = [
                                ...existingCardData,
                                ...res.data
                            ];
                        }
                    } else {
                        this.$message({
                            message: res.message,
                            type: 'error',
                        });
                    }
                })
            },
            addSubheading() {
                this.ruleForm.subheadings.push({
                    value: '',
                    key: Date.now(),
                    hasCardData: false,
                    cardData: [],
                });
            },
            removeSubheading(item) {
                const index = this.ruleForm.subheadings.indexOf(item);
                if (index !== -1) {
                    this.ruleForm.subheadings.splice(index, 1);
                }
            },
            getAllClassByTeacherId() {
                this.$api.classObj.getAllClassByTeacherId(localStorage.getItem("id")).then(res => {
                    if (res.code == 2000) {
                        this.classList = res.data;
                        console.log("classList: " + JSON.stringify(this.classList));
                    }
                    else {
                        this.$message.error(res.message)
                    }

                })
            },
            getTitleList(subheading) {
                this.subheadingTmp = subheading;
                this.load();
                this.dialogTableVisible = true;
            },
            handleCheckAllChange(val) {
                if (val) {
                    this.ruleForm.checkedClass = this.classList.map(classItem => classItem.className);
                }
                else {
                    this.ruleForm.checkedClass = [];
                }
                this.isIndeterminate = false;
            },
            handleCheckedChange(value) {
                let checkedCount = value.length;
                this.checkAll = checkedCount === this.classList.length;
                this.isIndeterminate = checkedCount > 0 && checkedCount < this.classList.length;
                this.ruleForm.checkedClass = value;
            },
            mapClassNameToClassIds(classNames) {
                return classNames.map(className => {
                    const classItem = this.classList.find(item => item.className === className);
                    return classItem ? classItem.classId : null;
                });
            },
            handlePaperData() {
                const jsonData = this.ruleForm.subheadings.map((subheading, index) => {
                    const subheadingData = {
                        title: subheading.value,
                        ids: []
                    };
                    if (subheading.hasCardData) {
                        subheading.cardData.forEach((card, cardIndex) => {
                            subheadingData.ids.push(card.titleId);
                        });
                    }
                    return subheadingData;
                });

                const finalJson = {
                    paperName: this.ruleForm.name,
                    examTotalTime: this.ruleForm.examTotalTime,
                    selectedClasses: this.mapClassNameToClassIds(this.ruleForm.checkedClass),
                    subheadings: jsonData,
                    teacherId: parseInt(localStorage.getItem('id')),
                    isAllowCheck: this.ruleForm.isAllowCheck,
                };
                this.paperData = finalJson;
            },

            submitForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        this.ruleForm.subheadings.forEach((subheading, index) => {
                            if (!subheading.value || subheading.value.trim() === '' || !subheading.hasCardData) {
                                this.$message.error("提交失败，请检查标题和题目是否存在");
                                return;
                            }
                            this.handlePaperData();
                        });
                        this.$api.paperObj.releasePaper(this.paperData).then(res => {
                            if (res.code == 2000) {
                                this.$message.success("创建成功");
                            } else {
                                this.$message.error(res.message);
                            }
                        })
                    } else {
                        console.log('error submit!!');
                        return false;
                    }
                });
            },

            resetForm(formName) {
                this.$refs[formName].clearValidate();
                this.checkAll = false;
                this.isIndeterminate = false;
                this.ruleForm.checkedClass = [];
                this.ruleForm.subheadings.forEach(subheading => {
                    subheading.value = '';
                    subheading.hasCardData = false;
                    subheading.cardData = [];
                });
            },

            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.load();
            },
            handleCurrentChange(pageNum) {
                this.pageNum = pageNum;
                this.load();
            },
            handleSearch() {
                if (this.titleName.trim()) {
                    this.load();
                } else {
                    this.titleName = '';
                    this.load();
                }
            },
            handleSelectionChange(val) {
                this.multipleSelection = val;
            },
            load() {
                const data = {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                    id: parseInt(localStorage.getItem('id')),
                    name: this.titleName,
                    titleType: this.titleTypeFilter
                }
                this.$api.titleObj.getAllTitlesByTeacherId(data).then(res => {
                    this.tableData = res.data;
                    this.total = res.total;
                })
            },
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