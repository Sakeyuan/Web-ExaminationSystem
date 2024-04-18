<template>
    <div ref="videoContainer">
        <!-- <video ref="localVideo" src="" autoplay muted></video> -->
    </div>
</template>

<script>
    import { serverIp, serverPort } from '../../public/config';

    export default {
        name: 'test2',
        data() {
            return {
                socket: null,
                students: Object.create(null),
                onlineStudentList: new Array,
                counts: 0,
                videoContainer: null,
            };
        },
        mounted() {
            this.getOnlineStudents();
            this.videoContainer = this.$refs.videoContainer;
        },
        methods: {
            getOnlineStudents() {
                // 获取在线学生列表
                this.$api.teacherObj.getOnlineStudents(localStorage.getItem("id")).then(res => {
                    if (res.code === 2000) {
                        this.onlineStudentList = res.data.split(",");
                        this.initWebSocket();
                    }
                    else {
                        this.$message.error(res.message);
                    }
                })
            },
            initWebSocket() {
                this.socket = new WebSocket(`wss://${serverIp}:${serverPort}/video-stream/teacher/${localStorage.getItem('id')}`);
                this.socket.onopen = this.handleSocketOpen;
                this.socket.onmessage = this.handleSocketMessage;
                this.socket.onerror = this.handleSocketError;
            },
            async handleSocketOpen() {
                console.log('连接建立成功');
                if (this.onlineStudentList && this.onlineStudentList.length > 0) {
                    for (const studentId of this.onlineStudentList) {
                        await this.invite(studentId);
                    }
                } else {
                    console.error('没有学生考试');
                }
            },
            invite(studentId) {
                this.sendMessage({ code: 'invite', id: studentId, data: { studentId } });
            },
            async handleOffer({ offer }, studentId) {
                try {
                    const conn = new RTCPeerConnection();
                    conn.ontrack = this.handleTrackEvent;
                    conn.onicecandidate = (event) => this.handleIceCandidateEvent(event, studentId);
                    await conn.setRemoteDescription(offer);
                    const answer = await conn.createAnswer();
                    await conn.setLocalDescription(answer);
                    this.$set(this.students, studentId, conn);
                    const message = { code: 'answer', id: studentId, data: { answer } };
                    this.sendMessage(message);
                } catch (error) {
                    console.error('Error handling offer:', error);
                }
            },
            handleIceCandidateEvent(event, studentId) {
                if (event.candidate) {
                    const message = { code: 'icecandidate', id: studentId, data: { candidate: event.candidate } };
                    this.sendMessage(message);
                }
                else {
                    console.log('Candidate is null');
                }
            },
            handleSocketMessage(e) {
                const { code, data, id } = JSON.parse(e.data);
                switch (code) {
                    case 'offer':
                        this.handleOffer(data, id);
                        break;
                    case 'icecandidate':
                        this.handleIceCandidate(data, id);
                        break;
                    default:
                        console.warn('Unknown message type:', code);
                }
            },
            handleSocketError(e) {
                console.error('WebSocket connection error:', e);
            },
            async handleIceCandidate({ candidate }, studentId) {
                try {
                    const conn = this.students[studentId];
                    if (conn) {
                        await conn.addIceCandidate(new RTCIceCandidate(candidate));
                    } else {
                        console.warn('RTCPeerConnection not found for student:', studentId);
                    }
                } catch (error) {
                    console.error('Error adding ICE candidate:', error);
                }
            },
            handleTrackEvent(event) {
                const remoteStream = event.streams[0];
                const videoElement = document.createElement('video');
                videoElement.autoplay = true;
                videoElement.muted = true;
                videoElement.width = 200;
                videoElement.height = 200;
                videoElement.style.margin = '15px';
                videoElement.style.border = '1px solid black';
                videoElement.srcObject = remoteStream;
                this.videoContainer.appendChild(videoElement);
                this.videoContainer.appendChild(videoElement);
            },
            sendMessage(message) {
                if (this.socket && this.socket.readyState === WebSocket.OPEN) {
                    this.socket.send(JSON.stringify(message));
                } else {
                    console.error('WebSocket connection not open');
                }
            },
        },
        beforeDestroy() {
            if (this.socket && this.socket.readyState === WebSocket.OPEN) {
                this.socket.close();
                console.log('WebSocket connection closed');
            }
        },
    };
</script>

<style>
</style>