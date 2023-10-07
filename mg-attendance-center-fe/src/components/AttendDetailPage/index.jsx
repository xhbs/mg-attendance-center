/*
 * @Author: xiedan WX042
 * @since: 2020-12-07 20:39:46
 * @lastTime: 2021-07-13 11:17:32
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\components\AttendDetailPage\index.jsx
 * @Description: 考勤明细列表页   日常
 */
import React from 'react';
import { Button, Icon, message, Modal, Tooltip, Upload } from 'antd';
import moment from 'moment';
import { downloadFileStream } from '@utils/utils';
import { Table } from '@hz-components/react-base';
import { HzForm, ExportOptions,
  // ImportModal
} from '@hz-components/biz-scene-components';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import AttendDetailModal from '@components/AttendDetailModal';
import SearchDropdown from '@components/SearchDropdown';
import { API_ERROR_CODE, PROVINCE, DATA_TYPE_NO_SPACE, ATTEND_DETAIL_STATUS } from '@constants';
import {
	getDailyTableList,
	getDailyTableHeader,
	exportDailyList,
	changeDailyStatus,
  // downTemplate,
  exportDailyListProvince,
} from '@/services/detailPage';
import { getUserRoleInfo } from '@/services/common';
import { tableColumns, tableSearchOptions } from './utils/options';
import { commonForm } from './utils/form';
import mystyle from './style/index.less';

const defaultTime = [
	moment().subtract(6, 'days'),
	moment(),
]

function getCookie(name) {
	let arr;
	const reg = new RegExp(`(^| )${name}=([^;]*)(;|$)`);
	if ((arr = document.cookie.match(reg))) {  // eslint-disable-line
	  return unescape(decodeURIComponent(arr[2]));
	}; 
	return '';
}

const userCode = getCookie('usercode') || 'admin';
const userName = getCookie('username') || 'admin';

class AttendDetailPage extends React.Component {
	state = {
		userRole: '',

		visible: false,
		batchVisible: false,
    importVisable: false,
    templateKey: '', // 上传的excel模板key，导出的时候要用
    excelSuffix: 'xlsx', // 导出的文件后缀名，省级是根据上传的模板来的
    loadingExp: false, // 导出按钮加载状态--省级

		detail: {},
		searchType: '1',
		dataSource: [],
		currentIndex: 0,
		queryParams: {},
		total: 0,
		statusList: ATTEND_DETAIL_STATUS.filter(i => i.code !== 1), // 状态列表，日常考勤没有误报
		columns: [],
		selectedRowKeys: [],
		isBatch: 0, // 是否批量修改

		uploadData: [], // 省级日常明细导入学生数据解析的值
		selectedOrg: [], // 所选的组织
	}

	UNSAFE_componentWillMount() {
		getUserRoleInfo().then(res => {
			if(res) {
				this.setState({
					userRole: res[0] && res[0].role_code || '',
				})
			}
		})
	}

	componentDidMount() {
		// 获取表头
		getDailyTableHeader().then(res => {
			if (res && res.error_code === API_ERROR_CODE) {
				this.setState({
					columns: res.data || [],
				},
					this.tableRef.dataLoad
				)
			} else {
				message.error(res && res.message || '未获取表头数据');
			}
		})
	}

	createPromise = param => {
		const params = this.initFlied(param);
		const result = { totalCount: 0, currentPageResult: [], pageIndex: 1 };

		return new Promise(resolve => {
			getDailyTableList(params).then(res => {
				if (res && res.error_code === API_ERROR_CODE) {
					const { data, paging } = res.data || {};
					result.totalCount = paging.total || 0;
					result.currentPageResult = data || [];
					result.pageIndex = paging.page_num;

					this.setState({
						total: paging.total || 0,
						dataSource: data || [],
					})
					resolve(result);
				} else {
					this.setState({
						total: 0,
					})
					resolve(result);
				}
			})
			.catch(() => {
				this.setState({
					total: 0,
				})
				resolve(result);
			});
		});
	};

	// 获取明细列表
	initFlied = (param) => {
		const { searchType, userRole } = this.state;
		const { pageIndex, pageSize, search = '', date = defaultTime, status, organization = {}, person_nos = [] } = param;
    const { selectedKeys, selectedNodes } = organization;

		const params = {
			page_size: pageSize,
			page_num: pageIndex,
		};

		if(status !== '') {
			// 日常--考勤状态筛选
			params.status_code = status;
		}
		// 省级日常考勤
		if(userRole === PROVINCE && person_nos.length > 0) {
			params.person_nos = person_nos;
		}

		if(selectedKeys && selectedKeys.length > 0) {
			// 日常--组织筛选
			params.org_list = selectedNodes.map(i => {
				return {
					index_path: i.index_path,
					org_index: i.org_index,
					sub_type: i.sub_type,
				}
			});
		}

		if(search && searchType === '1') {
			params.person_name = search.trim();
		} else if(search && searchType === '2') {
			params.person_no = search.trim();
		}

		if (date && date.length) {
			params.ats_start_time = moment(`${moment(date[0]).format('YYYY-MM-DD')} 00:00:00`).valueOf();
			params.ats_end_time = moment(`${moment(date[1]).format('YYYY-MM-DD')} 23:59:59`).valueOf();
		}
		this.setState({
			queryParams: {...params},
			selectedOrg: selectedKeys,
		})
		return params;
	}

	// 查看明细
	handleDetail = (data, index) => {
		this.setState({
			currentIndex: index,
			visible: true,
		})
	};

	// 打开修改状态的弹框 batch=1代表批量
	openStatusChange = (data, batch = 0) => {
		const { statusList } = this.state;
		// 将result名称替换成code
		const arr = [...statusList];
		const obj = data ? {...data} : null;
		if(obj) {
			const str = data.result || '';
			const target = arr.find(i => i.name === str) || {code: ''};
			obj.result = target.code;
		}

		this.setState({
			detail: obj || {},
			isBatch: batch,
			batchVisible: true,
		})
	}

  // 批量修改状态
  batchChangeStatus = () => {
		const { selectedRowKeys, isBatch, detail } = this.state;

		this.HzFormRef.submit().then(values => {
			if(values) {
				const ids = isBatch ? selectedRowKeys : [detail.id];
				const param = {
					ids,
					code: values.status,
					reason: values.reason,
				}

				changeDailyStatus(param).then(res => {
					if(res && res.error_code === API_ERROR_CODE) {
						this.setState({
							batchVisible: false,
							selectedRowKeys: [],
							// selectedRow: [],
						})
						this.tableRef.dataLoad();
					} else {
						message.error(res && res.message || '无法更新状态');
					}
				})
			}
		});
  }

	// value：1=全部 0=当前页
	handleExport = (value) => {
		const { queryParams, total, templateKey, userRole, excelSuffix } = this.state;
		if(total > 100000) {
			message.info('最大导出10万条数据', 3);
		}
		const params = {
			...queryParams,
    }
    let fun = exportDailyList;
    if(userRole === PROVINCE) {
      params.template_key = templateKey;
      fun = exportDailyListProvince;
    } else {
      params.export_all = !!value;
    }

		fun(params)
			.then(res => {
				if(res && !res.error_code) {
					const name = `考勤明细_${moment().format(DATA_TYPE_NO_SPACE)}.${excelSuffix}`;
					downloadFileStream(name, res);
				}
			})
			.catch(() => {
				message.error('导出失败');
			});
  };
  
    // 按钮栏
    renderCustomizeBtn = () => {
		const { total, uploadData, userRole, loadingExp, templateKey } = this.state;
		let afterBtn = [];
		// 只有省级日常明细才有上传
		if(userRole === PROVINCE) {
			let stu = '';
			if(uploadData.length > 0) {
				const arr = [...uploadData];
				const a = arr.splice(0,5);
				stu = a.join('、');
				stu = `${stu}等${uploadData.length}人考勤信息`;
			}

			afterBtn = [{
				elementType: "custom",
				render: () => {
					return (
						<>
							<Tooltip title="最多上传500人" placement='bottom'>
                <Upload
                  name="file"
                  headers={{
                    User: encodeURIComponent(`username:${userName}&usercode:${userCode}`),
                  }}
                  action={`/api/biz-scene/v1/attendance/result/export-template`}
                  onChange={(res) => {
                    // res: 导入接口返回的结果
                    const { file = {} } = res;
                    if(file.status === 'done') {
                      const { response, name = '' } = file;
                      const suffix = name.split('.') || [];
                      if (response && response.error_code === API_ERROR_CODE) {
                        const { data = {} } = response;
                        this.setState({
                          uploadData: data.person_names || [],
                          templateKey: data.template_key || '',
                          importVisable: false,
                          excelSuffix: suffix[suffix.length - 1] || 'xlsx',
                        }, () => {
                          this.tableRef.dataLoad({
                            person_nos: data.person_nos || [],
                          })
                        })
                      } else {
                        message.error(response && response.message || '无法解析数据');
                      }
                    }
                  }}
                >
                  <Button>
                    <Icon type="hz-export" theme="outlined" />
                    上传考勤学生
                  </Button>
                </Upload>
							</Tooltip>
							<span style={{color: '#ffb000', margin: '0 10px'}}>
								{stu}
								<Icon
									type="hz-delete"
									theme="outlined"
									style={{color: '#1890ff', marginLeft: '8px', cursor: 'pointer', display: uploadData.length > 0 ? 'inline-block' : 'none'}}
									onClick={() => {
										this.setState({
                      uploadData: [],
                      templatePath: '',
										}, () => {
											this.tableRef.dataLoad({
                        person_nos: [],
                        date: defaultTime,
											})
										})
									}}
								/>
							</span>
						</>
					);
				},
			}]
		}

        return [
          userRole === PROVINCE ? {
            antdProps: {
              icon: 'hz-export',
              children: '导出',
              disabled: total === 0 || !templateKey,
              onClick: this.handleExport,
              loading: loadingExp,
              title: "最大导出数量为 10万 条数据",
            },
          } : 
          {
              elementType: "custom",
              render: () => {
                return <ExportOptions itemNum={['data']} exportDisabled={total === 0} exportData={this.handleExport} />;
              },
          },
          ...afterBtn,
        ]
	}

	render() {
		const {
			columns,
			selectedRowKeys,
			queryParams,
			// importVisable,
			statusList,
			batchVisible,
			visible,
			dataSource,
			currentIndex,
			isBatch,
			selectedOrg,
			userRole,
      detail,
		} = this.state;

		const rowSelection = {
			onChange: (selectedRowKeys, selectedRow) => {
				this.setState({
					selectedRowKeys,
					// selectedRow,
				})
			},
			selectedRowKeys,
		}

		return (
			<div className={`hz-layout-vertical ${mystyle.detailTableWapper}`}>
				<div style={{ margin: 16 }} className="hz-layout-vertical-body">
					<Table
						rowKey='id'
						columns={tableColumns({
							userRole,
							type: 'daily',
							columns,
							handleDetail: this.handleDetail,
							handleStatusChange: this.openStatusChange,
						})}
						handleBarOptions={HandleBarOptions({
							handleStatusChange: userRole === PROVINCE ? () => this.openStatusChange(null, 1) : null,
							disabledStu: selectedRowKeys.length === 0,
              // searchPlaceholder: '学生姓名/学号',
              customizeBtn: this.renderCustomizeBtn(),
              customizeSearch: {
								render: () => (
									<SearchDropdown
										dropEvent={{
											onSelect: ({key}) => {
												this.setState({
													searchType: key,
												})
											},
										}}
										placeholder={this.state.searchType === '1' ? '学生姓名' : '学号'}
										onSearch={value => this.tableRef.dataLoad({ search: value })}
										onChange={evt => {
											const { value } = evt.target;
											if(value) {
												this.tableRef.updateQuery({ search: value });
											} else {
												this.tableRef.dataLoad({ search: value })
											}
										}}
										allowClear
									/>
								),
							},
						})}
						searchBarOptions={tableSearchOptions({
							type: 'daily',
							userRole,
							statusList,
							value: {
								time: defaultTime,
								defaultKeysArr: selectedOrg || [],
								defaultCheckTask: queryParams.task || [],
							},
						})}
						hasSerialNo
						hasFilter // 是否带有"漏斗"（以操作栏和筛选栏存在为前提），控制打开和收起筛选栏
						filterDefaultIsOpened // 筛选"漏斗"初始化状态是否开启
						serialNoOverlying // 序号是否累加
						createPromise={this.createPromise}
						setRef={tableRef => this.tableRef = tableRef}
						scrollX={columns.length * 150}
						antdProps={{
							rowSelection,
						}}
						forceFirstLoadDisabled  // 设置首次不加载，等表头获取完之后再加载
					/>
				</div>
				{/* 导入 */}
				
				{/* <ImportModal
					visible={importVisable}
					name="file"
					action={`/api/biz-scene/v1/attendance/auth/statistics/excel/analysis`}
					onCancel={() => {
						this.setState({
							importVisable: false,
						})
					}}
					handleDownload={async() => {
						downTemplate().then(res => {
							const name = `考勤明细上传学生导入模板.xlsx`;
							downloadFileStream(name, res);
						})
						.catch(() => {
							message.error('无法下载');
						});
					}}
					importCallback={(res) => {
						// res: 导入接口返回的结果
						if (res && res.error_code === API_ERROR_CODE) {
							const stu = res.data.map(i => i.person_no);
							this.setState({
								uploadData: res.data || [],
								importVisable: false,
							}, () => {
								this.tableRef.dataLoad({
                  person_nos: stu,
                  // date: [],
								})
							})
						} else {
							message.error(res && res.message || '无法解析数据');
						}
					}}
				/> */}

				{/* 修改状态 */}
				<Modal
					title={isBatch ? "批量状态更改" : "状态更改"}
					width={460}
					centered
					destroyOnClose
					maskClosable={false}
					visible={batchVisible}
					onOk={this.batchChangeStatus}
					onCancel={() => {
						// setBatchVisible(false);
						this.setState({
							batchVisible: false,
						})
					}}
				>
					<HzForm
						{...{
							labelCol: { span: 6 },
							wrapperCol: { span: 18 },
						}}
						fields={commonForm({
							statusList,
							statusValue: !isBatch && detail && detail.result,
						})}
						setRef={ref => this.HzFormRef = ref}
					/>
				</Modal>

				{/* 查询明细 */}
				{
					visible && (
						<AttendDetailModal
							visible={visible}
							dataArr={dataSource}
							currentIndex={currentIndex}
							userRole={userRole}
							onCancel={() => {
								this.setState({
									visible: false,
								})
							}}
						/>
					)
				}
			</div>
		);
	}
};

export default AttendDetailPage;
