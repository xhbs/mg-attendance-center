/*
 * @Author: xiedan WX042
 * @FilePath: Do not edit
 * @Description: 查看大图
 * @since: 2021-07-12 10:49:43
 * @lastTime: 2021-07-13 17:49:56
 */
import React from 'react';
import { Modal } from 'antd';
import emptyImg from '@hz-design/base/public/load-empty-icon.svg';
import styles from './index.less';

const BigImgModal = (props) => {
  const { visible, currentPic = '', onCancel = ()=>{} } = props;

  return (
    <Modal
      title='查看大图'
      visible={visible}
      width="80vw"
      onCancel={onCancel}
      footer={null}
      wrapClassName={styles.bigImgModal}
      style={{ minWidth: '600px', top: '50px', height: 'calc(100% - 100px)' }}
    >
      <div className={styles.imgbox}>
        <img
            alt=""
            src={currentPic || emptyImg
            }
            style={{height:"100%"}}
            onError={(e) => {
                e.target.onerror = null;
                e.target.src = emptyImg;
            }}
          />
      </div>
    </Modal>
  )
}

export default BigImgModal;