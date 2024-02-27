<template>
    <div style="font-size: 12px; line-height: 60px; display: flex">
        <div style="flex: 1; font-size: 18px">
            <span :class="collapseBtnClass" style="cursor: pointer" @click="collapse"></span>
            <el-breadcrumb separator="/" style="display: inline-block; margin-left: 10px">
                <el-breadcrumb-item :to="'/teacher/home'">首页</el-breadcrumb-item>
                <el-breadcrumb-item>{{$route.meta.title}}</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="header-avatar">
            <el-dropdown>
                <el-avatar :src="avatarUrl"></el-avatar>
                <el-dropdown-menu slot="dropdown">
                    <router-link to="/personal" class="no-underline">
                        <el-dropdown-item><i class="el-icon-user-solid"></i>个人信息</el-dropdown-item>
                    </router-link>
                    <el-dropdown-item @click.native="handleLogout">
                        <font-awesome-icon :icon="['fas', 'right-from-bracket']" class="mr-5" />退出系统
                    </el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
    </div>
</template>

<script>
    export default {
        name: "Header",
        data() {
            return {
                avatarData: null,
            }
        },
        props: {
            collapseBtnClass: String,
            isCollapse: Boolean,
        },
        computed: {
            avatarUrl() {
                return !this.avatarData
                    ? require("@/assets/default.png")
                    : `data:image/jpeg;base64,${this.avatarData}`;
            },
        },
        mounted() {
            this.load();
        },
        methods: {
            handleLogout() {
                this.$store.commit('clearUser');
            },
            load() {
                this.$api.uploadObj.getAvatarByToken()
                    .then(res => {
                        if (res.code === 2000) {
                            this.avatarData = res.data;
                            localStorage.setItem('avatarData', this.avatarData);
                        } else {
                            console.error(res.message);
                        }
                    }).catch(err => {
                        console.error('Error loading image:', err);
                    });
            },
            collapse() {
                this.$emit('collapse-change', !this.isCollapse);
            }
        },
    }
</script>

<style scoped>
    .header-avatar {
        margin-top: 12px;
        margin-right: 2px;
    }

    .el-dropdown-menu {
        text-align: center;
        min-width: 100px;
    }

    .no-underline {
        text-decoration: none !important;
    }
</style>