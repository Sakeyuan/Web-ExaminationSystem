<template>
    <div>
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
            <el-form-item label="题目名称" prop="name">
                <el-input v-model="ruleForm.name" type="textarea" autosize placeholder="请输入题目"></el-input>
            </el-form-item>

            <el-form-item label="题目选项" class="mt-30" prop="selectInput">
                <div v-for="item in ruleForm.selectInput" :key="item.id" style="display: flex;">
                    <el-select v-model="item.select" placeholder="选项"
                        style="width: 80px; margin-bottom: 10px; display: inline;">
                        <el-option v-for="letter in letters" :key="letter" :label="letter" :value="letter"></el-option>
                    </el-select>
                    <el-input v-model="item.content" style="width: 800px; margin-left: 10px; display: inline;"
                        placeholder="请输入题目">
                    </el-input>
                </div>
                <el-button @click="addInput">添加选项</el-button>
                <el-button @click="deleteInput(index)">删除选项</el-button>
            </el-form-item>

            <el-form-item label="题目答案" prop="answer" class="mt-30">
                <el-checkbox :indeterminate="ruleForm.isIndeterminate" v-model="ruleForm.checkAll"
                    @change="handleCheckAllChange">全选</el-checkbox>
                <div style="margin: 15px 0;"></div>
                <el-checkbox-group v-model="selectedAnswers">
                    <el-checkbox :label="item.select" v-for="item in ruleForm.selectInput" :key="item.id">
                        {{ item.select }}
                    </el-checkbox>
                </el-checkbox-group>
            </el-form-item>

            <el-form-item label="题目分数" prop="scores" class="mt-30">
                <el-slider v-model="ruleForm.scores" show-input>
                </el-slider>
            </el-form-item>

            <el-form-item class="mt-30">
                <el-button type="primary" @click="submitForm('ruleForm')">立即创建</el-button>
                <el-button @click="resetForm('ruleForm')">重置</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>
<script>
    import "@/styles/createTitle.css"
    import "@/styles/global.css"

    export default {
        name: 'createMultipleTitle',
        data() {
            var checkSelect = (rule, value, callback) => {
                for (let i = 0; i < value.length; i++) {
                    const item = value[i];
                    if (item.select === '') {
                        callback(new Error(`第 ${i + 1} 个选项需要选择选项`));
                    }
                    if (item.content === '') {
                        callback(new Error(`第 ${i + 1} 个选项需要填写题目`));
                    }
                }
                callback();
            };
            return {
                selectedAnswers: [],
                ruleForm: {
                    name: '',
                    scores: 1,
                    type: 2,
                    checkAll: false,
                    isIndeterminate: true,
                    selectInput: [
                        { id: '1', select: 'A', content: '' },
                        { id: '2', select: 'B', content: '' },
                        { id: '3', select: 'C', content: '' },
                        { id: '4', select: 'D', content: '' },
                    ],
                    answer: '',
                    teacherId: 0,
                },
                letters: Array.from({ length: 26 }, (_, index) => String.fromCharCode(65 + index)), // 生成 A 到 Z 的字母数组
                rules: {
                    name: [
                        { required: true, message: '请输入题目名称', trigger: 'blur' },
                        { min: 1, max: 500, message: '长度在 1 到 500 个字符', trigger: 'blur' }
                    ],
                    answer: [
                        { required: true, message: '请选择标准答案', trigger: 'change' }
                    ],
                    scores: [ // 添加得分的验证规则
                        { required: true, message: '请填写题目分数', trigger: 'blur' },
                        { type: 'number', min: 1, max: 100, message: '分数范围为1到100', trigger: 'blur' }
                    ],
                    selectInput: [
                        { validator: checkSelect, trigger: 'blur' },
                    ],
                }
            };
        },
        methods: {
            handleChange(value) {
                this.$forceUpdate();
            },
            handleCheckAllChange(val) {
                if (val) {
                    this.selectedAnswers = this.ruleForm.selectInput.map(item => item.select);
                } else {
                    this.selectedAnswers = [];
                }
                this.ruleForm.isIndeterminate = false;
            },
            handleCheckedCitiesChange(value) {
                const checkedCount = value.length;
                this.ruleForm.checkAll = checkedCount === this.ruleForm.selectInput.length;
                this.ruleForm.isIndeterminate = checkedCount > 0 && checkedCount < this.ruleForm.selectInput.length;
            },
            handleChange(value) {
                this.$forceUpdate();
            },
            addInput() {
                const newId = this.ruleForm.selectInput.length + 1; // 设置新的 ID
                console.log("newId:" + newId);
                this.ruleForm.selectInput.push({ id: String(newId), select: '', content: '' });
            },
            deleteInput() {
                this.ruleForm.selectInput.pop();
            },
            submitForm(formName) {
                this.ruleForm.answer = this.selectedAnswers.join('、');
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        this.ruleForm.teacherId = localStorage.getItem('id');
                        // 发送网络请求
                        this.$api.titleObj.createTitle(this.ruleForm).then(res => {
                            if (res.code == 2000) {
                                this.$message({
                                    message: '创建成功',
                                    type: 'success',
                                });
                            }
                            else {
                                this.$message({
                                    message: res.message,
                                    type: 'error',
                                });
                            }
                        }).catch(error => {
                            this.$message({
                                message: error.message,
                                type: 'error',
                            });
                        });
                    } else {
                        console.log('表单验证失败，未发送网络请求。');
                    }
                });
            },
            resetForm(formName) {
                this.$refs[formName].clearValidate();
                this.ruleForm = {
                    name: '',
                    scores: 1,
                    type: 2,
                    checkAll: false,
                    isIndeterminate: true,
                    selectInput: [
                        { id: '1', select: 'A', content: '' },
                        { id: '2', select: 'B', content: '' },
                        { id: '3', select: 'C', content: '' },
                        { id: '4', select: 'D', content: '' },
                    ],
                    answer: '',
                    teacherId: 0,
                };
                this.selectedAnswers = [];
                this.$forceUpdate();
            },

        }
    }
</script>