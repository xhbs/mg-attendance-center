import React, { memo } from 'react';
import { Pie, measureTextWidth } from '@ant-design/plots';

const PieChat = (props) => {
  let { list, count, color } = props

  function renderStatistic(containerWidth, text, style) {
    const { width: textWidth, height: textHeight } = measureTextWidth(text, style);
    const R = containerWidth / 2; // r^2 = (w / 2)^2 + (h - offsetY)^2

    let scale = 1;

    if (containerWidth < textWidth) {
      scale = Math.min(Math.sqrt(Math.abs(Math.pow(R, 2) / (Math.pow(textWidth / 2, 2) + Math.pow(textHeight, 2)))), 1);
    }

    const textStyleStr = `width:${containerWidth}px;`;
    return `<div style="${textStyleStr};font-size:${scale}em;line-height:${scale < 1 ? 1 : 'inherit'};">${text}</div>`;
  }

  const config = {
    appendPadding: 10,
    height: '100%',
    data: list,
    angleField: 'value',
    colorField: 'type',
    color: ({ type }) => {
      if (['在校学生数', '省直属学校'].includes(type)) {
        if (color) {
          return color
        }
        return '#5584FF' 
      }
      return '#D9D9D9'
    },
    radius: 1,
    innerRadius: 0.75,
    label: false,
    // {
    //   type: 'inner',
    //   offset: '-50%',
    //   content: '{value}',
    //   style: {
    //     textAlign: 'center',
    //     fontSize: 14,
    //   },
    // },
    interactions: [
      {
        type: 'element-selected',
      },
      {
        type: 'element-active',
      },
    ],
    statistic: {
      title: {
        offsetY: -4,
        customHtml: (container, view, datum) => {
          const { width, height } = container.getBoundingClientRect();
          const d = Math.sqrt(Math.pow(width / 2, 2) + Math.pow(height / 2, 2));
          const text = '总计';
          return renderStatistic(d, text, {
            fontSize: 28,
          });
        },
      },
      content: {
        offsetY: 4,
        style: {
          fontSize: '32px',
        },
        customHtml: (container, view, datum, data) => {
          const { width } = container.getBoundingClientRect();
          const text = count ? count : `${data.reduce((r, d) => r + d.value, 0)}`;
          return renderStatistic(width, text, {
            fontSize: 32,
          });
        },
      },
    },
    legend: {
      offsetY: 10,
      layout: 'vertical',
      position: 'bottom',
      itemName: {
        formatter: (text, item, index) => {
          return `${text} | ${list[index]?.proportion}%  ${list[index]?.value}`
        },
      },
    },
  };

  return <Pie {...config} />;
}

export default memo(PieChat);
