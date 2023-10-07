/**
 * 实习点名
 */
import React, { useEffect, useState } from 'react';
import classNames from 'classnames';
// import { organizationType } from '../../dailyAttendance/dailyAttendanceStatistics/utils';
import LeftTreeSelect from '@components/LeftTreeSelectCom';
// import LeftTreeSelect from '../../dailyAttendance/dailyAttendanceStatistics/components/leftTreeSelect';
import RightTable from './components/rightTable';
import { getUserRoleInfo } from '@/services/common';
import { SCHOOL } from '@/constants';
import styles from './index.less';

function PracticeRollCall() {
  
  const [userRole, setuserRole] = useState('');
  const [activeTreeNode, setactiveTreeNode] = useState(null);

  useEffect(() => {
    getUserRoleInfo().then(res => {
			if(res) {
        setuserRole(res[0] && res[0].role_code || '');
        // setuserRole(SCHOOL);
			}
		})
  }, [])

  const changeTreeNode = (node) => {
    setactiveTreeNode(node);
  }

	return (
    <div className="hz-layout-vertical">
      <div className={classNames(
        'hz-layout-horizontal',
        styles.mainWapper
      )}>
        <div className="hz-layout-fl" style={{
          width: 280,
          height: '100%',
          backgroundColor: '#fff',
        }}>
          {
            userRole ? (
              <LeftTreeSelect setActiveInfo={changeTreeNode} showSubType={userRole !== SCHOOL ? 4 : ''} />
            ) : null
          }
        </div>
        <div className="hz-layout-main" style={{height: '100%'}}>
          <div className='hz-layout-vertical'>
            <div className="hz-layout-vertical-body">
              <RightTable
                activeInfo={activeTreeNode}
              />
            </div>
          </div>
        </div>
      </div>
		</div>
	);
}

export default PracticeRollCall;
