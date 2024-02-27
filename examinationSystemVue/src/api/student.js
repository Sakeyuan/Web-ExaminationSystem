import http from "@/utils/http";

const STUDENT_STR = '/student'

const studentObj = {
    getAllStudentInfoPage(params) {
        return http.get(`${STUDENT_STR}/page`, params)
    },
    removeStudentFromClass(params) {
        return http.delete(`${STUDENT_STR}/removeStudentFromClass/` + params)
    },
    batchRemoveStudentFromClass(params) {
        return http.post(`${STUDENT_STR}/batchRemoveStudentFromClass/`, params)
    },

}

export default studentObj;
