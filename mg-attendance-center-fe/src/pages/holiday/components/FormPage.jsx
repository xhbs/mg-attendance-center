import React, { Component } from 'react';
import {Form,Input,Select,message,DatePicker} from 'antd';
import {addHolidayType,updateHolidayType} from '@services/holidayManageService';
import { API_ERROR_CODE} from '@constants';
import moment from 'moment';
const {RangePicker}=DatePicker
const {Option}=Select
const formLayout={
  labelCol: {span:2},
  wrapperCol: {span:8},
}
const showTime = 'HH:mm:ss';
class FormPage extends Component {
  state={
    end_date:[],
  }
  handleSubmit=(callback)=>{
    const {form,onFormPageClose,reloadTable,editRowData}=this.props
    form.validateFields(async(err,{name,type,start_end})=>{      
      if(!err){
        let start_date=start_end[0].format('YYYY-MM-DD')
        let end_date=start_end[1].format('YYYY-MM-DD')
        let id=0
        if(editRowData){
          id=editRowData.id
        }
        if(id){//编辑
          type=type==='法定节假日'||type==='1' ? 1 : 2
          let res=null
          try{
            res=await updateHolidayType({id,name,type,start_date,end_date})
          }catch(err){
            res=err
          }
          if(res&&res.error_code===API_ERROR_CODE){
            message.success(res.message)
            onFormPageClose()
            reloadTable()
            callback(true)
          }else if(res&&res.error_code!==API_ERROR_CODE){
            callback(true)
          }else if(res===null){
            callback(true)
          }
        }else{//新增
          console.log(name);
          type=Number(type)
          let res=null
          try{
            res=await addHolidayType({name,type,start_date,end_date}) 
          }catch(err){
            res=err
          }                   
          if(res&&res.error_code===API_ERROR_CODE){
            message.success(res.message)
            onFormPageClose()
            reloadTable()
            if(callback){
              callback(true)
            }
          }else if(res&&res.error_code!==API_ERROR_CODE){
            callback(true)
          }else if(res===null){
            callback(true)
          }    
        }        
      }else{
        callback(true)
      }
    })
  }
  componentDidMount(){
    let {editRowData,form}=this.props
    if(editRowData===undefined){
      form.resetFields()
    }else{
      let {name,type,start_date,end_date}=editRowData
      type=type===1 ? '法定节假日' : '自定义节假日'
      let start_end=[moment(start_date,'YYYY-MM-DD'),moment(end_date,'YYYY-MM-DD')]
      form.setFieldsValue({name,type,"start_end":start_end})      
    }
  }

  render() {
    let {getFieldDecorator}=this.props.form
    return (     
      <>
        <Form onSubmit={this.handleSubmit} {...formLayout} style={{paddingTop:'15px'}}>
          <Form.Item label="节假日">
            {
              getFieldDecorator('name',{
                initialValue: '',
                rules:[{required:true,message:'节假日名称不能为空',whitespace:true}],
              })
              ( 
                <Input placeholder="请输入"/>
              )
            }
          </Form.Item>
          <Form.Item label="节假日类型">
            {
              getFieldDecorator('type',{                
                rules:[{required:true,message:'请选择节假日类型'}],
              })
              (
                <Select placeholder="请选择">
                  <Option value="1">法定节假日</Option>
                  <Option value="2">自定义节假日</Option>
                </Select>
              )
            }            
          </Form.Item>
          <Form.Item label="日期选择">
            {
              getFieldDecorator('start_end',{
                initialValue: '',
                rules:[{required:true,message:'请选择日期范围'}],
              })
              (               
                <RangePicker
                  showTime={showTime}
                  dropdownClassName="dateDropdown"
                  format={'YYYY-MM-DD'}
                />
              )
            }            
          </Form.Item>
        </Form>        
      </>
    );
  }
}
const WrapperForm=Form.create()(FormPage)
export default WrapperForm;
