/*
 * @Author: xiedan WX042
 * @since: 2020-12-10 14:39:53
 * @lastTime: 2020-12-10 15:27:58
 * @FilePath: \biz-scene-attendance-web\src\components\GraphOrList\index.jsx
 * @Description: 图型和列表切换按钮
 */
import React from "react";
import { Radio, Icon } from "antd";
import styles from "./index.less";

export default function GraphOrList (props) {
    const { handleTypeChange, defaultValue = "graph", size='small' } = props;

    return (
        <Radio.Group
            defaultValue={defaultValue}
            onChange={handleTypeChange}
            buttonStyle="solid"
            size={size}
            style={{ float: "right" }}
            className={styles.RadioGroup}
        >
            <Radio.Button value="graph">
                <span title="图形模式">
                    <Icon type="hz-img" theme="outlined" />
                </span>
            </Radio.Button>
            <Radio.Button value="list">
                <span title="列表模式">
                    <Icon type="hz-list" theme="outlined" />
                </span>
            </Radio.Button>

        </Radio.Group>
    );
}
