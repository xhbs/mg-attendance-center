/*
 * @Author: xiedan WX042
 * @since: 2020-08-28 09:48:32
 * @lastTime: 2021-01-30 15:22:40
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\task\Rules\components\NewDetailPage\index.jsx
 * @Description: 考勤规则管理---表单页
 */

import React from 'react';
import moment from 'moment';
import { Form, Input, Radio, message, Spin, DatePicker } from 'antd';
import AddPeriodPage from './AddPeriodPage';
import { addRule, updateRule, getRulesDetailById, checkRulesName, getExpressGroupId } from '@services/ruleService';
import { API_ERROR_CODE, DATE_ARR_MAP, REGEX_NAME, CITY, COUNTY } from '@constants';
import styles from '../../styles/index.less';

const { Item: FormItem } = Form;
const { Group, Button: RButton  } = Radio;
const { RangePicker } = DatePicker;

const formItemLayout = {
    labelCol: {
      xs: { span: 2 },
      sm: { span: 2 },
    },
    wrapperCol: {
      xs: { span: 16 },
      sm: { span: 16 },
    },
};
const tailFormItemLayout = {
    wrapperCol: {
      sm: {
        span: 12,
        offset: 2,
      },
    },
};

class NewDetailPage extends React.Component {
    state = {
        ruleName: '',
        cycleTimeObj: {},   // 循环时间的数据结构
        cycleType: '0',
        checkName: true,  // 名称验重  true--没有重复
        express_group_ids: [],  // 编辑时获取的express_group_ids，传参用
        loading: false,
        lifeCycle: '0', // 规则类型  0--日常  1--抽查
    }

    componentDidMount() {
        const { operationType } = this.props;
        if(operationType !== 'add') {
            this.getRulesDetail();
            // 获取固定的表达式分组id
            getExpressGroupId().then(res => {
                if(res && res.error_code === API_ERROR_CODE) {
                    this.setState({
                        express_group_ids: res.data && res.data[0] && [res.data[0].id] || [],
                    })
                }
            })
        }
    }

    // 清空数据
    initFormData = () => {
        this.setState({
            cycleTimeObj: {},
        })
    }

    // 获取详情
    getRulesDetail = () => {
        this.setState({
            loading: true,
        })
        const { editId } = this.props;
        getRulesDetailById(editId).then(res => {
            if(res && res.error_code === API_ERROR_CODE) {
                const { data } = res;
                const {cycle_day_list: cdList } = data;
                let cycleTimeObj = {};
                let temporaryTime = [];
                if(cdList && cdList.length > 0) {
                    cdList.forEach(item => {
                        /*** Tips：cycleTimeObj   {'1': [express_name: '', start: '', end: '', comm_express_id: '', express_group_ids: []], '2': []} */
                        const {cycle_date, start, end, express_name, comm_express_id, express_group_ids } = item;
                        cycleTimeObj[cycle_date] = [{
                            express_name: express_name || '',
                            start: start && moment(`2020-11-06 ${start}`) || '',
                            end: end && moment(`2020-11-06 ${end}`) || '',
                            // 以下为修改数据的时候要传的参数
                            comm_express_id: comm_express_id || '',
                            express_group_ids: express_group_ids || [],
                        }];
                    })
                }
                if(data.life_cycle === '1') {
                    temporaryTime = [data.start_time && moment(data.start_time) || '', data.end_time && moment(data.end_time) || '']
                }

                this.setState({
                    ruleName: data.rule_name || '',
                    cycleType: `${data.cycle_type}` || '',
                    cycleTimeObj,  // {'1':[], '2':[]}
                    lifeCycle: data.life_cycle || '',
                    temporaryTime,
                })
            }
        }).finally(() => {
            this.setState({
                loading: false,
            })
        })
    }

    // 获取子组件构造的数据  key代表双击之后需要删除key相应数据
    getDataFromChild = (obj, key = '') => {
        // obj结构为： { '1': [], '2':[] }
        // console.log('obj=====::', obj)
        const { cycleTimeObj } = this.state;
        const formObj = {...cycleTimeObj};
        if(key) {
            delete formObj[key];
            this.setState({
                cycleTimeObj: {...formObj},
            })
        } else {
            this.setState({
                cycleTimeObj: {...formObj, ...obj},
            })
        }
    }

    // 提交表单
    handleSubmit = (callback) => {
        const { form, onFormPageClose, operationType } = this.props;
        form.validateFieldsAndScroll((err, values) => {
            if (!err) {
                const validatorFlag = this.validator(values);
                if(validatorFlag) {
                    const params = this.getParams(values);
                    // console.log('params=====::', params);
                    const fun = operationType === 'add' ? addRule(params) : updateRule(params);
                    const text = operationType === 'add' ? `新建` : `修改`;
                    fun.then(res => {
                        if(res && res.error_code === API_ERROR_CODE) {
                            message.success(`${text}表达式成功`);
                            onFormPageClose()
                        }
                    }).finally(() => {
                        callback(true);
                    })
                } else {
                    callback(true);
                }
            } else {
                callback(true);
            }
        });
    };

    // 错误信息提示文字
    errorTextContent = (t1, left = '请设置', right = '的考勤时间及名称') => (
        <>
            {left}{<span style={{color: '#fc2344', padding: '0 4px'}}>{t1}</span>}{right}
        </>
    )

    // 手动校验
    validator = (values) => {
        const { cycle_type } = values;
        let flag0 = true;
        const { cycleTimeObj, checkName } = this.state;
        if(!checkName) {
            message.error('规则名称已存在，请重新输入');
            return false;
        }

        const objArr = Object.keys(cycleTimeObj);
        objArr.forEach(item=> {
            const index = parseInt(item) - 1;
            const error_text = `${DATE_ARR_MAP[values.cycle_type][index]}${cycle_type === '1' ? '号' : ''}`;
            const arr = cycleTimeObj[item];
            if(!arr || (arr && arr.length === 0)) {
                flag0 = false;
                message.error(this.errorTextContent(error_text));
            } else if(!(arr[0].express_name && arr[0].start && arr[0].end)) {
                flag0 = false;
                message.error(this.errorTextContent(error_text));
            } else {
                const composeStartTime = moment(arr[0].start);
                const composeEndTime = moment(arr[0].end);
                if (composeStartTime > composeEndTime) {
                    flag0 = false;
                    message.error(this.errorTextContent(error_text, '', '的考勤开始时间不能大于结束时间'));
                }
            }
        })
        return flag0;
    }

    // 构造请求参数
    getParams = (values) => {
        const { editId, operationType, isHigh, userRole } = this.props;
        const { cycleTimeObj } = this.state;
        let param = {
            id: operationType === 'add' ? '' : editId,
            rule_name: values.rule_name,
            cycle_type: parseInt(values.cycle_type, 10),
            type: isHigh ? 1 : 0,
            life_cycle: [CITY, COUNTY].includes(userRole) ? '1' : values.life_cycle,
        };

        // 抽查
        if(param.life_cycle === '1') {
            const [start, end] = values.temporary_time;
            param.start_time = start && start.startOf('day').valueOf();
            param.end_time = end && end.endOf('day').valueOf();
        }

        // 循环时间
        let cycleDayList = [];
        const cycleDayArr = Object.keys(cycleTimeObj);
        cycleDayArr.forEach(key => {
            /** Tips：arr: [{ express_name: '', start: '', end: '', comm_express_id: '', express_group_ids: [] }] */
            const arr = cycleTimeObj[key].map(i => {
                const a = {
                    express_name: i.express_name,
                    start: i.start && moment(i.start).format('HH:mm:ss'),
                    end: i.end && moment(i.end).format('HH:mm:ss'),
                    comm_express_id: i.comm_express_id || '',
                }
                // 新增：不传express_group_ids， 编辑：已有的数据取详情返回的express_group_ids,新加的数据则express_group_ids传空
                if(operationType !== 'add') {
                    // a.express_group_ids = i.express_group_ids || express_group_ids;
                    a.express_group_ids = i.express_group_ids || [];
                }
                return a;
            })
            cycleDayList.push({
                cycle_date: key,
                ...arr[0],
            })
        })
        param.cycle_day_list = cycleDayList;
        return param;
    }

    // 名称验重
    checkRulesNameReq = (name) => {
        const { editId, operationType } = this.props;
        checkRulesName({
            id: operationType === 'add' ? '' : editId,
            rule_name: name,
        }).then(res => {
            if(res && res.error_code === API_ERROR_CODE) {
                this.setState({
                    checkName: res.data,
                })
            }
        }).catch(()=>{
            this.setState({
                checkName: false,
            })
        })
    }

    // 改变循环方式，按周 or 按月
    onChangeCycleType = (e) => {
        this.setState({
            cycleType: e.target.value,
        })
        this.initFormData();
    }

    // 改变类型，日常 or 抽查
    changeTaskType = (e) => {
        // 请求考勤规则
        this.setState({
            lifeCycle: e.target.value,
        })
    }

    render() {
        const { form, operationType, userRole } = this.props;
        const { getFieldDecorator } = form;
        const { ruleName, cycleTimeObj, cycleType, loading, lifeCycle, temporaryTime } = this.state;

        return (
            <div className={styles.detailPageWapper} style={{ padding: 16 }}>
                <Spin spinning={loading}>
                <Form {...formItemLayout} onSubmit={this.handleSubmit}>
                    <FormItem label='规则名称'>
                        {getFieldDecorator('rule_name', {
                            initialValue: ruleName,
                            validateTrigger: 'onBlur', // 失去焦点时校验
                            rules: [{
                                required: true,
                                message: '规则名称不能为空',
                            },{
                                validator: (rules, val, callBack) => {
                                    if (val && val.length > 100) {
                                        callBack('规则名称不能超过100个字符');
                                    } else if (!(REGEX_NAME.test(val))) {
                                        callBack('请输入正确的规则名称');
                                    } else {
                                        callBack();
                                    }
                                },
                            }],
                        })(
                            <Input style={{width: 340}} onBlur={(v) => {
                                this.checkRulesNameReq(v.target.value);
                            }}  />
                        )}
                    </FormItem>
                    {
                        ![CITY, COUNTY].includes(userRole) && (
                            <FormItem label='规则类型'>
                                {getFieldDecorator('life_cycle', {
                                    initialValue: lifeCycle,
                                    rules: [{
                                        required: true,
                                        message: '请选择规则类型',
                                    }],
                                })(
                                    <Group buttonStyle="solid" onChange={this.changeTaskType}>
                                        <RButton value="0">日常考勤</RButton>
                                        <RButton value="1">抽查考勤</RButton>
                                    </Group>
                                )}
                            </FormItem>
                        )
                    }
                    {
                        ([CITY, COUNTY].includes(userRole) || lifeCycle === '1') && (
                            <FormItem label='规则有效期'>
                                {getFieldDecorator('temporary_time', {
                                    initialValue: temporaryTime || [],
                                    rules: [{
                                        required: true,
                                        message: '请选择规则有效期',
                                    },{
                                        validator: (rules, val, callBack) => {
                                            if (val && val.length === 2 && (!val[0] || !val[1])) {
                                                callBack('请选择完整的时间段');
                                            } else {
                                                callBack();
                                            }
                                        },
                                    }],
                                })(
                                    <RangePicker
                                        format="YYYY-MM-DD"
                                        getCalendarContainer={triggerNode => triggerNode.parentNode}
                                    />
                                )}
                            </FormItem>
                        )
                    }
                    <FormItem label='考勤周期'>
                        {getFieldDecorator('cycle_type', {
                            initialValue: cycleType || '0',
                            rules: [{
                                required: true,
                                message: '请选择考勤周期',
                            }],
                        })(
                            <Group onChange={this.onChangeCycleType}>
                                <Radio value="0">按周循环</Radio>
                                <Radio value="1">按月循环</Radio>
                            </Group>
                        )}
                    </FormItem>
                    <FormItem {...tailFormItemLayout}>
                        <AddPeriodPage
                            form={form}
                            cycleType={cycleType}
                            formResult={cycleTimeObj}
                            getDataFromChild={this.getDataFromChild}
                            initParentData={this.initFormData}
                            operationType={operationType}
                        />
                    </FormItem>
                </Form>
                </Spin>
            </div>
        )
    }
}

export default Form.create()(NewDetailPage);