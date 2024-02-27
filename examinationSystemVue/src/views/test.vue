<template>
    <h1>剩余时间：{{ remainingMinutes }}</h1>
</template>

<script>
    import io from 'socket.io-client';

    export default {
        data() {
            return {
                socket: null,
                localStorageKey: "examRemainingTime",
                websocketUrl: "ws://localhost:9090/websocket/",
                remainingTime: 0,
                remainingMinutes: 0,
                timer: null,
            };
        },
        created() {
            localStorage.setItem(this.localStorageKey, 5 * 60);
            localStorage.setItem("userId", 3);
            this.initWebSocket();
        },
        methods: {
            startTime() {
                this.loadRemainingTime(); // 从 localStorage 加载剩余时间
                this.updateTimer(); // 初始更新一次
                this.timer = setInterval(this.updateTimer, 1000); // 每秒更新一次
            },
            loadRemainingTime() {
                const savedRemainingTime = localStorage.getItem(this.localStorageKey);
                if (savedRemainingTime) {
                    this.remainingTime = parseInt(savedRemainingTime, 10);
                    this.timerInterval = savedRemainingTime;
                }
            },
            saveRemainingTime() {
                localStorage.setItem(this.localStorageKey, this.remainingTime.toString());
            },
            updateTimer() {
                this.remainingMinutes = Math.ceil(this.remainingTime / 60); // 使用 Math.ceil 向上取整
                if (this.remainingTime > 0) {
                    this.remainingTime -= 1;
                    this.saveRemainingTime(); // 每秒保存剩余时间到 localStorage
                } else {
                    clearInterval(this.timer);
                    localStorage.removeItem(this.localStorageKey);
                }
            },
            initWebSocket() {
                this.socket = new WebSocket(this.websocketUrl + localStorage.getItem("userId"));
                this.socket.onopen = this.webSocketOnopen;
                this.socket.onmessage = this.webSocketOnmessage;
                this.socket.onerror = this.webSocketOnerror;
                this.socket.onclose = this.webSocketClose;
            },
            // 连接成功后调用
            webSocketOnopen() {
                const messageObject = {
                    command: "uploadExamTotalTime",
                    data: {
                        userId: parseInt(localStorage.getItem("userId")),
                        examTotalTime: parseInt(localStorage.getItem(this.localStorageKey))
                    }
                };
                this.webSocketSend(JSON.stringify(messageObject));
                this.startTime();
            },
            // 发生错误时调用
            webSocketOnerror() {
                console.log("WebSocket连接发生错误");
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
                console.log("WebSocket连接关闭 (" + e.code + ")");
            },
        },
        beforeDestroy() {
            localStorage.removeItem(this.localStorageKey);
            clearInterval(this.timer);
            if (this.socket) {
                this.socket.close();
            }
        },
    };
</script>