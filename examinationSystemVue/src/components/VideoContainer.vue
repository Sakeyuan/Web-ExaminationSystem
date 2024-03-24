<template>
    <div id="video-container">
        <video v-for="(video, index) in videos" :key="index" :src="video.url" controls></video>
    </div>
</template>

<script>
    export default {
        props: ['videos']
    }
</script>

<style scoped>
    #video-container {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        grid-gap: 10px;
        margin-top: 20px;
    }

    video {
        width: 100%;
        height: auto;
        border: 1px solid #ccc;
    }
</style>