/*
 * @Author: xiedan WX042
 * @since: 2020-12-03 16:47:26
 * @lastTime: 2021-09-08 14:16:44
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\checkAppeal\AppealSchool\index.jsx
 * @Description: 抽查申诉---校级
 */
import React, { useState } from 'react';
import { Button, message, Modal } from 'antd';
import moment from 'moment';
import ReactScrollbar from 'react-custom-scrollbars';
import { CreateDrawer } from '@hz-components/ists-common';
import { Table } from '@hz-components/react-base';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import AppealDetail from '@components/AppealDetail';
import AppealForm from '@components/AppealForm';
import { getAppealList, bacthAppeal, delAppeal, addAppeal, updateAppeal } from '@/services/appeal';
import { API_ERROR_CODE, SCHOOL } from '@constants';
import { tableColumns, tableSearchOptions } from './utils/options';
import mystyle from './style/index.less';

const CheckAppeal = () => {
	const [visible, setVisible] = useState(false);  // 详情弹框显示
	const [formVisible, setFormVisible] = useState(false);  // 表单页
	const [detail, setDetail] = useState([]);  // 详情
	const [selectedRowKeys, setSelectedRowKeys] = useState([]);
	const [operationType, setOperationType] = useState('add'); // 新建--add, 编辑--update
	const [tableRef, setTableRef] = useState(null);
	const [HzFormRef, setHzFormRef] = useState(null);
	const [selectedDisable, setSelectedDisable] = useState([]); // 状态为同意 or 拒绝的数据，用于判断是否可以批量申诉

	const createPromise = params => {
		setSelectedRowKeys([])
		setSelectedDisable([]);
        const init = initFlied(params);
		const result = { totalCount: 0, currentPageResult: [], pageIndex: 1 };
		return new Promise(resolve => {
			getAppealList(init).then(res => {
				if(res && res.error_code === API_ERROR_CODE) {
                    const { paging, data } = res.data || {};
                    resolve({
                        pageIndex: paging.page_num,
                        totalCount: paging.total,
                        currentPageResult: data || [],
                    })
				} else {
					resolve(result);
				}
			}).catch(() => {
				resolve(result);
			});
		});
	};

	/**
     * @description: 构造表格检索参数
     * @param info
     */
    const initFlied = (info) => {
		console.log(info)
        const { pageIndex, pageSize, search = '', status, date, sorter } = info || {};
        const { order } = sorter || {};
        const obj = {
            page_size: pageSize,
            page_num: pageIndex,
            search,
            status,
            order_rule: order && order === 'descend' ? 'DESC' : 'ASC', // 默认升序
            order_field: 'status',
        };
        if(date && date.length === 2 ) {
            obj.start_time = date[0] && moment(moment(date[0]).format('YYYY-MM-DD') + ' 00:00:00').valueOf();
            obj.end_time = date[1] && moment(moment(date[1]).format('YYYY-MM-DD') + ' 23:59:59').set('millisecond', 999).valueOf();
        }
        
        return obj;
	};
	
	// 申诉
	const handleAppeal = (ids) => {
		console.log(ids, selectedRowKeys)
        const data = ids ? [ids] : selectedRowKeys;
        const options = {
            title: `确定要申诉所选数据吗？`,
            content: '此操作不可逆，请慎重考虑。',
            okText: '申诉',
            okType: 'warn',
            cancelText: '取消',
            onOk: () => {
                appealReq(data);
            },
        }
        Modal.confirm(options)
	}

    // 申诉req请求
    const appealReq = (data = []) => {
        bacthAppeal(data).then(res => {
            if(res && res.error_code === API_ERROR_CODE) {
                message.success('申诉成功');
				setSelectedRowKeys([]);
				setSelectedDisable([]);
                tableRef.dataLoad();
            } else {
                message.error( res && res.message || '无法申诉');
            }
        })
    }

	// 删除
	const handleDel = (ids) => {
		console.log(ids, selectedRowKeys)
        const data = ids ? ids : selectedRowKeys.join(',');
        const options = {
            title: `确定要删除所选数据吗？`,
            content: '此操作不可逆，请慎重考虑。',
            okText: '删除',
            okType: 'danger',
            cancelText: '取消',
            onOk: () => {
                delAppealReq(data);
            },
        }
        Modal.confirm(options)
	}

    // 删除申诉req请求
    const delAppealReq = (data = '') => {
        delAppeal(data).then(res => {
            if(res && res.error_code === API_ERROR_CODE) {
				message.success('删除成功');
				setSelectedRowKeys([]);
				setSelectedDisable([]);
                tableRef.dataLoad();
            } else {
                message.error( res && res.message || '无法删除');
            }
        })
    }

	// 查看明细 未申诉的支持修改
	const handleDetail = (data) => {
		setDetail(data);
		if(data && data.status === 1) {
			setFormVisible(true);
			setOperationType('update');
		} else {
			setVisible(true);
		}
	};

	const rowSelection = {
		onChange: (selectedRowKeys, selectedRow) => {
			setSelectedRowKeys(selectedRowKeys)
			// 状态为审批中 or 同意 or 拒绝的数据
			const arr = selectedRow.filter(i => [2,3,4].includes(i.status) )
			setSelectedDisable(arr);
		},
		selectedRowKeys,
		// 审批中，不允许任何操作, 同意和拒绝不支持批量申诉
		getCheckboxProps: record => ({
		  disabled: record.status === 2,
		}),
	}

    // 关闭新建页面
    const onCancel = () => {
		setFormVisible(false);
        // tableRef.dataLoad();
	}

	// 新增及修改req
    const addAppealReq = (report) => {
		const { handleSubmit } = HzFormRef;
		handleSubmit((values) => {
			if(values) {
				const { title, content, files, person_nos, date, name_list_excel_path } = values;
				const rArr = person_nos && person_nos.map(x => x.user_code || x.person_no) || [];
				const params = {
					data: {
						title,
						content,
						person_nos: rArr,
						start_time: date[0] && moment(moment(date[0]).format('YYYY-MM-DD') + ' 00:00:00').valueOf(),
						end_time: date[1] && moment(moment(date[1]).format('YYYY-MM-DD') + ' 23:59:59').set('millisecond', 999).valueOf(),
						report: !!report, // 是否上报
            files,
            name_list_excel_path, // 申诉学生名单excel路径
					},
				}
				let fun = operationType === 'add' ? addAppeal : updateAppeal;
				if(operationType !== 'add') {
					params.id = detail.id;
				}
				fun(params).then(res => {
					if(res && res.error_code === API_ERROR_CODE) {
						message.success('操作成功');
						if(operationType === 'add') {
							tableRef.dataLoad({ pageIndex: 1 });
						} else {
							tableRef.dataLoad();
						}
						setVisible(false);
						setFormVisible(false);
					} else {
						message.error( res && res.message || '无法操作');
					}
				})
			}
		})
	}

	// 渲染表单页底部按钮
	const renderFooter = () => {
		return (
			<React.Fragment>
				<Button
					onClick={() => {
						addAppealReq(false);
					}}
					type="primary"
                    style={{ marginRight: 16 }}
				>
					保存
				</Button>
				<Button
					onClick={() => { 
						addAppealReq(true);
					}}
					type="primary"
                    style={{ marginRight: 16 }}
				>
					保存并申诉
				</Button>
				<Button onClick={onCancel}>取消</Button>
			</React.Fragment>
		);
    }
	return (
		<div className={`hz-layout-vertical ${mystyle.detailTableWapper}`}>
			<div className="hz-layout-vertical-body" style={{margin: 16, display: formVisible ? 'none' : 'block'}}>
				<Table
					rowKey='id'
					columns={tableColumns({
						handleDetail,
						handleAppeal,
						handleDel,
					})}
					handleBarOptions={HandleBarOptions({
						searchPlaceholder: '申诉标题',
						handleAdd: () => {
							setFormVisible(true);
							setOperationType('add');
						},
						handleDel,
						disabledDel: selectedRowKeys.length === 0,
						customizeBtn: [{
							antdProps: {
								icon: 'hz-complex',
								children: '申诉',
							    disabled: selectedRowKeys.length === 0 || selectedDisable.length > 0,
								onClick: () => { handleAppeal() },
							},
						}],
					})}
					searchBarOptions={tableSearchOptions()}
					hasSerialNo
					hasFilter
					filterDefaultIsOpened
					serialNoOverlying
					createPromise={createPromise}
					setRef={tableRef => setTableRef(tableRef)}
					antdProps={{
						rowSelection,
					}}
				/>
			</div>

			{/* 查询申诉明细 */}
			{
				visible && (
					<AppealDetail
						visible={visible}
						onCancel={() => setVisible(false)}
						detail={detail}
						userRole={SCHOOL}
					/>
				)
			}
			{/* 新增 */}
			<CreateDrawer
				visible={formVisible}
				title={operationType === 'add' ? '新建' : '编辑'}
				handleCancel={onCancel}
				footer={renderFooter()}
				
			>
				<ReactScrollbar renderTrackHorizontal={() => <div />}>
					<AppealForm
                    	wrappedComponentRef={ref => setHzFormRef(ref)}
						detail={detail}
						operationType={operationType}
					/>
				</ReactScrollbar>
			</CreateDrawer>
		</div>
	);
};

export default CheckAppeal;
