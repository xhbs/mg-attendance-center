import React from 'react'
import { Table } from '@hz-components/react-base';
import {Button,Modal,message} from 'antd';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import {tableColumnsHight,createSearchBarOptions} from './utils/tableOptions';
import { CreateDrawer } from '@hz-components/ists-common';
import ReactScrollbar from 'react-custom-scrollbars';
import FormPage from './components/FormPage'
import {getHolidayList,deleteHoliday} from '@services/holidayManageService'
import { API_ERROR_CODE } from '@constants';

// const { 
//   Ellipsis, 
//   EnumSelect,
//   ValidateWrapper,
//   OPERATE_SPAN,
//   VALIDATE_TIPS_TYPE_NORMAL,
//   VALIDATE_TIPS_TYPE_POPOVER,
// } = Table;

export default class PreviewHzTable extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      listRef: null,
      selectedRowKeys: [],
      // selectedRows: [],
      visible: false,  // 表单页显示隐藏
      operationType: 'add',//表单页标题类型
      editRowData: undefined, // 待编辑的表格行
      isLoading: false, // 表单页面确认按钮是否加载中
    }    
  }

  componentWillUnmount() {
    this.setState = () => {}  
  }

/**
 * 分页查询考勤点列表
 * @param query
 */
createPromise = (params) => {
  const init = this.initFlied(params);
  return new Promise(resolve => {
    getHolidayList(init).then(res => {
      if(res&&res.error_code===API_ERROR_CODE) {
        const { data, paging } = res.data;          
        data.forEach(item=>{
          item['start_end']=item.start_date+'~'+item.end_date
        })            
        resolve({
            pageIndex: paging && paging.page_num || 1,
            totalCount: paging && paging.total || 0,
            currentPageResult: data || [],
        });
      }
    })
  })
};

/**
 * @description: 构造表格检索参数
 * @param info
 */
 initFlied = (info) => {
  let { pageIndex, pageSize, search = '', type } = info || {};
  let obj = {
      page_size: pageSize,
      page_num: pageIndex,
      name: search.trim(),
  };
  if(parseInt(type)){
    obj={...obj,type:parseInt(type)}
  }
  console.log(obj);
  return obj;
};


//新增功能 开启新增表单页
handleAdd = () => {
  this.setState({
      visible: true,
      operationType: 'add',
      editRowData: undefined,
  })    
}

 //确认模态框删除操作项
 handleDel = (ids) => {
  const idsArr = Array.isArray(ids) && ids || [ids];
  deleteHoliday(idsArr).then(res => {
      if(res && res.error_code === API_ERROR_CODE) {
          message.success('删除成功')
          this.setState({ selectedRowKeys: [] })
          this.listRef.dataLoad();
      }
  })
}

// 批量删除
batchDelete = () => {
  const { selectedRowKeys } = this.state;
  const ids = selectedRowKeys || []
  this.confirmDelete(ids)
}

// 点击单个数据的删除确认
confirmDelete = ids => {
  const options = {
      title: '确定删除数据吗？',
      content: '此操作不可逆，请慎重考虑。',
      okText: '删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: () => {
          this.handleDel(ids)
      },
  }
  Modal.confirm(options)
}

//关闭弹窗抽屉
onCancel=()=>{
  this.setState({visible:false})
}
//编辑操作存储单行数据
handleEdit = (data) => {
  // console.log(data);
  this.setState({
      editRowData: data,
      visible: true,
      operationType: 'update',
  })
  // console.log(this.state.editRowData);
}

//自定义渲染弹窗抽屉底部
renderFooter = () => {
  const { isLoading } = this.state;
  return (
  <React.Fragment>
    <Button    
      type="primary"
      style={{ marginRight: 16 }}
      loading={isLoading}
      onClick={this.onConfirm}
    >
      保存
    </Button>
    <Button onClick={this.onCancel}>取消</Button>
  </React.Fragment>
  );
}

  // 点击确认调用表单提交
  onConfirm = () => {
    // 确认按钮先变成loading状态
    this.setState({ 
        isLoading: true,
    }, () => {
        const { handleSubmit } = this.FormRefs;
        //调用handleSubmit时传入callback，再通过传回的值进行更新isLoading的state
        handleSubmit((v) => {
            if(v) {
                this.setState({
                    isLoading: false,
                })
            }
        });
    });
  }

  //表单页面新增后刷新表格
  reloadTable=()=>{
    this.listRef.dataLoad()
  }

  render() {
    let {visible,selectedRowKeys,operationType,editRowData}=this.state
    const rowSelection = {
			onChange: selectedRowKeys => {
			  this.setState({ selectedRowKeys })
			},
			selectedRowKeys,
		}
    return (
      <>     
        <div className="hz-layout-vertical">
          <div className="hz-layout-vertical-body" style={{margin: 16}}>
            <Table
              rowKey="id"
              columns={tableColumnsHight({
                confirmDelete: this.confirmDelete,
                handleEdit: this.handleEdit,
              })}              
              createPromise={this.createPromise}
              
              hasSerialNo
              serialNoOverlying
              setRef={ref => {
                  this.listRef = ref;
              }}
              searchBarOptions={createSearchBarOptions}
              handleBarOptions={HandleBarOptions({
                  handleAdd: this.handleAdd,
                  handleDel: this.batchDelete,
                  disabledDel: this.state.selectedRowKeys.length === 0,
                  searchPlaceholder: '节假日',
              })}
              antdProps={{
                  rowSelection,
              }}             
            />
          </div>
          {/* 抽屉弹窗 */}
          <CreateDrawer
            visible={visible}
            title={operationType === 'add' ? '新建' : '编辑'}
            handleConfirm={this.onConfirm}
            handleCancel={this.onCancel}
            footer={this.renderFooter()}
          >
            <ReactScrollbar renderTrackHorizontal={() => <div />}>
              <FormPage
                onFormPageClose={this.onCancel}
                wrappedComponentRef={ref=>{this.FormRefs = ref}}
                reloadTable={this.reloadTable}
                editRowData={editRowData}
              />
            </ReactScrollbar>
          </CreateDrawer>
        </div>
      </>
    )
  }
}


