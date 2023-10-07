/*
 * @Author: heqiang 00621
 * @Date: 2020-09-01 10:20:56
 * @Description: 新增/编辑
 */
import React, { PureComponent } from 'react';
import moment from 'moment';
import { Form, Input, Select, DatePicker, Spin, message } from 'antd';
import Scrollbars from 'react-custom-scrollbars';
import { DATE_TYPE, ADJUST_TYPE } from '@constants';
import { getTaskListReq } from '@services/common';
import { queryAdjustDetailReq } from '@services/adjustService';
// import { disabledDate } from '@utils/utils';
import OrganizationPersonTree from '@components/organization_person';
// import Transfer from './transfer';
import styles from './detailModalContent.less';

const formItemLayout = {
    labelCol: { span: 2 },
    wrapperCol: { span: 8 },
};
const { Option } = Select;
const showTime = 'HH:mm:ss';
const ADJUST_TASK = 'adjust_task'; // 调整对象

class DetailModalContent extends PureComponent {
    state = {
        defaultKeys: [],
        taskList: [], // 考勤任务列表
        loading: false,
    };

    componentDidMount() {
        const { id } = this.props;
        const flag = id || id === 0;
        if (flag) {
            this.setState({ loading: true });
        }
        getTaskListReq().then(res => {
            if (res) {
                this.setState({ taskList: res.data });
                if (flag) {
                    this.getDetail(id);
                }
            } else {
                message.error('无法获取考勤任务列表');
            }
        }).catch().finally(() => this.setState({ loading: false }));
    }

    // 获取详情
    getDetail = id => {
        const { setFieldsValue } = this.props.form;
        queryAdjustDetailReq(id).then(res => {
            if (res) {
                const {
                    target_list,
                    type,
                    start_adjust_time,
                    end_adjust_time,
                    reason,
                    task_id,
                } = res.data || {};
                this.setState({ defaultKeys: target_list });
                setFieldsValue({
                    [ADJUST_TASK]: target_list,
                    "type": type,
                    "adjust_time": [moment(start_adjust_time), moment(end_adjust_time)],
                    "reason": reason,
                    'task_id': task_id,
                });
            } else {
                message.error('无法获取任务详情');
            }
        }).catch().finally(() => this.setState({ loading: false }));
    }

    treeDataChange = value => {
        const { setFieldsValue } = this.props.form;
        const { selectedKeys } = value || {};
        setFieldsValue({ [ADJUST_TASK]: selectedKeys });
    }

    // 二期切换穿梭框类型返回函数
    handleChangeTree = value => {
        console.log('handleChangeTree==',value);
        const { setFieldsValue } = this.props.form;
        setFieldsValue({ [ADJUST_TASK]: value });
    }

    render() {
        const { taskList, loading } = this.state;
        const { form: { getFieldDecorator, getFieldValue } } = this.props;
        return (
            <Scrollbars style={{ height: '100%' }}>
                <Spin spinning={loading}>
                    <Form {...formItemLayout} className={styles.detailFormStyles}>
                        <Form.Item label='调整类型'>
                            {getFieldDecorator('type', {
                                initialValue: ADJUST_TYPE[0].code,
                            })(<Select placeholder='请选择调整类型'>
                                {ADJUST_TYPE.map(item => (
                                    <Option value={item.code} key={item.code}>{item.name}</Option>
                                ))}
                            </Select>)}
                        </Form.Item>
                        <Form.Item label='调整时间'>
                            {getFieldDecorator('adjust_time', {
                                rules: [{ required: true, message: '请选择调整时间' }],
                            })(
                                <DatePicker.RangePicker
                                    showTime={{ format: showTime }}
                                    format={DATE_TYPE}
                                    style={{ width: '100%' }}
                                // disabledDate={disabledDate}
                                />
                            )}
                        </Form.Item>
                        <Form.Item label='考勤任务'>
                            {getFieldDecorator('task_id', {
                                rules: [{ required: true, message: '请选择考勤任务' }],
                            })(<Select placeholder='请选择考勤任务'>
                                {taskList.map(item => (
                                    <Option value={item.task_id} key={item.task_id}>{item.task_name}</Option>
                                ))}
                            </Select>)}
                        </Form.Item>
                        <Form.Item label='调整对象'>
                            {getFieldDecorator(ADJUST_TASK, {
                                rules: [{ required: true, message: '请选择调整对象' }],
                            })(
                                // <Transfer treeList={treeList} treeDataChange={this.treeDataChange} defaultKeys={defaultKeys} />
                                <OrganizationPersonTree handleChange={this.handleChangeTree} taskId={getFieldValue('task_id')} />
                            )
                            }
                        </Form.Item>
                        <Form.Item label='调整原因' style={{ marginTop: 5 }}>
                            {getFieldDecorator('reason', {
                                rules: [{ max: 100, message: '最多可输入100个字符' }],
                            })(<Input.TextArea
                                autosize={{ minRows: 5, maxRows: 6 }}
                                placeholder='请输入调整原因（100个字符以内）'
                            />)}
                        </Form.Item>
                    </Form>
                </Spin>
            </Scrollbars>
        )
    }
}

export default Form.create()(DetailModalContent);
