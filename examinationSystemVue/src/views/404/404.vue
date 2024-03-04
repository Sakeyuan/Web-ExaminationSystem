<template>
    <div class="not-found-container">
        <div class="not-found-content">
            <h1 class="title">404</h1>
            <p class="message">哎呀！您所查找的页面不存在。</p>
            <router-link to="/">返回首页</router-link>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'NotFound',
    };
</script>

<style scoped>
    /* 样式可以根据需要进行调整 */
    .not-found-container {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100vh;
        background-color: #f5f5f5;
    }

    .not-found-content {
        text-align: center;
    }

    .title {
        font-size: 100px;
        color: #ff7c80;
        margin-bottom: 20px;
        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
        /* 为404字体添加阴影 */
        transform: skew(-10deg);
        /* 添加斜体效果 */
        position: relative;
    }

    .title::before {
        content: '';
        /* 使用伪元素创建倒影 */
        position: absolute;
        top: 100%;
        left: 0;
        width: 100%;
        height: 20px;
        background: linear-gradient(to bottom, rgba(255, 255, 255, 0), rgba(255, 255, 255, 1));
        /* 使用渐变创建倒影效果 */
    }

    .message {
        font-size: 24px;
        margin-top: 20px;
    }
</style>