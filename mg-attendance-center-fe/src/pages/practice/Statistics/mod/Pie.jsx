/*
 * @Author: xiedan WX042
 * @since: 2019-10-12 09:20:07
 * @lastTime: 2020-12-31 17:15:33
 * @文件相对于项目的路径: \biz-scene-attendance-web\src\pages\practice\Statistics\mod\Pie.jsx
 * @Description:
 */
/**
 * @param graphData 饼状图的数据 必传
 * @param domId 渲染画布的ID 必传
 * @param picClassName 画布className
 * @param picStyle 画布style
 * @param colorLists 饼块儿颜色列表
 * @param showSeriesLabel 是否显示线条
 * @param showSeriesLabelIn 移入是否显示线条
 * @param noDataText 数据为空时的文字
 */

import React, { useEffect } from "react";
import { initPieArea, initPiePercent } from "./setOptions";

const PieComponent = (props) => {
    const { domId, picClassName, picStyle, graphData, total, type } = props

    // 画饼状图
    const drawBar = () => {
        if (type === 'number' && graphData) {
            initPieArea({
                targetDoc: document.getElementById(domId),
                info: {
                    ...props,
                },
            });
        } else {
            initPiePercent({
                targetDoc: document.getElementById(domId),
                info: {
                    ...props,
                },
            });
        }
    };

    useEffect(() => {
        drawBar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [graphData, total])

    return (
        <div
            id={domId}
            className={picClassName}
            style={picStyle}
        />
    );
}

export default PieComponent;
