/*
 * @Author: heqiang 00621
 * @Date: 2020-09-01 14:34:56
 * @Description: 调整对象穿梭框选择
 */
import React from 'react';
import { Transfer } from '@hz-components/mg-biz';

const treeSetting = {
    dataKey: 'id',
    dataViewKey: "group_name",
    childArrayKey: "children",
    parentKey: 'parent',
};

export default function transfer(props) {
    const { treeDataChange, defaultKeys, treeList } = props;

    const getTreeFn = data => {
        return new Promise(resolve => {
            resolve({ isSuccess: true, data });
        });
    }

    return (
        <Transfer
            overlayType="page"
            searchSetting={{
                placeholder:'搜索人员',
            }}
            getPopupContainer={triggerNode => triggerNode.parentNode}
            onChange={treeDataChange}
            getTreeFn={() => getTreeFn(treeList)}
            treeSetting={treeSetting}
            height={450}
            defaultKeys={defaultKeys}
            treeTarget={node => !!node.user_name}
        />
    )
}
