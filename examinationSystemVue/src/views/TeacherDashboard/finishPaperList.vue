<template>
    <div v-if="tableData && tableData.length > 0">
        <div style="padding: 10px 0">
            <el-select v-model="className" placeholder="班级" class="mr-5">
                <el-option v-for="item in options" :key="item.id" :label="item.label" :value="item.value">
                </el-option>
            </el-select>
            <el-input style="width: 200px" placeholder="请输入姓名" suffix-icon="el-icon-search" class="mr-5"
                v-model="studentName">
            </el-input>
            <el-input style="width: 200px" placeholder="请输入试卷名称" suffix-icon="el-icon-search" class="mr-5"
                v-model="paperName">
            </el-input>
            <el-button type="primary" icon="el-icon-search" @click="load">搜索</el-button>
        </div>

        <el-table :data="tableData" :header-cell-class-name="tableTitle">
            </el-table-column>
            <el-table-column prop="userRealName" label="姓名" align="center"></el-table-column>
            <el-table-column prop="studentNumber" label="学号" width="120px" align="center"></el-table-column>
            <el-table-column prop="className" label="所属班级" align="center"></el-table-column>
            <el-table-column prop="paperName" label="试卷名称" align="center"></el-table-column>
            <el-table-column prop="paperScore" label="试卷总分" align="center"></el-table-column>
            <el-table-column :label="'考试时长'" align="center">
                <template slot-scope="scope">
                    {{ paperTotalTimeInHours[scope.$index] }}
                </template>
            </el-table-column>

            <el-table-column prop="paperCreateStamp" label="创建时间" align="center"></el-table-column>
            <el-table-column prop="paperStartTime" label="开始时间" align="center">
                <template slot-scope="scope">
                    {{ scope.row.paperStartTime || '——' }}
                </template>
            </el-table-column>

            <el-table-column prop="paperEndTime" label="结束时间" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.paperStartTime ? scope.row.paperStartTime : '——' }}</span>
                </template>
            </el-table-column>

            <el-table-column prop="expire" label="是否过期">
                <template slot-scope="scope">
                    <span>{{ scope.row.expire ? '已过期' : '未过期' }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="operate" label="操作" align="center">
                <template slot-scope="scope">
                    <el-button type="primary" @click="getHadCorrectPaper(scope.row.paperId,scope.row.studentId)">
                        详情</el-button>
                </template>
            </el-table-column>
        </el-table>

        <div style="padding: 10px 0">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum"
                :page-sizes="[2, 5, 10, 20]" :page-size="pageSize" layout="total,sizes, prev, pager, next"
                :total="total">
            </el-pagination>
        </div>
    </div>
    <el-empty v-else description="已经批改完成啦"></el-empty>
</template>
<script>
    import '@/styles/global.css';
    import "@/styles/studentList.css";

    export default {
        name: 'UserListView',
        data() {
            return {
                pageNum: 1,
                pageSize: 10,
                userName: '',
                tableData: [],
                total: 0,
                tableTitle: 'tableTitle',
                userIdToDelete: 0,
                className: '',
                paperName: '',
                studentName: '',
                options: [{
                    id: 0,
                    value: '1',
                    label: '无'
                }],
            }
        },
        computed: {
            paperTotalTimeInHours() {
                return this.tableData.map(item => {
                    const hours = Math.floor(item.paperTotalTime / 60);
                    const minutes = item.paperTotalTime % 60;
                    return `${hours}小时${minutes}分钟`;
                });
            },
        },
        mounted() {
            this.load();
            this.loadClass();
        },
        watch: {
            className(newVal) {
                console.log('newVal:', newVal);
                if (newVal.trim() === '') {
                    this.load();
                } else {
                    this.handleSearch(); // 执行搜索操作
                }
            },
        },
        methods: {
            handleSearch() {
                if (this.className.trim()) {
                    this.load();
                } else {
                    this.className = '';
                    this.load();
                }
            },
            loadClass() {
                this.$api.classObj.getAllClassByTeacherId(parseInt(localStorage.getItem('id'))).then(res => {
                    if (res.code == 2000) {
                        res.data.forEach(item => {
                            this.options.push({ id: item.classId, value: item.className, label: item.className });
                        });
                    }
                });
            },
            load() {
                try {
                    const data = {
                        pageNum: this.pageNum,
                        pageSize: this.pageSize,
                        id: localStorage.getItem('id'),
                        className: this.className,
                        studentName: this.studentName,
                        paperName: this.paperName,
                    };
                    const res = this.$api.paperObj.getFinishCorrectPaper(data).then(res => {
                        if (res.code === 2000) {
                            this.tableData = res.data;
                            this.total = res.total;
                        } else {
                            this.$message({
                                type: 'error',
                                message: res.message
                            });
                        }
                    });
                } catch (err) {
                    this.$message({
                        type: 'error',
                        message: err.message
                    });
                }
            },
            getHadCorrectPaper(paperId, studentId) {
                this.$router.push({
                    path: "/teacher/hadCorrectPaper",
                    query: {
                        paperId: paperId,
                        studentId: studentId
                    }
                });
            },
            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.load();
            },
            handleCurrentChange(pageNum) {
                this.pageNum = pageNum;
                this.load();
            },
        },
    }
</script>