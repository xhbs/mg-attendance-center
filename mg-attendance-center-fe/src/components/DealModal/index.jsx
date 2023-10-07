import React, { useState } from 'react';
import { Button, Modal } from 'antd';
import { HzForm } from '@hz-components/biz-scene-components';
import { dealFields } from "@utils/commonFields";

const DealModal = (props) => {
    const {
        title = '处置',
        width = 460,
        visible = false,
        onOk = () => {},
        onCancel = () => {},
        loading = false,
    } = props;

    const [hzFormRef, setHzFormRef] = useState(null);

    // 确认
    const onSubmit = () => {
        hzFormRef.submit().then(values => {
            onOk(values)
		});
    }
    
    // footer
    const renderFooter = () => {
        return (
            <React.Fragment>
                <Button
                    onClick={onSubmit}
                    type="primary"
                    style={{ marginRight: 16 }}
                    loading={loading}
                >
                    确认
				</Button>
                <Button onClick={onCancel}>取消</Button>
            </React.Fragment>
        );
    }

    return (

        <Modal
            title={title}
            width={width}
            centered
            destroyOnClose
            maskClosable={false}
            visible={visible}
            onCancel={onCancel}
            footer={renderFooter()}
        >
            <HzForm
                {...{
                    labelCol: { span: 6 },
                    wrapperCol: { span: 18 },
                }}
                fields={dealFields()}
                setRef={ref => setHzFormRef(ref)}
            />
        </Modal>
    )
}
export default DealModal;