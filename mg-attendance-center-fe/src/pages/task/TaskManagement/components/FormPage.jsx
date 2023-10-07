/*
 * @Author: xiedan WX042
 * @since: 2020-08-27 11:01:08
 * @lastTime: 2021-01-25 14:46:25
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\task\TaskManagement\components\FormPage.jsx
 * @Description: 考勤任务---任务管理---表单页面
 */
import React from 'react';
import { Form, Input, message, Select, Radio } from 'antd';
import CommonTransfer from '@components/CommonTransfer';
import { addTask, updateTask, getRulesEnum, getTaskDetailById, checkTaskName } from '@services/taskService';
import { API_ERROR_CODE, REGEX_NAME, CITY, COUNTY } from '@constants';
import styles from '../styles/index.less';

const { Item } = Form;
const { Option } = Select;
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

class FormPage extends React.Component {
    state = {
        taskAddrTree: [], // 已选的任务地点
        taskPersonTree: [], // 已选的任务对象
        formData: {
            taskName: '',
            taskType: '0',
        },
        defaultAddrTree: [], // 初始值
        defaultPersonTree: [],

        rulesEnumListDaily: [], // 考勤规则列表--日常
        rulesEnumListCheck: [], // 考勤规则列表--抽查
        selectedRuleDaily: '', // 已选的规则--日常
        selectedRuleCheck: '', // 已选的规则--抽查

        checkName: true,  // 名称验重  true--没有重复
    }

    componentDidMount() {
        const { operationType, userRole } = this.props;
        // 若为市县级， 则为抽查考勤
        if([CITY, COUNTY].includes(userRole)) {
            this.setState({
                formData: {
                    taskName: '',
                    taskType: '1',
                },
            })
            this.getRulesEnumList('1');
        } else {
            this.getRulesEnumList('0');
            this.getRulesEnumList('1');
        }
        if (operationType !== 'add') {
            this.getTaskDetailByIdReq()
        }
    }

    // 获取考勤规则列表
    getRulesEnumList = (value = '') => {
        const param = {
            life_cycle: value,
        }
        getRulesEnum(param).then(res => {
            if (res && res.error_code === API_ERROR_CODE) {
                const filed = value === '0' ? 'rulesEnumListDaily' : 'rulesEnumListCheck';
                this.setState({
                    [filed]: res.data || [],
                })
            }
        })
    }

    // 获取详情
    getTaskDetailByIdReq = () => {
        const { detailId } = this.props;
        getTaskDetailById(detailId).then((res) => {
            if (res && res.error_code === API_ERROR_CODE) {
                const { setFieldsValue } = this.props.form;
                const { data } = res;
                const { name, rule_id: ruleId, task_type, site_group_list: sgList, ob_group_list: ogList } = data || {};
                setFieldsValue({
                    task_name: name || '',
                })
                const filed = `${task_type}` === '0' ? 'taskRluesDaily' : 'taskRluesCheck';
                this.setState({
                    formData: {
                        taskName: name || '',
                        [filed]: ruleId || '',
                        taskType: `${task_type}` || '',
                    },
                    taskAddrTree: sgList || [],
                    taskPersonTree: ogList || [],
                    defaultAddrTree: sgList || [],
                    defaultPersonTree: ogList || [],
                })
            }
        })
    }

    // 获取穿梭框选中的数据
    getTreeData = (data, key) => {
        this.setState({
            [key]: data.selectedKeys || [],
        })
    }

    // 提交表单
    handleSubmit = (callback) => {
        const { form, onFormPageClose, operationType, detailId, userRole } = this.props;
        const { taskAddrTree, taskPersonTree, checkName } = this.state;
        form.validateFieldsAndScroll((err, values) => {
            if (!err) {
                if (!checkName) {
                    callback(true);
                    return message.error('任务名称已存在，请重新输入');
                }

                if (taskAddrTree.length === 0) {
                    callback(true);
                    return message.error('任务地点不能为空')
                }

                if (taskPersonTree.length === 0) {
                    callback(true);
                    return message.error('任务对象不能为空')
                }

                const params = {
                    id: operationType === 'add' ? '' : detailId,
                    name: values.task_name || '',
                    task_type: [CITY, COUNTY].includes(userRole) ? '1' : (values.task_type || ''),
                    rule_id: values.task_type === '0' ? values.task_rlues_daily : values.task_rlues_check,
                    site_group_list: taskAddrTree,
                    ob_group_list: taskPersonTree,
                };
                const fun = operationType === 'add' ? addTask(params) : updateTask(params);
                const text = operationType === 'add' ? `新建` : `修改`;
                fun.then(res => {
                    if (res && res.error_code === API_ERROR_CODE) {
                        message.success(`${text}任务成功`);
                        onFormPageClose()
                    }
                }).finally(() => {
                    callback(true);
                })
            } else {
                callback(true);
            }
        });
    };

    // 名称验重
    checkTaskNameReq = (name) => {
        const { detailId, operationType } = this.props;
        checkTaskName({
            id: operationType === 'add' ? undefined : detailId,
            task_name: name,
        }).then(res => {
            if (res && res.error_code === API_ERROR_CODE) {
                this.setState({ checkName: res.data })
            }
        }).catch(() => {
            this.setState({ checkName: false })
        })
    }

    // 选择考勤规则
    onSelectRules = (v) => {
        const { form } = this.props;
        const { getFieldValue } = form;
        const { formData } = this.state;
        const filed = getFieldValue('task_type') === '0' ? 'taskRluesDaily' : 'taskRluesCheck';
        this.setState({
            formData: {
                ...formData,
                [filed]: v || '',
            },
        })
    }

    render() {
        const { form, userRole } = this.props;
        const { getFieldDecorator, getFieldValue } = form;
        const { defaultAddrTree, defaultPersonTree, rulesEnumListDaily, rulesEnumListCheck, formData: { taskName, taskRluesDaily, taskRluesCheck, taskType } } = this.state;

        return (
            <div style={{ padding: 16 }}>
                <Form {...formItemLayout} onSubmit={this.handleSubmit}>
                    <Item label='任务名称'>
                        {getFieldDecorator('task_name', {
                            initialValue: taskName,
                            validateTrigger: 'onBlur', // 失去焦点时校验
                            rules: [{
                                required: true,
                                message: '任务名称不能为空',
                            }, {
                                validator: (rules, val, callBack) => {
                                    if (val && val.length > 100) {
                                        callBack('任务名称不能超过100个字符');
                                        return;
                                    }
                                    if (!(REGEX_NAME.test(val))) {
                                        callBack('请输入正确的任务名称');
                                        return;
                                    }
                                    callBack();
                                },
                            }],
                        })(
                            <Input style={{ width: 340 }} onBlur={(v) => {
                                this.checkTaskNameReq(v.target.value);
                            }} />
                        )}
                    </Item>
                    {/* 县市级只能创建抽查任务 */}
                    {
                        ![CITY, COUNTY].includes(userRole) && (
                            <Item label='任务类型'>
                                {getFieldDecorator('task_type', {
                                    initialValue: taskType,
                                    rules: [{
                                        required: true,
                                        message: '请选择任务类型',
                                    }],
                                })(
                                    <Group buttonStyle="solid" onChange={this.changeTaskType}>
                                        <RButton value="0">日常考勤</RButton>
                                        <RButton value="1">抽查考勤</RButton>
                                    </Group>
                                )}
                            </Item>
                        )
                    }
                    {
                        // 日常考勤
                        getFieldValue('task_type') === '0' ? (
                            <Item label='考勤规则'>
                                {getFieldDecorator('task_rlues_daily', {
                                    initialValue: taskRluesDaily,
                                    rules: [{
                                        required: true,
                                        message: '请选择考勤规则',
                                    }],
                                })(
                                    <Select
                                        showSearch
                                        style={{ width: 340 }}
                                        placeholder="请选择"
                                        optionFilterProp="children"
                                        getPopupContainer={triggerNode => triggerNode.parentNode}
                                        filterOption={(input, option) =>
                                            option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                                        }
                                        onSelect={this.onSelectRules}
                                    >
                                        {
                                            rulesEnumListDaily.map(item => {
                                                return (
                                                    <Option value={item.rule_id} key={item.rule_id}>{item.rule_name}</Option>
                                                )
                                            })
                                        }
                                    </Select>
                                )}
                            </Item>
                        ) : (
                            // 抽查考勤
                            <Item label='考勤规则'>
                                {getFieldDecorator('task_rlues_check', {
                                    initialValue: taskRluesCheck,
                                    rules: [{
                                        required: true,
                                        message: '请选择考勤规则',
                                    }],
                                })(
                                    <Select
                                        showSearch
                                        style={{ width: 340 }}
                                        placeholder="请选择"
                                        optionFilterProp="children"
                                        getPopupContainer={triggerNode => triggerNode.parentNode}
                                        filterOption={(input, option) =>
                                            option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                                        }
                                        onSelect={this.onSelectRules}
                                    >
                                        {
                                            rulesEnumListCheck.map(item => {
                                                return (
                                                    <Option value={item.rule_id} key={item.rule_id}>{item.rule_name}</Option>
                                                )
                                            })
                                        }
                                    </Select>
                                )}
                            </Item>
                        )
                    }
                    <Item label={<label className={styles.label}>任务地点</label>}>
                        <CommonTransfer
                            type='deviceGroup'
                            getTransferSelectedData={(data) => this.getTreeData(data, 'taskAddrTree')}
                            defaultKeys={defaultAddrTree}
                            treeTarget={null}
                            checkStrictly
                        />
                    </Item>
                    <Item label={<label className={styles.label}>任务对象</label>}>
                        <CommonTransfer
                            type='person'
                            getTransferSelectedData={(data) => this.getTreeData(data, 'taskPersonTree')}
                            defaultKeys={defaultPersonTree}
                            treeTarget={null}
                            checkStrictly
                        />
                    </Item>
                </Form>
            </div>
        )
    }
}

export default Form.create()(FormPage);