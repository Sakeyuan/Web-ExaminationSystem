<template>
    <div class="fullscreen-container">
        <div class="exam-container">
            <el-container>
                <el-header class="exam-header">
                    <div class="header-content">
                        <h2>{{ paper.paperName }}</h2>
                        <div style="display: flex;flex-direction: column;margin-left: 450px">
                            <p>总时间: {{ paper.paperTotalTime }} 分钟</p>
                            <p>总分: {{ paper.paperScore }} 分</p>
                        </div>

                    </div>
                    <div class="timer-container">
                        <p :style="{ color: remainingMinutes < 10 ? 'red' : 'white' }">
                            剩余时间: {{ formatTime(remainingTime) }}
                        </p>
                    </div>
                </el-header>
                <el-main>
                    <div v-if="questions.length > 0" class="exam-body">
                        <div v-for="(section, sectionIndex) in questions" :key="sectionIndex">
                            <div class="section-title">{{ section.title }}</div>
                            <div v-for="(question, index) in section.contents" :key="index">
                                <div class="exam-question">
                                    <div class="question-title">
                                        <div class="inline-div" v-if="question.titleType!== 4">
                                            <p class="inline-div">{{ index + 1 }}<span
                                                    style="margin-right: 10px">.</span>
                                            </p>
                                            <span style="display: inline;line-height: 3.0">
                                                {{ question.content.name }}<span class="title-scores">({{
                                                    question.content.scores
                                                    }}分)</span>
                                            </span>
                                        </div>
                                    </div>
                                    <div v-if="question.titleType === 1">
                                        <!-- 单选题 -->
                                        <el-radio-group v-model="studentAnswers[sectionIndex][index]"
                                            class="custom-radio-group">
                                            <el-radio v-for="(option, optionIndex) in question.content.selectInput"
                                                :key="optionIndex" :label="option.select" class="custom-radio">
                                                {{ option.select }}. {{ option.content }}
                                            </el-radio>
                                        </el-radio-group>
                                    </div>

                                    <div v-else-if="question.titleType === 2">
                                        <el-checkbox-group v-model="studentAnswers[sectionIndex][index]"
                                            class="custom-checkbox-group">
                                            <el-checkbox v-for="(option, optionIndex) in question.content.selectInput"
                                                :key="optionIndex" :label="option.select" class="custom-checkbox">
                                                {{ option.select }}. {{ option.content }}
                                            </el-checkbox>
                                        </el-checkbox-group>
                                    </div>

                                    <div v-else-if="question.titleType === 3">
                                        <el-radio-group v-model="studentAnswers[sectionIndex][index]">
                                            <el-radio label="true">正确</el-radio>
                                            <el-radio label="false">错误</el-radio>
                                        </el-radio-group>
                                    </div>
                                    <div v-else-if="question.titleType === 4">
                                        <!-- 填空题-->
                                        <div class="inline-div">
                                            <p class="inline-div">{{ index + 1 }}<span
                                                    style="margin-right: 10px">.</span>
                                            </p>
                                            <div class="inline-div"
                                                v-for="(part, i) in getFillTitleParts(question.content, sectionIndex, index)"
                                                :key="i">
                                                <span v-if="part.slice(0,5) == 'input'" class="inline-div">
                                                    <el-input
                                                        v-model="studentAnswers[sectionIndex][index][part[part.length-1]]"
                                                        class="custom-input" style="padding: 5px">
                                                    </el-input>
                                                </span>
                                                <span v-else class="question-title inline-div"
                                                    style="line-height: 2.0">{{
                                                    part }}</span>
                                            </div>
                                            <span class="title-scores">({{ question.content.scores }}分)</span>
                                        </div>
                                    </div>
                                    <div v-else-if="question.titleType === 5">
                                        <!-- 简答题目 -->
                                        <el-input v-model="studentAnswers[sectionIndex][index]" placeholder="请输入答案"
                                            type="textarea" :autosize="{ minRows: 4, maxRows: 100}">
                                        </el-input>
                                    </div>
                                </div>
                            </div>

                        </div>
                        <div class="submit-btn">
                            <el-button size="medium" type="primary" @click="submitAnswers">提交答案</el-button>
                        </div>
                    </div>
                    <div v-else>
                        <p>暂无试题数据</p>
                    </div>
                </el-main>
            </el-container>
        </div>
        <el-dialog :visible.sync="showExamDialog" title="注意事项" width="60%">
            <div style="text-align: center; padding: 20px;">
                <p style="font-size: 16px;">请在考试开始前仔细阅读以下注意事项：</p>
                <ul style="list-style-type: none; padding: 0; margin-bottom: 20px;">
                    <li style="font-size: 14px; margin-bottom: 10px;">请确保在稳定的网络环境下参加考试。</li>
                    <li style="font-size: 14px; margin-bottom: 10px;">禁止使用任何形式的作弊工具。</li>
                    <li style="font-size: 14px; margin-bottom: 10px;">请在规定时间内完成考试。</li>
                    <li style="font-size: 14px; margin-bottom: 10px;">不要随意离开考试界面，以免影响考试顺利进行。</li>
                </ul>
                <div>
                    <el-icon name="information" style="font-size: 30px; color: #409EFF;"></el-icon>
                    <p style="font-size: 14px; margin-top: 10px;">如果有任何疑问，请联系考务人员。</p>
                </div>
            </div>
            <div slot="footer" class="dialog-footer" style="text-align: center;">
                <el-button @click="confirmExam" type="primary" size="medium">知道了</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import "@/styles/exam.css";
    import io from 'socket.io-client';
    export default {
        name: 'exam',
        data() {
            return {
                showExamDialog: true,
                paper: {},
                studentAnswers: [],
                questions: [],
                fillTitlePartsCache: new Map(),
                socket: null,
                localStorageKey: "examRemainingTime",
                websocketUrl: "ws://localhost:9090/websocket/",
                remainingTime: 0,
                remainingMinutes: 0,
                timer: null,
                debouncedResizeHandler: null,
                fillInputValues: {},
            };
        },
        beforeDestroy() {
            this.destroyResizeHandler();
            localStorage.removeItem(this.localStorageKey);
            clearInterval(this.timer);
            if (this.socket) {
                this.socket.close();
            }
        },
        methods: {
            confirmExam() {
                this.showExamDialog = false;
                this.enterFullScreen();
            },
            exitFullScreen() {
                if (document.exitFullscreen) {
                    document.exitFullscreen();
                } else if (document.webkitExitFullscreen) {
                    document.webkitExitFullscreen();
                } else if (document.msExitFullscreen) {
                    document.msExitFullscreen();
                }
            },
            initResizeHandler() {
                this.debouncedResizeHandler = _.debounce(() => {
                    if (!this.keyDown()) {
                        this.$alert('考试期间请勿退出全屏，以免影响考试', '警告', {
                            confirmButtonText: '确定',
                            callback: action => {
                                this.enterFullScreen();
                            }
                        });
                    }
                }, 500);
                window.addEventListener('resize', this.debouncedResizeHandler);
                window.addEventListener('beforeunload', this.confirmLeave);
                // document.addEventListener('contextmenu', function (event) {
                //     event.preventDefault();
                // });
                window.addEventListener('popstate', function (event) {
                    history.pushState(null, null, document.URL);
                    const message = "在考试期间，请不要返回页面，以免影响考试进行。";
                    if (window.confirm(message)) {
                        this.enterFullScreen();
                    }
                });
                document.addEventListener('keydown', this.handleKeydown);
                document.addEventListener('fullscreenchange', this.handleFullscreenChange);
                document.addEventListener('webkitfullscreenchange', this.handleFullscreenChange);
                document.addEventListener('mozfullscreenchange', this.handleFullscreenChange);
                document.addEventListener('MSFullscreenChange', this.handleFullscreenChange);
            },
            handleKeydown(event) {
                // 阻止 Ctrl+C, Ctrl+X, Ctrl+V and Ctrl + S
                if ((event.ctrlKey || event.metaKey) && ['c', 'x', 'v', 's'].includes(event.key.toLowerCase())) {
                    event.preventDefault();
                }
                if (event.key === 'F12' || event.key === 'F11') {
                    event.preventDefault();
                }
            },
            confirmLeave(event) {
                const message = "您确定要离开考试页面吗？离开页面将提交当前试卷。";
                if (window.confirm(message)) {
                    this.$message.info("试卷将在后台提交，请稍候...");
                    this.submitAnswers();
                }
                (event || window.event).returnValue = message;
                return message;
            },
            handleFullscreenChange() {
                this.isFullScreen = !!(
                    document.fullscreenElement ||
                    document.webkitFullscreenElement ||
                    document.msFullscreenElement ||
                    document.mozFullScreenElement
                );
            },
            destroyResizeHandler() {
                window.removeEventListener('resize', this.debouncedResizeHandler);
                window.removeEventListener('beforeunload', this.confirmLeave);
                document.removeEventListener('fullscreenchange', this.handleFullscreenChange);
                document.removeEventListener('webkitfullscreenchange', this.handleFullscreenChange);
                document.removeEventListener('mozfullscreenchange', this.handleFullscreenChange);
                document.removeEventListener('MSFullscreenChange', this.handleFullscreenChange);
                document.removeEventListener('keydown', this.handleKeydown);
                clearTimeout(this.debouncedResizeHandler.timer);
            },
            formatTime(timeInSeconds) {
                const hours = Math.floor(timeInSeconds / 3600);
                const minutes = Math.floor((timeInSeconds % 3600) / 60);
                const seconds = timeInSeconds % 60;
                const formatNumber = (number) => (number < 10 ? `0${number}` : `${number}`);
                return `${formatNumber(hours)}:${formatNumber(minutes)}:${formatNumber(seconds)}`;
            },
            initWebSocket() {
                this.socket = new WebSocket(this.websocketUrl + localStorage.getItem("userId"));
                this.socket.timeout = setTimeout(() => {
                    this.socket.close(); // 关闭连接
                    this.$message.error("WebSocket连接超时，请刷新页面重试或联系管理员");
                }, (this.remainingTime + 30) * 1000);

                this.socket.onopen = this.webSocketOnopen;
                this.socket.onmessage = this.webSocketOnmessage;
                this.socket.onerror = this.webSocketOnerror;
                this.socket.onclose = this.webSocketClose;
            },
            webSocketOnopen() {
                const messageObject = {
                    command: "uploadExamTotalTime",
                    data: {
                        userId: parseInt(localStorage.getItem("userId")),
                        examTotalTime: parseInt(this.remainingTime)
                    }
                };
                this.webSocketSend(JSON.stringify(messageObject));
                this.startTime();
            },
            webSocketOnerror() {
                this.$message.error("WebSocket连接发生错误，请刷新页面重试或联系管理员");
            },
            webSocketOnmessage(event) {
                const data = JSON.parse(event.data);
                if (data.type === "uploadRemainingTime") {
                    this.remainingTime = data.data.remainingTime;
                    this.updateTimer();
                }
            },
            webSocketSend(message) {
                console.log("WebSocket发送消息:" + message);
                this.socket.send(message);
            },
            webSocketClose(e) {
                console.log("WebSocket连接已关闭");
            },
            getFillTitleParts(content, sectionIndex, index) {
                const cacheKey = `${sectionIndex}-${index}`;
                if (this.fillTitlePartsCache.has(cacheKey)) {
                    return this.fillTitlePartsCache.get(cacheKey);
                }
                const regex = /(_+)/g;
                const parts = content.name.split(regex);
                let countInput = 0;
                for (let i = 0; i < parts.length; i++) {
                    if (i % 2 === 1) {
                        parts[i] = 'input' + countInput++;
                    }
                }
                this.fillTitlePartsCache.set(cacheKey, parts);
                return parts;
            },
            load() {
                const paperContent = this.paper.paperContent;
                if (!paperContent) {
                    return [];
                }
                const parsedContent = JSON.parse(paperContent);
                parsedContent.forEach(section => {
                    section.contents.forEach(question => {
                        question.content = this.parseQuestionContent(question.content);
                    });
                });
                this.questions = parsedContent;
            },
            submitAnswers() {
                const loadingInstance = this.$loading({
                    lock: true,
                    text: '提交中，请稍候...',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });
                const answersWithIds = [];
                this.questions.forEach((section, sectionIndex) => {
                    section.contents.forEach((question, index) => {
                        const questionId = question.titleId
                        let studentAnswers = this.studentAnswers[sectionIndex][index];
                        if (question.titleType === 4) {
                            for (let i = 0; i < studentAnswers.length; i++) {
                                if (!studentAnswers[i]) {
                                    studentAnswers[i] = "";
                                }
                            }
                        }
                        answersWithIds.push({ titleId: questionId, answer: studentAnswers });
                    });
                });
                const data = {
                    studentId: parseInt(localStorage.getItem("id")),
                    paperId: this.paper.paperId,
                    answers: answersWithIds
                }
                this.$api.examObj.submitAnswers(data).then(res => {
                    setTimeout(() => {
                        if (res.code == 2000) {
                            this.$message.success("提交成功");
                            localStorage.removeItem('paperItem');
                            this.exitFullScreen();
                            this.$router.push('/student/myPaper');
                        } else {
                            this.$message.error(res.message);
                        }
                    }, 1000);
                }).catch((error) => {
                    this.$message.error(error.message);
                }).finally(() => {
                    loadingInstance.close();
                });
            },
            parseQuestionContent(content) {
                try {
                    return JSON.parse(content);
                } catch (error) {
                    return null;
                }
            },
            loadRemainingTime() {
                const savedRemainingTime = localStorage.getItem(this.localStorageKey);
                if (savedRemainingTime) {
                    this.remainingTime = parseInt(savedRemainingTime, 10) * 60;
                }
            },
            startTime() {
                this.updateTimer(); // 初始更新一次
                this.timer = setInterval(this.updateTimer, 1000); // 每秒更新一次
            },
            saveRemainingTime() {
                localStorage.setItem(this.localStorageKey, this.remainingTime.toString());
            },
            updateTimer() {
                this.remainingMinutes = Math.ceil(this.remainingTime / 60); // 使用 Math.ceil 向上取整
                if (this.remainingTime > 0) {
                    this.remainingTime -= 1;
                    this.saveRemainingTime();
                }
                else {
                    clearInterval(this.timer);
                    localStorage.removeItem(this.localStorageKey);
                    this.submitAnswers();
                }
            },
            fullEle() {
                return (
                    document.fullscreenElement ||
                    document.webkitFullscreenElement ||
                    document.msFullscreenElement ||
                    document.mozFullScreenElement ||
                    null
                );
            },
            // 返回是否按下了全屏键
            keyDown() {
                return !!document.webkitIsFullScreen || this.fullEle();
            },
            enterFullScreen() {
                let element = document.documentElement;  // 获取整个文档的元素
                if (element.requestFullscreen) {
                    element.requestFullscreen();
                } else if (element.webkitRequestFullscreen) {
                    element.webkitRequestFullscreen();
                } else if (element.msRequestFullscreen) {
                    element.msRequestFullscreen();
                }
            },
        },
        mounted() {
            this.initResizeHandler();
            this.loadRemainingTime();
            this.initWebSocket();
        },
        destroyed() {
            window.removeEventListener('resize', this.debouncedResizeHandler);
            clearTimeout(this.debouncedResizeHandler.timer);
        },
        created() {
            const loadingInstance = this.$loading({
                text: '加载中...',
                fullscreen: true,
                spinner: 'el-icon-loading',
                lock: true,
            });
            history.pushState(null, null, document.URL);
            const storedPaperItem = localStorage.getItem('paperItem');
            if (storedPaperItem) {
                this.paper = JSON.parse(storedPaperItem);
                const data = {
                    paperId: this.paper.paperId,
                    studentId: parseInt(localStorage.getItem('id'))
                }
                const param = new URLSearchParams(data).toString();
                this.$api.paperObj.getOnePaperWithContentAndScore(param).then(res => {
                    if (res.code == 2000) {
                        this.paper = res.data;
                        this.load();
                        this.studentAnswers = this.questions.map(section =>
                            section.contents.map(question => {
                                if (question.titleType === 2) {
                                    // 多选题
                                    return [];
                                }
                                else if (question.titleType === 4) {
                                    return [""]
                                }
                                else {
                                    // 其他类型题目
                                    return '';
                                }
                            })
                        );
                    }
                    else {
                        console.error("数据加载错误")
                    }
                }).catch(error => {
                    console.error("数据加载失败", error);
                }).finally(() => {
                    loadingInstance.close();
                });
            } else {
                this.paper = {};
                loadingInstance.close();
            }
        }
    };
</script>

<style scoped>
    .fullscreen-container {
        height: 100vh;
        width: 100vw;
        overflow: hidden;
    }

    /* 使用 ::v-deep */
    ::v-deep .el-checkbox .el-checkbox__input {
        border-radius: 50%;
        /* 将边框设置为圆形 */
    }

    ::v-deep .el-checkbox .el-checkbox__inner {
        border-radius: 50%;
        /* 将内部勾选框设置为圆形 */
    }

    ::v-deep .custom-input .el-input__inner {
        border-radius: 0px !important;
        border-top-width: 0px !important;
        border-left-width: 0px !important;
        border-right-width: 0px !important;
        border-bottom-width: 1px !important;
        margin-left: 7px !important;
        min-width: 50px;
        width: 80px;
        max-width: 500px;
    }

    .exam-container {
        height: 100vh;
    }
</style>