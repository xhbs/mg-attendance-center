/* eslint-disable react-hooks/exhaustive-deps */
/*
 * @Author: xiedan WX042
 * @since: 2020-08-31 09:26:36
 * @lastTime: 2020-11-17 15:02:49
 * @FilePath: \biz-scene-attendance-web\src\pages\task\Rules\components\AddPeriodPage.jsx
 * @Description: 添加时间段组件（通用时间）
 */
import React, { useState, useEffect, useRef } from 'react';
import { cloneDeep } from 'lodash';
import PropsType from 'prop-types';
import { Form, Radio, DatePicker } from 'antd';
import CommonTransfer from '@components/CommonTransfer';
import { disabledDate } from '@utils/utils';
import { DATE_ARR_MAP } from '@constants';
import CycleDate from './CycleDate';

const FormItem = Form.Item;
const { Group  } = Radio;
const { RangePicker } = DatePicker;

const AddPeriodPage = (props) => {
    const { form, getDataFromChild, formResult, lifeCycle, initParentData, temporaryTime, cycleType } = props;
    const { getFieldDecorator, getFieldValue, setFieldsValue } = form;

    const [cycleDate, setCycleDate] = useState([]);  // 已选的循环的日期 or 星期
    const [currentCycleDate, setCurrentCycleDate] = useState('');  // 当前点击的日期 or 星期
    const [detailDataGroup, setDetailDataGroup] = useState({});  // 详情  { '1': [], '2': [] }

    const lifeRef = useRef({lifeCycle});

    // 清空当前组件数据
    const initData = () => {
        setFieldsValue({'cycle_date': ''});
        setCycleDate([]);
        setCurrentCycleDate('');
        setDetailDataGroup({});
    }

    // 改变循环方式，按周 or 按月
    const onChangeCycleType = (e) => {
        initData()
        if(initParentData) {
            initParentData();
        }
    }

    // 选择循环 日期 or 星期
    const onSelectDate = (value) => {
        if(value) {
            const newCycleDate = [...cycleDate];
            // 判断当前所选日期是否被选择过
            if(!(newCycleDate.includes(value))) {
                newCycleDate.push(value)
                setCycleDate(newCycleDate);
            }
            const newDetail = cloneDeep(detailDataGroup);
            const currentDetail = newDetail[value];
            // 如果当前所选日期没有选择表达式，则复制上一个日期对应的表达式
            if(!currentDetail || (currentDetail && !currentDetail.length === 0)) {
                newDetail[value] = newDetail[currentCycleDate] || [];
                setDetailDataGroup(newDetail);
                getDataFromChild(newDetail);
            }

            setCurrentCycleDate(value);
        }
    }

    // 日期 or 星期的双击事件
    const onDblClick = (value) => {
        if(value) {
            // 1、如果是清空当前正在查看中的日期，则需要清空记录
            if(value === currentCycleDate) {
                setCurrentCycleDate('');
            }
            // 2、判断当前所选日期是否被选择过
            let newCycleDate = [...cycleDate];
            if(newCycleDate.includes(value)) {
                // 2-1、从已选记录里面删除
                newCycleDate = newCycleDate.filter(v => v !== value);
                setCycleDate(newCycleDate);

                // 2-2、去掉表单的值
                setFieldsValue({'cycle_date': newCycleDate});

                // 2-3、去掉对应的详情数据
                const newDetail = cloneDeep(detailDataGroup);
                delete newDetail[value];
                setDetailDataGroup(newDetail);
                getDataFromChild(newDetail, value);
            }
        }
    }

    // 选择资源树
    const getTreeDataToItem = (data, total) => {
        // 为当前选择项设置tree
        if(currentCycleDate) {
            const newDetail = cloneDeep(detailDataGroup);
            const arr = data.selectedKeys || [];
            newDetail[currentCycleDate] = arr;
            getDataFromChild(newDetail);
            setDetailDataGroup(newDetail);
        }
    }

    useEffect(() => {
        const {lifeCycle: preL} = lifeRef.current;
        if(lifeCycle !== preL) {
            initData();
            lifeRef.current = {lifeCycle};
        }
        setDetailDataGroup(formResult);
        setCycleDate(Object.keys(formResult));
    }, [formResult, lifeCycle])

    return (
        <>
            {
                lifeCycle === '1' && (
                    <>
                        <div>临时时段</div>
                        <FormItem>
                            {getFieldDecorator('temporary_time', {
                                initialValue: temporaryTime || [],
                                rules: [{
                                    required: true,
                                    message: '请选择临时时间段',
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
                                    disabledDate={disabledDate}
                                    format="YYYY-MM-DD"
                                    getCalendarContainer={triggerNode => triggerNode.parentNode}
                                />
                            )}
                        </FormItem>
                    </>
                )
            }
            <FormItem>
                {getFieldDecorator('cycle_type', {
                    initialValue: cycleType || '0',
                })(
                    <Group onChange={onChangeCycleType}>
                        <Radio value="0">按周循环</Radio>
                        <Radio value="1">按月循环</Radio>
                    </Group>
                )}
            </FormItem>
            <FormItem>
                {getFieldDecorator('cycle_date', {
                    initialValue: cycleDate,
                    // validateTrigger: ['onSubmit', 'onBlur', ''],
                    rules: [{
                        required: true,
                        message: `请选择${getFieldValue('cycle_type') === '0' ? '星期' : '日期'}`,
                    }],
                })(
                    <CycleDate
                        data={DATE_ARR_MAP[getFieldValue('cycle_type')]}
                        onChange={onSelectDate}
                        onDblClick={onDblClick}
                        cycleDateArr={cycleDate}
                        // lifeCycle={lifeCycle}
                        // cycleType={getFieldValue('cycle_type')}
                        currentCycleDate={currentCycleDate}
                    />
                )} 
            </FormItem>
            <FormItem>
                <div>表达式选择</div>
                <CommonTransfer
                    type='expression'
                    mode='tree'
                    viewMode={!currentCycleDate}
                    getTransferSelectedData={getTreeDataToItem}
                    defaultKeys={detailDataGroup[currentCycleDate] || []}
                />
            </FormItem>
        </>
    )
}

AddPeriodPage.propTpyes = {
    form: PropsType.object.isRequired, // form
    formResult: PropsType.object,  // 外部传入的时间详情 类似：{'1': [], '2': []}
    lifeCycle: PropsType.string.isRequired,   //  1--临时 or 0--永久
    cycleType: PropsType.string, // 时间循环的类型
    temporaryTime: PropsType.array, // 临时时间段
    getDataFromChild: PropsType.func.isRequired,  // 向父级传递数据结构
    initParentData: PropsType.func,  // 清空父级的数据
};

AddPeriodPage.defaultProps = {
    detailDataGroup: {},
    initParentData: () => {},
    cycleType: '',
    temporaryTime: [],
};

export default AddPeriodPage;