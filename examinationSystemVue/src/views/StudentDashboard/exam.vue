<template>
    <div class="fullscreen-container">
        <div class="exam-container">
            <div class="video-container">
                <video ref="localVideo" disablePictureInPicture autoplay style="width: 150px;height: 150px;"
                    @mousedown="startDragging" @mousemove="dragging" @mouseup="stopDragging"></video>
            </div>
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
                                                        class="exam-input" style="padding: 5px">
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
                            <el-button size="medium" type="primary" @click="submitConfirmation">提交答案</el-button>
                        </div>
                    </div>
                    <div v-else>
                        <p>暂无试题数据</p>
                    </div>
                </el-main>
            </el-container>
        </div>
        <el-dialog v-if="showSubmitConfirmation" :visible.sync="showSubmitConfirmation" title="确认提交答案">
            <p>您确定要提交答案吗？提交后无法更改。</p>
            <div slot="footer" class="dialog-footer" style="text-align: center;">
                <el-button @click="showSubmitConfirmation = false" size="medium">取消</el-button>
                <el-button @click="submitAnswers" type="primary" size="medium">确定提交</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import "@/styles/exam.css";
    import io from 'socket.io-client';
    import { serverIp, serverPort } from '../../../public/config';

    export default {
        name: 'exam',
        data() {
            return {
                isDragging: false,
                initialX: 0,
                initialY: 0,
                offsetX: 0,
                offsetY: 0,
                localStream: null,
                conn: null,
                id: localStorage.getItem('id'),
                showExamDialog: true,
                showSubmitConfirmation: false,
                paper: {},
                studentAnswers: [],
                questions: [],
                fillTitlePartsCache: new Map(),
                socketText: null, // 用于文本数据的WebSocket实例
                socketVideo: null, // 用于视频数据的WebSocket实例
                localStorageKey: "examRemainingTime",
                websocketUrl: `wss://${serverIp}:${serverPort}/`,
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
            if (this.socketText) {
                this.socketText.close();
            }
            if (this.socketVideo && this.socketVideo.readyState === WebSocket.OPEN) {
                this.sendMessage({ code: "close", id: this.id, data: { data: "closed" } })
                this.socketVideo.close();
            }

        },
        methods: {
            startDragging(event) {
                // 记录鼠标按下时视频元素的初始位置
                this.initialX = event.clientX;
                this.initialY = event.clientY;
                // 记录视频元素的初始位置
                const rect = this.$refs.localVideo.getBoundingClientRect();
                this.offsetX = rect.left;
                this.offsetY = rect.top;
                // 标记为正在拖动
                this.isDragging = true;
            },
            dragging(event) {
                if (this.isDragging) {
                    // 计算鼠标位置相对于初始位置的偏移量
                    const dx = event.clientX - this.initialX;
                    const dy = event.clientY - this.initialY;
                    // 更新视频元素的位置
                    this.$refs.localVideo.style.left = `${this.offsetX + dx}px`;
                    this.$refs.localVideo.style.top = `${this.offsetY + dy}px`;
                }
            },
            stopDragging() {
                // 标记停止拖动
                this.isDragging = false;
            },
            submitConfirmation() {
                this.showSubmitConfirmation = true;
            },
            confirmExam() {
                this.showExamDialog = false;
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
            async startGetUserMedia() {
                try {
                    navigator.mediaDevices.getUserMedia({ video: true, audio: false }).then(stream => {
                        this.localStream = stream;
                        this.$refs.localVideo.srcObject = this.localStream;
                        this.initPeerConnection();
                    })
                } catch (error) {
                    console.error('Error starting getUserMedia:', error);
                }
            },
            async sendOffer(studentId) {
                console.log("send offer")
                try {
                    const description = await this.conn.createOffer();
                    await this.conn.setLocalDescription(description);
                    const message = { code: 'offer', id: studentId, data: { offer: description } };
                    this.sendMessage(message);
                }
                catch (error) {
                    console.error('Error sending offer:', error);
                }
            },
            async handleAnswer({ answer }) {
                try {
                    await this.conn.setRemoteDescription(answer);
                } catch (error) {
                    console.error('Error setting remote description:', error);
                }
            },
            handleSocketMessage(e) {
                const { code, id, data } = JSON.parse(e.data);
                switch (code) {
                    case 'invite':
                        this.sendOffer(id);
                        break;
                    case 'answer':
                        this.handleAnswer(data);
                        break;
                    case 'icecandidate':
                        this.conn.addIceCandidate(new RTCIceCandidate(data.candidate));
                        break;
                    default:
                        console.warn('Unknown message code:', code);
                }
            },
            async handleSocketOpen() {
                console.log('WebSocket connection established');
                await this.startGetUserMedia();
            },
            handleSocketError(e) {
                console.error('WebSocket connection error:', e);
            },
            handleIceCandidateError(event) {
                console.error("Error generating ICE candidate:", event.error); // 记录ICE candidate生成错误
            },
            sendMessage(message) {
                if (this.socketVideo && this.socketVideo.readyState === WebSocket.OPEN) {
                    this.socketVideo.send(JSON.stringify(message));
                } else {
                    console.error('WebSocket connection is not open');
                }
            },
            handleIceCandidate(e) {
                if (e.candidate) {
                    const message = { code: 'icecandidate', id: this.id, data: { candidate: e.candidate } };
                    this.sendMessage(message);
                }
                else {
                    console.log('Candidate is null');
                }
            },
            initPeerConnection() {
                this.conn = new RTCPeerConnection({});
                this.localStream.getTracks().forEach(track => this.conn.addTrack(track, this.localStream));
                this.conn.onicecandidate = this.handleIceCandidate;
                this.conn.onicecandidateerror = this.handleIceCandidateError;
            },
            initWebSocket() {
                // 建立WebSocket连接（文本数据）
                this.socketText = new WebSocket(this.websocketUrl + "websocketSendText/" + localStorage.getItem("userId"));
                this.socketText.onopen = this.webSocketOnopenText;
                this.socketText.onmessage = this.webSocketOnmessageText;
                this.socketText.onerror = this.webSocketOnerrorText;
                this.socketText.onclose = this.webSocketCloseText;

                this.socketVideo = new WebSocket(`wss://${serverIp}:${serverPort}/video-stream/student/${localStorage.getItem('id')}`);
                this.socketVideo.onopen = this.handleSocketOpen;
                this.socketVideo.onmessage = this.handleSocketMessage;
                this.socketVideo.onerror = this.handleSocketError;
            },
            webSocketOnopenText() {
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
            webSocketOnerrorText() {
                this.$message.error("WebSocket连接发生错误，请刷新页面重试或联系管理员");
            },
            webSocketOnmessageText(event) {
                const data = JSON.parse(event.data);
                if (data.type === "uploadRemainingTime") {
                    this.remainingTime = data.data.remainingTime;
                    this.updateTimer();
                }
            },
            webSocketSend(message) {
                console.log("WebSocket发送消息:" + message);
                this.socketText.send(message);
            },
            webSocketCloseText(e) {
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

                const loadingInstance = this.$loading({
                    lock: true,
                    text: '正在提交试卷中，请勿操作...',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });

                this.$api.examObj.submitAnswers(data).then(res => {
                    if (res.code === 2000) {
                        localStorage.removeItem('paperItem');
                        localStorage.removeItem('examRemainingTime');
                        setTimeout(() => {
                            loadingInstance.close();
                            this.exitFullScreen();
                            this.$message.success("提交成功");
                            this.$router.push('/student/myPaper');
                        }, 5000);
                    } else {
                        setTimeout(() => {
                            loadingInstance.close();
                            this.$message.error(res.message);
                        }, 1000);
                    }
                }).catch(error => {
                    setTimeout(() => {
                        loadingInstance.close();
                        this.$message.error(error.message);
                    }, 1000);
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
            this.enterFullScreen();

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

    .video-container {
        position: fixed;
        bottom: 10px;
        right: 10px;
        z-index: 9999;
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

    .exam-input {
        width: auto;
        min-width: 80px;
        width: 120px;
        padding: 10px;
    }

    ::v-deep .exam-input .el-input__inner {
        background-color: #f3f3f3;
        border-top: 0px;
        border-left: 0px;
        border-right: 0px;
        border-radius: 0%;
        border-bottom: 1px solid #000 !important;
    }

    .exam-container {
        height: 100vh;
    }
</style>