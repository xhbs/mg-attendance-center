import React, { Component } from 'react';
import {Form,Switch,Button,Checkbox,message,Spin} from 'antd';
// import classNames from 'classnames';
import {updateDailyAttend,getDailyAttend} from '@services/dailyAttendService'
import { API_ERROR_CODE } from '@constants';
const formLayout={
  labelCol: {span:2},
  wrapperCol: {span:12},
}
class DailySetting extends Component {
  handleReset=()=>{
    let {setFieldsValue}=this.props.form
    setFieldsValue({
      enable:true,
      checkboxGroup:['exclude_weekends','exclude_holidays','exclude_custom_dates'],
    })
  }
  handleSubmit=(e)=>{
    e.preventDefault();
    this.setState({loading:true})
    let {enable,checkboxGroup}=this.props.form.getFieldsValue()
    let exclude_weekends=checkboxGroup.includes('exclude_weekends')
    let exclude_holidays=checkboxGroup.includes('exclude_holidays')
    let exclude_custom_dates=checkboxGroup.includes('exclude_custom_dates')
    let params={enable,exclude_weekends,exclude_holidays,exclude_custom_dates}
    updateDailyAttend(params).then(res=>{
      if(res&&res.error_code===API_ERROR_CODE){
        message.success(res.message)
      }
      this.setState({loading:false})
    })
  } 
  state={
    enable:true,
    checkboxGroup:['exclude_weekends','exclude_holidays','exclude_custom_dates'],
    loading:false,
  }
  async componentDidMount(){
    this.setState({loading:true})
    let res=await getDailyAttend()
    if(res&&res.error_code===API_ERROR_CODE){
      let {enable,exclude_weekends,exclude_holidays,exclude_custom_dates}=res.data
      exclude_weekends=exclude_weekends===true?'exclude_weekends':''
      exclude_holidays=exclude_holidays===true?'exclude_holidays':''
      exclude_custom_dates=exclude_custom_dates===true?'exclude_custom_dates':''
      this.setState({
        enable,
        checkboxGroup: [exclude_weekends,exclude_holidays,exclude_custom_dates],
        loading:false,
      })
    }
  }
  render() {
    let {getFieldDecorator}=this.props.form
    let {enable,checkboxGroup,loading}=this.state
    return (          
      <div className="hz-layout-vertical">
        <div className="hz-layout-vertical-body" style={{backgroundColor:'#fff',margin:'16px',borderRadius:'8px',paddingTop:'20px'}}>
          <Spin spinning={loading} size="large"> 
            <Form onSubmit={this.handleSubmit} {...formLayout}>
              <Form.Item label="日常考勤" >
                {
                  getFieldDecorator('enable',{
                    valuePropName: 'checked',
                    initialValue: enable,
                  })
                  ( 
                    <Switch checkedChildren="开" unCheckedChildren="关" />
                  )
                }
              </Form.Item>
              <Form.Item label="不考勤日期">
                {
                  getFieldDecorator('checkboxGroup',{initialValue:checkboxGroup} )
                  (
                    <Checkbox.Group style={{width: '100%'}}>
                      <Checkbox value="exclude_weekends">周末</Checkbox>
                      <Checkbox value="exclude_holidays">法定节假日</Checkbox>
                      <Checkbox value="exclude_custom_dates">自定义节假日</Checkbox>
                    </Checkbox.Group>
                  )
                }  
              </Form.Item>
              <Form.Item wrapperCol={{span:16,offset:2}}>
                  <Button type="primary" shape="round" htmlType="submit" style={{marginRight:'15px'}} >保存</Button>
                  <Button type="primary" shape="round" htmlType="button" ghost onClick={this.handleReset}>重置</Button>
              </Form.Item>
            </Form>
          </Spin>        
        </div>
      </div>      
    )
  }
}
const WrapperForm=Form.create()(DailySetting)
export default WrapperForm;
