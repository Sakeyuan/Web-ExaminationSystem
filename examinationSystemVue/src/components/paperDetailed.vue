<template>
    <div class="correctPaper-body">
        <div v-if="paperQuestions && paperQuestions.length > 0" class="correctPaper-title">
            <div v-for="(section, sectionIndex) in paperQuestions" :key="sectionIndex" style>
                <div class="correctPaper-section-title">{{ section.title }}</div>
                <div v-for="(question, index) in section.contents" :key="index">
                    <div class="exam-question">
                        <div class="question-title">
                            <div class="inline-div">
                                <p class="inline-div">{{ index + 1 }}<span style="margin-right: 10px">.</span>
                                </p>
                                <span style="display: inline; line-height: 1.3;" v-if="question.titleType !== 4">
                                    {{ question.content.name }}<span class="title-scores">({{ question.content.scores
                                        }}分)</span>
                                </span>

                                <div class="inline-div" v-else-if="question.titleType === 4">
                                    <div class="inline-div"
                                        v-for="(part, i) in getFillTitleParts(question.content, sectionIndex, index)"
                                        :key="i">
                                        <span v-if="part.slice(0,5) == 'input'" class="inline-div">
                                            <el-input
                                                v-model="question.content.studentAnswer[parseInt(part[part.length-1], 10)]"
                                                class="custom-input" readonly type="text"></el-input>
                                        </span>
                                        <span v-else class="question-title inline-div">{{ part }}</span>
                                    </div>
                                    <span class="title-scores">({{ question.content.scores }}分)</span>
                                </div>

                                <el-input-number v-model="question.content.studentScores" :min="0"
                                    :max="question.content.scores" :step="1" controls-position="right" class="ml-10"
                                    :disabled=isForbidden :controls=inputNumberControls style="margin: 20px">
                                </el-input-number>
                            </div>
                        </div>
                        <div v-if="question.titleType === 1">
                            <!-- 单选题 -->
                            <el-radio-group class="custom-radio-group" v-model="question.content.studentAnswer"
                                disabled>
                                <el-radio v-for="(option, optionIndex) in question.content.selectInput"
                                    :key="optionIndex" :label="option.select" class="custom-radio">
                                    {{ option.select }}. {{ option.content }}
                                </el-radio>
                            </el-radio-group>
                        </div>
                        <div v-else-if="question.titleType === 2" class="ml-10">
                            <!-- 多选题 -->
                            <el-checkbox-group class="custom-checkbox-group" v-model="question.content.studentAnswer"
                                disabled>
                                <el-checkbox
                                    v-for="(option, optionIndex) in question.content && question.content.selectInput"
                                    :key="optionIndex" :label="option.select" class="custom-checkbox">
                                    {{ option.select }}. {{ option.content }}
                                </el-checkbox>
                            </el-checkbox-group>
                        </div>

                        <div v-else-if="question.titleType === 3">
                            <!-- 判断题 -->
                            <el-radio-group v-model="question.content.studentAnswer" disabled>
                                <el-radio label="true">正确</el-radio>
                                <el-radio label="false">错误</el-radio>
                            </el-radio-group>
                        </div>
                        <div v-else-if="question.titleType === 5" text-color="#d61010">
                            <!-- 简答题目 -->
                            <div class="ml-30">
                                <p class="student-answer"
                                    style="margin: 10px 0; padding: 10px; border: 1px solid #ddd; border-radius: 5px;">
                                    {{ question.content.studentAnswer }}
                                </p>
                            </div>
                        </div>
                        <div class="answer-style">
                            <span>
                                标准答案 : {{ question.content.answer}}
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div v-else>
            <el-empty description="空空如也"></el-empty>
        </div>
    </div>

</template>

<script>
    import "@/styles/exam.css";
    export default {
        name: 'exam',
        data() {
            return {
                paper: {},
                paperId: 0,
                studentId: 0,
                paperQuestions: [],
                correctResult: {},
                studentAnswers: [],
                otherQuestionScore: 0,
                fillTitlePartsCache: new Map(),
                isForbidden: true,
                inputNumberControls: true,
            };
        },
        created() {
            this.load();
        },
        methods: {
            setInputNumberControls(val) {
                this.inputNumberControls = val;
            },
            getFillTitleParts(content, sectionIndex, index) {
                const cacheKey = `${sectionIndex}-${index}`;
                if (this.fillTitlePartsCache.has(cacheKey)) {
                    return this.fillTitlePartsCache.get(cacheKey);
                }
                let countInput = 0;
                const regex = /(_+)/g;
                const parts = content.name.split(regex);
                for (let i = 0; i < parts.length; i++) {
                    if (i % 2 === 1) {
                        parts[i] = 'input' + countInput++;
                    }
                }
                this.fillTitlePartsCache.set(cacheKey, parts);
                return parts;
            },
            submitCorrectResult() {

            },

            async load() {
                this.paperId = this.$route.query.paperId;
                this.studentId = this.$route.query.studentId;
                try {
                    const params = new URLSearchParams({
                        paperId: this.paperId,
                        studentId: this.studentId
                    });
                    const res = await this.$api.paperObj.getOnePaperWithContentAndScore(params.toString());
                    if (res.code === 2000) {
                        this.paper = res.data;
                        this.otherQuestionScore = res.total;
                        this.studentAnswers = res.other;
                        await this.handlePaperContent();
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

            findAnswerByTitleId(titleId) {
                return this.studentAnswers.find(answer => answer.titleId === titleId);
            },
            async handlePaperContent() {
                try {
                    const paperContent = this.paper.paperContent;
                    if (!paperContent) {
                        return [];
                    }
                    const parsedContent = JSON.parse(paperContent);
                    // 遍历试卷内容
                    parsedContent.forEach(section => {
                        section.contents.forEach(question => {
                            question.content = this.parseQuestionContent(question.content);
                            this.$set(this.correctResult, question.titleId, 0);
                            const answerObj = this.findAnswerByTitleId(question.titleId);
                            question.content.studentScores = answerObj.scores;
                            if (answerObj) {
                                // 如果是多选题，将答案转换为数组
                                question.content.studentAnswer = question.titleType === 2 || question.titleType === 4 ? answerObj.answer.split('、')
                                    : answerObj.answer;
                            }
                        });
                    });

                    this.paperQuestions = parsedContent;

                } catch (error) {
                    console.error('Error parsing content:', error);
                    return [];
                }
            },
            parseQuestionContent(content) {
                try {
                    return JSON.parse(content);
                } catch (error) {
                    console.error('Error parsing question content:', error);
                    return null;
                }
            },
        }
    };
</script>

<style scoped>
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
</style>