<template>
    <div class="login-page">
        <div class="login-container">
            <div class="form-container">
                <div class="form-body">
                    <div class="header">
                        <h2>找回密码</h2>
                        <p>没有账户? <router-link to="/register">创建账户</router-link>
                        </p>
                    </div>
                    <div class="form-group">
                        <div class="input-group">
                            <el-input type="text" placeholder="请输入手机号或者邮箱" suffix-icon="
                            el-icon-mobile-phone" v-model="username">
                            </el-input>
                        </div>
                        <div class="input-group">
                            <el-input type="text" placeholder="验证码" suffix-icon="el-icon-s-check" v-model="username">
                            </el-input>
                        </div>
                        <div class="input-group right">
                            <el-button @click="login">确认</el-button>
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
        data() {
            return {
                username: '',
                password: '',
            };
        },
        methods: {
            login() {
                console.log('Username:', this.username);
                console.log('Password:', this.password);
                console.log('Remember Me:', this.remember);
            }
        }
    };
</script>