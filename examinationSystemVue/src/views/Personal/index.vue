<template>
  <div class="personal-root-container">
    <div v-if="userData" class="personal-main-page personal-account-center">
      <div class="personal-layout">
        <div class="personal-container personal-account-center-header">
          <div class="personal-avatar personal-account-center-header__avatar personal-avatar__xxl">
            <img :src="avatarUrl" class="personal-avatar__img">
          </div>
          <div class="personal-account-center-user">
            <div class="personal-account-center-user__header">
              <div class="personal-account-center-user__title">
                <span class="personal-account-center-user__name">{{userData.user.userRealName}}</span>
                <!---->
              </div>
              <div class="personal-account-center-header__buttons">
                <div
                  class="personal-button personal-account-center-header__edit personal-button-outlined personal-button-md">
                  <button class="personal-button__button" @click="updateMyInfo">编辑</button>
                  <router-link to="/">
                    <button class="personal-button__button mt-10">首页</button>
                  </router-link>
                </div>
                <!---->
              </div>
            </div>
            <div class="personal-account-center-user__audit">
              <span class="personal-account-center-user__uid">用户名: {{userData.user.userName}}</span>
              <!---->
            </div>
            <div class="personal-account-center-user__intro">
              <p><i class="el-icon-tickets"
                  style="color: #ad4e69;margin-right: 10px;font-size: 18px;"></i>系统原装签名，送给每一位小可爱~</p>
            </div>
            <div class="personal-account-center-user__intro">
              <p><i class="el-icon-location-information"
                  style="color: #00c3ff;margin-right: 10px;font-size: 18px;"></i>IP属地：{{IP}}</p>
            </div>
          </div>
        </div>

        <!--左侧菜单栏-->
        <div class="personal-container personal-side-menu personal-account-center__menu">
          <header class="personal-side-menu__header">个人中心</header>
          <ul class="personal-side-menu__list">
            <li v-for="(item,index) in menus" :key="index" @click="selMenu(item)">
              <a
                :class="'personal-router-link personal-side-menu__item '+ (activeIndex === item.path?'nuxt-link-active':'')">
                <i :class="item.icon" style="font-size: 18px;margin-right: 10px;"></i>
                <span>{{item.name}}</span>
              </a>
            </li>
          </ul>
        </div>
        <!-- 右侧内容-->
        <div class="personal-container personal-account-center-content">
          <router-view></router-view>
        </div>
      </div>
    </div>
    <div v-else>
      <p>Loading...</p>
    </div>
  </div>

</template>

<script>
  export default {
    data() {
      return {
        avatarData: null,
        userData: null,
        activeIndex: '/index',
        menus: null,
        IP: '地球',
      };
    },
    computed: {
      avatarUrl() {
        return !this.userData || !this.userData.avatar
          ? require("@/assets/default.png")
          : `data:image/jpeg;base64,${this.avatarData}`;
      },
    },
    mounted() {
    },
    created() {
      if (this.$store.getters.getUser.role === '学生') {
        this.menus = [
          { name: '个人简介', path: '/personal/userInfo', icon: 'el-icon-info' },
          { name: '我的班级', path: '/personal/myClass', icon: 'el-icon-s-grid' },
        ];
      } else if (this.$store.getters.getUser.role === '教师') {
        this.menus = [
          { name: '个人简介', path: '/personal/userInfo', icon: 'el-icon-info' },
        ];
      }
      this.load();
      this.avatarData = localStorage.getItem('avatarData') || null;
      this.getIp();
    },
    methods: {
      getIp() {
        this.$api.ipObj.getIp().then(res => {
          if (res.code == 2000) {
            this.IP = res.data;
          }
        });
      },
      load() {
        this.$api.userObj.getPersonalInfo().then(res => {
          if (res.code === 2000) {
            this.userData = res.data;
            localStorage.setItem("userData", JSON.stringify(this.userData));
          } else {
            this.$message.error(res.message);
          }
        }).catch(err => {
          this.$message.error(err.message);
        });
      },
      selMenu(item) {
        this.activeIndex = item.path;
        this.$router.push({ path: item.path });
      },
      updateMyInfo() {
        this.$router.push('/personal/userInfo');
      }
    }
  };
</script>

<style scoped>
  .personal-root-container {
    height: 100vh;
    background-color: #f4f4f5;
  }

  .personal-main-page {
    padding-top: 30px;
    position: relative;
  }

  .personal-layout {
    width: 1000px;
    margin: 0 auto;
    padding-left: 100px;
    padding-right: 100px;
    -webkit-box-sizing: content-box;
    box-sizing: content-box;
  }

  .personal-layout {
    zoom: 1;
  }

  .personal-account-center-header {
    padding: 20px 50px 24px;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    margin-bottom: 20px;
  }

  .personal-container {
    background-color: #fff;
    border-radius: 4px;
  }

  .personal-account-center-header__avatar {
    margin-right: 24px;
    -ms-flex-negative: 0;
    flex-shrink: 0;
  }

  .personal-avatar__xxl {
    width: 120px;
    height: 120px;
  }

  .personal-avatar {
    display: inline-block;
    position: relative;
  }

  .personal-avatar__img {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    border: 1px solid #ebebeb;
    vertical-align: top;
  }

  .personal-avatar__img {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    border: 1px solid #ebebeb;
    vertical-align: top;
  }

  img {
    border-style: none;
  }

  .personal-account-center-user {
    -webkit-box-flex: 1;
    -ms-flex-positive: 1;
    flex-grow: 1;
    height: 100%;
  }

  .personal-account-center-user__header {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
  }

  .personal-account-center-user__title {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
  }

  .personal-account-center-user__name {
    font-size: 16px;
    line-height: 21px;
    font-weight: 600;
  }

  .personal-account-center-user__level.personal-img-icon {
    margin-left: 10px;
    height: 16px;
    -ms-flex-negative: 0;
    flex-shrink: 0;
  }

  .personal-account-center-user__level--self {
    cursor: pointer;
  }

  .personal-img-icon {
    height: 1em;
    fill: currentColor;
    overflow: hidden;
  }

  .personal-account-center-header__buttons {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
  }

  .personal-button.personal-button-md.personal-button-outlined {
    line-height: 32px;
  }

  .personal-button.personal-button-md {
    width: 86px;
    height: 34px;
    line-height: 34px;
  }

  .personal-button {
    display: inline-block;
    cursor: pointer;
    -ms-flex-negative: 0;
    flex-shrink: 0;
  }

  .personal-button-outlined .personal-button__button {
    background-color: #fff;
    color: #00b2ff;
    border: 1px solid #00c3ff;
    border-radius: 4px;
    -webkit-transition-duration: .2s;
    -o-transition-duration: .2s;
    transition-duration: .2s;
    -webkit-transition-property: border-color, color;
    -o-transition-property: border-color, color;
    transition-property: border-color, color;
  }

  .personal-button__button {
    display: -ms-inline-flexbox;
    display: inline-flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    -webkit-box-pack: center;
    -ms-flex-pack: center;
    justify-content: center;
    cursor: pointer;
    outline: none;
    font-size: inherit;
    color: inherit;
    width: 100%;
    height: 100%;
    background-color: transparent;
    border: none;
    border-radius: 0;
    font-weight: inherit;
    line-height: inherit;
  }

  input,
  button,
  textarea {
    color: inherit;
    font: inherit;
  }

  .personal-account-center-user__audit {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
  }

  .personal-account-center-user__uid {
    font-size: 12px;
    color: #ccc;
  }

  a {
    text-decoration: none;
  }

  .personal-account-center-user__certification,
  .personal-account-center-user__intro {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    margin-top: 12px;
    color: #666;
    line-height: 18px;
  }

  .personal-account-center-user__certification span,
  .personal-account-center-user__intro span {
    margin-top: 2px;
    -ms-flex-negative: 0;
    flex-shrink: 0;
    line-height: 1;
  }

  p {
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    margin: 0;
    padding: 0;
    outline: none;
  }

  .personal-account-center-user__certification,
  .personal-account-center-user__intro {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    margin-top: 12px;
    color: #666;
    line-height: 18px;
  }

  .personal-account-center-user__certification .ip-icon,
  .personal-account-center-user__intro .ip-icon {
    color: #76bfe3;
    font-size: 16px;
    margin-right: 8px;
  }

  .personal-icon {
    font-size: inherit;
  }

  .iconfont {
    font-family: "iconfont" !important;
    font-size: 16px;
    font-style: normal;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }

  .icon-ip:before {
    content: "";
  }

  .personal-account-center-header__data {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    padding-top: 19px;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    -ms-flex-item-align: center;
    align-self: center;
    -ms-flex-negative: 0;
    flex-shrink: 0;
    margin-top: 20px;
    border-top: 1px solid #f0f0f0;
  }

  .personal-account-center-header__data-item {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    padding-right: 40px;
  }

  .personal-account-center-header__data-link {
    cursor: pointer;
  }

  .personal-account-center-header__data-num {
    color: #333;
    font-size: 20px;
  }

  .personal-account-center-header__data-label {
    margin-left: 10px;
    color: #ccc;
  }

  .personal-account-center-header__data-item {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    padding-right: 40px;
  }

  .personal-account-center-header__data-link {
    cursor: pointer;
  }

  .personal-account-center-header__data-num {
    color: #333;
    font-size: 20px;
  }

  .personal-account-center-header__data-label {
    margin-left: 10px;
    color: #ccc;
  }

  .personal-account-center-header__data-item {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    padding-right: 40px;
  }

  .personal-account-center-header__data-num {
    color: #333;
    font-size: 20px;
  }

  .personal-account-center-header__data-label {
    margin-left: 10px;
    color: #ccc;
  }

  /*左侧菜单栏*/
  .personal-side-menu {
    width: 280px;
    float: left;
    height: 360px;
  }

  .personal-container {
    background-color: #fff;
    border-radius: 4px;
  }

  .personal-side-menu__header {
    width: 100%;
    padding: 0 30px;
    line-height: 50px;
    border-bottom: 1px solid #ebebeb;
    font-size: 16px;
  }

  .personal-side-menu__list {
    padding: 0 30px 10px;
  }

  ol,
  ul {
    margin: 0;
    padding: 0;
    list-style: none;
  }

  ul,
  li {
    list-style: none;
  }

  .personal-account-center__menu li:nth-of-type(3),
  .personal-account-center__menu li:nth-of-type(5) {
    border-bottom: 1px solid #f0f0f0;
  }

  .personal-side-menu .nuxt-link-active {
    color: #00c3ff;
  }

  .personal-side-menu__item {
    padding: 0 20px;
    line-height: 50px;
    font-size: 14px;
    color: #666;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    cursor: pointer;
  }

  .personal-side-menu .nuxt-link-active .personal-icon {
    color: #00c3ff;
  }

  .personal-side-menu__item .personal-icon {
    width: 18px;
    font-size: 18px;
    vertical-align: top;
    display: inline-block;
    color: #d9d9d9;
    margin-right: 15px;
  }

  .personal-icon {
    font-size: inherit;
  }

  .iconfont {
    font-family: "iconfont" !important;
    font-size: 16px;
    font-style: normal;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }

  .icon-wodefatie:before {
    content: "";
  }

  /*右侧内容*/
  .personal-account-center-content {
    width: 700px;
    float: right;
    height: 360px;
  }
</style>