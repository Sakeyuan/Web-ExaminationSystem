<template>
  <el-container style="height: 100vh;">
    <el-aside :width="sizeWidth + 'px'"
      style="background-color: rgb(238, 241, 246); box-shadow: 2px 0 6px rgb(0 21 41 / 35%);">
      <Aside :logTextShow="logTextShow" :isCollapse="isCollapse"></Aside>
    </el-aside>

    <el-container>
      <el-header style="border-bottom: 1px solid #ccc">
        <Header :collapseBtnClass="collapseBtnClass" :isCollapse="isCollapse" @collapse-change="updateCollapse">
        </Header>
      </el-header>
      <el-main>
        <transition name="slide-fade" mode="out-in">
          <router-view></router-view>
        </transition>
      </el-main>
    </el-container>
  </el-container>
</template>

<style></style>
<script>
  import studentList from '@/views/TeacherDashboard/studentList.vue';
  import Aside from '@/components/Aside.vue';
  import Header from '@/components/Header.vue';
  import '@/styles/global.css'

  export default {
    name: 'index',
    components: {
      studentList,
      Aside,
      Header,
    },
    data() {
      return {
        collapseBtnClass: 'el-icon-s-fold',
        isCollapse: false,
        sizeWidth: 200,
        logTextShow: true
      }
    },
    methods: {
      updateCollapse(newCollapseState) {
        this.isCollapse = newCollapseState;
        if (this.isCollapse) {
          this.sizeWidth = 64
          this.collapseBtnClass = 'el-icon-s-unfold'
          this.logTextShow = false
        }
        else {
          this.sizeWidth = 200
          this.collapseBtnClass = 'el-icon-s-fold'
          this.logTextShow = true;
        }
      }
    }
  }
</script>

<style>
  .el-aside {
    height: 100vh;
  }

  .el-main {
    height: 100%;
  }

  .el-menu {
    height: 100%;
  }
</style>