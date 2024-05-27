<template>
    <div class="home-container">
        <div>
            <el-row :gutter="20">
                <el-col :span="6">
                    <el-card>
                        <div class="card-content">
                            <font-awesome-icon icon="users" :size="iconSize" class="icon-style" />
                            <div class="card-content-number">
                                <span>学生数量</span>
                                <span class="card-number">{{ headerNumbers ? headerNumbers.studentNumbers :
                                    'Loading...'}}</span>
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card>
                        <div class="card-content">
                            <font-awesome-icon icon="users-rectangle" :size="iconSize" class="icon-style" />
                            <div class="card-content-number">
                                <span>班级数量</span>
                                <span class="card-number">{{ headerNumbers ? headerNumbers.classNumbers : 'Loading...'
                                    }}</span>
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card>
                        <div class="card-content">
                            <font-awesome-icon icon="paperclip" :size="iconSize" class="icon-style" />
                            <div class="card-content-number">
                                <span>题目数量</span>
                                <span class="card-number">{{ headerNumbers ? headerNumbers.titleNumbers : 'Loading...'
                                    }}</span>
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card>
                        <div class="card-content">
                            <font-awesome-icon icon="pen-to-square" :size="iconSize" class="icon-style" />
                            <div class="card-content-number">
                                <span>试卷数量</span>
                                <span class="card-number">{{ headerNumbers ? headerNumbers.paperNumbers : 'Loading...'
                                    }}</span>
                            </div>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </div>
        <div>
            <el-row>
                <el-col :span="12">
                    <div id="class-pie" class="class-pie-style"></div>
                </el-col>
                <el-col :span="12">
                    <div id="paper-pie" class="class-pie-style"></div>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script>
    import * as echarts from 'echarts';
    export default {
        name: "home",
        data() {
            return {
                iconSize: '2x',
                headerNumbers: {
                    studentNumbers: 0,
                    classNumbers: 0,
                    titleNumbers: 0,
                    paperNumbers: 0,
                },
                pipeData: null,
            }
        },
        mounted() {
            this.load();
        },
        methods: {
            load() {
                this.$api.teacherObj.getHomeData().then(res => {
                    if (res.data) {
                        this.headerNumbers = res.data;
                        this.pipeData = res.other;
                        if (this.pipeData && this.pipeData.length >= 2) {
                            const classData = this.pipeData[0];
                            const paperData = this.pipeData[1];
                            this.$nextTick(() => {
                                this.createPieChart('class-pie', '班级人数分布', classData, 'className', 'inClassNumbers');
                                this.createPieChart('paper-pie', '班级试卷分布', paperData, 'className', 'paperCounts');
                            });
                        }
                    }
                })
            },
            createPieChart(chartId, title, data, nameKey, valueKey) {
                const chartDom = document.getElementById(chartId);
                const myChart = echarts.init(chartDom);
                const option = {
                    title: {
                        text: title,
                        left: 'center'
                    },
                    tooltip: {
                        trigger: 'item'
                    },
                    series: [
                        {
                            name: '数量',
                            type: 'pie',
                            radius: '60%',
                            data: data.map(item => ({
                                value: item[valueKey],
                                name: item[nameKey]
                            })),
                            emphasis: {
                                itemStyle: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }
                    ]
                };
                option && myChart.setOption(option);
            },
        }
    }
</script>

<style scoped>
    .home-container {
        height: 100%;
        width: 100%;
    }

    .icon-style {
        padding: 12px
    }

    .card-content {
        display: flex;
    }

    .card-content-number {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 8px
    }

    .card-number {
        margin-top: 5px;
        font-weight: bold;
    }

    .class-pie-style {
        padding: 20px;
        width: 530px;
        height: 410px;
        margin-top: 10px;
    }
</style>