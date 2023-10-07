/*
 * @Author: xiedan WX042
 * @since: 2020-10-28 17:23:48
 * @lastTime: 2021-10-11 14:46:27
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\askForLeave\Apply\utils\tableOptions.jsx
 * @Description: 表格配置项
 */

import { Button } from 'antd';
import { Table } from '@hz-components/react-base';
import moment from 'moment';
import CommonTransfer from '@components/CommonTransfer';
import { LEAVE_TYPE, DATE_TYPE, LEAVE_STATUS, DATE_TYPE_DAY } from '@constants';
import styles from '../styles/index.less';

const { Ellipsis, RangePicker, EnumSelect } = Table;

/**
 * @description: 筛选配置
 * @param {*}
 * @return {*}
 */
export const createSearchBarOptions = (obj) => {
  const { defaultGrade = [] } = obj;

  return {
    trigger: null,
    conditions: [
      {
        label: "处理状态",
        render: (getFieldDecorator) => {
          return (
            getFieldDecorator("status", {
              initialValue: "",
            })(
              <EnumSelect
                hasAll
                list={LEAVE_STATUS}
              />
            )
          )
        },
      },
      {
        label: "请假类型",
        render: (getFieldDecorator) => {
          return (
            getFieldDecorator("type", {
              initialValue: "",
            })(
              <EnumSelect
                hasAll
                list={LEAVE_TYPE}
              />
            )
          )
        },
      },
      {
        label: '班级',
        render: getFieldDecorator => {
          return getFieldDecorator('department_codes')(
            <CommonTransfer
              type='organization'
              defaultKeys={defaultGrade}
              checkStrictly
              treeTarget={{}}
              overlayType='popover'
              searchSet={{
                placeholder: '搜索班级',
              }}
              getPopupContainer={null}
              customButtonValue={(value) => {
                return (
                  <Button style={{ width: 220 }}>
                    {
                      defaultGrade.length > 0 ? `已选${value || defaultGrade.length}个班级` : '全选'
                    }
                  </Button>
                )
              }}
            />,
          );
        },
      },
      {
        label: '请假时段',
        render: getFieldDecorator => {
          return getFieldDecorator('date', {
            initialValue: '',
          })(
            <RangePicker
              dropdownClassName="dateDropdown"
              format={DATE_TYPE_DAY}
              showTime={{ format: "HH:mm:ss" }}
            />,
          );
        },
      },
    ],
  }
}

/**
 * @description: 表格列
 * @param {*}
 * @return {*}
 */
export const tableColumns = (obj) => {
  const { handleEdit, deal } = obj;

  return [
    {
      title: '处理状态',
      dataIndex: 'status',
      width: '10%',
      render: (text) => {
        const map = LEAVE_STATUS;
        const target = map.find(item => text === item.code) || {};
        return <div style={{ color: target.color }}>{target.name || '--'}</div>
      },
      sorter: true,
      defaultSortOrder: 'ascend',
      sortDirections: ['descend', 'ascend'],
    },
    {
      title: '学生姓名',
      dataIndex: 'person_name',
      width: '10%',
      render: (text, record, index) => (
        <Ellipsis>
          <div className={styles.linkText} onClick={() => handleEdit(record, index)}>{text || '--'}</div>
        </Ellipsis>
      ),
    },
    {
      title: '学号',
      dataIndex: 'person_no',
      width: '10%',
      render: text => <Ellipsis>{text || '--'}</Ellipsis>,
    },
    {
      title: '学校',
      dataIndex: 'school_name',
      width: '15%',
      render: text => <Ellipsis>{text || '--'}</Ellipsis>,
    },
    {
      title: '班级',
      dataIndex: 'org_name',
      width: '12%',
      render: text => <Ellipsis>{text || '--'}</Ellipsis>,
    },
    {
      title: '请假类型',
      dataIndex: 'type',
      width: '10%',
      render: text => {
        const target = LEAVE_TYPE.find(item => text === item.code) || {};
        return <Ellipsis>{target.name || '--'}</Ellipsis>;
      },
    },
    {
      title: '请假时段',
      dataIndex: 'start_time',
      width: '10%',
      render: (text, record) => (
        <Ellipsis>
          { record.start_time && moment(record.start_time).format(DATE_TYPE_DAY) || '--'}
                    ~
          { record.end_time && moment(record.end_time).format(DATE_TYPE_DAY) || '--'}
        </Ellipsis>
      ),
    },
    // {
    //   title: '上报组织',
    //   dataIndex: 'org_path_name',
    //   width: '10%',
    //   render: (text) => <Ellipsis>{text || '--'}</Ellipsis>,
    // },
    {
      title: '上报人',
      dataIndex: 'creator_name',
      width: '10%',
      render: (text) => <Ellipsis>{text || '--'}</Ellipsis>,
    },
    {
      title: '审核人',
      dataIndex: 'approved_by',
      width: '10%',
      render: (text) => <Ellipsis>{text || '--'}</Ellipsis>,
    },
    // {
    //   title: '上报人角色',
    //   dataIndex: 'creator_role_name',
    //   width: '10%',
    //   render: (text) => <Ellipsis>{text || '--'}</Ellipsis>,
    // },
    {
      title: '上报时间',
      dataIndex: 'reported_at',
      width: '10%',
      render: text => <Ellipsis>{text && moment(text).format(DATE_TYPE) || '--'}</Ellipsis>,
    },
    {
      title: '操作',
      dataIndex: 'x',
      width: '10%',
      align: 'center',
      render: (t, record) => {
        const status = Number(record.status);
        return (
          <span
            className={styles.linkText}
            style={{
              color: status !== 2 && '#999',
              cursor: status !== 2 && 'not-allowed',
            }}
            onClick={() => { status === 2 && deal(record) }}
          >
            处置
          </span>
        )
      },
    },
  ]
}
