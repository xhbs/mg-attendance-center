import { Tabs } from 'antd'
import styles from './index.less';
import Details from './Details';
import Statistics from './statistics';
const { TabPane } = Tabs;

const attendance = () => {
	return (
		<div className={styles['wrap']}>
			<div className={styles['chart-wrap']}>
			<Tabs defaultActiveKey="1">
				<TabPane tab="学生注册明细" key="1">
					<Details />
				</TabPane>
				<TabPane tab="学生注册状态统计" key="2">
					<Statistics />
				</TabPane>
			</Tabs>
			</div>
		</div>
	);
};

export default attendance;
