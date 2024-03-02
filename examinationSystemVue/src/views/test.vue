<template>
    <div class="login-page">
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
                            <el-input type="text" placeholder="请输入密码" v-model="email" suffix-icon="el-icon-s-check">
                            </el-input>
                        </div>
                        <div style="display: flex">
                            <el-input type="text" placeholder="请再次输入密码" suffix-icon="el-icon-s-check"
                                v-model="verificationCode"></el-input>
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
                isSending: false,
                email: "",
                verificationCode: "",
                countdown: 60,
                isCounting: false, // 控制按钮状态
            };
        },
        methods: {
            async sendVerificationCode(event) {
                event.preventDefault();
                if (this.email && this.isValidEmail(this.email)) {
                    console.log("发送验证码中...........");
                    this.isSending = true; // 设置发送中状态
                    try {
                        const res = await this.$api.userObj.sendCode(this.email);
                        if (res.code === 2000) {
                            this.$message.success("发送成功");
                            this.startCountdown();
                        }
                    } catch (error) {
                        console.error("发送验证码请求错误:", error);
                    } finally {
                        this.isSending = false; // 无论请求成功或失败，都要设置发送结束状态
                    }
                } else {
                    this.$message.error("请输入有效的邮箱地址");
                }
            },
            startCountdown() {
                this.isCounting = true;
                const timer = setInterval(() => {
                    this.countdown -= 1;
                    if (this.countdown <= 0) {
                        this.resetCountdown();
                        clearInterval(timer);
                    }
                }, 1000);
            },
            resetCountdown() {
                this.countdown = 60;
                this.isCounting = false;
            },
            isValidEmail(email) {
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                return emailRegex.test(email);
            },
            confirm() {
                if (this.verificationCode) {
                }
                else {
                    this.$message.error("请输入验证码");
                }
            },
        },
    };
</script>