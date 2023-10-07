
/*
 * @Author: xiedan WX042
 * @since: 2020-08-31 09:26:36
 * @lastTime: 2021-07-12 11:26:54
 * @FilePath: \biz-scene-attendance-web\src\pages\task\Rules\components\NewDetailPage\AddPeriodPage.jsx
 * @Description: 添加时间段组件（通用时间）
 */
import React, { useState, useEffect, useRef } from 'react';
import { cloneDeep } from 'lodash';
import PropsType from 'prop-types';
import moment from 'moment';
import { Form, Button, Modal, Radio, message } from 'antd';
import { EditTable } from '@hz-components/react-base';
import { DATE_ARR_MAP } from '@constants';
import { editTableColums } from '../../utils/tableOptions';
import CycleDate from '../CycleDate';
import styles from '../../styles/index.less';

const FormItem = Form.Item;

// 系统默认时间列表
const sysDefaultTimeList = [{
    name: '上午考勤(08:00:00~10:00:00)',
    code: '08:00:00~10:00:00',
},{
    name: '中午考勤(12:00:00~14:00:00)',
    code: '12:00:00~14:00:00',
},{
    name: '下午考勤(16:00:00~18:00:00)',
    code: '16:00:00~18:00:00',
},{
    name: '晚上考勤(20:00:00~22:00:00)',
    code: '20:00:00~22:00:00',
}]

const AddPeriodPage = (props) => {
    const { form, getDataFromChild, formResult, cycleType, operationType } = props;
    const { getFieldDecorator, setFieldsValue } = form;

    const [cycleDate, setCycleDate] = useState([]);  // 已选的循环的日期 or 星期
    const [currentCycleDate, setCurrentCycleDate] = useState('');  // 当前点击的日期 or 星期
    const [detailDataGroup, setDetailDataGroup] = useState({});  // 详情  { '1': [], '2': [] }
    const [editTableRef, setEditTableRef] = useState(null);
    const [timeModalVis, setTimeModalVis] = useState(false);
    const [systemTime, setSystemTime] = useState(sysDefaultTimeList[0].code); // 选择的系统默认时间
    const [isEdit, setIsEdit] = useState(true); // 可编辑表格每行是否可编辑，选择系统默认时间则不可编辑
    const [isfirst, setIsfirst] = useState( operationType!=='add' );   // 用于记录点击编辑时的初次加载，true--初次加载， false--非初次加载

    const typeRef = useRef({cycleType})

    // 清空当前组件数据
    const initData = () => {
        setFieldsValue({ 'cycle_date': '' });
        setCycleDate([]);
        setCurrentCycleDate('');
        setDetailDataGroup({});
        setIsEdit(true);
    }

    // 选择循环 日期 or 星期
    const onSelectDate = (value) => {
        if (value) {
            const newCycleDate = [...cycleDate];
            // 判断当前所选日期是否被选择过
            if (!(newCycleDate.includes(value))) {
                newCycleDate.push(value)
                setCycleDate(newCycleDate);
            }
            const newDetail = cloneDeep(detailDataGroup); // {'1':[]}
            const currentDetail = newDetail[value]; // []
            // 如果当前所选日期没有选择时间段
            if (!currentDetail || (currentDetail && !currentDetail.length === 0)) {
            //     newDetail[value] = newDetail[currentCycleDate] || [];
                newDetail[value] = [];
                setDetailDataGroup(newDetail);
                getDataFromChild(newDetail);
            }
            setIsEdit(currentDetail && currentDetail[0] && currentDetail[0].isEdit !== 0);
            // 设置当前选择的日期
            setCurrentCycleDate(value);
        }
    }

    // 日期 or 星期的双击事件
    const onDblClick = (value) => {
        if (value) {
            // 1、如果是清空当前正在查看中的日期，则需要清空记录
            if (value === currentCycleDate) {
                setCurrentCycleDate('');
            }
            // 2、判断当前所选日期是否被选择过
            let newCycleDate = [...cycleDate];
            if (newCycleDate.includes(value)) {
                // 2-1、从已选记录里面删除
                newCycleDate = newCycleDate.filter(v => v !== value);
                setCycleDate(newCycleDate);

                // 2-2、去掉表单的值
                setFieldsValue({ 'cycle_date': newCycleDate });

                // 2-3、去掉对应的详情数据
                const newDetail = cloneDeep(detailDataGroup);
                delete newDetail[value];
                setDetailDataGroup(newDetail);
                getDataFromChild(newDetail, value);
            }
        }
    }

    // 添加表格行
    const addTableRow = (dataSource) => {
        const newDetail = cloneDeep(detailDataGroup);
        // 设置当前点击的日期的数据
        newDetail[currentCycleDate] = dataSource;
        setDetailDataGroup(newDetail);
        getDataFromChild(newDetail);
    }

    // 打开默认时段选择弹框
    const openTimeModal = () => {
        setTimeModalVis(true);
    }

    // 点击确认按钮，关闭默认时段选择弹框并给表格赋值
    const closeTimeModal = () => {
        const startTime = systemTime.split('~')[0];
        const endTime = systemTime.split('~')[1];
        const newDetail = cloneDeep(detailDataGroup); // {'1': []}
        const data = cloneDeep(newDetail[currentCycleDate]); // []

        // data只有一条数据，即每天只能添加一条规则
        const oneData = data[0];
        oneData.start = moment(`2020-11-06 ${startTime}`);
        oneData.end = moment(`2020-11-06 ${endTime}`);
        // 加入是否可编辑的标志
        oneData.isEdit = 0;
        data.pop();
        data.push(oneData);
        // 设置当前点击的日期的数据
        newDetail[currentCycleDate] = data;
        // 用系统默认时间填充时，不可编辑
        setIsEdit(false);
        setDetailDataGroup(newDetail);
        getDataFromChild(newDetail);
        setTimeModalVis(false);
    }

    // 选择系统默认时间
    const selectSystemTime = (e) => {
        setSystemTime(e.target.value);
    }

    useEffect(() => {
        // console.log('formResult=====::', formResult)
        setDetailDataGroup(formResult);
        setCycleDate(Object.keys(formResult));
        // 当切换考勤周期时,清空数据
        const { cycleType: preC } = typeRef.current;
        if(cycleType !== preC) {
            // 如果是编辑，则初次加载时不清空数据，避免考勤周期为月时无法回显
            if(!isfirst) {
                initData();
            }
            setIsfirst(false);
            typeRef.current = {cycleType};
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [formResult, cycleType, currentCycleDate])

    return (
        <React.Fragment>
            <FormItem>
                {getFieldDecorator('cycle_date', {
                    initialValue: cycleDate,
                    rules: [{
                        required: true,
                        message: `请选择${cycleType === '0' ? '星期' : '日期'}`,
                    }],
                })(
                    <CycleDate
                        data={DATE_ARR_MAP[cycleType]}
                        onChange={onSelectDate}
                        onDblClick={onDblClick}
                        cycleDateArr={cycleDate}
                        // cycleType={cycleType}
                        currentCycleDate={currentCycleDate}
                    />
                )}
            </FormItem>

            <Button
                icon="hz-add"
                onClick={() => {
                    if(detailDataGroup[currentCycleDate] && detailDataGroup[currentCycleDate].length > 0) {
                        message.warn('每天只能添加一条数据');
                    } else {
                        editTableRef.addRow();
                    }
                }}
                disabled={!currentCycleDate}
            >
                添加
            </Button>
            <Button
                icon="hz-complex"
                onClick={openTimeModal}
                style={{ marginLeft: '15px' }}
                disabled={!detailDataGroup[currentCycleDate] || detailDataGroup[currentCycleDate].length === 0}
            >
                选择系统默认时段
            </Button>

            <FormItem>
                <EditTable
                    style={{ marginTop: '5px', maxWidth: 700 }}
                    columns={editTableColums({isEdit})}
                    dataSource={detailDataGroup[currentCycleDate] || []}
                    onChange={addTableRow}
                    hasDefaultAddBtn={false}
                    setRef={ref => setEditTableRef(ref)}
                    // bridgeForm={form}
                    rowDeleteBtn={onTrigger => {
                        return (
                            <a
                                onClick={() => {
                                    setIsEdit(true);
                                    onTrigger();
                                }}
                                title="删除"
                            >
                                删除
                            </a>
                        );
                    }}
                />
            </FormItem>
            {
                timeModalVis && (
                    <Modal
                        visible={timeModalVis}
                        title='选择系统默认考勤时段'
                        centered
                        wrapClassName={styles.ruleTimeModal}
                        onCancel={() => {
                            setTimeModalVis(false);
                        }}
                        onOk={closeTimeModal}
                    >
                        <div className="hz-layout-grid" style={{border: '1px solid #f2f2f2', borderRadius: '4px'}}>
                            <Radio.Group
                                className="hz-row"
                                name="radiogroup"
                                defaultValue={systemTime}
                                onChange={selectSystemTime}
                            >
                                {
                                    sysDefaultTimeList.map(item => {
                                        return (
                                            <Radio className="hz-col hz-col-11" value={item.code}>{item.name}</Radio>
                                        )
                                    })
                                }
                            </Radio.Group>
                        </div>
                    </Modal>

                )
            }
        </React.Fragment>
    )
}

AddPeriodPage.propTpyes = {
    form: PropsType.object.isRequired, // form
    formResult: PropsType.object,  // 外部传入的时间详情 类似：{'1': [], '2': []}
    cycleType: PropsType.string, // 时间循环的类型
    getDataFromChild: PropsType.func.isRequired,  // 向父级传递数据结构
};

AddPeriodPage.defaultProps = {
    cycleType: '',
    formResult: {},
};

export default AddPeriodPage;