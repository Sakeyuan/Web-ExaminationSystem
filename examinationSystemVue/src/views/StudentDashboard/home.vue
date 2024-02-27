<template>
    <div>
        <div style="background-color: white; margin-top: 20px;">
            <el-carousel :interval="6000" height="200px" type="card">
                <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
                    <img :src="item.image" :alt="`Slide ${index + 1}`" style="width: 100%; height: 100%;" />
                </el-carousel-item>
            </el-carousel>
        </div>
        <div style="margin-top: 50px">
            <el-row :gutter="10">
                <el-col v-for="(card, index) in cardItems" :key="index" :span="8">
                    <el-card :style="`color: ${card.color}`" shadow="hover">
                        <div>{{ card.title }}</div>
                        <div style="padding: 10px 0; text-align: center; font-weight: bold;">
                            <span>{{ getCardValue(card) }}</span>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script>
    export default {
        data() {
            return {
                numbers: null,
                carouselItems: Array.from({ length: 10 }, (_, index) => ({
                    image: `https://picsum.photos/460/200?random=${index + 1}`,
                })),
                cardItems: [
                    { title: '试卷总数', color: '#409EFF', value: null },
                    { title: '未完成试卷', color: '#E6A23C', value: null },
                    { title: '已批改试卷', color: '#67C23A', value: null },
                ],
            };
        },
        async created() {
            await this.load();
        },
        methods: {
            async load() {
                const res = await this.$api.paperObj.getStudentHomeData(parseInt(localStorage.getItem('id')));
                if (res.code === 2000) {
                    this.numbers = res.data;
                    this.cardItems[0].value = this.numbers.paperTotalNumbers;
                    this.cardItems[1].value = this.numbers.unFinishNumbers;
                    this.cardItems[2].value = this.numbers.hadCorrectNumbers;
                }
            },
            getCardValue(card) {
                return card.value !== null ? card.value : 'Loading...';
            },
        },
    };
</script>