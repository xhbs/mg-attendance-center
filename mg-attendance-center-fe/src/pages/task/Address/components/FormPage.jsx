import React from 'react';
import { Form, message } from 'antd';
import CommonTransfer from '@components/CommonTransfer';
import { EnumSelect } from '@hz-components/react-base';
import { addDevices } from '@services/deviceService';
import { API_ERROR_CODE } from '@constants';
import styles from '../styles/index.less';

const { Item } = Form;

const formItemLayout = {
    labelCol: {
      xs: { span: 3 },
      sm: { span: 3 },
    },
    wrapperCol: {
      xs: { span: 16 },
      sm: { span: 16 },
    },
};

class FormPage extends React.Component {
    constructor() {
        super();
        this.state = {
            sourceTreeObj: [],
            type: 1,
            defaultSourceTreeObj: [],
        }
    }

    // 获取资源树数据
    getTreeData = (data) => {
        this.setState({
            sourceTreeObj: data.selectedNodes || [],
        })
    }

    // 提交表单
    handleSubmit = () => {
        const { form } = this.props;
        const { sourceTreeObj } = this.state;
        form.validateFieldsAndScroll((err, values) => {
            if (!err) {
                if(sourceTreeObj.length === 0) {
                    return message.error('考勤地点不能为空')
                }
                const params = this.getParams(values);
                this.addDevicesReq(params);
            }
        });
    };

    // 构造参数
    getParams = (values) => {
        let params = [];
        const { operationType, detailId, selectedGroup, selectedDevicesType, isHigh } = this.props;
        const { sourceTreeObj } = this.state;
        sourceTreeObj.forEach(item => {
            params.push({
                id: operationType === 'add' ? '' : detailId,
                sub_type: selectedDevicesType,
                type: isHigh ? values.type : 1, // 云南中职默认签到
                channel_code: item.resource_code || '',
                device_code: item.parent_resource_code || '',
                group_id: selectedGroup || '',
                name: item.resource_name || '',
            })
        })
        return params;
    }

    // 新增考勤点
    addDevicesReq = (params) => {
        addDevices(params).then(res => {
            if(res && res.error_code === API_ERROR_CODE) {
                message.success('操作成功');
                const { onFormPageClose } = this.props;
                onFormPageClose();
            }
        })
    }

    render() {
        // isHigh:是否高级  false--云南中职  true--原来的功能
        const { form, sourceData, selectedDevicesType, isHigh } = this.props;
        const { getFieldDecorator } = form;
        const { defaultSourceTreeObj, type } = this.state;

        return (
            <div style={{ padding: 16 }}>
                <Form {...formItemLayout} onSubmit={this.handleSubmit}>
                    {/* 云南中职屏蔽 */}
                    {
                        isHigh && (
                            <Item label='考勤点类型'>
                                {getFieldDecorator('type', {
                                    initialValue: type || 1,
                                    rules: [{
                                        required: true,
                                        message: '请选择考勤点类型',
                                    }],
                                })(
                                    <EnumSelect
                                        style={{width: 340}}
                                        placeholder="请选择"
                                        createPromise={() => {
                                            return new Promise(resolve => {
                                                resolve([
                                                    { code: 1, name: '签到' },
                                                    { code: 2, name: '签退' },
                                                ]);
                                            });
                                        }}
                                    />
                                )}
                            </Item>
                        )
                    }

                    <Item label={<label className={styles.label}>考勤点设备选择</label>}>
                        <CommonTransfer
                            type='device'
                            getTreeParams={{
                                lazy_load: false,
                                type: isHigh && `6_${selectedDevicesType}` || '6_1,6_4',
                                // type: isHigh && `6_${selectedDevicesType}` || '6_1,6_4,6_5',
                            }}
                            getTransferSelectedData={this.getTreeData}
                            defaultKeys={defaultSourceTreeObj}
                            sourceData={sourceData}
                            treeNodeRender={(nodeData) => {
                                // 只能选择 available=true 的数据
                                return {
                                    disableCheckbox: !nodeData.available,
                                    disabled: !nodeData.available,
                                }
                            }}
                            height={600}
                        />
                    </Item>
                </Form>
            </div>
        )
    }
}

export default Form.create()(FormPage);