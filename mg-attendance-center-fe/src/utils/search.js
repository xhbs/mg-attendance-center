/*
 * @Author: xiedan WX042
 * @since: 2020-12-31 11:35:28
 * @lastTime: 2020-12-31 17:48:59
 * @文件相对于项目的路径: \biz-scene-attendance-web\src\utils\search.js
 * @Description: 公共数据
 */
import React from 'react';
import { Icon, Dropdown, Menu } from 'antd';

const MenuItem = Menu.Item;

export const searchTypeList = [{code: '1', name: '学生姓名'}, {code: '2', name: '学号'}];

// 搜索框右侧下拉列表
export const renderAddonAfter = ({ onSelect = () => {} }) => {
        return (
        <Dropdown
            placement="bottomCenter"
            overlay={
                <Menu selectable defaultSelectedKeys={['1']} onSelect={onSelect}>
                    {
                        searchTypeList.map((v) => {
                            return (
                            <MenuItem
                                key={v.code}
                                value={v.code}
                            >
                                {v.name}
                            </MenuItem>
                            );
                        })
                    }
                </Menu>}
            // getPopupContainer={ triggerNode=> triggerNode.parentNode}
        >
            <Icon type="hz-down-arrow" theme="outlined" />
        </Dropdown>
    )
}