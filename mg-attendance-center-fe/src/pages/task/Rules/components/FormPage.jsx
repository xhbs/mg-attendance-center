/*
 * @Author: xiedan WX042
 * @since: 2020-08-28 09:48:32
 * @lastTime: 2020-11-30 17:37:45
 * @FilePath: \biz-scene-attendance-web\src\pages\task\Rules\components\FormPage.jsx
 * @Description: 考勤规则管理---表单页
 */

import React from 'react';
import moment from 'moment';
import { Form, Input, Radio, message } from 'antd';
import AddPeriodPage from './AddPeriodPage';
import AddTime from './AddTime';
import { addRule, updateRule, getRulesDetailById, checkRulesName } from '@services/ruleService';
import { API_ERROR_CODE, DATE_ARR_MAP, REGEX_NAME } from '@constants';
// import { attachedTime, excludeTime, cycleTimeObj } from './eg';

const { Item: FormItem } = Form;
const { Group, Button: RButton  } = Radio;

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

class FormPage extends React.Component {
    state = {
        ruleName: '',
        lifeCycle: '0',
        cycleTimeObj: {},   // 循环时间的数据结构
        cycleType: '',
        temporaryTime: [], // 临时时间段
        attachedTime: [], // 附属时间
        excludeTime: [], // 排除时间
        checkName: true,  // 名称验重  true--没有重复
    }

    componentDidMount() {
        const { operationType } = this.props;
        if(operationType !== 'add') {
            this.getRulesDetail()
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
        const { editId } = this.props;
        getRulesDetailById(editId).then(res => {
            if(res && res.error_code === API_ERROR_CODE) {
                const { data } = res;
                const {attach_date_list: adList, exclude_date_list: edList, cycle_day_list: cdList } = data;
                let cycleTimeObj = {};
                let temporaryTime = [];
                if(cdList) {
                    cdList.forEach(item => {
                        const {cycle_date, express_group_ids } = item;
                        cycleTimeObj[cycle_date] = express_group_ids;
                    })
                }
                if(data.life_cycle === '1') {
                    temporaryTime = [data.start_time && moment(data.start_time) || '', data.end_time && moment(data.end_time) || '']
                }

                this.setState({
                    ruleName: data.rule_name || '',
                    lifeCycle: data.life_cycle || '0',
                    cycleType: `${data.cycle_type}` || '',
                    temporaryTime,
                    cycleTimeObj,
                    attachedTime: adList || [],
                    excludeTime: edList || [],
                })
            }
        })
    }

    // 改变时间类型，临时 or 永久
    onChangeTimeType = (e) => {
        this.setState({
            lifeCycle: e.target.value,
            cycleTimeObj: {},
        })
    }

    // 获取子组件构造的数据  key代表双击之后需要删除key相应数据
    getDataFromChild = (obj, key = '') => {
        // obj结构为： { '1': [], '2':[] }
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
    errorTextContent = (t1, t2 = '表达式') => (
        <>
            请选择{<span style={{color: '#fc2344', padding: '0 4px'}}>{t1}</span>}的{t2}
        </>
    )

    // 手动校验
    validator = (values) => {
        const { cycle_type } = values;
        let flag0 = true;
        let flag1 = true;
        let flag2 = true;
        const { cycleTimeObj, attachedTime, excludeTime, checkName } = this.state;
        if(!checkName) {
            message.error('规则名称已存在，请重新输入');
            return false;
        }

        const objArr = Object.keys(cycleTimeObj);
        objArr.forEach(item => {
            const index = parseInt(item) - 1;
            const error_text = `${DATE_ARR_MAP[values.cycle_type][index]}${cycle_type === '1' ? '号' : ''}`;

            if(!cycleTimeObj[item] || (cycleTimeObj[item] && cycleTimeObj[item].length === 0)) {
                flag0 = false;
                message.error(this.errorTextContent(error_text));
            }
        })

        if(flag0 && attachedTime) {
            attachedTime.forEach(item => {
                if(item && !item.name) {
                    flag1 = false;
                    message.error(`请设置附属时间名称`);
                } else if(item && !(item.start_time && item.end_time)) {
                    flag1 = false;
                    message.error(this.errorTextContent(`${item.name}`, '时间段'));
                } else if((item && !item.express_group_ids) || (item && item.express_group_ids && item.express_group_ids.length === 0)) {
                    flag1 = false;
                    message.error(this.errorTextContent(`${item.name}`));
                }
            })
        }

        if(flag0 && flag1 && excludeTime) {
            excludeTime.forEach(item => {
                if(item && !item.name) {
                    flag2 = false;
                    message.error(`请设置排除时间名称`);
                } else if(item && !(item.start_time && item.end_time)) {
                    flag2 = false;
                    message.error(this.errorTextContent(`${item.name}`, '时间段'));
                }
            })
        }
        return flag0 && flag1 && flag2;
    }

    // 构造请求参数
    getParams = (values) => {
        const { editId, operationType } = this.props;
        const { cycleTimeObj, attachedTime, excludeTime } = this.state;
        let param = {
            id: operationType === 'add' ? '' : editId,
            life_cycle: values.life_cycle,
            rule_name: values.rule_name,
            cycle_type: parseInt(values.cycle_type, 10),
        };
        // 临时时间段
        if(values.life_cycle === '1') {
            const [start, end] = values.temporary_time;
            param.start_time = start && start.startOf('day').valueOf();
            param.end_time = end && end.endOf('day').valueOf();
        }

        // 循环时间
        let cycleDayList = [];
        const cycleDayArr = Object.keys(cycleTimeObj);
        cycleDayArr.forEach(key => {
            cycleDayList.push({
                cycle_date: key,
                express_group_ids: cycleTimeObj[key],
            })
        })
        param.cycle_day_list = cycleDayList;

        // 附属时间
        if(attachedTime && attachedTime.length > 0) {
            param.attach_date_list = attachedTime;
        }

        // 排除时间
        if(excludeTime && excludeTime.length > 0) {
            param.exclude_date_list = excludeTime;
        }

        return param;
    }

    // 附属时间
    getDataAttached = (obj) => {
        const newObj = [...obj];
        this.setState({
            attachedTime: newObj,
        })
    }

    // 排除时间
    getDataExclude = (obj) => {
        const newObj = [...obj];
        this.setState({
            excludeTime: newObj,
        })
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

    render() {
        const { form } = this.props;
        const { getFieldDecorator } = form;
        const { attachedTime, excludeTime, ruleName, lifeCycle, cycleTimeObj, cycleType, temporaryTime } = this.state;

        return (
            <div style={{ padding: 16 }}>
                <Form {...formItemLayout} onSubmit={this.handleSubmit}>
                    <FormItem label='规则名称'>
                        {getFieldDecorator('rule_name', {
                            initialValue: ruleName,
                            rules: [{
                                required: true,
                                message: '规则名称不能为空',
                            },{
                                validator: (rules, val, callBack) => {
                                    this.checkRulesNameReq(val);
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
                            <Input style={{width: 340}} />
                        )}
                    </FormItem>
                    <FormItem label='时间类型'>
                        {getFieldDecorator('life_cycle', {
                            initialValue: lifeCycle,
                            rules: [{
                                required: true,
                                message: '请选择时间类型',
                            }],
                        })(
                            <Group buttonStyle="solid" onChange={this.onChangeTimeType}>
                                <RButton value="0">永久</RButton>
                                <RButton value="1">临时</RButton>
                            </Group>
                        )}
                    </FormItem>

                    <FormItem {...tailFormItemLayout}>
                        {/* 时间类型 */}
                        <AddPeriodPage
                            form={form}
                            lifeCycle={lifeCycle}
                            cycleType={cycleType}
                            temporaryTime={temporaryTime}
                            formResult={cycleTimeObj}
                            getDataFromChild={this.getDataFromChild}
                            initParentData={this.initFormData}
                        />
                    </FormItem>

                    {/* 附属时间 */}
                    <FormItem {...tailFormItemLayout}>
                        <AddTime
                            key="attached"
                            hzLabelTitle='附属时间'
                            sendDataToParent={this.getDataAttached}
                            detailData={attachedTime || []}
                            hasTree
                        />
                    </FormItem>

                    {/* 排除时间 */}
                    <FormItem {...tailFormItemLayout}>
                        <AddTime
                            key="exclude"
                            dateFiled={['name', 'time']}
                            hzLabelTitle='排除时间'
                            sendDataToParent={this.getDataExclude}
                            detailData={excludeTime || []}
                        />
                    </FormItem>
                </Form>
            </div>
        )
    }
}

export default Form.create()(FormPage);