import React, { memo } from 'react';
import { Column } from '@ant-design/plots';
const ColumnChart = (props) => {
  let { list, color = '#5B8FF9' } = props

  const config = {
    data: list,
    xField: 'type',
    yField: 'value',
    color: color,
    maxColumnWidth: 20,
    tooltip: {
      showMarkers: false,
      customContent: (title, items) => {
        return (
          <>
            <h5 style={{ marginTop: 16 }}>{title}</h5>
            <ul style={{ paddingLeft: 0 }}>
              {items?.map((itemData, index) => {
                const { value, color, data: {item = {}} } = itemData;
                // console.log(data, 'data--')
                return (
                  <li
                    key={index}
                    className="g2-tooltip-list-item"
                    data-index={index}
                  >
                    <div style={{ marginBottom: '4px', display: 'flex', alignItems: 'center' }}>
                      <span className="g2-tooltip-marker" style={{ backgroundColor: color }}></span>
                      <span style={{ display: 'inline-flex', flex: 1, justifyContent: 'space-between' }}>
                        <span style={{ marginRight: 16 }}>在校率:</span>
                        <span className="g2-tooltip-list-item-value">{value}%</span>
                      </span>
                    </div>
                    <div style={{ marginBottom: '4px', display: 'flex', alignItems: 'center' }}>
                      <span className="g2-tooltip-marker" style={{ backgroundColor: color }}></span>
                      <span style={{ display: 'inline-flex', flex: 1, justifyContent: 'space-between' }}>
                        <span style={{ marginRight: 16 }}>缺勤率:</span>
                        <span className="g2-tooltip-list-item-value">{item.absent_weeks_percent ? (item.absent_weeks_percent * 100).toFixed(2) : 0}%</span>
                      </span>
                    </div>
                    <div style={{ marginBottom: '4px', display: 'flex', alignItems: 'center' }}>
                      <span className="g2-tooltip-marker" style={{ backgroundColor: color }}></span>
                      <span style={{ display: 'inline-flex', flex: 1, justifyContent: 'space-between' }}>
                        <span style={{ marginRight: 16 }}>在校学生数:</span>
                        <span className="g2-tooltip-list-item-value">{item.normal_weeks || 0}</span>
                      </span>
                    </div>
                    <div style={{ marginBottom: '4px', display: 'flex', alignItems: 'center' }}>
                      <span className="g2-tooltip-marker" style={{ backgroundColor: color }}></span>
                      <span style={{ display: 'inline-flex', flex: 1, justifyContent: 'space-between' }}>
                        <span style={{ marginRight: 16 }}>缺勤学生数:</span>
                        <span className="g2-tooltip-list-item-value">{item.absent_weeks || 0}</span>
                      </span>
                    </div>
                  </li>
                );
              })}
            </ul>
          </>
        );
      },
    },
    label: {
      // 可手动配置 label 数据标签位置
      position: 'middle',
      // 'top', 'bottom', 'middle',
      // 配置样式
      style: {
        fill: '#FFFFFF',
        opacity: 0.6,
      },
    },
    xAxis: {
      label: {
        autoHide: true,
        autoRotate: false,
      },
    },
    yAxis: {
      label: {
        formatter: (value) => {
          return `${value}%` ;
        },
      },
     
    },
    meta: {
      type: {
        alias: '州市',
      },
      sales: {
        alias: '在校率',
      },
    },
    slider: {
      start: 0.1,
      end: 0.9,
      trendCfg: {
        isArea: true,
      },
    },
   
  };
  return <Column {...config} />;
}

export default memo(ColumnChart);
