import { getPersonList } from '@services/common';

/**
 * @Author: heqiang 00621
 * @description: 一个将组织树和人员相结合的处理函数（使用递归函数）
 * @param {Array}: originalData 组织树
 * @param {fun}: callBack 数据返回回调函数  123
 * @return {fun} callBack
 */
export function initTreeStructure(originalData, callBack) {
    const params = [...originalData];
    const treeIdList = []; // 原始树节点平铺列表

    const getPersonLists = ids => {
        return getPersonList(ids).then(res => {
            return res;
        });
    }

    // 遍历获取所有树节点id
    const getTreeId = (data) => {
        data.forEach(item => {
            treeIdList.push(item);
            if (item.children && item.children.length > 0) {
                return getTreeId(item.children);
            }
        });
    }

    // 将人员列表与原始树结合为一棵树
    const assembleTree = (data = [], resData) => {
        data.forEach(item => {
            if (!(item instanceof Array)) {
                const str = resData.find(x => x[item.id]);
                const obj = ((str && str[item.id]) || []).map(y => {
                    return {
                        ...y,
                        group_name: y.user_name,
                        parent: item.id,
                        children: [],
                        id: y.user_code,
                    }
                });
                if (obj.length) {
                    item.children.push(...obj);
                }
                if (item.children && item.children.length > 0) {
                    return assembleTree(item.children, resData);
                }
            }
        });
    }

    getTreeId(params);

    const ids = treeIdList.map(x => x.id);
    const fun = ids.map(x => getPersonLists([x]));
    Promise.all(fun).then(res => {
        const tree = res.map((x, i) => { return { [ids[i]]: x.data } });
        assembleTree(params, tree);
        callBack(params);
    });
}