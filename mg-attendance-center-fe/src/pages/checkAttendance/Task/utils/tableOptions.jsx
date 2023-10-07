/*
 * @Author: xiedan WX042
 * @since: 2020-10-28 17:23:48
 * @lastTime: 2021-10-19 14:36:41
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\task\TaskManagement\utils\tableOptions.jsx
 * @Description: 表格配置项
 */
import { Table, EnumSelect } from '@hz-components/react-base';
import moment from 'moment';
import { DATE_TYPE, CHECK_ATTEND_TASK_STATUS, SCHOOL } from '@constants';
import styles from '../styles/index.less';

const { Ellipsis } = Table;

/**
 * @description: 筛选配置
 * @param {*}
 * @return {*}
 */
export const createSearchBarOptions = (obj) => {
  const { onChangeType = () => { }, yearEnum = [], monthEnum = [], value: { yearValue, termValue, monthValue } } = obj;
  console.log(obj, 'createSearchBarOptions')
  return {
    trigger: null,
    conditions: [
      {
        label: "状态",
        render: (getFieldDecorator) => {
          return (
            getFieldDecorator("status", {
              initialValue: "",
            })(
              <EnumSelect
                hasAll
                list={CHECK_ATTEND_TASK_STATUS}
              />
            )
          )
        },
      },
      {
        label: "学年",
        render: () => {
          return (
            <EnumSelect
              list={yearEnum}
              defaultValue={yearValue}
              value={yearValue}
              onChange={(e) => onChangeType(e, 'year')}
            />
          )
        },
      },
      {
        label: "学期",
        render: () => {
          return (
            <EnumSelect
              hasAll
              list={[
                { code: '1', name: '秋季' },
                { code: '0', name: '春季' },
              ]}
              defaultValue={termValue}
              value={termValue}
              onChange={(e) => onChangeType(e, 'term')}
            />
          )
        },
      },
      {
        label: "月份",
        render: () => {
          return (
            <EnumSelect
              hasAll
              list={monthEnum}
              defaultValue={monthValue}
              value={monthValue}
              onChange={(e) => onChangeType(e, 'month')}
            />
          )
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
  const { handleEdit, openStatisticsPage, confirmDelete, userCode, userRole } = obj;
  console.log(obj, 'tableColumns')
  return [
    {
      title: '状态',
      dataIndex: 'state',
      width: '7%',
      render: text => {
        const target = CHECK_ATTEND_TASK_STATUS.find(i => i.code === text) || {};
        return (
          <span style={{ color: target.color || '#ccc' }}>{target.name || '--'}</span>
        )
      },
      sorter: true,
      defaultSortOrder: 'ascend',
      sortDirections: ['descend', 'ascend'],
    },
    {
      title: '任务名称',
      dataIndex: 'name',
      width: '10%',
      render: (text, record) => {
        return <div className={styles.linkText} onClick={() => handleEdit(record)}><Ellipsis>{text || '--'}</Ellipsis></div>
      },
    },
    {
      title: '考勤日期',
      dataIndex: '$date',
      width: '15%',
      render: (text, record) => {
        const str = (record.start_date || record.end_date) ? `${record.start_date || '--'}~${record.end_date || '--'}` : '';
        return <Ellipsis>{str || '--'}</Ellipsis>;
      },
    },
    {
      title: '抽查天数',
      dataIndex: 'day_count',
      width: '7%',
      render: text => <Ellipsis>{text || '--'}</Ellipsis>,
    },
    {
      title: '考勤目标',
      dataIndex: 'target_org_names',
      width: '200px',
      render: text => <Ellipsis>{text || '--'}</Ellipsis>,
    },
    {
      title: '目标筛选',
      dataIndex: '$school',
      width: '10%',
      render: (text, record) => {
        const str = `${record.school_year || '--'}、${record.semester || '不限'}、${record.month || '不限'}，缺勤率≥${(record.minimum_absence_rate || record.minimum_absence_rate === 0) ? record.minimum_absence_rate : '--'}%`;
        return <Ellipsis>{str}</Ellipsis>
      },
    },
    {
      title: '创建组织',
      dataIndex: 'creator_org_name',
      width: '8%',
      render: text => <Ellipsis>{text || '--'}</Ellipsis>,
    },
    {
      title: '创建人',
      dataIndex: 'creator_name',
      width: '8%',
      render: text => <Ellipsis>{text || '--'}</Ellipsis>,
    },
    {
      title: '创建时间',
      dataIndex: 'created_at',
      width: '10%',
      render: text => <Ellipsis>{text && moment(text).format(DATE_TYPE) || '--'}</Ellipsis>,
    },
    {
      title: '操作',
      dataIndex: 'x',
      width: '10%',
      align: 'center',
      render: (t, record) => {
        return (
          <div>
            <span
              className={styles.linkText}
              style={{ marginRight: 14 }}
              onClick={() => {
                openStatisticsPage(record)
              }}>
              考勤统计
            </span>
            {
              userRole !== SCHOOL &&  (
                <span
                className={styles.linkText}
                style={{
                    marginRight: 14,
                    color: (record.state !== 1 || userCode !== record.creator_code) && '#999',
                    cursor: (record.state !== 1 || userCode !== record.creator_code) && 'not-allowed',
                }}
                onClick={() => { (record.state === 1 && userCode === record.creator_code) && confirmDelete(record.id) }}
                >
                  删除
                </span>
              )
            }
          </div>
        )
      },
    },
  ]
}
