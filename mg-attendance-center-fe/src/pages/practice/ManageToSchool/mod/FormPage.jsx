/*
 * @Author: xiedan WX042
 * @since: 2020-12-09 14:10:57
 * @Description: 实习申请表单页
 */

import React from 'react';
import { Form, Input, message, DatePicker, Spin } from 'antd';
import moment from 'moment';
// import CommonTransfer from '@components/CommonTransfer';
// import { addPracticeApply, updatePracticeApply } from '@/services/practiceManageServices';
import TransferTable from "@components/TransferTable";
import { API_ERROR_CODE, PHONE_REG, DATE_TYPE_DAY } from '@/constants';
import { getDetailById } from '@/services/practiceManageServices';

const { Item } = Form;
const { RangePicker } = DatePicker;
const formItemLayout = {
    labelCol: { xs: { span: 2 }, sm: { span: 2 } },
    wrapperCol: { xs: { span: 16 }, sm: { span: 16 } },
};

class FormPage extends React.Component {
    state = {
        loading: false, // 加载状态
        formData: {
            person_nos: [],
            date: [
                // moment('2020-01-01'),
                // moment('2020-12-10'),
            ],
            practice_company: '',
            company_contacts: '',
            contacts_phone: '',
        },
    }

    componentDidMount() {
        const { operationType, detailId } = this.props;
        if (operationType !== 'add' && detailId) {
            this.getTaskDetailByIdReq()
        }
    }

    // 获取详情
    getTaskDetailByIdReq = () => {
        const { detailId } = this.props;
        this.setState({ loading: true });
        getDetailById(detailId).then((res) => {
            const { error_code, data, message: msg } = res || {};
            if (error_code !== API_ERROR_CODE) {
                return message.error(msg || '无法获取详情数据');
            }
            const {
                start_time,
                end_time,
                practice_company,
                company_contacts,
                contacts_phone,
                persons,
            } = data || {};
            const start = start_time ? moment(start_time) : null;
            const end = end_time ? moment(end_time) : null;
            this.setState({
                formData: {
                    person_nos: persons || [],
                    date: [start, end],
                    practice_company,
                    company_contacts,
                    contacts_phone,
                },
            })
            // setFieldsValue({
            //     "date": [start, end],
            //     'practice_company': practice_company,
            //     'company_contacts': company_contacts,
            //     'contacts_phone': contacts_phone,
            //     'person_nos': ids,
            // })
        })
            .catch(() => message.error('无法获取详情数据'))
            .finally(() => this.setState({ loading: false }));
    }

    // 提交表单
    handleSubmit = (callback) => {
        const { form: { validateFieldsAndScroll } } = this.props;
        validateFieldsAndScroll((err, values) => {
            // console.log(values, 'values')
            if(!err) {
                callback(values, true)
            } else {
                callback(null, false);
            }
        });
    };

    // 实习学生校验
    validatorPerson = (rule, value, callback) => {
        if (value && value.length > 100) {
            callback('实习学生不能超过100个');
        } else {
            callback();
        }
    }

    render() {
        const { form } = this.props;
        const { getFieldDecorator } = form;
        const { loading, formData } = this.state;

        return (
            <div style={{ padding: 16 }}>
                <Spin spinning={loading}>
                    <Form {...formItemLayout} onSubmit={this.handleSubmit}>
                        <Item label='实习学生'>
                            {getFieldDecorator('person_nos', {
                                initialValue: formData.person_nos || '',
                                rules: [
                                    { required: true, message: '请选择实习学生' },
                                    { validator: this.validatorPerson },
                                ],
                            })(
                                <TransferTable defaultSelected={formData.person_nos} />
                            )}
                        </Item>
                        <Item label='实习时段'>
                            {getFieldDecorator('date', {
                                initialValue: formData.date || '',
                                rules: [{ required: true, message: '请选择实习时段' }],
                            })(
                                <RangePicker
                                    format={DATE_TYPE_DAY}
                                    getCalendarContainer={triggerNode => triggerNode.parentNode}
                                />
                            )}
                        </Item>
                        <Item label='实习单位'>
                            {getFieldDecorator('practice_company', {
                                initialValue: formData.practice_company || '',
                                validateTrigger: 'onBlur', // 失去焦点时校验
                                rules: [
                                    { required: true, message: '实习单位不能为空' },
                                    { max: 100, message: '最多可输入100个字符' }],
                            })(<Input style={{ width: 340 }} autoComplete='off' />)}
                        </Item>
                        <Item label='单位联系人'>
                            {getFieldDecorator('company_contacts', {
                                initialValue: formData.company_contacts || '',
                                rules: [
                                    { required: true, message: '单位联系人不能为空' },
                                    { max: 100, message: '最多可输入100个字符' }],
                            })(<Input style={{ width: 340 }} autoComplete='off' />)}
                        </Item>
                        <Item label='联系电话'>
                            {getFieldDecorator('contacts_phone', {
                                initialValue: formData.contacts_phone || '',
                                rules: [
                                    { required: true, message: '联系电话不能为空' },
                                    {
                                        validator: (rules, val, callBack) => {
                                            console.log(val, typeof val)
                                            if (val && !PHONE_REG.test(val)) {
                                                callBack('请输入正确的联系电话');
                                            } else {
                                                callBack();
                                            }
                                        },
                                    }],
                            })(<Input style={{ width: 340 }} autoComplete='off' />)}
                        </Item>
                    </Form>
                </Spin>
            </div>
        )
    }
}

export default Form.create()(FormPage);