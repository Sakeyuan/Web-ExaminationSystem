<template>
    <div class="login-page">
        <span class="back-link" @click="goBack"><i class="el-icon-back"></i> 返回</span>
        <div class="login-container">
            <div class="form-container">
                <div class="form-body">
                    <div class="header">
                        <h2>重置密码</h2>
                        <p>
                            没有账户? <router-link to="/register">创建账户</router-link>
                        </p>
                    </div>
                    <div class="form-group">
                        <div class="input-group">
                            <el-input type="text" placeholder="请输入密码" v-model="password" suffix-icon="el-icon-s-check">
                            </el-input>
                        </div>
                        <div style="display: flex">
                            <el-input type="text" placeholder="请再次输入密码" suffix-icon="el-icon-s-check"
                                v-model="rePassword"></el-input>
                        </div>
                        <div class="input-group right">
                            <el-button @click="confirm" style="margin-top:30px">确认</el-button>
                        </div>
                    </div>
                </div>
                <div class="form-image">
                    <div class="text">
                        <h2>欢迎使用考试系统</h2>
                        <p>Sake考试系统为您服务!</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import "@/styles/login.css";
    import "@/styles/global.css";

    export default {
        data() {
            return {
                email: "",
                password: "",
                rePassword: "",
            };
        },
        created() {
            this.email = this.$route.query.email;
        },
        methods: {
            goBack() {
                this.$router.push('/forgot-password');
            },
            confirm() {
                if (this.password) {
                    if (this.password === this.rePassword) {
                        const loading = this.$loading({
                            lock: true,
                            text: '正在重新设置密码中，请稍等.......',
                            spinner: 'el-icon-loading',
                            background: 'rgba(0, 0, 0, 0.7)'
                        });
                        const data = {
                            email: this.email,
                            password: this.password,
                        }
                        this.$api.userObj.setPassword(data).then(res => {
                            if (res.code === 2000) {
                                setTimeout(() => {
                                    loading.close();
                                    this.$message.success("设置成功，稍后将重新登录");
                                    this.$router.push("/");
                                }, 2000);
                            }
                            else {
                                this.$message.error(res.message);
                            }
                        })
                    }
                    else {
                        this.$message.error("密码不一致")
                    }

                }
                else {
                    this.$message.error("请输入密码");
                }
            },
        },
    };
</script>