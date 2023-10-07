/*
 * @Author: heqiang
 * @since: 2020-12-17 16:05:15
 * @lastTime: 2021-02-07 10:21:45
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\components\TransferTable\index.js
 * @Description: 组织树选人员
 */
import React from 'react';
import { Button, Icon, Table } from 'antd';
import _ from 'lodash';
import { Tree, Table as TableAS } from '@hz-components/react-base';
import PropTypes from 'prop-types';
import { cloneDeep } from 'lodash';
import SearchDropdown from '@components/SearchDropdown';
import { API_ERROR_CODE } from "@constants";
import { getUserOrgIndex, getOrganizationsTree, getPersonByCondition } from '@services/common';
import styles from './index.less';

const { Ellipsis } = TableAS;

class TransferTable extends React.Component {
    
    state = {
        loading: false, // 表格loading
        treeData: [], // 左侧树
        selectedRow: [], // 待选人员复选框选中的值obj
        selectedRowKeys: [], // 待选人员复选框选中的值key
        checkedTreeNode: '', // 被选中的组织id
        tableSearchContent: '', // 待选人员搜索框内容
        tableData: [], // 待选人员列表
        // oldTableData: [], // 待选人员列表备份，用于前端执行筛选功能
        selectedTableData: [], // 被选的人员列表
        personRowKeys: [], // 被选人员复选框选中的值key
        orgIndexPath: '', // 被选的组织org_index和index_path拼接，用于获取人员列表传参
        total: 0, // 待选人员总数
        searchType: '1',
        asyncLoad: false, // 人员列表请求load，防止频繁请求，返回的数据错乱
    }

    defaultColumns = [{
        title: "姓名",
        dataIndex: this.props.tableSetting.dataViewKey,
        width: "40%",
        render: (text) => <Ellipsis>{text}</Ellipsis>,
    },
    {
        title: "学号",
        dataIndex: this.props.tableSetting.dataKey,
        width: "50%",
        render: (text) => <Ellipsis>{text}</Ellipsis>,
    }];

    componentDidMount() {
        this.getTreeData()

        const { defaultSelected } = this.props;
        const arr = defaultSelected.map(item => {
            return {
                user_code: item.person_no,
                user_name: item.person_name,
            }
        })
        this.setState({
            selectedTableData: arr,
            selectedRowKeys: arr.map(i => i.user_code),
            selectedRow: arr,
        })
    }

    shouldComponentUpdate(nextProps, nextState) {
        const { defaultSelected } = nextProps;
        const { defaultSelected: preD } = this.props;
        if(!_.isEqual(defaultSelected, preD)) {
            const arr = defaultSelected.map(item => {
                return {
                    user_code: item.person_no,
                    user_name: item.person_name,
                }
            })
            this.setState({
                selectedTableData: arr,
                selectedRowKeys: arr.map(i => i.user_code),
                selectedRow: arr,
            })
        }
        return true;
    }

    // 获取组织tree
    getTreeData = () => {
        getUserOrgIndex().then(res => {
            if(res) {
                getOrganizationsTree({
                    orgIndex: res.org_index,
                    type: 1,
                }).then(respose => {
                    if(respose) {
                        this.setState({
                            treeData: respose,
                        })
                        if(this.tableRef) {
                            // 获取待选人员
                            if(respose.length > 0) {
                                console.log(respose,'树数据');
                                const data = respose[0] || {};
                                const path = data.index_path.split('/').join('_');
                                // const orgIndexPath = `${path}${data.org_index}`;
                                const org_index=data.org_index||''
                                const org_id=data.org_id||''
                                // this.tableRef.dataLoad({
                                //     pageIndex: 1,
                                //     org_index,
                                //     org_id
                                // })
                            }
                        }
                    }
                })
            }
        })
    }

    createPromise = async (params) => {
        const { searchType } = this.state;
        const { pageIndex, org_index,org_id, tableSearchContent = '' } = params;

        if(!org_index) {
            return;
        }

        let field = '';
		if(tableSearchContent && searchType === '1') {
			field = 'user_name';
		} else if(tableSearchContent && searchType === '2') {
			field = 'user_code';
        }

        let conditions = [{"operate":"eq","value": org_index,"key":"org_index"}];
        if(field) {
            conditions.push({
                "operate":"like","value": tableSearchContent.trim(),"key": field,
            })
        }

        const param = {
            page_num: pageIndex,
            page_size: 10,
            // match_code: 'OR',  // 匹配模式 ADD：所以  OR：单一
            conditions,
            org_id
        }

        return new Promise(resolve => {
            getPersonByCondition(param).then(res => {
                if(res && res.error_code === API_ERROR_CODE) {
                    const { data, paging } = res.data || {};
                    // const arr = Array.from({length: 20}, (v, k) => { const obj = {'person_name': `李${k}`, 'person_no': `a-${k}`}; return obj});
                    this.setState({
                        total: paging.total,
                        tableData: data,
                    })
                    resolve({
                        totalCount: paging.total,
                        currentPageResult: data,
                        pageIndex: paging.page_num,
                      })
                }
            }).finally(() => {
                this.setState({
                    asyncLoad: false,
                })
            })
        })
    }

    // 搜索框发生改变
    changeSearch = evt => {
        const { value } = evt.target;
        const val = value.trim();
        this.setState({
            tableSearchContent: val,
        })
    }

    // 表格执行搜索
    tableSearch = val => {
        const { asyncLoad } = this.state;
        if(asyncLoad) {
            return;
        }
        this.setState({
            tableSearchContent: val,
            asyncLoad: true,
        }, () => {
            this.tableRef.dataLoad({
                pageIndex: 1,
                tableSearchContent: val.trim(),
            })
        })
    }

    // 点击左侧树，清空待选列表并重新查询
    onSelect = data => {
        const id = data ? data.id : null
        this.setState({
            checkedTreeNode: id,
            // selectedRow: [],
            // selectedRowKeys: [],
            // personRowKeys: [],
            tableSearchContent: '',
        })
        if (data) {
            const path = data.index_path.split('/').join('_');
            const orgIndexPath = `${path}${data.org_index}`;
            const org_index=data.org_index||''
            const org_id=data.org_id||''
            this.setState({
                orgIndexPath,
                total: 0,
                asyncLoad: false,
            })
            this.tableRef.dataLoad({
                pageIndex: 1,
                orgIndexPath,
                tableSearchContent: '',
                org_index,
                org_id
            })
        }
    }

    handlePersonSelected = flag => {
        const { tableSetting, onChange } = this.props;
        const { selectedRowKeys, selectedRow, personRowKeys, selectedTableData } = this.state;
        if (flag === 'add') {
            let arr = selectedRow.map(x => {
                return { [tableSetting.dataViewKey]: x[tableSetting.dataViewKey], [tableSetting.dataKey]: x[tableSetting.dataKey] }
            });
            arr = arr.filter(item => {
                let list = selectedTableData.map(v => v[tableSetting.dataKey]);
                return !list.includes(item[tableSetting.dataKey]);
            })
            const arrs = [...cloneDeep(selectedTableData), ...arr]

            this.setState({
                selectedTableData: arrs,
            })
            // const rArr = arrs.map(x => x[tableSetting.dataKey]);
            onChange(arrs);
        } else {
            const leftListkey = selectedRowKeys.filter(x => !personRowKeys.includes(x));
            const leftList = selectedRow.filter(x => !personRowKeys.includes(x[tableSetting.dataKey]));
            const rightList = selectedTableData.filter(x => !personRowKeys.includes(x[tableSetting.dataKey]));
            // const arr = rightList.map(x => x[tableSetting.dataKey]);
            onChange(rightList);
            this.setState({
                selectedRowKeys: leftListkey,
                selectedRow: leftList,
                selectedTableData: rightList,
                personRowKeys: [],
            })
        }
    }
    render() {
        const { columns, dataSetting, tableSetting } = this.props;
        const { treeData, selectedRowKeys, selectedRow, total, loading, personRowKeys, selectedTableData, tableSearchContent } = this.state;

        return (
            <div className={styles.contentBox}>
                <div className={styles.leftBox}>
                    <div className={styles.treeBox}>
                        <div className={styles.title}>组织</div>
                        <div className={styles.tree}>
                            <Tree
                                dataSetting={dataSetting}
                                data={treeData}
                                hasSearch
                                searchSetting={{ placeholder: '搜索组织' }}
                                onSelect={this.onSelect}
                            />
                        </div>
                    </div>
                    <div className={styles.table}>
                        <div className={styles.title}>人员 {`${selectedRow.length}/${total}`}</div>
                        <div className={styles.tableInput}>
                            <SearchDropdown
                                dropEvent={{
                                    onSelect: ({key}) => {
                                        this.setState({
                                            searchType: key,
                                        })
                                    },
                                }}
                                placeholder={this.state.searchType === '1' ? '姓名' : '学号'}
                                onSearch={this.tableSearch}
                                onChange={this.changeSearch}
                                value={tableSearchContent}
                                allowClear
                            />
                        </div>
                        <div className='hz-table-fixed' style={{ height: "calc(100% - 76px)" }}>
                            <TableAS
                                rowKey={[tableSetting.dataKey]}
                                scroll={{ y: "calc(100% - 48px)" }} // size = 'small' 的时候用38px
                                columns={columns || this.defaultColumns}
                                createPromise={this.createPromise}
                                antdProps={{
                                    rowSelection: {
                                        selectedRowKeys,
                                        onChange: (keys, rows) => this.setState({selectedRowKeys: keys, selectedRow: rows}),
                                    },
                                }}
                                pagination={{
                                    simple: true,
                                    defaultPageSize: 10,
                                    total,
                                }}
                                setRef={tableRef => this.tableRef = tableRef}
                                loading={loading}
                                forceFirstLoadDisabled
                            />
                        </div>
                    </div>
                </div>
                <div className={styles.midContent}>
                    <Button
                        className={styles.btn}
                        onClick={() => this.handlePersonSelected('add')}
                    >
                        添加<Icon type='hz-next-arrow' theme="outlined" />
                    </Button>
                    <Button
                        className={styles.btn}
                        onClick={() => this.handlePersonSelected()}
                    >
                        移除<Icon type='hz-previous' theme="outlined" />
                    </Button>
                </div>
                <div className={styles.rightContent}>
                    <div className={styles.title}>已选人员 {`${personRowKeys.length}/${selectedTableData.length}`}</div>
                    <div className='hz-table-fixed' style={{ height: "calc(100% - 32px)" }}>
                        <Table
                            rowKey={[tableSetting.dataKey]}
                            scroll={{ y: "calc(100% - 48px)" }} // size = 'small' 的时候用38px
                            columns={columns || this.defaultColumns}
                            dataSource={selectedTableData}
                            rowSelection={{
                                selectedRowKeys: personRowKeys,
                                onChange: keys => this.setState({ personRowKeys: keys }),
                            }}
                            pagination={{
                                simple: true,
                                defaultPageSize: 10,
                            }}
                        />
                    </div>
                </div>
            </div>
        )
    }
}

TransferTable.propTypes = {
    onChange: PropTypes.func.isRequired,
}

TransferTable.defaultProps = {
    // 表格要显示的值
    tableSetting: {
        dataKey: 'user_code',
        dataViewKey: 'user_name',
    },
    // 左侧组织树节点的取值
    dataSetting: {
        dataKey: 'org_index',
        dataViewKey: 'org_name',
        parentKey: 'org_parent_index',
        childArrayKey: 'child',
    },
    defaultSelected: [],
}

export default TransferTable;

