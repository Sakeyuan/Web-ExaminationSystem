<template>
    <div class="login-page">
        <div class="step-block">
            <el-steps :active="2" finish-status="success" simple>
                <el-step title="步骤 1"></el-step>
                <el-step title="步骤 2"></el-step>
            </el-steps>
        </div>
        <div class="login-container">
            <div class="form-container">
                <div class="form-body">
                    <div class="header">
                        <h2>教师注册</h2>
                    </div>
                    <div class="form-group">
                        <div class="input-group">
                            <el-input type="text" placeholder="请完善工号" suffix-icon="el-icon-user"
                                v-model="teacherNumber">
                            </el-input>
                        </div>
                        <div class="input-group">
                            <el-input type="password" placeholder="请输入密码" suffix-icon="el-icon-lock"
                                v-model="userPassword">
                            </el-input>
                        </div>
                        <div class="input-group">
                            <el-input type="password" placeholder="再次请输入密码" suffix-icon="el-icon-lock"
                                v-model="reuserPassword">
                            </el-input>
                        </div>
                        <div class="input-group flex" style="margin-top: 15px">
                            <router-link to="/login/teacher-login" class="forgot">
                                <i class="el-icon-user-solid mr-2"></i>教师登录
                            </router-link>
                            <router-link to="/login/student-login" class="forgot">
                                <i class="el-icon-user-solid mr-2"></i>学生登录
                            </router-link>
                            <router-link to="/forgot-password" class="forgot">
                                <i class="el-icon-question mr-2"></i>忘记密码?
                            </router-link>
                        </div>
                        <div class="input-group right">
                            <el-button @click="register">注册</el-button>
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
                teacherNumber: '',
                userPassword: '',
                reuserPassword: ''
            };
        },
        mounted() {
            this.userPhone = this.$route.query.userPhone;
        },
        methods: {
            register() {
                if (this.teacherNumber == '') {
                    this.$message.error("请输入工号")
                    return;
                }
                if (this.userPassword == '' || this.reuserPassword == '') {
                    this.$message.error("请输入密码")
                    return;
                }

                if (this.userPassword !== this.reuserPassword) {
                    this.$message.error("密码不一致");
                    return;
                }
                const data = {
                    userPhone: this.userPhone,
                    userPassword: this.userPassword,
                    number: this.teacherNumber,
                    role: 2,
                };
                this.$api.registerObj.teacherRegister(data).then(res => {
                    if (res.code == 2000) {
                        this.$message.success("注册成功");
                        this.$store.commit('setUserRegisterInfo', 0, 0);
                        setTimeout(() => {
                            if (localStorage.getItem('token')) {
                                this.$store.commit('clearUser');
                            }
                            this.$router.push("/");
                        }, 1000);
                    } else {
                        this.$message.error("注册失败" + res.message);
                    }
                }).catch(err => {
                    this.$message.error("注册失败" + err.message);
                });
            }
        }


    };
</script>