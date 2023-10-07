/**
 * 右侧表格
 */
import React, { useEffect, useState } from 'react';
import { Table } from '@hz-components/react-base';
import { message } from 'antd';
import moment from 'moment';

import AppealDetail from '@components/AppealDetail';
import {
  createHandleBarOptions,
  createSearchBarOptions,
	createColumns,
	createStudentColumns,
} from '../utils/options';
import { getOrgDetailByOrgIndex } from '@/services/common';
import { getStatisticsList, getStatisticsListOfClass, exportStatisticsList, exportStatisticsOfClass } from '@/services/appeal';
import { API_ERROR_CODE, DATA_TYPE_NO_SPACE } from '@constants';
import { downloadFileStream } from '@/utils/utils';

const CLASS_TYPE = 5; // 为5，表示班级，需要展示学生列表

function RightTable(props) {
	const {
    activeTreeNode = '', // 左侧点击的节点的org_index
    selectedNode = () => {},
    userRole, // 角色
    orgIndx, // 当前用户的组织
  } = props;
  
	// 表格 ref
  const [listRef, setListRef] = useState(null);
  // 当前触发的层级，为5时，表示展示学生列表
  const [currentType, setCurrentType] = useState('');
  const [paramData, setParamData] = useState({});
  const [activeInfo, setActiveInfo] = useState({});
  // 详情弹框
  const [visible, setVisible] = useState(false);
  const [detail, setDetail] = useState({});

	useEffect(() => {
    if(activeTreeNode) {
      // 重新请求节点详情，解决问题：在展示学生列表时，点击左侧节点请求数据正常，但是右侧表格数据不展示
      getOrgDetailByOrgIndex(activeTreeNode).then(res => {
        if(res) {
          setCurrentType(res.sub_type);
          setActiveInfo({
            ...res,
          })
        }
      })
    }

	// eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeTreeNode]);

	useEffect(() => {
    // console.log(activeInfo)
    if(activeInfo && activeInfo.org_index && listRef) {
      // 将节点回传父级，用于资源树回显
      selectedNode(activeInfo)

      listRef.dataLoad({
        org_index: activeInfo.org_index,
        pageIndex: 1,
        search: '',
      })
    }
	// eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeInfo.org_index]);

	/**
	 * 获取数据的请求 数据处理
	 */
	const transformQuery = params => {
    console.log('123========::', params)
		const {
			pageIndex: page_num,
      pageSize: page_size,
      org_index,
      status,
      search,
      search_s,
    } = params;

		const isStudent = currentType === CLASS_TYPE;

    const obj = {
			page_num,
      page_size,
      org_index: org_index || activeInfo.org_index,
      search: isStudent ? search_s : search,
    }
    if(isStudent && status) {
      obj.status = status;
    }
    setParamData(obj);
		return obj;
  }

	/**
	 * 获取数据
	 */
	const getData = async params => {
    const isStudent = currentType === CLASS_TYPE;

    const fun = !isStudent ? getStatisticsList : getStatisticsListOfClass;

		try {
			const result = await fun(params);
			const {
				data: {
					data: currentPageResult = [],
					paging: {
						page_num: pageIndex,
						total: totalCount,
					} = {},
				},
				error_code,
				message: msg,
			} = result || {};
			if (error_code === API_ERROR_CODE) {
				return {
					totalCount,
					currentPageResult,
					pageIndex,
				}
			}
			message.error(msg);
		} catch (err) {
			console.log(err);
		}
	}

	/**
	 * 导出
	 */
	const exportData = async () => {
		if (listRef.getList().length === 0) {
			return message.error('暂无数据');
		}

		const fun = currentType !== CLASS_TYPE ? exportStatisticsList : exportStatisticsOfClass;
    fun(paramData).then(res => {
      const name = `申诉统计_${moment().format(DATA_TYPE_NO_SPACE)}.xlsx`;
      downloadFileStream(name, res)
  }).catch(() => message.error('无法导出数据'));
	}

	/**
	 * 查看明细 省市区校班
	 */
	const handleItemDetails = row => {
		const {
			sub_type,
			org_index,
    } = row;
    setCurrentType(sub_type);
    
    // 获取当前点击的组织的详情
    getOrgDetailByOrgIndex(org_index).then(res => {
      if(res) {
        setActiveInfo({
          ...res,
        })
      }
    })
	}

	/**
	 * 考勤明细 学生
	 */
	const handleStudentItemDetails = row => {
    setVisible(true);
    const data  = {
      ...row,
      id: row.appeal_record_id,
    }
    setDetail(data);
	}

	/**
	 * 返回上一级
	 */
	const handleBack = async () => {
		const {
      org_index,
    } = paramData;

		// 根据当前节点的org_index掉接口查找详情，从而找出父级节点！
		try {
			const tree = await getOrgDetailByOrgIndex(org_index);
			const {
				org_parent_index: parentIndex,
      } = tree || {};
      // 根据找到的父级节点org_index获取父级组织详情
      getOrgDetailByOrgIndex(parentIndex).then(res => {
        if(res) {
          setCurrentType(res.sub_type);
          setActiveInfo({
            ...res,
          })
        }
      })
		} catch (err) {
			console.log(err);
		}
	}

	return (
    <>
      <Table
        // 不可省略  不然表格不会重新渲染
        // key={currentType !== CLASS_TYPE ? '1' : '5'}
        key={currentType || '5'}
        // key={activeInfo && activeInfo.sub_type || '5'}
        columns={currentType !== CLASS_TYPE ? createColumns(handleItemDetails) : createStudentColumns(handleStudentItemDetails)}
        transformQuery={transformQuery}
        createPromise={getData}
        hasSerialNo
        handleBarOptions={createHandleBarOptions(exportData, currentType === CLASS_TYPE, handleBack, paramData.org_index === orgIndx)}
        searchBarOptions={currentType === CLASS_TYPE && createSearchBarOptions({ userRole })}
        forceFirstLoadDisabled
        setRef={listRef => setListRef(listRef)}
      />
			{
				visible && (
          <AppealDetail
            visible={visible}
            onCancel={() => setVisible(false)}
            detail={detail}
            userRole={userRole}
          />
				)
			}
		</>
	);
}

export default RightTable;
