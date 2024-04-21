<template>
    <div>
        <el-button type="primary" @click="preReleaseDialogVisible = true">点击打开 Dialog</el-button>
        <el-dialog title="预发布日期" :visible.sync="preReleaseDialogVisible" width="30%">
            <div class="dateBlock">
                <el-date-picker v-model="preReleaseDate" type="datetime" placeholder="选择日期时间" align="right"
                    :picker-options="pickerOptions">
                </el-date-picker>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button type="primary" @click="onlyStore">只是存储</el-button>
                <div>
                    <el-button @click="preReleaseDialogVisible = false">取 消</el-button>
                    <el-button type="primary" @click="confirmPreRelease">确 定</el-button>
                </div>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        data() {
            return {
                preReleaseDate: '',
                preReleaseDialogVisible: false,
                // 选择开始时间大于等于当前时间
                pickerOptions: {
                    disabledDate(time) {
                        const dateTime = new Date();
                        const startDateTime = dateTime.setDate(dateTime.getDate() - 1);
                        const endDateTime = dateTime.setDate(dateTime.getDate() + 30000); //30000为当前日期之后多少天
                        return (
                            time.getTime() < new Date(startDateTime).getTime() ||
                            time.getTime() > new Date(endDateTime).getTime()
                        );
                    },
                    selectableRange:
                        //setMinutes,getMinutes限制分 , 年、月、日、时、分、秒同理,+1为数字区间
                        //重点就在这个set get, 渔给了, 鱼自己捕吧
                        this.parseTime(new Date().setMinutes(new Date().getMinutes() + 1), "{hh}:{ii}:{ss}") + "- 23:59:00",
                },
                //this.parseTime为封装的方法在下面贴出来了
            };
        },
        methods: {
            parseTime(time, pattern) {
                if (arguments.length === 0 || !time) {
                    return null
                }
                const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}'
                let date
                if (typeof time === 'object') {
                    date = time
                } else {
                    if ((typeof time === 'string') && (/^[0-9]+$/.test(time))) {
                        time = parseInt(time)
                    } else if (typeof time === 'string') {
                        time = time.replace(new RegExp(/-/gm), '/');
                    }
                    if ((typeof time === 'number') && (time.toString().length === 10)) {
                        time = time * 1000
                    }
                    date = new Date(time)
                }
                const formatObj = {
                    y: date.getFullYear(),
                    m: date.getMonth() + 1,
                    d: date.getDate(),
                    h: date.getHours(),
                    i: date.getMinutes(),
                    s: date.getSeconds(),
                    a: date.getDay()
                }
                const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
                    let value = formatObj[key]
                    // Note: getDay() returns 0 on Sunday
                    if (key === 'a') { return ['日', '一', '二', '三', '四', '五', '六'][value] }
                    if (result.length > 0 && value < 10) {
                        value = '0' + value
                    }
                    return value || 0
                })
                return time_str
            },
            onlyStore() {
                this.preReleaseDialogVisible = false;
            },
            confirmPreRelease() {
                // 在这里执行确认操作，比如提交数据等
                this.preReleaseDialogVisible = false;
                console.log("预发布日期：", this.preReleaseDate);
            }
        }
    };
</script>

<style scoped>
    .dateBlock {
        display: flex;
        justify-content: center;
    }

    .dialog-footer {
        display: flex;
        justify-content: space-between;
    }
</style>