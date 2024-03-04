<template>
    <div>
        <font-awesome-icon class="star-icon" :class="{ 'filled': isFavorite }"
            :icon="isFavorite ? ['fas', 'star'] : ['far', 'star']" @click="toggleFavorite" />
    </div>
</template>

<script>
    export default {
        data() {
            return {
                isFavorite: false,
            };
        },
        methods: {
            toggleFavorite() {
                this.isFavorite = !this.isFavorite;
                if (this.isFavorite) {
                    this.$message.success('已收藏');
                } else {
                    this.$message.info('已取消收藏');
                }
            },
        },
    };
</script>

<style>
    .star-icon {
        font-size: 24px;
        cursor: pointer;
    }

    .star-icon.filled {
        color: red;
        position: relative;
    }
</style>