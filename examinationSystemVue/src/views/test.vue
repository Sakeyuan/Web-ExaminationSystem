<template>
    <div class="container">
        <div>
            <video ref="localVideo" autoplay style="width: 200px;height: 200px"></video>
        </div>
    </div>
</template>

<script>
    import { serverIp, serverPort } from '../../public/config';
    export default {
        name: 'test',
        data() {
            return {
                localStream: null,
                socket: null,
                conn: null,
                id: localStorage.getItem('id'),
            };
        },
        methods: {
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
            handleIceCandidate(e) {
                if (e.candidate) {
                    const message = { code: 'icecandidate', id: this.id, data: { candidate: e.candidate } };
                    this.sendMessage(message);
                }
                else {
                    console.log('Candidate is null');
                }
            },
            sendMessage(message) {
                if (this.socket && this.socket.readyState === WebSocket.OPEN) {
                    this.socket.send(JSON.stringify(message));
                } else {
                    console.error('WebSocket connection is not open');
                }
            },

            async handleSocketOpen() {
                console.log('WebSocket connection established');
                await this.startGetUserMedia();
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
            handleSocketError(e) {
                console.error('WebSocket connection error:', e);
            },
            handleIceCandidateError(event) {
                console.error("Error generating ICE candidate:", event.error); // 记录ICE candidate生成错误
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
            initPeerConnection() {
                this.conn = new RTCPeerConnection({});
                this.localStream.getTracks().forEach(track => this.conn.addTrack(track, this.localStream));
                this.conn.onicecandidate = this.handleIceCandidate;
                this.conn.onicecandidateerror = this.handleIceCandidateError;
            },
            initWebsocket() {
                this.socket = new WebSocket(`wss://${serverIp}:${serverPort}/video-stream/student/${localStorage.getItem('id')}`);
                this.socket.onopen = this.handleSocketOpen;
                this.socket.onmessage = this.handleSocketMessage;
                this.socket.onerror = this.handleSocketError;
            },
        },
        mounted() {
            this.initWebsocket();
        },
        beforeDestroy() {
            if (this.socket && this.socket.readyState === WebSocket.OPEN) {
                this.socket.close();
            }
        },
    };
</script>