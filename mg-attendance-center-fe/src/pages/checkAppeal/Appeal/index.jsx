/*
 * @Author: xiedan WX042
 * @since: 2020-12-03 16:47:26
 * @lastTime: 2021-10-13 15:20:43
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\checkAppeal\Appeal\index.jsx
 * @Description: 抽查申诉---省级、县市级
 */
import React, { useState } from 'react';
import { message } from 'antd';
import moment from 'moment';
import classNames from 'classnames';
import { Table } from '@hz-components/react-base';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import AppealDetail from '@components/AppealDetail';
import DealModal from '@components/DealModal';
import LeftTreeSelect from '@components/LeftTreeSelectCom';
import { getAppealList, dealAppeal, exportAppeal } from '@/services/appeal';
import { getUserRoleInfo } from '@/services/common';
import { API_ERROR_CODE, DATA_TYPE_NO_SPACE, SCHOOL_SUB_TYPE, SCHOOL } from '@constants';
import { downloadFileStream } from "@utils/utils";
import { tableColumns, tableSearchOptions } from './utils/options';
import mystyle from './style/index.less';
import { useEffect } from 'react';

const CheckAppeal = () => {
  const [userRole, setUserRole] = useState('');  // 用户角色
  const [visible, setVisible] = useState(false);  // 详情弹框显示
  const [modalVisible, setModalVisible] = useState(false); // 处置弹框
  const [detail, setDetail] = useState([]);  // 表格数据
  const [detailId, setDetailId] = useState([]); // 要处置的数据id集合
  const [queryParams, setQueryParams] = useState({});
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [total, setTotal] = useState(0);
  const [dealBtnLoading, setDealBtnLoading] = useState(false);
  const [TableRef, setTableRef] = useState(null);
  // const [defaultOrgKeys, setDefaultOrgKeys] = useState([]);
  const [activeTreeNode, setActiveTreeNode] = useState(null);

  useEffect(() => {
    getUserRoleInfo().then(res => {
      if (res) {
        // 用户有多个角色时，取最高级角色, 即 role_id 越小，角色等级越高
        // const role = _.minBy(res, 'role_id');
        setUserRole(res[0] && res[0].role_code || '');
        // setUserRole(SCHOOL)
      }
    })
  }, [])

  useEffect(() => {
    if (userRole) {
      TableRef.dataLoad()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userRole, activeTreeNode])

  const createPromise = params => {
    const init = initFlied(params);
    const result = { totalCount: 0, currentPageResult: [], pageIndex: 1 };
    return new Promise(resolve => {
      getAppealList(init).then(res => {
        if (res && res.error_code === API_ERROR_CODE) {
          const { paging, data } = res.data || {};
          setTotal(paging.total || 0)
          resolve({
            pageIndex: paging.page_num || 1,
            totalCount: paging.total || 0,
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
    console.log('activeTreeNode====::', activeTreeNode)
    const { pageIndex, pageSize, search = '', status, date, sorter } = info || {};
    const { order } = sorter || {};
    const obj = {
      page_size: pageSize,
      page_num: pageIndex,
      order_rule: order && order === 'descend' ? 'DESC' : 'ASC', // 默认升序
      order_field: 'status',
      search,
      status,
    };

    if (activeTreeNode) {
      const { index_path, org_index } = activeTreeNode;
      obj.org_index_paths = [`${index_path}${org_index}`];
    }
    if (date && date.length === 2) {
      obj.start_time = date[0] && moment(moment(date[0]).format('YYYY-MM-DD') + ' 00:00:00').valueOf();
      obj.end_time = date[1] && moment(moment(date[1]).format('YYYY-MM-DD') + ' 23:59:59').set('millisecond', 999).valueOf();
    }

    setQueryParams({ ...obj });
    return obj;
  };

  // 查看明细
  const handleDetail = (record) => {
    setDetailId(record && [record.id]);
    setDetail(record);
    setVisible(true);
  };

  // 处置
  const handleDeal = (record) => {
    const ids = record && [record.id] || selectedRowKeys;
    setDetailId(ids);
    setModalVisible(true);
  }

  // 处置操作
  const onConfirm = (values) => {
    setDealBtnLoading(true);
    if (values) {
      dealAppealReq(values)
    } else {
      setDealBtnLoading(false);
    }
  }

  // 处置req
  const dealAppealReq = (values) => {
    const { status, comment } = values;
    if (status) {
      const data = {
        ids: detailId,
        result: status,
        comment: comment || '',
      }

      dealAppeal(data).then(res => {
        if (res && res.error_code === API_ERROR_CODE) {
          message.success('操作成功');
          setModalVisible(false);
          setVisible(false);
          setDetailId([]);
          setSelectedRowKeys([])
          TableRef.dataLoad();
        } else {
          message.error(res && res.message || '无法操作');
        }
      }).finally(() => setDealBtnLoading(false))
    } else {
      setDealBtnLoading(false);
    }
  }

  const rowSelection = {
    onChange: selectedRowKeys => {
      setSelectedRowKeys(selectedRowKeys)
    },
    selectedRowKeys,
    // 已经处理的不需要再处理，只能审核下一级提交的申请
    getCheckboxProps: record => ({
      // disabled: record.status !== 2 || (userRole === PROVINCE && !record.is_province_school),
      disabled: record.status !== 2 || (SCHOOL_SUB_TYPE[record.school_parent_sub_type] !== userRole),
    }),
  }

  // value：1=全部 0=当前页
  const handleExport = () => {
    message.destroy();
    if (total > 100000) {
      message.info('最大导出10万条数据', 3);
    }
    const obj = {
      ...queryParams,
    }
    delete obj.page_num;
    delete obj.page_size;
    exportAppeal(obj).then(res => {
      const name = `抽查申诉_${moment().format(DATA_TYPE_NO_SPACE)}.xlsx`;
      downloadFileStream(name, res)
    }).catch(() => message.error('无法导出数据'));
  };

  const changeTreeNode = (node) => {
    setActiveTreeNode(node)
  }

  return (
    <div className="hz-layout-vertical">
      <div className={classNames(
        'hz-layout-horizontal',
        mystyle.mainWapper
      )}>
        <div className="hz-layout-fl" style={{
          width: 280,
          height: '100%',
          backgroundColor: '#fff',
        }}>
          {
            userRole ? (
              <LeftTreeSelect setActiveInfo={changeTreeNode} showSubType={userRole !== SCHOOL ? 4 : ''} />
            ) : null
          }
        </div>
        <div className="hz-layout-main" style={{ height: '100%' }}>

          <div className={`hz-layout-vertical ${mystyle.detailTableWapper}`}>
            <div className="hz-layout-vertical-body">
              <Table
                rowKey="id"
                columns={tableColumns({
                  userRole,
                  handleDetail,
                  handleDeal,
                })}
                handleBarOptions={HandleBarOptions({
                  searchPlaceholder: '申诉标题',
                  handleExport: handleExport,
                  disabledExp: total === 0,
                  customizeBtn: [{
                    antdProps: {
                      icon: 'hz-set',
                      children: '处置',
                      disabled: selectedRowKeys.length === 0,
                      onClick: () => handleDeal(null),
                    },
                  }],
                })}
                searchBarOptions={tableSearchOptions({
                  // value: {
                  //   defaultKeysArr: defaultOrgKeys || [],
                  // },
                  userRole,
                })}
                hasSerialNo
                serialNoOverlying
                hasFilter
                filterDefaultIsOpened
                createPromise={createPromise}
                setRef={tableRef => setTableRef(tableRef)}
                antdProps={{
                  rowSelection,
                }}
                forceFirstLoadDisabled
              />
            </div>

            {/* 查询申诉明细 */}
            {
              visible && (
                <AppealDetail
                  visible={visible}
                  onCancel={() => setVisible(false)}
                  detail={detail}
                  userRole={userRole}
                  onDeal={dealAppealReq}
                />
              )
            }

            {/* 修改状态弹框 */}
            {
              modalVisible && (
                <DealModal
                  visible={modalVisible}
                  loading={dealBtnLoading}
                  onOk={onConfirm}
                  onCancel={() => {
                    setModalVisible(false);
                    setDealBtnLoading(false);
                  }}
                />
              )
            }
          </div>
        </div>
      </div>
    </div>
  );
};

export default CheckAppeal;
