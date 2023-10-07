/*
 * @Author: xiedan WX042
 * @since: 2020-10-29 09:12:42
 * @lastTime: 2020-11-06 17:19:17
 * @FilePath: \biz-scene-attendance-web\src\components\DetailContent\data.js
 * @Description: 模拟数据  type: imgandlist--图片和列表模式, img--仅有图片, list--仅有列表, process--流程记录状态
 */
export const data = [{
    type: 'imgandlist',
    label: '学生信息',
    url: 'http://192.168.108.117:14178/file/mg/2020/1010/20201010145816483.jpgs',
    status: '请假',
    data: [{
        type: 'text',
        label: '姓名',
        value: '李丽',
    },{
        type: 'text',
        label: '性别',
        value: '女',
    },{
        type: 'text',
        label: '学号',
        value: '2234234243',
    },{
        type: 'text',
        label: '入学时间',
        value: '2019-09-01',
    },{
        type: 'text',
        label: '所属组织',
        value: '大一1班<xx学校<xx县<xx市<xx县<xx市2324',
    }],
},{
    type: 'table',
    label: '考勤记录',
    columns: [{
        title: '规则',
        dataIndex: 'express_name',
        width: '35%',
    },
    {
        title: '打卡时间',
        dataIndex: 'pass_time',
        width: '40%',
    },
    {
        title: '结果',
        dataIndex: 'result',
        width: '25%',
    }],
    data: [{
        id: '1',
        express_name: '正常',
        pass_time: '2020-11-05 10:17:09',
        result: '正常',
    },{
        id: '2',
        express_name: '缺勤',
        pass_time: '2020-11-05 10:17:09',
        result: '缺勤',
    }],
},{
    type: 'img',
    label: '最近抓拍记录',
    data: [{
        url: 'http://192.168.111.51:9003/?dispositionvehicles/dn@1_0/1/16-3df0f18a-1fcdd',
        time: '2020-10-29 10:00:00',
        device: '学校大门抓拍机',
    },{
        url: 'http://192.168.111.51:9003/?namelists/dn@1_4/1/1-2f486ec-2046',
        time: '2020-10-28 09:00:00',
        device: '学校大门抓拍机',
    },{
        url: 'http://192.168.108.117:14178/file/mg/2020/1010/20201010145816483.jpg',
        time: '2020-10-27 09:30:00',
        device: '学校大门抓拍机学校大门抓拍机学校大门抓拍机学校大门抓拍机243325dsfaew',
    }],
},{
    type: 'list',
    label: '考勤信息',
    data: [{
        type: 'text',
        label: '考勤任务',
        value: '日常考勤',
    },{
        type: 'text',
        label: '考勤日期',
        value: '2020-10-29',
    },{
        type: 'text',
        label: '考勤记录',
        value: '无',
    }],
}]