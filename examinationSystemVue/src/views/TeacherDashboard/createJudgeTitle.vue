<template>
    <div>
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
            <el-form-item label="题目名称" prop="name">
                <el-input v-model="ruleForm.name" type="textarea" autosize placeholder="请输入题目"></el-input>
            </el-form-item>

            <el-form-item label="题目答案" prop="answer" class="mt-30">
                <el-select v-model="ruleForm.answer" placeholder="请选择">
                    <el-option v-for="item in ruleForm.options" :key="item.value" :label="item.label"
                        :value="item.value">
                    </el-option>
                </el-select>
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
        data() {
            return {
                ruleForm: {
                    name: '',
                    scores: 1,
                    type: 3,
                    options: [{
                        value: 'true',
                        label: '正确'
                    }, {
                        value: 'false',
                        label: '错误    '
                    }],
                    answer: '',
                    teacherId: 0,
                },
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
                }
            };
        },
        methods: {
            submitForm(formName) {
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
                    type: 3,
                    options: [{
                        value: 'true',
                        label: '正确'
                    }, {
                        value: 'false',
                        label: '错误    '
                    }],
                    answer: '',
                    teacherId: 0,
                };
                this.$forceUpdate();
            },

        }
    }
</script>