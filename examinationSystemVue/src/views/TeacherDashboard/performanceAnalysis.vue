<template>
    <div>
        <div style="padding: 10px 0">
            <el-input style="width: 200px" placeholder="请输入试卷名称" suffix-icon="el-icon-search" class="mr-5"
                v-model="paperName" @keyup.enter.native="handleSearch">
            </el-input>
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        </div>
        <el-table :data="tableData" :header-cell-class-name="tableTitle">
            <el-table-column type="selection" width="55">
            </el-table-column>
            <el-table-column prop="paper.paperName" label="试卷名称"></el-table-column>
            <el-table-column prop="paper.paperCreateStamp" label="创建时间"></el-table-column>
            <el-table-column prop="paper.paperTotalTime" label="考试总时长">
                <template slot-scope="scope">
                    {{ paperTotalTimeInHours[scope.$index] }}
                </template>
            </el-table-column>
            <el-table-column label="发布的班级">
                <template slot-scope="scope">
                    <el-tooltip effect="dark">
                        <div>
                            <span v-for="classItem in scope.row.classList.slice(0, 2)" :key="classItem.classId">
                                {{ classItem.className }}
                            </span>
                            <span v-if="scope.row.classList.length > 2">...</span>
                        </div>
                        <div slot="content">
                            {{ scope.row.classList.map(c => c.className).join(', ') }}
                        </div>
                    </el-tooltip>
                </template>
            </el-table-column>
            <el-table-column prop="operate" label="操作">
                <template slot-scope="scope">
                    <el-button type="primary" size="mini" class="mr-2" @click="getGradeDetail(scope.row.paper.paperId)">
                        答题情况
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <div style="padding: 10px 0">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum"
                :page-sizes="[2, 5, 10, 20]" :page-size="pageSize" layout="total,sizes, prev, pager, next"
                :total="total">
            </el-pagination>
        </div>
        <el-dialog title="班级平均分" :visible.sync="centerDialogVisible" width="80%" center>
            <div id="class-pilar" style="width: 100%; height: 300px;"></div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="centerDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="centerDialogVisible = false">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>
<script>
    import '@/styles/global.css';
    import "@/styles/studentList.css";
    import * as echarts from 'echarts';
    export default {
        name: 'UserListView',
        data() {
            return {
                pageNum: 1,
                pageSize: 10,
                paperName: '',
                tableData: [],
                total: 0,
                tableTitle: 'tableTitle',
                oneDialogVisible: false,
                classList: [],
                centerDialogVisible: false,
                pilarData: null,
            }
        },
        computed: {
            // 计算属性：将 paperTotalTime 转换为小时
            paperTotalTimeInHours() {
                return this.tableData.map(item => {
                    // 将分钟转换为小时
                    const hours = Math.floor(item.paper.paperTotalTime / 60);
                    // 计算剩余的分钟数
                    const minutes = item.paper.paperTotalTime % 60;
                    // 返回格式化后的字符串
                    return `${hours}小时${minutes}分钟`;
                });
            },
        },
        watch: {
            paperName(newVal) {
                if (newVal.trim() === '') {
                    this.load();
                } else {
                    this.handleSearch();
                }
            },
        },
        methods: {
            getGradeDetail(paperId) {
                this.$api.examObj.getGradeDetail(paperId).then(res => {
                    if (res.code === 2000) {
                        this.pilarData = res.data;
                        // this.centerDialogVisible = true;
                        // this.$nextTick(() => {
                        //     this.createPillarChart('class-pilar', '班级平均分', this.pilarData, 'className', 'inClassNumbers');
                        // });
                        const allZeros = Object.values(this.pilarData).every(value => value === 0);
                        if (allZeros) {
                            this.$message.warning("暂无数据，请批改试卷再查看");
                        } else {
                            this.centerDialogVisible = true;
                            this.$nextTick(() => {
                                this.createPillarChart('class-pilar', '班级平均分', this.pilarData, 'className', 'inClassNumbers');
                            });
                        }
                    } else {
                        this.$message.error(res.message);
                    }
                })
            },
            createPillarChart(chartId, title, data, nameKey, valueKey) {
                const chartDom = document.getElementById(chartId);
                const myChart = echarts.init(chartDom);
                const classNames = Object.keys(data);
                const averageScores = Object.values(data);

                const option = {
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'cross',
                            crossStyle: {
                                color: '#999'
                            }
                        }
                    },
                    toolbox: {
                        feature: {
                            dataView: { show: true, readOnly: false },
                            magicType: { show: true, type: ['line', 'bar'] },
                            restore: { show: true },
                            saveAsImage: { show: true }
                        }
                    },
                    legend: {
                        data: ['柱状图', '折线图']
                    },
                    xAxis: [
                        {
                            type: 'category',
                            data: classNames,
                            axisPointer: {
                                type: 'shadow'
                            }
                        }
                    ],
                    yAxis: [
                        {
                            type: 'value',
                            name: '分',
                            min: 0,
                            max: Math.max(...averageScores) + 10,
                            interval: 5,
                            axisLabel: {
                                formatter: '{value}'
                            }
                        },
                        {
                            type: 'value',
                            name: '分',
                            min: 0,
                            max: Math.max(...averageScores) + 10,
                            interval: 5,
                            axisLabel: {
                                formatter: '{value}'
                            }
                        }
                    ],
                    series: [
                        {
                            name: '柱状图',
                            type: 'bar',
                            label: {
                                show: true,
                                position: 'top',
                                formatter: '{c} 分'
                            },
                            data: averageScores
                        },
                        {
                            name: '折线图',
                            type: 'line',
                            yAxisIndex: 1,
                            data: averageScores
                        }
                    ]
                };
                option && myChart.setOption(option);
            },
            handleSearch() {
                if (this.paperName.trim()) {
                    this.load();
                } else {
                    this.paperName = '';
                    this.load();
                }
            },
            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.load();
            },
            handleCurrentChange(pageNum) {
                this.pageNum = pageNum;
                this.load();
            },
            load() {
                const data = {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                    id: parseInt(localStorage.getItem('id')),
                    name: this.paperName,
                }
                this.$api.paperObj.getAllPaperListByTeacherId(data).then(res => {
                    if (res.code == 2000) {
                        this.tableData = res.data;
                        this.total = res.total;
                    }
                    else {
                        this.$message({
                            message: res.message,
                            type: 'error',
                        })
                    }
                })
            },
        },
        mounted() {
            this.load(); // 在组件挂载后加载数据
        }
    }
</script>