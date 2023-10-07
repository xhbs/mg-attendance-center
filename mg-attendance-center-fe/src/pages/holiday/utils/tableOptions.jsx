import { Table } from '@hz-components/react-base';
import styles from '../styles/index.less';
const {EnumSelect,Ellipsis}=Table
//表格列标题
export const tableColumnsHight = (obj) => {
  const { confirmDelete,handleEdit } = obj;
  return [
    {
      title: '节假日',
      dataIndex: 'name',
      width: '20%',
      render: (text, record) => (
      <Ellipsis>
        <div className={styles['linkText']} onClick={()=>handleEdit(record)}>{text}</div>
      </Ellipsis>
      ),
    },
    {
      title: '节假日类型',
      dataIndex: 'type',
      width: '30%',
      render: text => <Ellipsis>{text===1?'法定节假日':'自定义节假日'}</Ellipsis>,
    },
    {
      title: '日期',
      dataIndex: 'start_end',
      width: '30%',
      render: text => <Ellipsis>{text}</Ellipsis>,
    },
    {
      title: '操作',
      dataIndex: 'action',
      width: '15%',
      render: (text,record)=>{
        return (
          <div className={styles.linkText} onClick={()=>{ confirmDelete(record.id) }}>删除</div>
        )
      },
    },
  ]
}


/**
* @description: 筛选配置
* @param {*}
* @return {*}
*/
export const createSearchBarOptions = {
  trigger: null,
  conditions: [{
      label: "法定节类型",
      render: (getFieldDecorator) => {
          return (
              getFieldDecorator("type", {
                  initialValue: "",
              })(
                  <EnumSelect
                    hasAll
                    list={[
                        {code: '1', name: '法定节假日'},
                        {code: '2', name: '自定义节假日'},
                    ]}
                  />
              )
          )
      },
  }],
}