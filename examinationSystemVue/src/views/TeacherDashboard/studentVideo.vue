<template>
    <div id="app">
        <h4>学生考试视频</h4>
        <video-container :videos="videos"></video-container>
    </div>
</template>

<script>
    import VideoContainer from '@/components/VideoContainer.vue';
    import { serverIp, serverPort } from '../../../public/config';

    export default {
        name: 'Test',
        components: {
            VideoContainer
        },
        data() {
            return {
                videos: [],
                websocketUrl: `ws://${serverIp}:${serverPort}/`,
                socket: null
            };
        },
        created() {
            this.socket = new WebSocket(this.websocketUrl + 'video-stream/teacher/' + localStorage.getItem("userId"));
            this.socket.onmessage = (event) => {
                // 接收到视频数据时处理
                const videoData = event.data;
                // 将视频数据添加到 videos 数组中
                this.videos.push({ url: URL.createObjectURL(new Blob([videoData], { type: 'video/webm' })) });
            };
        },
        beforeDestroy() {
            this.socket.close();
            this.videos.forEach(video => URL.revokeObjectURL(video.url));
        }
    };
</script>

<style>
    /* 根据需要添加样式 */
</style>