/*
 * @Desc: 表达式
 * @Author: ranguangyu
 * @Date: 2020-09-01 15:18:21
 */
import React, { PureComponent } from 'react';
import { SmartTable } from '@hz-components/biz-scene-components';
import { Table } from '@hz-components/react-base';
import { CreateDrawer } from '@hz-components/ists-common';
import Scrollbars from 'react-custom-scrollbars';
import request from '@/utils/request';

import OperateTree from './components/OperateTree';
import FormRender from './components/FormRender';
import tableJson from './table.json';

const { Ellipsis } = Table;

class ExpressionManagement extends PureComponent {
	constructor(props) {
		super(props);

		this.state = {
			visible: false,
			isEdit: false, // 状态：编辑 or 新增
			record: {},
			node: undefined, // 分组节点
		};

		this.SmartTableRef = React.createRef(null);
		this.FormRenderRef = React.createRef(null);
	}

	componentDidMount() {
		const tableObserver = this.SmartTableRef.current.getTableObserver();
		tableObserver.on('onAdd', this.onAdd);
	}

	componentWillUnmount() {
		const tableObserver = this.SmartTableRef.current.getTableObserver();
		tableObserver.on('onAdd', this.onAdd);
	}

	onAdd = () => {
		console.log('add');
		this.setState({ visible: true, record: {}, isEdit: false });
	};

	onCancel = () => {
		this.setState({ visible: false });
	};

	onConfirm = () => {
		const { node, isEdit, record } = this.state;
		this.form.submit().then(data => {
			data = {
				...data,
				express_group_id: isEdit ? record.express_group_id : (node && node.id),
			};
			request({
				url: '/api/biz-scene/v1/attendance/express',
				method: isEdit ? 'put' : 'post',
				data,
			}).then(result => {
				this.setState({ visible: false });
				const tableObserver = this.SmartTableRef.current.getTableObserver();
				tableObserver.trigger('dataLoad');
			});
		});
	};

	render() {
		const { visible, isEdit, record, node } = this.state;

		return (
			<div
				className="hz-layout-horizontal"
				style={{
					position: 'relative',
					height: '100%',
					padding: 16,
				}}
			>
				<div
					className="hz-layout-fl"
					style={{
						width: 260,
						height: '100%',
						backgroundColor: '#fff',
					}}
				>
					<OperateTree
						onSelect={node => {
							const tableObserver = this.SmartTableRef.current.getTableObserver();
							this.setState({ node }, () => {
								tableObserver.trigger('dataLoad');
							});
						}}
					/>
				</div>
				<div className="hz-layout-main" style={{ height: '100%' }}>
					<div
						className="hz-layout-vertical"
						style={{
							height: '100%',
							backgroundColor: '#fff',
						}}
					>
						<SmartTable
							tableId="expression_table" // 必传
							tableJson={tableJson}
							ref={this.SmartTableRef}
							registerPool={{
								transformRetrieveInput: data => {
									const {
										page_index: page_num,
										page_size,
										search: query,
										...rest
									} = data;
									return {
										...rest,
										express_group_id: node && node.id,
										page_num,
										page_size,
										query,
									};
								},
								transformDeleteInput: ({ ids }) => {
									return ids;
								},
								addBtnDisabled: () => {
									return !node;
								},
								columnRuleNameSlot: (text, record) => {
									return (
										<Ellipsis>
											<a
												onClick={() => {
													this.setState({
														visible: true,
														isEdit: true,
														record,
													});
												}}
											>
												{text}
											</a>
										</Ellipsis>
									);
								},
							}}
						/>
					</div>
				</div>

				<CreateDrawer
					visible={visible}
					title={isEdit ? '编辑' : '新建'}
					handleConfirm={this.onConfirm}
					handleCancel={this.onCancel}
				>
					<Scrollbars>
						<FormRender
							wrappedComponentRef={form => (this.form = form)}
							data={record}
						/>
					</Scrollbars>
				</CreateDrawer>
			</div>
		);
	}
}

export default ExpressionManagement;
