import http from "@/utils/http";

const TEACHER_STR = '/teacher';

const teacherObj = {
    importStudentInfo(params) {
        return http.post(`${TEACHER_STR}/importStudentInfo`, params)
    },
    invite(params) {
        return http.post(`${TEACHER_STR}/invite`, params)
    },
    singleInvite(params) { 
        return http.post(`${TEACHER_STR}/singleInvite`, params)
    },
    getHomeData() {
        return http.get(`${TEACHER_STR}/getHomeData`)
    },
    getOnlineStudents(params) {
        return http.get(`${TEACHER_STR}/getOnlineStudents/` + params);
    }

}

export default teacherObj;
