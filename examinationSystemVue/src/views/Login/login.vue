<template>
    <div class="login-page">
        <div class="login-container">
            <div class="form-container">
                <div class="form-body">
                    <div class="header">
                        <h2>登录</h2>
                        <p>没有账户? <router-link to="/register">创建账户</router-link>
                        </p>
                    </div>
                    <div class="form-group">
                        <div class="input-group">
                            <el-input type="text" placeholder="请输入手机号" suffix-icon="el-icon-mobile-phone"
                                v-model="userPhone" @keyup.enter.native="login"></el-input>
                        </div>
                        <div class="input-group">
                            <el-input type="password" placeholder="请输入密码" suffix-icon="el-icon-lock"
                                v-model="userPassword" @keyup.enter.native="login"></el-input>
                        </div>
                        <div class="input-group flex" style="margin-top: 20px">
                            <router-link to="/forgot-password" class="forgot">
                                <i class="el-icon-question mr-2"></i>忘记密码?
                            </router-link>
                        </div>
                        <div class="input-group right">
                            <el-button @click="login" :disabled="!canSubmit">登录</el-button>
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
    import "@/styles/global.css"
    export default {
        beforeCreate() {
            document.querySelector("body").classList.add("login-page");
        },
        beforeDestroy() {
            document.querySelector("body").classList.remove("login-page");
        },
        data() {
            return {
                userPhone: '',
                userPassword: '',
            };
        },
        computed: {
            canSubmit() {
                return Boolean(this.userPhone && this.userPassword);
            },
        },
        methods: {
            login() {
                if (!this.userPhone.trim() || !this.userPassword.trim()) {
                    this.$message.error("请输入手机号和密码");
                    return;
                }
                const UserDTO = {
                    userPhone: this.userPhone,
                    userPassword: this.userPassword,
                }
                this.$api.loginObj.login(UserDTO).then(res => {
                    if (res.code === 2000) {
                        this.$store.dispatch('initialize', res.data);
                        if (res.data.role === "教师") {
                            this.$router.push("/teacher/home");
                        }
                        else if (res.data.role === "学生") {
                            this.$router.push("/student");
                        }
                        else {
                            this.$message.error(res.message);
                        }

                    } else {
                        this.$message.error(res.message);
                    }

                }).catch(err => {
                    this.$message.error("登录失败" + err.message);
                })

            }
        }
    };
</script>

<style scoped>

</style>