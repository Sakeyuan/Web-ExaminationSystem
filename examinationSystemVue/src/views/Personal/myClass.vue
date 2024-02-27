<template>
    <div class="mhy-container mhy-account-center-content">
        <div class="mhy-account-center-content-container mhy-account-center-collection">
            <div class="mhy-account-center__subheader">
                <span>我的班级</span>
                <div class="mhy-account-center-collection-menu">
                    <div class="mhy-button mhy-account-center-collection-menu__create mhy-button-outlined">
                        <button v-if="!classInfo" class="mhy-button__button"
                            @click="addClassDialog = true">加入班级</button>
                        <el-popconfirm v-else title="确定退出班级吗？" icon="el-icon-info" icon-color="red"
                            @confirm="bowOutClass"><button class="mhy-button__button" slot="reference">退出班级</button>
                        </el-popconfirm>
                    </div>
                </div>
            </div>
            <div v-if="classInfo" class="mhy-account-center-content-container__list">
                <div class="mhy-collection-card mhy-account-center-collection-card">
                    <a class="mhy-router-link mhy-collection-card__link">
                        <div class="mhy-collection-card__cover"
                            :style="{ backgroundImage: 'url(' + require('@/assets/class.png') + ')' }"></div>
                    </a>
                    <div class="mhy-collection-card__info">
                        <a class="mhy-router-link mhy-collection-card__link" target="_blank">
                            <p class="mhy-collection-card__name">{{classInfo.className}}</p>
                            <p class="mhy-collection-card__desc">{{classInfo.userRealName}}</p>
                        </a>
                        <div class="mhy-collection-card__stats">
                            <span>{{classInfo.userPhone}}</span>
                        </div>
                    </div>
                </div>
                <div>
                    <ul class="infinite-list" v-infinite-scroll="loadStudentData" style="overflow-y:auto;max-height: 220px;
                ">
                        <li v-for="item in displayedList" :key="item.id" class="infinite-list-item">
                            <div style="margin:20px 0px 0px 40px; display: flex">
                                <img src="@/assets/user.png" style="width: 7%;">
                                <div style="margin-left: 10px">
                                    <span>
                                        {{item.userName }}
                                    </span>
                                    <span class="mhy-collection-card__stats mt-5">
                                        {{item.userPhone }}
                                    </span>
                                </div>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
            <div v-else>
                <el-empty description="请先加入班级"></el-empty>
            </div>
            <el-dialog title="加入班级" :visible.sync="addClassDialog" center>
                <el-form :model="form" :rules="rules" ref="form">
                    <el-form-item label="班级名称/班级代码" prop="className">
                        <el-input v-model="form.className" style="width: 70%" placeholder="请输入班级名称或者班级代码"></el-input>
                    </el-form-item>
                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="addClassDialog = false">取 消</el-button>
                    <el-button type="primary" @click=" submitForm('form')">确 定</el-button>
                </div>
            </el-dialog>
        </div>
    </div>
</template>

<script>
    export default {
        data() {
            return {
                studentData: null,
                classInfo: null,
                addClassDialog: false,
                pageSize: 4,
                displayedList: [],
                form: {
                    className: ''
                },
                rules: {
                    className: [
                        { required: true, message: '请输入班级名称或者班级代码', trigger: 'blur' },
                    ],
                }
            };
        },
        mounted() {
        },
        created() {
            this.load();
        },
        methods: {
            bowOutClass() {
                this.$api.classObj.bowOutClass().then(res => {
                    if (res.code === 2000) {
                        this.load();
                        this.$message.success("退出成功");
                    }
                    else {
                        this.$message.error(res.message);
                    }
                })
            },
            loadStudentData() {
                this.displayedList = this.studentData.slice(0, this.displayedList.length + this.pageSize);
            },
            submitForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        const data = {
                            className: this.form.className
                        }
                        this.$api.classObj.addClass(data).then(res => {
                            if (res.code === 2000) {
                                console.log("studentData:" + this.studentData);
                                this.$message.success("加入成功")
                                this.addClassDialog = false;
                                this.load();
                            }
                            else {
                                this.$message.error(res.message);
                            }
                        })
                    } else {
                        console.log('error submit!!');
                        return false;
                    }
                });
            },
            load() {
                this.$api.classObj.getClassByToken().then(res => {
                    if (res.code === 2000) {
                        this.classInfo = res.data;
                        this.studentData = res.other;
                        console.log("this.studentData: " + JSON.stringify(this.studentData))
                    } else {
                        this.$message.error(res.message);
                    }
                }).catch(err => {
                    this.$message.error(err.message);
                });
            }
        }
    };
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

    input,
    button,
    textarea {
        color: inherit;
        font: inherit;
    }

    .mhy-account-center-collection-card {
        padding: 15px 40px 15px 30px;
    }

    .mhy-collection-card {
        display: -webkit-box;
        display: -ms-flexbox;
        display: flex;
        -webkit-box-align: center;
        -ms-flex-align: center;
        align-items: center;
    }

    .mhy-collection-card__link {
        color: #333;
    }

    a {
        text-decoration: none;
    }

    .mhy-collection-card__cover {
        position: relative;
        overflow: hidden;
        border-radius: 4px;
        /* border: 1px solid #ebebeb; */
        width: 60px;
        height: 60px;
        background-position: center;
        background-size: cover;
        background-repeat: no-repeat;
    }

    .mhy-collection-card__info {
        display: inline-block;
        margin-left: 20px;
        -webkit-box-flex: 1;
        -ms-flex-positive: 1;
        flex-grow: 1;
        overflow: hidden;
    }

    .mhy-collection-card__desc,
    .mhy-collection-card__name,
    .mhy-collection-card__info .mhy-collection-card__link {
        overflow: hidden;
        -o-text-overflow: ellipsis;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .mhy-collection-card__link {
        color: #333;
    }

    .mhy-collection-card__name {
        position: relative;
        line-height: 1;
        height: 20px;
    }

    .mhy-collection-card__desc,
    .mhy-collection-card__name,
    .mhy-collection-card__info .mhy-collection-card__link {
        overflow: hidden;
        -o-text-overflow: ellipsis;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .mhy-collection-card__desc {
        color: #999;
        font-size: 12px;
        line-height: 1;
        height: 22px;
    }

    .mhy-collection-card__desc,
    .mhy-collection-card__name,
    .mhy-collection-card__info .mhy-collection-card__link {
        overflow: hidden;
        -o-text-overflow: ellipsis;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .mhy-collection-card__stats {
        color: #ccc;
        font-size: 12px;
        display: -webkit-box;
        display: -ms-flexbox;
        display: flex;
        -webkit-box-align: center;
        -ms-flex-align: center;
        align-items: center;
    }

    .mhy-collection-card__stats span {
        display: -ms-inline-flexbox;
        display: inline-flex;
        -webkit-box-align: center;
        -ms-flex-align: center;
        align-items: center;
    }

    .mhy-collection-card__stats span {
        display: -ms-inline-flexbox;
        display: inline-flex;
        -webkit-box-align: center;
        -ms-flex-align: center;
        align-items: center;
    }

    .mhy-collection-card__stats span:not(:first-child)::before {
        content: "";
        display: inline-block;
        width: 2px;
        height: 2px;
        border-radius: 50%;
        background-color: #ccc;
        margin: 0 6px;
    }

    .mhy-container__footer {
        height: 70px;
        display: -webkit-box;
        display: -ms-flexbox;
        display: flex;
        -webkit-box-pack: center;
        -ms-flex-pack: center;
        justify-content: center;
        -webkit-box-align: center;
        -ms-flex-align: center;
        align-items: center;
        border-top: 1px solid #ebebeb;
    }

    .mhy-loadmore__btn .mhy-button__button,
    .mhy-loadmore__nomore {
        color: #ccc;
        font-size: 16px;
    }
</style>