import { Table } from '@hz-components/react-base';
import styles from '../styles/index.less';
const {EnumSelect,Ellipsis}=Table
//表格列标题
export const tableColumnsHight = (obj) => {
  const { openDetailPage } = obj;
  return [
    {
      title: '学校名称',
      dataIndex: 'school_org_name',
      width: '10%',
      render: text => <Ellipsis>{text || '--'}</Ellipsis>,
    },
    {
      title: '所属组织',
      dataIndex: 'index_path_name',
      width: '10%',
      render: text => <Ellipsis>{text || '--'}</Ellipsis>,
    },
    {
      title: '考勤日期',
      dataIndex: 'attendance_date',
      width: '10%',
      render: text => <Ellipsis>{text || '--'}</Ellipsis>,
    },
    {
      title: '抽查学生总数',
      dataIndex: 'student_num',
      width: '10%',
      render: text => <Ellipsis>{String(text) || '--'}</Ellipsis>,
    },
    {
      title: '缺勤率',
      dataIndex: 'absence_rate',
      width: '10%',
      render: text => <Ellipsis>{text.toFixed(2) + '%' || '--'}</Ellipsis>,
      sorter: true,
      defaultSortOrder: 'ascend',
      sortDirections: ['descend', 'ascend'],
    },
    {
      title: '缺勤学生',
      dataIndex: 'absence_num',
      width: '10%',
      render: text => <Ellipsis>{String(text) || '--'}</Ellipsis>,
    },
    {
      title: '在校学生',
      dataIndex: 'normal_num',
      width: '10%',
      render: text => <Ellipsis>{String(text) || '--'}</Ellipsis>,
    },
    {
      title: '请假学生',
      dataIndex: 'leave_num',
      width: '10%',
      render: text => <Ellipsis>{String(text) || '--'}</Ellipsis>,
    },
    {
      title: '实习学生',
      dataIndex: 'practice_num',
      width: '10%',
      render: text => <Ellipsis>{String(text) || '--'}</Ellipsis>,
    },
    {
      title: '操作',
      dataIndex: 'action_school',
      width: '10%',
      // align: 'center',
      render: (text, record) => (
        <Ellipsis>
          <div className={styles['linkText']} onClick={()=>openDetailPage(record)}>考勤明细</div>
        </Ellipsis>
      ),
    },
  ]
}


/**
* @description: 筛选配置
* @param {*}
* @return {*}
*/
//筛选栏
export const createSearchBarOptions = (obj={})=>{
  const { attendanceDateList } = obj;

  return {
    trigger: null,
    conditions: [{
      label: "考勤日期",      
      render: (getFieldDecorator) => {
        return (
            getFieldDecorator("attendance_date", {
                initialValue: "",
            })(
                <EnumSelect
                  hasAll
                  list={attendanceDateList}
                />
            )
        )
      },
    },
  ],      
  }  
}

//操作栏
export const createHandleBarOptions = (obj) => {
  const { exportData }=obj
	return {
		handleOptions: {
			elements: [
				{
					antdProps: {
						icon: 'hz-import',
						children: '导出',
						onClick: exportData,
					},
				},
			],
		},
		searchOptions: {
			searchKey: 'school_name',
			antdProps: {
        allowClear: true,
				placeholder: '学校名称',
			},
		},
	}
}