<template>
    <div>
        <el-container>
            <el-header class="centered-header">
                <div class="header-left">
                    <font-awesome-icon icon="user-graduate" size="2x" />
                    <span class="ml-10">在线考试系统</span>
                </div>
                <div class="head-right">
                    <div>
                        <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal"
                            active-text-color="#409EFF" router>
                            <el-menu-item index="/student/home">首页</el-menu-item>
                            <el-menu-item index="/student/myPaper">我的试卷</el-menu-item>
                            <el-menu-item index="/student/examinationRecords">考试记录</el-menu-item>
                        </el-menu>
                    </div>
                    <div class="ml-20 mr-5">
                        <el-dropdown @command="handleCommand">
                            <el-avatar :src="avatarUrl"></el-avatar>
                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item command="personalInfo">
                                    <font-awesome-icon icon="user" class="mr-5" />个人信息
                                </el-dropdown-item>
                                <el-dropdown-item command="myCollection">
                                    <font-awesome-icon icon="star" class="mr-5" /></i>我的收藏
                                </el-dropdown-item>
                                <el-dropdown-item command="logout">
                                    <font-awesome-icon icon="right-from-bracket" class="mr-5" />退出系统
                                </el-dropdown-item>
                            </el-dropdown-menu>
                        </el-dropdown>
                    </div>
                </div>
            </el-header>
            <el-main>
                <transition name="fade" mode="out-in">
                    <router-view></router-view>
                </transition>
            </el-main>
        </el-container>
    </div>
</template>

<script>
    import '@/styles/global.css'
    import '@/styles/studentIndex.css'

    export default {
        name: 'index',
        computed: {
            avatarUrl() {
                return !this.avatarData
                    ? require("@/assets/default.png")
                    : `data:image/jpeg;base64,${this.avatarData}`;
            },
        },
        data() {
            return {
                avatarData: null,
                activeIndex: '1',
            }
        },
        mounted() {
            this.load();
        },
        methods: {
            load() {
                this.$api.uploadObj.getAvatarByToken()
                    .then(res => {
                        if (res.code === 2000) {
                            this.avatarData = res.data;
                            localStorage.setItem('avatarData', this.avatarData);
                        } else {
                            console.error(res.message);
                        }
                    })
            },
            handleCommand(command) {
                switch (command) {
                    case 'personalInfo':
                        this.handlePersonalInfo();
                        break;
                    case 'myCollection':
                        this.handleMyCollection();
                        break;
                    case 'logout':
                        this.handleLogout();
                        break;
                    default:
                        break;
                }
            },
            handlePersonalInfo() {
                this.$router.push({ path: '/personal' });
            },
            handleLogout() {
                this.$store.commit('clearUser');
            },
            handleMyCollection() {
                this.$router.push({ path: '/myCollection' });
            }

        }
    }
</script>

<style scoped>
    body {
        overflow: hidden;
    }

    .el-dropdown-menu {
        text-align: center;
        min-width: 100px;
    }

    .el-main {
        display: block;
        flex: 1;
        flex-basis: auto;
        overflow-y: auto;
        /* or overflow: auto; if you want scrollbars when necessary */
        overflow-x: hidden;
        /* or overflow: auto; if you want scrollbars when necessary */
        box-sizing: border-box;
        background-color: #fafafa;
        min-height: 100vh;
        padding: 0 !important;
    }
</style>