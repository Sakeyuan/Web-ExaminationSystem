<template>
    <div class="login-page">
        <span class="back-link" @click="goBack"><i class="el-icon-back"></i> 返回</span>
        <div class="login-container">
            <div class="form-container">
                <div class="form-body">
                    <div class="header">
                        <h2>找回密码</h2>
                        <p>
                            没有账户? <router-link to="/register">创建账户</router-link>
                        </p>
                    </div>
                    <div class="form-group">
                        <div class="input-group">
                            <el-input type="text" placeholder="请输入邮箱" v-model="email" suffix-icon="el-icon-s-promotion">
                            </el-input>
                        </div>
                        <div style="display: flex">
                            <el-input type="text" placeholder="验证码" suffix-icon="el-icon-s-check"
                                v-model="verificationCode" style="width: 204px"></el-input>
                            <el-button type="button" :disabled="isCounting || isSending" @click="sendVerificationCode"
                                class="ml-5">
                                {{ isSending ? '发送中...' : (isCounting ? `${countdown}s后重新发送` : '发送验证码') }}
                            </el-button>
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
                email: '',
                verificationCode: "",
                countdown: 60,
                isCounting: false, // 控制按钮状态
            };
        },
        methods: {
            goBack() {
                this.$router.push('/');
            },
            async sendVerificationCode() {
                if (this.email && this.isValidEmail(this.email)) {
                    console.log("发送验证码中...........");
                    this.isSending = true; // 设置发送中状态
                    try {
                        const data = {
                            email: this.email,
                        }
                        const res = await this.$api.userObj.sendCode(data);
                        if (res.code === 2000) {
                            this.$message.success("发送成功");
                            this.startCountdown();
                        }
                        else {
                            this.$message.error(res.message);
                        }
                    } catch (error) {
                        console.error("发送验证码请求错误:", error);
                    } finally {
                        this.isSending = false;
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
                    const loading = this.$loading({
                        lock: true,
                        text: '正在验证中，请稍等.......',
                        spinner: 'el-icon-loading',
                        background: 'rgba(0, 0, 0, 0.7)'
                    });
                    const data = {
                        email: this.email,
                        code: this.verificationCode,
                    };
                    this.$api.authObj.verifyCode(data).then(res => {
                        if (res.code === 2000) {
                            this.$router.push({
                                path: "/setPassword",
                                query: { email: this.email },
                            });
                        } else {
                            this.$message.error(res.message);
                        }
                    }).catch(error => {
                        console.error('Verification Error:', error); // 添加调试语句
                        this.$message.error('验证出现错误，请查看控制台日志'); // 添加错误提示
                    }).finally(() => {
                        loading.close();
                    });
                } else {
                    this.$message.error("请输入验证码");
                }
            },
        },
    };
</script>