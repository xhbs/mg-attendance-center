import React, { memo } from 'react';
import { Area } from '@ant-design/plots';

const LineChat = (props) => {
  let { list, color = '#5B8FF9' } = props
  const config = {
    data: list,
    xField: 'type',
    yField: 'value',
    label: {},
    smooth: true,
    color: color,
    point: {
      size: 5,
      shape: 'diamond',
      style: {
        fill: 'white',
        stroke: color,
        lineWidth: 2,
      },
    },
    yAxis: {
      label: {
        formatter: (value) => {
          return `${value}%` ;
        },
      },
    },
    tooltip: {
      showMarkers: false,
      // formatter: (datum) => {
      //   console.log(list.find(item => item.type === datum.type), 'obj')
      //   return { name: '在校率', value: datum.value + '%' };
      // },
      content: {
        customHtml: (title, items) => {
          console.log(title, items)
          // console.log(container, view, datum, data)
          return (
            <>
              <h5 style={{ marginTop: 16 }}>{title}</h5>
              <ul style={{ paddingLeft: 0 }}>
                {items?.map((item, index) => {
                  // const { name, value, color } = item;
                  return (
                    <div>llll</div>
                    // <li
                    //   key={item.year}
                    //   className="g2-tooltip-list-item"
                    //   data-index={index}
                    //   style={{ marginBottom: 4, display: 'flex', alignItems: 'center' }}
                    // >
                    //   <span className="g2-tooltip-marker" style={{ backgroundColor: color }}></span>
                    //   <span
                    //     style={{ display: 'inline-flex', flex: 1, justifyContent: 'space-between' }}
                    //   >
                    //     <span style={{ marginRight: 16 }}>{name}:</span>
                    //     <span className="g2-tooltip-list-item-value">{value}</span>
                    //   </span>
                    // </li>
                  );
                })}
              </ul>
            </>
          );
        },
      },
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
    state: {
      active: {
        style: {
          shadowBlur: 4,
          stroke: color,
          fill: 'red',
        },
      },
    },
    interactions: [
      {
        type: 'marker-active',
      },
    ],
  };
  return <Area {...config} />;
}

export default memo(LineChat);
