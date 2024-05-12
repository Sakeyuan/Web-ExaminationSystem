<template>
    <div class="login-page">
        <div class="step-block">
            <el-steps :active="1" finish-status="success" simple>
                <el-step title="步骤 1"></el-step>
                <el-step title="步骤 2"></el-step>
            </el-steps>
        </div>
        <div class="login-container">
            <div class="form-container">
                <div class="form-body">
                    <div class="header">
                        <h2>用户注册</h2>
                    </div>
                    <div class="form-group">
                        <div class="input-group">
                            <el-input type="text" placeholder="请输入手机号" suffix-icon="el-icon-mobile-phone"
                                v-model="userPhone">
                            </el-input>
                        </div>
                        <div class="input-group">
                            <el-select v-model="identity" placeholder="请选择身份">
                                <el-option v-for="item in options" :key="item.value" :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                        </div>

                        <div class="input-group flex" style="margin-top: 15px">
                            <router-link to="/" class="forgot">
                                <i class="el-icon-user-solid mr-2"></i>登录
                            </router-link>
                            <router-link to="/forgot-password" class="forgot">
                                <i class="el-icon-question mr-2"></i>忘记密码?
                            </router-link>
                        </div>
                        <div class="input-group right">
                            <el-button @click="next">下一步</el-button>
                        </div>
                    </div>
                </div>

                <div class="form-image">
                    <div class="text">
                        <h2>欢迎使用考试系统 <br /></h2>
                        <p>Sake考试系统为您服务 !</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import "@/styles/login.css"
    export default {
        data() {
            return {
                active: 1,
                options: [{
                    value: '1',
                    label: '学生'
                }, {
                    value: '2',
                    label: '教师'
                },],
                identity: '',
                userPhone: ''
            };
        },
        methods: {
            beforeCreate() {
                document.querySelector("body").classList.add("login-page");
            },
            beforeDestroy() {
                document.querySelector("body").classList.remove("login-page");
            },
            next() {
                if (this.userPhone == '') {
                    this.$message("请输入手机号或者邮箱")
                    return;
                }
                if (this.identity == '') {
                    this.$message("请选择身份")
                    return;
                }
                this.$api.registerObj.uploadRegisterInfo(this.userPhone).then(res => {
                    if (res.code == 2000) {
                        //  学生注册
                        if (this.identity == '1') {
                            this.$router.push({
                                path: "/register/student-register", query: {
                                    userPhone: this.userPhone,
                                }
                            });
                        }
                        // 老师注册
                        else if (this.identity == '2') {
                            this.$router.push({
                                path: "/register/teacher-register", query: {
                                    userPhone: this.userPhone,
                                }
                            });
                        }
                        else {
                            this.$message("请选择身份");
                        }
                    }
                    else {
                        this.$message.error("注册失败：" + res.message);
                    }
                }).catch(error => {
                    this.$message.error("注册失败" + error.message);
                });
            }
        }
    };
</script>