/*
 * @Author: xiedan WX042
 * @since: 2020-11-03 16:07:17
 * @lastTime: 2020-11-03 17:04:36
 * @FilePath: \uss-ats-webe:\hz\biz-scene-attendance-web\src\components\CommonTransfer\utils\index.js
 * @Description: 资源树参数
 */

export const treeSetting = {
    deviceGroup: {
        dataKey: 'id',
        dataViewKey: 'name',
        parentKey: 'parent_id',
        childArrayKey: 'child_groups',
    },
    device: {
        dataKey: 'resource_code',
        dataViewKey: 'resource_name',
        parentKey: 'parent_resource',
        childArrayKey: 'child',
    },
    person: {
        dataKey: 'id',
        dataViewKey: 'group_name',
        parentKey: 'parent',
        childArrayKey: 'children',
    },
    expression: {
        dataKey: 'id',
        dataViewKey: 'group_name',
        parentKey: '',
    },
    organization: {
        dataKey: 'org_index',
        dataViewKey: 'org_name',
        parentKey: 'org_parent_index',
        childArrayKey: 'child',
    },
};

export const searchSetting = {
    deviceGroup: {
        placeholder: '搜索分组',
    },
    device: {
        placeholder: '搜索考勤点',
    },
    person: {
        placeholder: '搜索分组',
    },
    expression: {
        placeholder: '搜索分组',
    },
    organization: {
        placeholder: '搜索组织',
    },
}

export const treeTarget = {
    dataKey: 'childCount',
    dataValue: 0,
}