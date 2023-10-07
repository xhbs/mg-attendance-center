/* eslint-disable react-hooks/exhaustive-deps */
/*
 * @Author: xiedan WX042
 * @since: 2020-09-01 14:36:05
 * @lastTime: 2020-11-17 15:03:57
 * @FilePath: \biz-scene-attendance-web\src\pages\task\Rules\components\CycleDate.jsx
 * @Description: 日期 or 星期 选择按钮组件   云南中职通用
 */
import React, { useState, useEffect } from 'react';
import PropsType from 'prop-types';
import ClassNames from 'classnames';
import styles from '../styles/cycleDate.less';

let timer = null;
let count = 0;   // 标志单机还是双击

const CycleDate = (props) => {
    const { data, onChange, onDblClick, cycleDateArr, currentCycleDate } = props;
    const [isfirst, setIsfirst] = useState(true);   // 用于记录初次加载，true--初次加载， false--非初次加载

    // const typeRef = useRef({lifeCycle, cycleType})

    const onSelected = (e, value) => {
        count = 0;
        // 区分双击事件
        timer = setTimeout(() => {
            if(!count) {
                setIsfirst(false);
                onChange(value)
            }
            clearTimeout(timer)
            timer = null;
        }, 200)
    }

    // 双击
    const ondblSelected = (value) => {
        count = 1;
        clearTimeout(timer)
        timer = null;
        onDblClick(value)
    }

    useEffect(()=>{
        // 初始化详情时，默认展示第一条数据
        if(isfirst && cycleDateArr.length > 0) {
            onChange(cycleDateArr[0])
        }
        // 当切换时间类型或者循环方式时，重置日期按钮的状态
        // const { lifeCycle: preL, cycleType: preC } = typeRef.current;
        // if(lifeCycle !== preL || cycleType !== preC) {
        //     // setCurrent('');
        //     typeRef.current = {lifeCycle, cycleType};
        // }
        return () => {
            clearTimeout(timer)
            timer = null;
        }
    }, [cycleDateArr])

    return (
        <div className={styles.btnWapper}>
            {data && data.map((item, index) => {
                const v = `${index + 1}`;
                return (
                    <span
                        className={ClassNames({
                            [styles.btnItem]: true,
                            [styles.isSelected]: cycleDateArr.includes(v),
                        })}
                        style={{
                            opacity: currentCycleDate === v ? '0.7' : '1',  // 当前选中的颜色
                        }}
                        key={index}
                        value={v}
                        onClick={(e) => {onSelected(e, v)}}
                        onDoubleClick={() => {ondblSelected(v)}}
                    >
                        {item}
                    </span>
                )
            })}
        </div>
    )
}

CycleDate.propTpyes = {
    data: PropsType.array.isRequired, // 按钮内容
    onChange: PropsType.func.isRequired, // 父组件的方法
    onDblClick: PropsType.func.isRequired, // 父组件的方法
    // lifeCycle: PropsType.string.isRequired,  // 生命周期  1--临时 or 0--永久
    // cycleType: PropsType.string.isRequired,  // 循环周期
    cycleDateArr: PropsType.array,  // 初始化选中的数据
    currentCycleDate: PropsType.string.isRequired,  // 当前选中的日期
}

CycleDate.defaultProps = {
    cycleDateArr: [],
}

export default CycleDate;