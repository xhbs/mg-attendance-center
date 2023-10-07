/*
 * @Author: xiedan WX042
 * @since: 2020-12-08 10:12:52
 * @lastTime: 2020-12-29 16:27:32
 * @文件相对于项目的路径: \biz-scene-attendance-web\src\components\AttendDetailPage\mod\QuickAppeal.jsx
 * @Description: 快捷申诉
 */
import React, {useState} from 'react';
import { message, Modal } from 'antd';
import moment from 'moment';
import ReactScrollbar from 'react-custom-scrollbars';
import AppealForm from '@components/AppealForm';
import { addAppeal } from '@/services/appeal';
import { API_ERROR_CODE } from '@constants';

const QuickAppeal = (props) => {
    const { visible, onCancel, studentList } = props;
    const [HzFormRef, setHzFormRef] = useState(null);

    // 确认
    const handleAppeal = () => {
        const { handleSubmit } = HzFormRef;
        handleSubmit((values) => {
            if(values) {
                if(values) {
                    const { title, content, files, person_nos, date } = values;
                    const rArr = person_nos.map(x => x.user_code || x.person_no) || [];
                    const params = {
                        data: {
                            title,
                            content,
                            person_nos: rArr,
                            start_time: date[0] && moment(moment(date[0]).format('YYYY-MM-DD') + ' 00:00:00').valueOf(),
                            end_time: date[1] && moment(moment(date[1]).format('YYYY-MM-DD') + ' 23:59:59').set('millisecond', 999).valueOf(),
                            report: true, // 是否上报
                            files,
                        },
                    }
                    addAppeal(params).then(res => {
                        if(res && res.error_code === API_ERROR_CODE) {
                            message.success('操作成功');
                            onCancel()
                        } else {
                            message.error( res && res.message || '无法操作');
                        }
                    })
                }
            }
        })
    }

    return (
        <Modal
            title="快捷申诉"
            width={640}
            bodyStyle={{
                position: 'relative',
                height: 500,
            }}
            centered
            destroyOnClose
            maskClosable={false}
            visible={visible}
            onOk={handleAppeal}
            onCancel={onCancel}
        >
            <ReactScrollbar renderTrackHorizontal={() => <div />}>
                <AppealForm
                    type='modal'
                    studentList={studentList}
                    wrappedComponentRef={ref => setHzFormRef(ref)}
                    operationType='add'
                />
            </ReactScrollbar>
        </Modal>
    )
}

export default QuickAppeal;