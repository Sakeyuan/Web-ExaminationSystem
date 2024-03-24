<template>
    <el-menu :default-openeds="[]" style="min-height: 100%;overflow-y: auto;overflow-x: hidden;"
        background-color="rgb(48, 65, 86)" text-color="#ffff" active-text-color="#ffd04b" :collapse-transition="false"
        :collapse="isCollapse" router>
        <div style="height:60px;line-height:60px;text-align: center;margin-right: 15px;">
            <i class="el-icon-monitor" style="color: white;margin-right: 8px; font-size: 17px; "></i>
            <b style="color: white;" v-show="logTextShow">考试管理系统</b>
        </div>
        <el-menu-item index="/teacher/home"><i class="el-icon-s-home"></i><span>首页</span></el-menu-item>
        <el-menu-item index="studentList"><i class="el-icon-user"></i><span>我的学生</span></el-menu-item>
        <el-menu-item index="class"><i class="el-icon-s-grid"></i><span>我的班级</span></el-menu-item>

        <el-submenu index="2" class="paper-submenu">
            <template slot="title"><i class="el-icon-document-copy"></i><span>我的试卷</span></template>
            <el-menu-item index="paper">试卷列表</el-menu-item>
            <el-menu-item index="releasePaper">发布试卷</el-menu-item>
        </el-submenu>

        <el-submenu index="3" class="problems-submenu">
            <template slot="title"><i class="el-icon-folder-opened"></i><span>我的题目</span></template>
            <el-menu-item index="title">题目列表</el-menu-item>
            <el-menu-item index="createSingleTitle">创建单选题</el-menu-item>
            <el-menu-item index="createMultipleTitle">创建多选题</el-menu-item>
            <el-menu-item index="createJudgeTitle">创建判断题</el-menu-item>
            <el-menu-item index="createFillTitle">创建填空题</el-menu-item>
            <el-menu-item index="createShortTitle">创建简单题</el-menu-item>
        </el-submenu>

        <el-submenu index="4" class="answer-paper-submenu">
            <template slot="title"><i class="el-icon-finished"></i><span>学生答卷</span></template>
            <el-menu-item index="correctPaperList">试卷批改</el-menu-item>
            <el-menu-item index="finishPaperList">完成试卷</el-menu-item>
        </el-submenu>

        <el-menu-item index="performanceAnalysis"><i class="el-icon-setting"></i><span>成绩分析</span></el-menu-item>

        <el-menu-item index="video"><i class="el-icon-s-platform"></i><span>学生视频</span></el-menu-item>
    </el-menu>
</template>

<script>
    export default {
        name: "Aside",
        props: {
            isCollapse: Boolean,
            logTextShow: Boolean,
        }
    }
</script>

<style scoped>

</style>