/*
 * @Author: heqiang 00621
 * @Date: 2020-09-22 09:30:38
 * @Description: 人员组织选择
 */
import React, { useEffect, useState } from 'react';
import { Button, Input, Table, Icon, message } from 'antd';
import { Tree } from '@hz-components/react-base';
import PropTypes from 'prop-types';
import { cloneDeep } from 'lodash';
import { getDepartmentTree, getMembersList } from '@services/adjustService';
import { PLACEHOLDER } from '@constants';
import styles from './index.less';

const dataSetting = {
    dataKey: 'ob_group_id',
    dataViewKey: 'ob_group_name',
    childArrayKey: 'children',
}

function OrganizationPersonTree(props) {
    const { handleChange, taskId } = props;
    const [loading, setLoading] = useState(false);
    const [treeData, setTreeData] = useState([]);
    const [selectedRowKeys, setSelectedRowKeys] = useState([]);
    const [, setCheckedTreeNode] = useState("");
    const [tableSearchContent, setTableSearchContent] = useState(null);
    const [tableData, setTableData] = useState([]);
    const [oldTableData, setOldTableData] = useState([]);
    // 已选人员
    const [selectedTableData, setSelectedTableData] = useState([]);
    // 已选人员勾选
    const [personRowKeys, setPersonRowKeys] = useState([]);

    useEffect(() => {
        if(taskId) {
            // 切换任务清空人员数据
            setTreeData([]);
            setTableData([]);
            setOldTableData([]);
            setTableSearchContent('');
            setSelectedRowKeys([]);
            setSelectedTableData([]);
            setPersonRowKeys([]);
            handleChange(null);

            getTreeData()
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [taskId]);

    const getTreeData = () => {
        getDepartmentTree(taskId).then(res => {
            if (res) {
                setTreeData(res.data);
            }
        });
    }

    // 获取人员列表
    const getPersonLists = groupId => {
        setLoading(true);
        getMembersList(groupId).then(res => {
            const arr = res ? res.data : [];
            setTableData(arr);
            setOldTableData(arr);
            setTableSearchContent('');
            setSelectedRowKeys([]);
            // setSelectedTableData([]);
            setPersonRowKeys([]);
        }).catch().finally(() => setLoading(false));
    }

    const personSearch = evt => {
        if (!oldTableData.length) {
            return message.info('暂无可搜索的内容');
        }
        const { value } = evt.target;
        const val = value.trim();
        const flag = val !== tableSearchContent;
        if (!val) {
            setTableData(oldTableData);
        } else if (flag) {
            const arr = tableData.filter(x => x.person_name.includes(val) || x.person_no.includes(val));
            setTableData(arr);
            setTableSearchContent(val);
        }
    }

    // 点击树节点选中
    const onSelect = data => {
        // console.log('data=====::', data)
        const id = data ? data.ob_group_id : null
        setCheckedTreeNode(id);
        // handleChange(null);
        if (data) {
            getPersonLists(id);
        }
    }

    const handlePersonSelected = flag => {
        if (flag === 'add') {
            let arr = selectedRowKeys.map(x => {
                return { person_name: x.person_name, person_no: x.person_no }
            });
            arr = arr.filter(item => {
                let list = selectedTableData.map(v => v.person_no);
                return !list.includes(item.person_no);
            })
            const arrs = [...cloneDeep(selectedTableData), ...arr]

            setSelectedTableData(arrs);
            const rArr = arrs.map(x => x.person_no);
            handleChange(rArr);
        } else {
            const leftList = selectedRowKeys.filter(x => !personRowKeys.includes(x.person_no));
            setSelectedRowKeys(leftList);
            const rightList = selectedTableData.filter(x => !personRowKeys.includes(x.person_no));
            setSelectedTableData(rightList);
            const arr = rightList.map(x => x.person_no);
            handleChange(arr);
            setPersonRowKeys([]);
        }
    }

    return (
        <div className={styles.contentBox}>
            <div className={styles.leftBox}>
                <div className={styles.treeBox}>
                    <div className={styles.title}>人员分组</div>
                    <div className={styles.tree}>
                        <Tree
                            dataSetting={dataSetting}
                            data={treeData}
                            hasSearch
                            searchSetting={{ placeholder: '搜索组织' }}
                            onSelect={onSelect}
                        />
                    </div>
                </div>
                <div className={styles.table}>
                    <div className={styles.title}>人员 {`${selectedRowKeys.length}/${tableData.length}`}</div>
                    <div className={styles.tableInput}>
                        <Input
                            placeholder="人员 / 编号"
                            onChange={personSearch}
                            value={tableSearchContent}
                        />
                    </div>
                    <div className='hz-table-fixed hide-pagination' style={{ height: "calc(100% - 74px)" }}>
                        <Table
                            rowKey='person_no'
                            scroll={{ y: "calc(100% - 48px)" }} // size = 'small' 的时候用38px
                            columns={columns()}
                            dataSource={tableData}
                            rowSelection={{
                                selectedRowKeys: selectedRowKeys.map(x => x.person_no),
                                onChange: (keys, rows) => setSelectedRowKeys(rows),
                            }}
                            pagination={false}
                            loading={loading}
                        />
                    </div>
                </div>
            </div>
            <div className={styles.midContent}>
                <Button
                    className={styles.btn}
                    onClick={() => handlePersonSelected('add')}
                >
                    添加<Icon type='hz-next-arrow' theme="outlined" />
                </Button>
                <Button
                    className={styles.btn}
                    onClick={() => handlePersonSelected()}
                >
                    移除<Icon type='hz-previous' theme="outlined" />
                </Button>
            </div>
            <div className={styles.rightContent}>
                <div className={styles.title}>已选人员 {`${personRowKeys.length}/${selectedTableData.length}`}</div>
                <div className='hz-table-fixed hide-pagination' style={{ height: "calc(100% - 32px)" }}>
                    <Table
                        rowKey='person_no'
                        scroll={{ y: "calc(100% - 48px)" }} // size = 'small' 的时候用38px
                        columns={columns()}
                        dataSource={selectedTableData}
                        rowSelection={{
                            selectedRowKeys: personRowKeys,
                            onChange: keys => setPersonRowKeys(keys),
                        }}
                        pagination={false}
                        loading={loading}
                    />
                </div>
            </div>
        </div>
    )
}

function columns() {
    return [
        {
            title: "对象名称",
            dataIndex: "person_name",
            width: "50%",
            render: (text) => <span title={text} className='ellipsis'> {text || PLACEHOLDER}</span>,
        },
        {
            title: "对象编号",
            dataIndex: "person_no",
            width: "40%",
            render: (text) => <span title={text} className='ellipsis'> {text || PLACEHOLDER}</span>,
        },
    ];
}

OrganizationPersonTree.propTypes = {
    handleChange: PropTypes.func.isRequired,
}

export default OrganizationPersonTree;

