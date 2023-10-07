import { Table } from '@hz-components/react-base';
import styles from '../styles/index.less';
import { ATTENDANCE_STATUS} from '@constants';
import {Icon} from 'antd'
const {EnumSelect,Ellipsis}=Table
//表格列标题
export const tableColumnsHight = (obj={}) => {
  const { openPopupPage } = obj;
  return [
    {
      title: '考勤状态',
      dataIndex: 'result',
      width: '20%',
      render: text => {
        const target=ATTENDANCE_STATUS.find(item=>item.code===text)||{}
        return(
          <span style={{color: target.color || '#ccc'}}>{target.name || '--'}</span>
        )
      },
      sorter: true,
      defaultSortOrder: 'ascend',
      sortDirections: ['descend', 'ascend'],
    },
    {
      title: '学生姓名',
      dataIndex: 'person_name',
      width: '20%',
      render: text => <Ellipsis>{text || '--'}</Ellipsis>,
    },
    {
      title: '学号',
      dataIndex: 'person_no',
      width: '20%',
      render: text => <Ellipsis>{text || '--'}</Ellipsis>,
    },
    {
      title: '班级',
      dataIndex: 'class_name',
      width: '20%',
      render: text => <Ellipsis>{text || '--'}</Ellipsis>,
    },
    {
      title: '操作',
      dataIndex: 'action',
      width: '20%',
      // align: 'center',
      render: (text, record) => (
          <span className={styles['linkText']} onClick={()=>openPopupPage(record)}>考勤详情</span>
      ),
    },
  ]
}

/**
* @description: 筛选配置
* @param {*}
* @return {*}
*/


// 筛选栏
export const createSearchBarOptions = (obj={})=>{
  return {
    trigger: null,
    conditions: [{
      label: "考勤状态",
      render: (getFieldDecorator) => {
        return (
          getFieldDecorator("result", {
              initialValue: "",
          })(
              <EnumSelect
                hasAll
                list={ATTENDANCE_STATUS}
              />
          )
        )
      },
    }],     
  }  
}

//操作栏
export const createHandleBarOptions = (props) => {
  const {onCancel,detailTaskName,exportData}=props
	return {
		handleOptions: {
			elements: [
        {
          elementType: 'custom',
          render:()=>(
            <span
              style={{            
                height: '32px',
                // marginRight: '10px',
              }}
            >
              <Icon
                type='hz-operation-back'
                style={{                  
                  cursor: 'pointer',
                  color: '#5584ff',
                  marginRight: 10,
                  fontSize: 24, 
                  transform: 'translateY(4px)',
                }}                
                onClick={onCancel}
              />
              <span>抽查考勤明细 ({detailTaskName})</span>              
          </span>
          ),
        },
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
			searchKey: 'search_key',
			antdProps: {
        allowClear: true,
				placeholder: '学生姓名/学号/班级',
			},
		},
	}
}

//详情弹窗列标题
export const tableColumns = [
	{
		title: '序号',
		key: 'order',
		render: (text, record, index) => index + 1,
	},
	{
		title: '抓拍时间',
		dataIndex: 'captured_at',
		key: 'captured_at',
		render: text => <span>{text || '--'}</span>,
		ellipsis: true,
	},
	{
		title: '抓拍地点',
		dataIndex: 'device_name',
		key: 'device_name',
		render: text => <span>{text || '--'}</span>,
		ellipsis: true,
	},
];