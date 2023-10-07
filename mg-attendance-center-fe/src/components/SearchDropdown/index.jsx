/*
 * @Author: xiedan WX042
 * @since: 2021-01-27 14:58:41
 * @lastTime: 2021-01-29 14:11:03
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\components\SearchDropdown\index.jsx
 * @Description: 表格右上角搜索框---带选项
 */
import React from 'react';
import { Input, Icon, Dropdown, Menu } from 'antd';
const { Search } = Input;
const MenuItem = Menu.Item;

const SearchDropdown = (props) => {
    const { searchTypeList, placeholder, defaultValue, dropEvent } = props;
    const { onSelect = () => {} } = dropEvent;

    const renderAddonAfter = (
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
                </Menu>
            }
        >
            <Icon type="hz-down-arrow" theme="outlined" />
        </Dropdown>
    )

    return (
        <Search
            addonAfter={renderAddonAfter}
            defaultValue={defaultValue}
            placeholder={placeholder}
            // value={value}
            // onSearch={onSearch}
            // onChange={onChange}
            // allowClear
            {...props}
        />
    )
}

SearchDropdown.defaultProps = {
    searchTypeList: [{code: '1', name: '学生姓名'}, {code: '2', name: '学号'}],
    placeholder: '学生姓名',
    defaultValue: '',
    dropEvent: {},
}

export default SearchDropdown;