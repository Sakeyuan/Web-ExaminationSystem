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
                        <h2>学生注册</h2>
                    </div>
                    <div class="form-group">
                        <div class="block" @click="showCamera">
                            <el-avatar shape="square" :size="100" :src="squareUrl" style="margin-left: 100px">
                            </el-avatar>
                        </div>
                        <div class="input-group">
                            <el-input type="text" placeholder="请输入学号" suffix-icon="el-icon-user"
                                v-model="studentNumber">
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
                            <router-link to="/login" class="forgot">
                                <i class="el-icon-user-solid mr-2"></i>登录
                            </router-link>
                            <router-link to="/forgot-password" class="forgot">
                                <i class="el-icon-question mr-2"></i>忘记密码?
                            </router-link>
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
        <el-dialog title="人脸获取" :visible.sync="dialogVisible" width="50%" :before-close="handleClose">
            <Camera ref="camera" @sendFile="uploadFile"></Camera>
        </el-dialog>
    </div>
</template>

<script>
    import "@/styles/login.css"
    import Camera from "@/components/camera.vue"
    export default {
        components: {
            Camera
        },
        data() {
            return {
                reuserPassword: '',
                userPhone: '',
                userPassword: '',
                file: '',
                dialogVisible: false,
                squareUrl: "https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png",
                studentNumber: '',
            };
        },
        mounted() {
            this.userPhone = this.$route.query.userPhone;
        },
        methods: {
            uploadFile(file) {
                if (this.userPassword !== this.reuserPassword) {
                    this.$message("密码不一致");
                    return;
                }
                this.file = file;
                if (this.file && this.studentNumber && this.userPassword && this.userPhone) {
                    const registerInfo = JSON.stringify(this.$store.state.registerInfo);
                    const fileName = `${this.studentNumber}.png`;
                    const formData = new FormData();
                    formData.append('file', this.file);
                    const studentData = {
                        userPhone: this.userPhone,
                        userPassword: this.userPassword,
                        number: this.studentNumber,
                        role: 1,
                    }
                    formData.append('data', new Blob([JSON.stringify(studentData)], { type: 'application/json' }));
                    this.$api.uploadObj.uploadFace(formData).then(res => {
                        if (res.code == 2000) {
                            this.$message.success("注册成功");
                            this.$store.commit('setUserRegisterInfo', { userPhone: 0, userPassword: 0 });
                            this.handleClose();
                            setTimeout(() => {
                                this.$router.push("/");
                            }, 1000);
                        }
                        else {
                            this.$message.error(res.message);
                        }
                    }).catch(error => {
                        this.$message.error("注册失败");
                    });
                }
                else {
                    this.$message.error('请先完善信息再拍照上传！');
                }
            },
            handleClose(done) {
                this.$refs.camera.clearVideoCamera();
                this.dialogVisible = false; // 关闭对话框
                // 清除相关数据
                if (done) {
                    done();
                }
            },

            showCamera() {
                this.dialogVisible = true
            },
        }
    };
</script>