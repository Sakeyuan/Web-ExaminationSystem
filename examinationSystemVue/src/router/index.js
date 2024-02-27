import Vue from 'vue'
import VueRouter from 'vue-router'
import api from '@/api';

Vue.use(VueRouter)

const routes = [
  {
    path: '/test',
    name: 'test',
    component: () => import('../views/test.vue'),
  },
  {
    path: '/personal',
    component: () => import('@/views/Personal/index.vue'),
    redirect:'/personal/userInfo',
    children: [
      {
        path: 'userInfo',
        name: '个人简介',
        component: () => import('@/views/Personal/userInfo.vue')
      },
      {
        path: 'myClass',
        name: '我的合集',
        component: () => import('@/views/Personal/myClass.vue')
      },
    ],
  },
  {
    path: '/',
    name: 'Login',
    component: () => import('../views/Login/login.vue'),
  },
  {
    path: '/register',
    name: '注册',
    component: () => import('../views/Register/register.vue'),
  },
  {
    path: '/register/student-register',
    name: '学生注册',
    component: () => import('../views/Register/studentRegister.vue'),
  },
  {
    path: '/register/teacher-register',
    name: '教师注册',
    component: () => import('../views/Register/teacherRegister.vue'),
  },
  {
    path: '/forgot-password',
    name: '忘记密码',
    component: () => import('../views/ForgetPassword/forgetPassword.vue'),
  },
  {
    path: '/student',
    name: '学生首页',
    component: () => import('../views/StudentDashboard/index.vue'),
    redirect:'/student/home',
    children: [
      {
        path: 'home',
        name: '首页',
        component: () => import('../views/StudentDashboard/home.vue'),
      },
      {
        path: 'myPaper',
        name: '试卷中心',
        component: () => import('../views/StudentDashboard/myPaper.vue'),
      },
      {
        path: 'examinationRecords',
        name: '考试记录',
        component: () => import('../views/StudentDashboard/examinationRecords.vue'),
      },
    ],
  },
  {
    path: '/paperDetailed',
    name: '试卷详情',
    component: () => import('../views/StudentDashboard/paperDetailed.vue'),
  },
  {
    path: '/exam',
    name: '考试界面',
    component: () => import('../views/StudentDashboard/exam.vue'),
  },
  {
    path: '/teacher',
    name: '教师页面',
    meta: {
      title: '教师页面',        
    },
    component: () => import('../views/TeacherDashboard/index.vue'),
    redirect: "/home",
    children: [
      {
        path: 'home',
        name: '教师首页',
        component: () => import('../views/TeacherDashboard/home.vue'),
        meta: {
          title: '教师首页',        
        },
      },
      {
        path: 'studentList',
        name: '学生列表',
        component: () => import('../views/TeacherDashboard/studentList.vue'),
        meta: {
          title: '学生列表', 
        },
      },
      {
        path: 'class',
        name: '班级列表',
        component: () => import('../views/TeacherDashboard/ClassList.vue'),
        meta: {
          title: '班级列表', 
        },
      },
      {
        path: 'paper',
        name: '试卷列表',
        component: () => import('../views/TeacherDashboard/PaperList.vue'),
        meta: {
          title: '试卷列表', 
        },
      },
      {
        path: 'title',
        name: '题目列表',
        component: () => import('../views/TeacherDashboard/titleLists.vue'),
        meta: {
          title: '题目列表', 
        },
      },
      {
        path: 'releasePaper',
        name: '发布试卷',
        component: () => import('../views/TeacherDashboard/releasePaper.vue'),
        meta: {
          title: '发布试卷', 
        },
      },
      {
        path: 'paperDetail',
        name: '空试卷详情',
        component: () => import('../views/TeacherDashboard/paperDetail.vue'),
        meta: {
          title: '空试卷详情', 
        },
      },
      {
        path: 'createSingleTitle',
        name: '创建单选题',
        component: () => import('../views/TeacherDashboard/createSingleTitle.vue'),
        meta: {
          title: '创建单选题', 
        },
      },
      {
        path: 'createMultipleTitle',
        name: '创建多选题',
        component: () => import('../views/TeacherDashboard/createMultipleTitle.vue'),
        meta: {
          title: '创建多选题', 
        },
      },
      {
        path: 'createJudgeTitle',
        name: '创建判断题',
        component: () => import('../views/TeacherDashboard/createJudgeTitle.vue'),
        meta: {
          title: '创建判断题', 
        },
      }, 
      {
        path: 'createFillTitle',
        name: '创建填空题',
        component: () => import('../views/TeacherDashboard/createFillTitle.vue'),
        meta: {
          title: '创建填空题', 
        },
      },
      {
        path: 'createShortTitle',
        name: '创建简答题',
        component: () => import('../views/TeacherDashboard/createShortTitle.vue'),
        meta: {
          title: '创建简答题', 
        },
      },
      {
        path: 'correctPaperList',
        name: '批改列表',
        component: () => import('../views/TeacherDashboard/correctPaperList.vue'),
        meta: {
          title: '批改列表', 
        },
      },
      {
        path: 'correctPaper',
        name: '批改试卷',
        component: () => import('../views/TeacherDashboard/correctPaper.vue'),
        meta: {
          title: '批改试卷', 
        },
      },
      {
        path: 'hadCorrectPaper',
        name: '批改详情',
        component: () => import('../views/TeacherDashboard/hadCorrectPaper.vue'),
        meta: {
          title: '批改详情', 
        },
      },
      {
        path: 'finishPaperList',
        name: '已经完成的试卷',
        component: () => import('../views/TeacherDashboard/finishPaperList.vue'),
        meta: {
          title: '已经完成的试卷', 
        },
      },
      {
        path: 'performanceAnalysis',
        name: '成绩分析',
        component: () => import('../views/TeacherDashboard/performanceAnalysis.vue'),
        meta: {
          title: '成绩分析', 
        },
      },
    ],
  },

]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => { 
  const token = localStorage.getItem('token') || null;
  if (to.path !== '/' && !token && to.path !== '/register' && to.path !== '/register/student-register' && to.path !== '/register/teacher-register' && to.path !== '/forgetPassword' && to.path !== '/test') { 
    next('/'); 
  }
  else if (to.path === '/' && token) {
      api.authObj.checkToken();
      const userRole = JSON.parse(localStorage.getItem("user")).role;
      if (userRole === "教师") {
        next("/teacher/home");
      } else if (userRole === "学生") {
        next("/student");
      } else {
        next('/');
      }
  }
  else { 
    next(); 
  } 
});


export default router
