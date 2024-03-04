<template>
    <div class="collect-body">
        <div v-if="collection && Object.keys(collection).length > 0" class="correctPaper-title">
            <div v-for="(exam, examId) in collection" :key="examId">
                <div class="exam-question">
                    <el-card>
                        <div class="question-title-container">
                            <div class="inline-div">
                                <div style="display: flex; justify-content: space-between;align-items: center;line-height: 1.3;"
                                    v-if="exam.titleType !== '填空题'">
                                    {{ exam.titleContent.name }}({{ exam.scores}}分)
                                </div>
                                <div v-else-if="exam.titleType === '填空题'">
                                    <div class="inline-div" v-for="(part, i) in getFillTitleParts(exam.titleContent)"
                                        :key="i">
                                        <span v-if="part.slice(0,5) == 'input'" class="inline-div">
                                            <el-input v-model="exam.answer[parseInt(part[part.length-1], 10)]"
                                                class="custom-input" readonly type="text"></el-input>
                                        </span>
                                        <span v-else class="question-title inline-div">{{ part }}</span>
                                    </div>
                                    ({{ exam.scores }}分)
                                </div>
                            </div>
                            <div class="collect-icon">
                                <font-awesome-icon class="star-icon" :class="{ 'filled': true }"
                                    :icon=" ['fas', 'star']" @click="toggleFavorite(examId)" />
                            </div>
                        </div>
                        <div v-if="exam.titleType === '单选题'">
                            <el-radio-group class="custom-radio-group" v-model="exam.answer" disabled>
                                <el-radio v-for="(option, optionIndex) in exam.titleContent.selectInput"
                                    :key="optionIndex" :label="option.select" class="custom-radio">
                                    {{ option.select }}. {{ option.content }}
                                </el-radio>
                            </el-radio-group>
                        </div>
                        <div v-else-if="exam.titleType === '多选题'" class="ml-10">
                            <!-- 多选题 -->
                            <el-checkbox-group class="custom-checkbox-group" disabled v-model="exam.answer">
                                <el-checkbox
                                    v-for="(option, optionIndex) in exam.titleContent && exam.titleContent.selectInput"
                                    :key="optionIndex" :label="option.select" class="custom-checkbox">
                                    {{ option.select }}. {{ option.content }}
                                </el-checkbox>
                            </el-checkbox-group>
                        </div>
                        <div v-else-if="exam.titleType === '判断题'">
                            <!-- 判断题 -->
                            <el-radio-group v-model="exam.answer" disabled>
                                <el-radio label="true">正确</el-radio>
                                <el-radio label="false">错误</el-radio>
                            </el-radio-group>
                        </div>
                        <div v-else-if="exam.titleType === '简答题'" text-color="#d61010">
                            <!-- 简答题目 -->
                            <div class="ml-30">
                                <p class="student-answer"
                                    style="margin: 10px 0; padding: 10px; border: 1px solid #ddd; border-radius: 5px;">
                                    {{ exam.answer }}
                                </p>
                            </div>
                        </div>
                        <div class="answer-style">
                            <span v-if="exam.titleType !== '判断题'">
                                标准答案 : {{ exam.titleContent.answer}}
                            </span>
                            <span v-else>
                                标准答案 : {{ exam.titleContent.answer ? "正确" : "错误"}}
                            </span>
                        </div>
                    </el-card>
                </div>
            </div>
        </div>
        <div v-else>
            <el-empty description="暂无数据"></el-empty>
        </div>
    </div>
</template>

<script>
    import "@/styles/exam.css";
    import Vue from 'vue';
    export default {
        name: 'exam',
        data() {
            return {
                collection: {},
                isFavorite: false,
                titles: null,
                studentId: 0,
                studentAnswers: [],
            };
        },
        async mounted() {
            await this.load();
        },
        methods: {
            toggleFavorite(examId) {
                this.$api.paperObj.cancelFavorite(examId).then(res => {
                    if (res.code === 2000) {
                        this.$delete(this.collection, examId);
                    }
                    else {
                        this.$message.error(res.message);
                    }
                })
            },
            getFillTitleParts(content) {
                let countInput = 0;
                const regex = /(_+)/g;
                const parts = content.name.split(regex);
                for (let i = 0; i < parts.length; i++) {
                    if (i % 2 === 1) {
                        parts[i] = 'input' + countInput++;
                    }
                }
                return parts;
            },
            async load() {
                try {
                    const res = await this.$api.paperObj.getFavorite(parseInt(localStorage.getItem('id')));
                    if (res.code === 2000) {
                        this.titles = res.data;
                        this.studentAnswers = res.other;
                        await this.handleTitle();
                    } else {
                        this.$message({
                            type: 'error',
                            message: res.message
                        });
                    }
                } catch (error) {
                    this.$message({
                        type: 'error',
                        message: error.message
                    });
                }
            },
            findTitleById(titleId) {
                return this.titles.find(title => title.titleId === titleId);
            },
            handleTitle() {
                this.studentAnswers.forEach(answer => {
                    let titleObj = this.findTitleById(answer.titleId);
                    const examId = answer.examId;  // 确保正确赋值
                    titleObj.scores = answer.scores;
                    titleObj.examId = examId;  // 确保正确赋值
                    titleObj.isFavorite = answer.isFavorite;
                    if (titleObj.titleType === '填空题' || titleObj.titleType === '多选题') {
                        titleObj.answer = answer.answer.split('、')
                    }
                    else {
                        titleObj.answer = answer.answer
                    }
                    if (titleObj) {
                        this.$set(this.collection, examId, titleObj);
                    }
                })
                console.log(JSON.stringify(this.collection))
            },

        }
    };
</script>

<style scoped>
    .collect-icon {
        display: flex;
        align-items: center;
    }

    .question-title-container {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
    }


    .collect-body {
        max-height: 100vh;
        overflow: auto;
    }

    /* 使用 ::v-deep */
    ::v-deep .el-checkbox .el-checkbox__input {
        border-radius: 50%;
        /* 将边框设置为圆形 */
    }

    ::v-deep .el-checkbox .el-checkbox__inner {
        border-radius: 50%;
    }

    ::v-deep .custom-input .el-input__inner {
        border-radius: 0px !important;
        border-top-width: 0px !important;
        border-left-width: 0px !important;
        border-right-width: 0px !important;
        border-bottom-width: 1px !important;
        margin-left: 7px !important;
        color: #0040fe;
        text-align: center;
        min-width: 50px;
        width: 80px;
        max-width: 500px;
    }

    /* 单选框禁用状态下选中的文本颜色 */
    ::v-deep .el-radio.is-disabled .el-radio__input.is-checked+.el-radio__label {
        color: #0040fe;
    }


    /* 多选框禁用状态下选中的文本颜色 */
    ::v-deep .el-checkbox.is-disabled .el-checkbox__input.is-checked+.el-checkbox__label {
        color: #0040fe;
    }

    /* 单选框禁用状态下未选中的文本颜色 */
    ::v-deep .el-radio.is-disabled .el-radio__input:not(.is-checked)+.el-radio__label {
        color: #333;
        /* 或者设置为默认颜色 */
    }

    /* 多选框禁用状态下未选中的文本颜色 */
    ::v-deep .el-checkbox.is-disabled .el-checkbox__input:not(.is-checked)+.el-checkbox__label {
        color: #333;
        /* 或者设置为默认颜色 */
    }

    .star-icon {
        font-size: 24px;
        cursor: pointer;
    }

    .star-icon.filled {
        color: red;
        position: relative;
    }
</style>