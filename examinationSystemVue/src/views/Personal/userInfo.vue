<template>
  <div class="mhy-container mhy-account-center-content">
    <div class="mhy-account-center-content-container mhy-account-center-collection">
      <div class="mhy-account-center__subheader">
        <span>个人简介</span>
        <div class="mhy-account-center-collection-menu">
          <div class="mhy-button mhy-account-center-collection-menu__create mhy-button-outlined">
            <button class="mhy-button__button" @click="updateModal">编辑</button>
          </div>
        </div>
      </div>
    </div>
    <div>
      <el-descriptions class="margin-top" :column="2" border>
        <el-descriptions-item>
          <template slot="label">
            <font-awesome-icon icon="user-tie" />
            头像
          </template>
          <div>
            <el-image :src="avatarUrl" style="width: 50px;height: 50px;"></el-image>
          </div>
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <font-awesome-icon icon="font" />
            真实姓名
          </template>
          {{userData.user.userRealName}}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <font-awesome-icon icon="signature" />
            昵称
          </template>
          {{userData.user.userName}}
        </el-descriptions-item>
        <el-descriptions-item v-if="this.$store.getters.getUser.role === '学生'">
          <template slot="label">
            <font-awesome-icon icon="graduation-cap" />
            学号
          </template>
          {{userData.number}}
        </el-descriptions-item>
        <el-descriptions-item v-if="this.$store.getters.getUser.role === '教师'">
          <template slot="label">
            <font-awesome-icon icon="graduation-cap" />
            工号
          </template>
          {{userData.number}}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <font-awesome-icon icon="timeline" />
            年龄
          </template>
          <span v-if="userData.user.userAge" size="small">{{userData.user.userAge}}</span>
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <font-awesome-icon icon="genderless" />
            性别
          </template>
          <span v-if="userData.user.userGender" size="small">{{userData.user.userGender}}</span>
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <font-awesome-icon icon="envelope" />
            邮箱
          </template>
          {{userData.user.userEmail}}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <font-awesome-icon icon="phone" />
            联系电话
          </template>
          {{userData.user.userPhone}}
        </el-descriptions-item>
        <el-descriptions-item v-if="faceData">
          <template slot="label">
            <font-awesome-icon icon="face-smile" />
            人脸照片
          </template>
          <div>
            <el-image style="width: 50px;height: 50px;" :src="faceData"></el-image>
          </div>
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <font-awesome-icon icon="sign-hanging" />
            签名
          </template>
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <!--编辑窗口-->
    <el-dialog title="修改信息" :visible.sync="box" v-loading="loading" width="80%" :close-on-click-modal="true" center>
      <div>
        <el-form status-icon :rules="rules" ref="form" :model="form" label-width="120px">
          <el-row>
            <el-col :span="8">
              <el-form-item label="头像：">
                <el-upload class="avatar-uploader" action="" ref="upload" :show-file-list="false" :auto-upload="false"
                  :before-upload="beforeUpload" :on-change="handleChange" :on-remove="handleRemove">
                  <div>
                    <el-avatar :size="60" :src="avatarUrlForm" :on-preview="previewAvatar">
                    </el-avatar>
                  </div>
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="真实姓名" prop="userRealName">
                <el-input v-model="form.userRealName" placeholder="请输入昵称" clearable>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="昵称" prop="userName">
                <el-input v-model="form.userName" placeholder="请输入昵称" clearable>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="8">
              <el-form-item label="学号" prop="number">
                <el-input placeholder="" v-model="userData.number" :disabled="true">
                </el-input>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="年龄" prop="userAge">
                <el-input v-model="form.userAge" placeholder="请输入年龄" clearable>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="性别" prop="userGender">
                <el-radio-group v-model="form.userGender">
                  <el-radio label="男">男</el-radio>
                  <el-radio label="女">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="8">
              <el-form-item label="邮箱" prop="userEmail">
                <el-input v-model="form.userEmail" placeholder="请输入邮箱" clearable>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="手机号" prop="userPhone">
                <el-input resize="none" v-model="form.userPhone" placeholder="请输入手机号" clearable>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <div style="text-align: center;" class="mt-30">
            <el-button type="primary" style="width: 100px;" @click="submitFun">提交</el-button>
            <el-button type="primary" plain style="width: 100px;" @click="box=false">取消</el-button>
          </div>
        </el-form>
      </div>
    </el-dialog>

  </div>
</template>

<script>
  import "@/styles/global.css"
  export default {
    data() {
      return {
        avatarData: null,
        faceData: '',
        userData: null,
        loading: false,
        box: false,
        avatarUrlForm: null,
        form: {
          userRealName: '',
          userName: '',
          userGender: '',
          file: null,
          userAge: null,
          userEmail: '',
          userPhone: '',
        },
        rules: {
          userRealName: [
            { required: true, message: '请输入真实姓名', trigger: 'blur' },
            { pattern: /^[\u4E00-\u9FA5]{2,4}$/, message: '请输入2-4个中文字符', trigger: ['blur', 'change'] }
          ],
          userEmail: [
            { required: true, message: '请输入邮箱', trigger: 'blur' },
            { type: 'email', message: '请输入有效的邮箱地址', trigger: ['blur', 'change'] }
          ],
          nickname: [
            { required: true, message: '请输入昵称', trigger: 'change' }
          ],
          gender: [
            { required: true, message: '请选择性别', trigger: 'change' }
          ]
        },
      };
    },
    computed: {
      avatarUrl() {
        return !this.userData || !this.userData.avatar
          ? require("@/assets/default.png")
          : `data:image/jpeg;base64,${this.avatarData}`;
      },
    },
    created() {
      this.userData = JSON.parse(localStorage.getItem("userData"));
      this.avatarData = localStorage.getItem('avatarData') || null;
    },
    mounted() {
      if (this.$store.getters.getUser.role === '学生') {
        this.getFace();
      }
    },
    methods: {
      previewAvatar(file) {
        this.avatarUrlForm = file.url;
      },
      getFace() {
        const fileName = this.userData.face.fileName + '.' + this.userData.face.fileType;
        this.$api.uploadObj.getFace(fileName)
          .then(res => {
            if (res.code == 2000) {
              this.faceData = `data:image/jpeg;base64,${res.data}`;
            } else {
              console.error(res.message);
            }
          })
          .catch(err => {
            console.error('Error loading image:', err);
          });
      },
      beforeUpload(file) {
        const isJPG = file.type === 'image/jpeg';
        const isLt2M = file.size / 1024 / 1024 < 2;
        if (!isJPG) {
          this.$message.error('上传头像图片只能是 JPG 格式!');
        }
        if (!isLt2M) {
          this.$message.error('上传头像图片大小不能超过 2MB!');
        }
        return isJPG && isLt2M;
      },
      handleRemove(file, fileList) {
        this.form.file = null;
      },
      handleChange(file, fileList) {
        this.form.file = file.raw;
        let URL = window.URL || window.webkitURL;
        this.avatarUrlForm = URL.createObjectURL(file.raw);
      },
      updateModal() {
        if (this.$refs.form) {
          this.$refs.form.resetFields();
        }
        this.form.gender = this.form.gender + '';
        this.setFormDefaultValue();
        this.box = true;
      },
      setFormDefaultValue() {
        this.avatarUrlForm = this.avatarUrl;
        this.form.userRealName = this.userData.user.userRealName;
        this.form.userName = this.userData.user.userName;
        this.form.userGender = this.userData.user.userGender;
        this.form.userAge = this.userData.user.userAge;
        this.form.userPhone = this.userData.user.userPhone;
        this.form.userEmail = this.userData.user.userEmail;
      },
      isValueChanged(field) {
        return this.form[field] !== this.userData.user[field];
      },

      submitFun() {
        // 判断是否有改动
        if (!this.isValueChanged('userRealName') &&
          !this.isValueChanged('userName') &&
          !this.isValueChanged('userGender') &&
          !this.isValueChanged('userAge') &&
          !this.isValueChanged('userPhone') &&
          !this.isValueChanged('userEmail') && !this.form.file) {
          this.$message.warning('没有修改内容')
          return;
        }
        const formData = new FormData();
        formData.append('file', this.form.file);
        const user = {
          userId: JSON.parse(localStorage.getItem('user')).userId,
          userRealName: this.form.userRealName,
          userName: this.form.userName,
          userGender: this.form.userGender,
          userAge: this.form.userAge,
          userPhone: this.form.userPhone,
          userEmail: this.form.userEmail,
        }
        formData.append('user', new Blob([JSON.stringify(user)], { type: 'application/json' }));
        this.$api.uploadObj.uploadPersonalInfo(formData).then(res => {
          if (res.code === 2000) {
            if (res.other && res.other.data != null) {
              this.$store.commit('setAvatarData', res.other.data);
            }
            if (res.data != null) {
              this.$store.commit('setUserData', res.data);
            }
            this.$message.success("修改成功");
            this.box = false;
            window.location.reload();
          }
          else {
            console.error('提交失败');
          }
        }).catch(err => {
          console.error('提交失败', err);
        });
      }
    }
  }
</script>

<style scoped>
  .mhy-account-center-content {
    width: 700px;
    float: right;
  }

  .mhy-container {
    background-color: #fff;
    border-radius: 4px;
  }

  p {
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    margin: 0;
    padding: 0;
    outline: none;
  }

  .mhy-account-center__subheader {
    padding: 0 30px;
    line-height: 50px;
    border-bottom: 1px solid #ebebeb;
    font-size: 16px;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
  }

  .mhy-account-center-collection-menu {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    font-size: 14px;
  }

  .mhy-account-center-collection-menu .mhy-button {
    height: 28px;
    line-height: 28px;
    font-weight: 600;
  }

  .mhy-account-center-collection-menu__create {
    width: 88px;
  }

  .mhy-button {
    display: inline-block;
    cursor: pointer;
    -ms-flex-negative: 0;
    flex-shrink: 0;
  }

  .mhy-button-outlined .mhy-button__button {
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

  .mhy-button__button {
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
</style>