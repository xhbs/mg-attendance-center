/*
 * @Author: xiedan WX042
 * @since: 2020-12-10 11:19:37
 * @lastTime: 2020-12-31 17:18:08
 * @文件相对于项目的路径: \biz-scene-attendance-web\src\pages\practice\Statistics\mod\setOptions.js
 * @Description: 
 */

import echarts from "echarts";

let formatNumber = function(num) {
    let reg = /(?=(\B)(\d{3})+$)/g;
    return num.toString().replace(reg, ',');
}

export function initPieArea(params) {
    const {
        colors = ["#5584FF", "#49EAC4", "#FC2344", "#ffe66a", "#F7B500"], // 非必传
        chartTitle = "", // 非必传
        graphData: chartData, // 必传
        total = 0, //总数
        radius = [80, 60],
    } = params.info;

    const pieChart = echarts.init(params.targetDoc);
    const isShow = chartData.length !== 0;
    const option = {
        title: {
            text: isShow ? '{name|考勤次数}\n{val|' + formatNumber(total) + '}' : "{empty|暂无数据}",
            textAlign: 'center',
            top: "30%",
            left: "25%",
            textStyle: {
                rich: {
                    name: {
                        fontSize: 14,
                        fontWeight: 'normal',
                        color: '#666666',
                        padding: [10, 0],
                    },
                    val: {
                        fontSize: 24,
                        fontWeight: 'bold',
                        color: '#333333',
                    },
                    empty: {
                        color: "#aaa",
                        fontSize: 24,
                        fontFamily: "Microsoft YaHei",
                    },
                },
            },
        },
        calculable: true,
        legend: {
            orient: 'vertical',
            icon: 'circle',
            x: '60%',
            y: 'center',
            itemWidth: 8,
            itemHeight: 8,
            align: 'left',
            textStyle: {
                rich: {
                    name: {
                        fontSize: 12,
                        padding: [0, 10, 0, 0],
                        lineHeight: 18,
                    },
                    value: {
                        fontSize: 12,
                    },
                    percent: {
                        fontSize: 12,
                        padding: [0, 10, 0, 10],
                    },
                    line: {
                        color: '#979797',
                    },
                },
            },
            formatter: function(name) {
                let res = chartData.filter(v => v.name === name);
                console.log(res)
                res = res[0] || {};
                return '{name|' + name + '}{line||}{percent|' + res.percent + '}{value|' + formatNumber(res.value) + '}'
            },
        },
        series: [
            {
                name: chartTitle,
                type: "pie",
                color: colors,
                startAngle: 0,
                radius,
                center: ["26%", "50%"],
                // 'radius' 面积展现数据的百分比，半径展现数据的大小。
                //  'area' 所有扇区面积相同，仅通过半径展现数据大小
                // roseType: "",
                // 是否启用防止标签重叠策略，默认开启，圆环图这个例子中需要强制所有标签放在中心位置，可以将该值设为 false。
                avoidLabelOverlap: false,
                label: {
                    normal: {
                        show: false,
                    },
                    emphasis: {
                        show: false,
                        rich: {
                            c: {
                                color: "#fff",
                                fontSize: 14,
                            },
                            hr: {
                                backgroundColor: "#eee",
                                width: "100%",
                                height: 0.6,
                                lineHeight: 10,
                            },
                            b: {
                                color: "#fff",
                                lineHeight: 16,
                                fontSize: 14,
                            },
                        },
                    },
                },
                labelLine: {
                    normal: {
                        show: false,
                        length: 1,
                    },
                },
                data: chartData,
            },
        ],
    };
    pieChart.clear();
    pieChart.setOption(option);
}

export function initPiePercent(params) {
    const {
        graphData = [],
    } = params.info;
    const pieChartP = echarts.init(params.targetDoc);
    const option = {
        title: {
            text: '{name|缺勤率}\n{val|' + graphData[0] + '%}',
            textStyle: {
                rich: {
                    name: {
                        fontSize: 14,
                        fontWeight: 'normal',
                        color: '#666666',
                        padding: [10, 0],
                    },
                    val: {
                        fontSize: 28,
                        fontWeight: 'bold',
                        color: '#333333',
                    },
                },
            },
            itemGap: 20,
            left: 'center',
            top: "30%",
        },
        tooltip: {
            formatter: function (params) {
                return '<span style="color: #fff;">缺勤率：'+ graphData[0] + '%</span>';
            },
        },
      angleAxis: {
        max: 100,
        clockwise: true, // 逆时针
        // 隐藏刻度线
        show: false,
      },
      radiusAxis: {
            type: 'category',
            show: true,
            axisLabel: {
                show: false,
            },
            axisLine: {
                show: false,
    
            },
            axisTick: {
                show: false,
            },
      },
      polar: {
        center: ['50%', '50%'],
        radius: [80, 60], // 图形大小
      },
      series: [{
        type: 'bar',
        data: graphData,
        showBackground: true,
        backgroundStyle: {
            color: '#D9D9D9',
        },
        coordinateSystem: 'polar',
        // roundCap: true,
        barWidth: 20,
        itemStyle: {
            normal: {
            opacity: 1,
            color:  new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                offset: 0,
                color: '#8C55FF',
              }, {
                offset: 1,
                color: '#918BFF',
              }]),
            // shadowBlur: 5,
            // shadowColor: '#918BFF',
        },
        },
      }],
    };

    pieChartP.clear();
    pieChartP.setOption(option);
}