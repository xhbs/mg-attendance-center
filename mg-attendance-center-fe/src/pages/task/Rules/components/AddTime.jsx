/*
 * @Author: xiedan WX042
 * @since: 2020-08-31 12:37:26
 * @lastTime: 2020-12-18 16:09:28
 * @FilePath: \biz-scene-attendance-web\src\pages\task\Rules\components\AddTime.jsx
 * @Description: 添加时间段组件
 */
/* eslint-disable react-hooks/exhaustive-deps */
import React, { useState, useEffect } from 'react';
import { Button, Icon, DatePicker, Modal, message, Tooltip } from 'antd';
import PropsType from 'prop-types';
import moment from 'moment';
import { cloneDeep } from 'lodash';
import { disabledDate } from '@utils/utils';
import { HzForm } from '@hz-components/biz-scene-components';
import CommonTransfer from '@components/CommonTransfer';
import styles from '../styles/addTime.less';

const { RangePicker } = DatePicker;

const AddTime = (props) => {
    const { hzLabelTitle, dateFiled, delIconStyle, required, sendDataToParent, hasTree, detailData } = props;

    const [isfirst, setIsfirst] = useState(true);   // 用于记录初次加载，true--初次加载， false--非初次加载
    const [count, setCount] = useState(0);  // 用于计算添加的控件个数
    const [nameModalVisbale, setNameModalVisbale] = useState(false); // 修改名称的弹框
    const [formData, setFormData] = useState([]);   // 最终的提交格式，结构为：[{name: '', start_time: '', end_time: '', express_group_ids: []}]
    const [modifyData, setModifyData] = useState({});   // 待修改的名称的数据信息， 结构为：{k: 1, name: ''}
    const [currentItem, setCurrentTime] = useState(0);  // 当前被选择的项，用于分别设置资源树数据
    const [currentSelectedTree, setCurrentSelectedTree] = useState([]);  // 当前已选的资源树数据
	const [HzFormRef, setRef] = useState(null);

    const dateFiledMap = (count) => {
        const obj = {};
        // if(dateFiled.includes('name')) {
            obj.name = `${hzLabelTitle.slice(0, -2)}No.${count + 1}`;
        // }
        if(dateFiled.includes('time')) {
            obj.start_time = '';
            obj.end_time = '';
        }
        if(dateFiled.includes('tree')) {
            obj.express_group_ids = [];
        }
        return obj
    }

    // 点击新增按钮
    const addTimeRange = () => {
        const flagTime = formData.every(item => item.start_time !== '' && item.end_time !== '');
        if( formData.length > 0 && !flagTime) {
            return message.error("请选择时间段");
        }
        let flagTree = true;
        if(dateFiled.includes('tree')) {
            flagTree = formData.every(item => item.express_group_ids && item.express_group_ids.length > 0);
        }
        if( formData.length > 0 && !flagTree) {
            return message.error("请选择表达式");
        }
        if(formData.length > 99) {
            return message.warn('最多添加100个时间段');
        }
        const newFormData = formData.concat(dateFiledMap(count));
        sendDataToParent(newFormData);
        setFormData(newFormData);
        setCount(count + 1);
    }

    // 点击删除按钮
    const deleteTimeRange = k => {
        const newFormData = formData.filter((v, key) => key !== k);
        // 删除一项之后，转移焦点到上一项，直到最后一项
        const n = parseInt(currentItem) - 1;
        if(n > -1) {
            setCurrentTime(n);
            setCurrentSelectedTree(newFormData[n].express_group_ids || []);
        } else if(newFormData.length > 0) {
            setCurrentTime(0);
            setCurrentSelectedTree(newFormData[0].express_group_ids || []);
        }
        sendDataToParent(newFormData);
        setFormData(newFormData);
    };

    // 修改名称弹框---确认操作
    const setNameOk = () => {
        HzFormRef.submit().then(values => {
            const newFormData = cloneDeep(formData);
            newFormData[modifyData.k].name = values.name;
            setFormData(newFormData);
            sendDataToParent(newFormData);
            setNameModalVisbale(false);
        })
    }

    // 设置时间项的名称
    const onOpenModal = (k, name) => {
        setModifyData({
            k,
            name,
        })
        setNameModalVisbale(true);
    }
    
    // 关闭弹框
    const onCloseModal = () => {
        setNameModalVisbale(false);
    }

    // 获取时间控件的时间
    const getTimeRange = (k, data) => {
        if(Array.isArray(data)) {
            const newFormData = cloneDeep(formData);
            newFormData[k].start_time = data[0] && moment(data[0]).valueOf();
            newFormData[k].end_time = data[1] && moment(data[1]).valueOf();
            sendDataToParent(newFormData);
            setFormData(newFormData);
        }
    };

    // 点击到某一项存下相应的 k 值，为了后面显示相应的资源树数据
    const onClickItem = (k) => {
        setIsfirst(false);
        setCurrentTime(k);
        setCurrentSelectedTree(detailData[k] && detailData[k].express_group_ids || []);
    }

    // 选择资源树
    const getTreeDataToItem = (data, total) => {
        // 为当前选择项设置tree
        if(currentItem !== '' && !isfirst) {
            const newFormData = cloneDeep(formData);
            const obj = {};
            obj.express_group_ids = data.selectedKeys || [];
            newFormData[currentItem] = {...newFormData[currentItem], ...obj};
            sendDataToParent(newFormData);
            setFormData(newFormData);
            setCurrentSelectedTree(data.selectedKeys || []);
        }
    }

    // 渲染内容
    const renderHtml = (
        detailData && detailData.map((item, k) => {
            return (
                <div key={k} style={{position: 'relative'}}>
                    {
                        <div
                            className={styles.timeBox}
                            style={{borderColor: currentItem === k ? '#b9ccff' : ''}}
                            onClick={() => {onClickItem(k)}}
                        >
                            <div
                                className={styles.timeNameText}
                                onDoubleClick={() => { onOpenModal(k, item.name) }}
                            >
                                <Tooltip placement="bottom" title='双击名称可修改'>
                                    <Icon type="question-circle" style={{color: '#1890ff', marginRight: 5}} />
                                </Tooltip>
                                { item.name }
                            </div>
                            <RangePicker
                                disabledDate={disabledDate}
                                value={item && [item.start_time && moment(item.start_time) || null, item.end_time && moment(item.end_time) || null] || null}
                                // value={[moment("2020-09-04 14:47:25"), moment("2020-09-05 14:47:25")]}
                                format="YYYY-MM-DD HH:mm:ss"
                                onChange={(dates, date) => {
                                    getTimeRange(k, date);
                                }}
                                showTime
                                allowClear={false}
                                getCalendarContainer={triggerNode => triggerNode.parentNode}
                            />
                        </div>
                    }
                    {
                        (detailData.length > 1 || (detailData.length <= 1 && !required)) && (
                            <Icon
                                type="minus-circle-o"
                                className={styles.delIcon}
                                style={delIconStyle}
                                onClick={() => { deleteTimeRange(k) }}
                            />
                        )
                    }
                </div>
            )
        })
    )

    useEffect(() => {
        if(detailData.length === 0) {
            setIsfirst(true);
            setCurrentSelectedTree([]);
        }
        if(isfirst && detailData.length > 0) {
            onClickItem(0);
            setCount(detailData && detailData.length || 0);
        }
        setFormData(detailData);
    }, [detailData])

    return (
        <React.Fragment>
            {hzLabelTitle && <div className="hz-label">{hzLabelTitle}</div>}

            <Button onClick={addTimeRange}>
                <Icon type="plus" />新增时段
            </Button>

            { renderHtml }

            {hasTree && detailData.length > 0 && (
                <>
                    <div>表达式选择</div>
                    <CommonTransfer type='expression' mode='tree' getTransferSelectedData={getTreeDataToItem} defaultKeys={currentSelectedTree} />
                </>
            )}
            {
                nameModalVisbale && (
                    <Modal
                        visible={nameModalVisbale}
                        title="修改名称"
                        onOk={setNameOk}
                        onCancel={onCloseModal}
                        okText="确认"
                        cancelText="取消"
                    > 
                        <HzForm
                            {...{
                                labelCol: { span: 4 },
                                wrapperCol: { span: 20 },
                            }}
                            fields={[
                                {
                                    label: '名称',
                                    comp: {
                                        fieldName: 'name',
                                        component: 'Input',
                                        options: {
                                            initialValue: modifyData && modifyData.name || '',
                                            rules: [
                                                {
                                                    required: true,
                                                    message: '名称不能为空',
                                                },
                                                {
                                                    max: 100,
                                                    message: '不能超过100个字符',
                                                },
                                            ],
                                        },
                                    },
                                },
                            ]}
                            setRef={ref => setRef(ref)}
                        />
                    </Modal>
                )
            }
        </React.Fragment>
    )
}

AddTime.propTpyes = {
    required: PropsType.bool,  // 时间是否是必选
    dateFiled: PropsType.array, // 提交数据时的键名
    hzLabelTitle: PropsType.string,   // label的名称
    delIconStyle: PropsType.object,   // 删除按钮的样式
    sendDataToParent: PropsType.func.isRequired,  // 向父级传递数据结构   类似：[{name:'', start_time:'', end_time: ''}]
    hasTree: PropsType.bool,  // 是否有资源数
    detailData: PropsType.array,  //  外部传入的详情数据  类似：[{name:'', start_time:'', end_time: '', express_group_ids: []}]
};

AddTime.defaultProps = {
    required: false,
    dateFiled: ['name', 'time', 'tree'],
    hzLabelTitle: '',
    delIconStyle: {},
    hasTree: false,
    detailData: [],
};

export default AddTime;