/*
 * @Author: xiedan WX042
 * @since: 2020-12-18 14:48:18
 * @lastTime: 2021-01-20 16:14:05
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\utils\commonFields.js
 * @Description: 处置表单页
 */
import { Radio, Input } from 'antd';
const { TextArea } = Input;

 // 处置的通用表单  同意、拒绝、备注
 export const dealFields = (obj = {}) => {
     const { statusValue = '', required = true, commentMaxLength = 200 } = obj;
     return [
        {
            label: '处置结果',
            comp: {
                fieldName: 'status',
                component: (
                    <Radio.Group>
                        <Radio value='1'>同意</Radio>
                        <Radio value='2'>拒绝</Radio>
                    </Radio.Group>
                ),
                options: {
                    initialValue: statusValue,
                    rules: [{
                        required: required,
                        message: '请选择处理结果',
                    }],
                },
            },
        },
        {
            label: '处置备注',
            comp: {
                fieldName: 'comment',
                component: (
                    <TextArea rows={4} />
                ),
                options: {
                    initialValue: '',
                    rules: [{
                        validator: (rules, val, callBack) => {
                            if (val && val.length > commentMaxLength ) {
                                callBack(`处置备注不能超过${commentMaxLength}个字符`);
                            } else {
                                callBack();
                            }
                        },
                    }],
                },
            },
        },
    ]
}