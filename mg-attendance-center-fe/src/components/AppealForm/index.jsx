/*
 * @Author: xiedan WX042
 * @since: 2020-12-07 11:41:00
 * @lastTime: 2021-10-09 15:43:44
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\components\AppealForm\index.jsx
 * @Description: 新增考勤申诉、校级考勤明细的快捷申诉(功能废弃)
 */
import React from 'react';
import moment from 'moment';
import { Button, Form, Icon, Input, message, Upload, DatePicker } from 'antd';
import TransferTable from "@components/TransferTable";
import { disabledDateBeforeRange, downloadFileStream } from '@utils/utils';
import { uploadFile, regisFile } from "@services/common";
import { getAppealDetail, exportTemp } from "@services/appeal";
import { getDateConfig } from '@/services/businessConfig';
import { API_ERROR_CODE } from '@constants';
import styles from './index.less';

const { Item } = Form;
const { TextArea } = Input;
const { RangePicker } = DatePicker;

const FILE_TYPE = '.doc,.docx,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,.pdf';
const FILE_STU_TYPE = '.xlsx,.xls,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel';
const FILE_MAXSIZE = 30;

class AppealForm extends React.Component {
  state = {
    files: [], // 申诉文件
    filesStu: {}, // 导入学生名单
    formData: {
      title: '',
      content: '',
      person_nos: [],
      date: [],
    },
    validity: 7, // 抽查申诉有效期，最小1天
  }

  componentDidMount() {
    const { operationType } = this.props;
    if (operationType !== 'add') {
      // 获取详情
      this.getAppealDetail();
    }
    // 获取申诉有效期，用于时间段选择限制
    getDateConfig().then(res => {
      const { data, error_code } = res || {};
      if (API_ERROR_CODE === error_code) {
        const obj = data.find(i => i.key === 'maximum_appeal_period_days') || {};
        this.setState({
          validity: obj.value || 7,
        })
      }
    })
  }

  // 获取申诉详情
  getAppealDetail = () => {
    const { detail } = this.props;
    if (detail && detail.id) {
      getAppealDetail(detail.id).then(res => {
        if (res && res.error_code === API_ERROR_CODE) {
          const { data } = res;
          const start = data.start_time ? moment(data.start_time) : null;
          const end = data.end_time ? moment(data.end_time) : null;
          this.setState({
            files: data.files,
            formData: {
              title: data.title || '',
              content: data.content || '',
              person_nos: data.persons || [],
              date: [start, end],
            },
          })
        }
      })
    }
  }

  // 提交表单
  handleSubmit = (callback) => {
    const { form } = this.props;
    const { files, filesStu } = this.state;
    form.validateFieldsAndScroll((err, values) => {
      if (err) { return }
      if (!files || (files && files.length < 1)) {
        message.error('请上传附件文件');
        return false;
      }
      const { person_nos } = values;
      if ((!person_nos || person_nos.length === 0) && (!filesStu || !filesStu.file_path)) {
        message.error('请选择申诉学生或者直接导入学生名单');
        return false;
      }
      values.files = files;
      values.name_list_excel_path = filesStu.file_path;
      callback(values)
    })
  }

  // 上传图片  type: '1'--上传图片  '2'--导入学生名单
  uploadFile = (info, type) => {
    if (info) {
      const { file } = info;
      let fileType = file.type.includes('pdf') ? 'pdf' : 'docx';
      if (type === '2') {
        const arr = file.name && file.name.split('.') || [];
        const str = arr[arr.length - 1] || '';
        fileType = str || 'xlsx';
      }
      // 文件注册
      regisFile({
        file_name: file.name,
        file_type: fileType,
        file_size: file.size,
        file_path: `/biz/attendance/v2`,
      }).then(res => {
        if (res && res.id) {
          const formdata = new FormData();
          formdata.append('file', file);
          // 文件上传
          uploadFile({
            file_id: res.id,
            file: formdata,
          }).then(data => {
            if (data && !data.error_code) {
              if (type === '2') {
                this.setState({
                  filesStu: {
                    file_name: res.file_name || '', // 取注册成功返回的名称
                    file_path: res.file_url || '', // 取注册成功返回的地址
                  },
                })
              } else {
                this.setState({
                  files: [{
                    file_name: res.file_name || '', // 取注册成功返回的名称
                    file_path: res.file_url || '', // 取注册成功返回的地址
                  }],
                })
              }
              message.success('上传文件成功');
            } else {
              message.error(data && data.message || '无法上传文件');
            }
          })
        }
      })
    }
  };

  // 清楚上传的学生名单
  onDeleteStuFile = () => {
    this.setState({
      filesStu: {},
    })
  }

  render() {
    const { form, type = 'page', studentList = [] } = this.props;
    const { getFieldDecorator } = form;
    const { files, formData, validity, filesStu } = this.state;

    const formItemLayout = type === 'page' ? {
      labelCol: {
        xs: { span: 2 },
        sm: { span: 2 },
      },
      wrapperCol: {
        xs: { span: 16 },
        sm: { span: 16 },
      },
    } : {
        labelCol: { span: 4 },
        wrapperCol: { span: 20 },
      }

    // 上传文件的参数
    const UploadProps = {
      name: 'file',
      accept: FILE_TYPE,
      showUploadList: false,
      beforeUpload(info) {
        const { type, size } = info;
        const ty = type.split('/')[1];
        const isJpgOrPng = FILE_TYPE.indexOf(ty) > -1 && type !== '';
        if (!isJpgOrPng) {
          message.error('文件格式仅为word、pdf!');
        }
        const isLt2M = size / 1024 / 1024 < FILE_MAXSIZE;
        if (!isLt2M) {
          message.error(`文件大小必须小于${FILE_MAXSIZE}MB!`);
        }
        return isJpgOrPng && isLt2M;

      },
      customRequest: (info) => { this.uploadFile(info, '1') },
    };

    // 上传学生名单的参数
    const UploadStuProps = {
      name: 'file',
      accept: FILE_STU_TYPE,
      showUploadList: false,
      beforeUpload(info) {
        const { type, size } = info;
        const ty = type.split('/')[1];
        const isJpgOrPng = FILE_STU_TYPE.indexOf(ty) > -1 && type !== '';
        if (!isJpgOrPng) {
          message.error('请上传.xls或.xlsx类型的文件!');
        }
        const isLt2M = size / 1024 / 1024 < FILE_MAXSIZE;
        if (!isLt2M) {
          message.error(`文件大小必须小于${FILE_MAXSIZE}MB!`);
        }
        return isJpgOrPng && isLt2M;

      },
      customRequest: (info) => { this.uploadFile(info, '2') },
    };

    // 导入模板下载
    const tempDownload = () => {
      console.log('下载模板')
      exportTemp().then(res => {
        const name = `导入模板.xlsx`;
        downloadFileStream(name, res)
      }).catch(() => message.error('无法下载导入模板'));
    }

    return (
      <div className={styles.AppealWapper} style={{ padding: type === 'page' ? 16 : 0 }}>
        <Form {...formItemLayout} onSubmit={this.handleSubmit}>
          <Item label='申诉标题'>
            {getFieldDecorator('title', {
              initialValue: formData.title || '',
              rules: [{
                required: true,
                message: '申诉标题不能为空',
              }, {
                validator: (rules, val, callBack) => {
                  if (val && val.length > 100) {
                    callBack('申诉标题不能超过100个字符');
                    return;
                  }
                  if (val.indexOf(' ') > -1) {
                    callBack('申诉标题不能包含空格');
                    return;
                  }
                  callBack();
                },
              }],
            })(
              <Input style={{ width: 340 }} />
            )}
          </Item>
          <Item label='申诉内容'>
            {getFieldDecorator('content', {
              initialValue: formData.content || '',
              rules: [{
                required: true,
                message: '申诉内容不能为空',
              }, {
                validator: (rules, val, callBack) => {
                  if (val && val.length > 200) {
                    callBack(`申诉内容不能超过200个字符`);
                  } else {
                    callBack();
                  }
                },
              }],
            })(
              <TextArea rows={5} style={{ width: 600 }} />
            )}
          </Item>
          <Item label='附件文件'>
            {getFieldDecorator('file', {
              initialValue: files,
              validateTrigger: 'onBlur', // 失去焦点时校验
              rules: [{
                required: true,
                message: '附件文件不能为空',
              }],
            })(
              <Upload {...UploadProps}>
                <Button>
                  <Icon type="upload" />上传
                                </Button>
                <span style={{ marginLeft: '10px', color: '#999' }}>
                  {files && files[0] && files[0].file_name || ''}
                </span>
              </Upload>
            )}
          </Item>
          {/* 页面展示形式，学生为必选 */}
          {
            type === 'page' ? (
              <Item
                label={(
                  <>
                    <span style={{
                      display: 'inline-block',
                      marginRight: '4px',
                      color: '#fc2344',
                      fontSize: '14px',
                      fontFamily: 'SimSun, sans-serif',
                    }}>*</span>
                              申诉学生名单
                  </>
                )}
              >
                <Upload {...UploadStuProps}>
                  <Button><Icon type="hz-import" />导入</Button>
                </Upload>

                <span
                  style={{ marginLeft: '10px', color: '#5584ff', lineHeight: '32px', textDecoration: 'underline', cursor: 'pointer' }}
                  onClick={tempDownload}
                >
                  下载导入模板
                </span>
                <div>
                  <span style={{ color: '#999' }}>
                    {filesStu && filesStu.file_name || ''}
                  </span>
                    {
                      filesStu && filesStu.file_name && <Icon type='hz-delete' title='删除' style={{ marginLeft: '10px', color: '#1890ff' }} onClick={this.onDeleteStuFile} />
                    }
                </div>

                {getFieldDecorator('person_nos', {
                  initialValue: formData.person_nos || [],
                })(
                  <TransferTable defaultSelected={formData.person_nos} />
                )}
              </Item>
            ) : (
                // 老版本的校级考勤明细--快捷申诉弹框（新版本没有此功能）
                <Item label='申诉学生名单'>
                  {getFieldDecorator('person_nos', {
                    initialValue: studentList,
                  })(
                    <div className={styles.box}>
                      {
                        studentList.map(item => {
                          return <span key={item.person_no}>{item.person_name}({item.person_no})</span>
                        })
                      }
                    </div>
                  )}
                </Item>
              )
          }
          <Item label='考勤时段'>
            {getFieldDecorator('date', {
              initialValue: formData.date || '',
              rules: [{
                required: true,
                message: '考勤时段不能为空',
              }],
            })(
              <RangePicker
                disabledDate={(current) => disabledDateBeforeRange(current, validity)}
                format="YYYY-MM-DD"
                getCalendarContainer={triggerNode => triggerNode.parentNode}
              />
            )}
          </Item>
        </Form>
      </div>
    )
  }
}

export default Form.create()(AppealForm);
