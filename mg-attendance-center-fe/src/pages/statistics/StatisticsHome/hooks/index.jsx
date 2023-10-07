

import { 
  getStatistics,
  getAllSchoolTop,
  getAllSchool, 
  getProvincialSchoolAttendance, 
  getProvincialSchool,
  getCityStatistics,
  getCitySchoolTop,
  getCityTerm,
  getCitySchool,
  getSchoolStatistics,getSchoolTop,getSchoolTerm,

} from '@/services/attendance.js'
export const useApi = () => {

  return {
    provincialStatistics: getStatistics,
    provincialSchoolTop: getAllSchoolTop,
    provincialAllSchool: getAllSchool,
    provincialSchoolAttendance: getProvincialSchoolAttendance,
    getCityStatistics,
    getCitySchoolTop,
    getCityTerm,
    getCitySchool,
    getSchoolStatistics,
    getSchoolTop,
    getSchoolTerm,
    getProvincialSchool,
  }
}