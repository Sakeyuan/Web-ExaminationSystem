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
                pc: null,
            };
        },
        methods: {
            async startGetUserMedia() {
                try {
                    this.localStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: false });
                    this.$refs.localVideo.srcObject = this.localStream;

                    this.initPeerConnection();
                    await this.sendOffer();
                } catch (error) {
                    console.error('Error starting getUserMedia:', error);
                }
            },
            async sendOffer() {
                try {
                    if (!this.localStream) {
                        console.error('Local media stream is not ready');
                        return;
                    }

                    const description = await this.pc.createOffer();
                    await this.pc.setLocalDescription(description);

                    const message = { code: 'offer', data: { offer: this.pc.localDescription } }; // 使用 this.pc.localDescription 作为 offer
                    this.sendMessage(message);
                } catch (error) {
                    console.error('Error sending offer:', error);
                }
            },

            async getAnswer({ answer }) {
                try {
                    await this.pc.setRemoteDescription(answer);
                } catch (error) {
                    console.error('Error setting remote description:', error);
                }
            },
            handleIceCandidate(e) {
                if (e.candidate) {
                    const message = { code: 'icecandidate', data: { candidate: e.candidate } };
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
            initWebsocket() {
                this.socket = new WebSocket(`wss://${serverIp}:${serverPort}/video-stream/test`);
                this.socket.onopen = this.handleSocketOpen;
                this.socket.onmessage = this.handleSocketMessage;
                this.socket.onerror = this.handleSocketError;
            },
            handleSocketOpen() {
                console.log('WebSocket connection established');
                this.startGetUserMedia();
            },
            handleSocketMessage(e) {
                const { code, data } = JSON.parse(e.data);
                switch (code) {
                    case 'answer':
                        this.getAnswer(data);
                        break;
                    case 'icecandidate':
                        this.pc.addIceCandidate(new RTCIceCandidate(data.candidate));
                        break;
                    default:
                        console.warn('Unknown message code:', code);
                }
            },
            handleSocketError(e) {
                console.error('WebSocket connection error:', e);
            },
            initPeerConnection() {
                this.pc = new RTCPeerConnection({});
                this.localStream.getTracks().forEach(track => this.pc.addTrack(track, this.localStream));
                this.pc.onicecandidate = this.handleIceCandidate; // 使用对象的 onicecandidate 属性设置 ICE 候选者事件处理程序
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
