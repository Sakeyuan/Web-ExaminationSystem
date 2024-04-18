<template>
    <div>
        <video ref="remoteVideo" autoplay muted style="width: 200px; height: 200px; border: 1px solid black;"></video>
    </div>
</template>

<script>
    import { serverIp, serverPort } from '../../public/config';

    export default {
        name: 'test2',
        data() {
            return {
                socket: null,
                pc: null,
                remoteVideo: null,
            };
        },
        mounted() {
            this.initWebsocket();
        },
        methods: {
            initWebsocket() {
                this.socket = new WebSocket(`wss://${serverIp}:${serverPort}/video-stream/test`);
                this.socket.onopen = this.handleSocketOpen;
                this.socket.onmessage = this.handleSocketMessage;
                this.socket.onerror = this.handleSocketError;
            },
            async handleSocketOpen() {
                console.log('WebSocket connection established');
            },
            handleSocketMessage(e) {
                const { code, data } = JSON.parse(e.data);
                switch (code) {
                    case 'offer':
                        this.handleOffer(data);
                        break;
                    case 'icecandidate':
                        this.handleIceCandidate(data);
                        break;
                    default:
                        console.warn('Unknown message type:', code);
                }
            },
            handleSocketError(e) {
                console.error('WebSocket connection error:', e);
            },
            async handleOffer({ offer }) {
                console.log('Handling offer:', offer);
                try {
                    if (!this.pc) {
                        this.pc = new RTCPeerConnection({});
                        this.pc.ontrack = this.handleTrackEvent;
                        this.pc.onicecandidate = this.handleIceCandidateEvent;
                    }

                    await this.pc.setRemoteDescription(offer);

                    const answer = await this.pc.createAnswer();
                    await this.pc.setLocalDescription(answer);


                    const message = { code: 'answer', data: { answer } };
                    this.sendMessage(message);
                } catch (error) {
                    console.error('Error handling offer:', error);
                }
            },
            async handleIceCandidate({ candidate }) {
                if (this.pc) {
                    await this.pc.addIceCandidate(new RTCIceCandidate(candidate));
                }
            },
            async sendAnswer() {
                if (this.pc) {
                    const description = await this.pc.createAnswer();
                    await this.pc.setLocalDescription(description);
                    const message = { code: 'answer', data: { answer: description } };
                    this.sendMessage(message);
                } else {
                    console.warn('RTCPeerConnection object not initialized');
                }
            },

            handleTrackEvent(event) {
                // 获取远程视频流
                let remoteStream = new MediaStream();
                if (event.track && event.track.kind && event.track.kind.toLowerCase() === "video") {
                    // 创建新的 MediaStream 对象
                    remoteStream = new MediaStream();
                }
                event.streams.forEach(stream => {
                    remoteStream.addTrack(stream.getTracks()[0]);
                });
                // 将远程视频流赋值给 remoteVideo 变量，并将其分配给 <video> 元素的 srcObject 属性
                this.remoteVideo = remoteStream;
                this.$refs.remoteVideo.srcObject = this.remoteVideo;
            },

            handleIceCandidateEvent(e) {
                if (e.candidate) {
                    const message = { code: 'icecandidate', data: { candidate: e.candidate } };
                    this.sendMessage(message);
                }
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