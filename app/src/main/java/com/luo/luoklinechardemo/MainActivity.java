package com.luo.luoklinechardemo;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    CombinedChart mChart;
    private int colorHomeBg;
    private int colorLine;
    private int colorText;
    private int colorMa5;
    private int colorMa10;
    private int colorMa20;
    private int kLineGreen;
    private int kLineRed;

    private int itemcount;
    private List<CandleEntry> candleEntries = new ArrayList<>();
    private ArrayList<String> xVals;
    private CombinedData combinedData;
    private CandleData candleData;
    private LineData lineData;
    Legend legend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mChart = (CombinedChart) findViewById(R.id.chart);
        initChart();
        loadChartData();

    }
    private void initChart() {
        colorHomeBg = getResources().getColor(R.color.home_page_bg);
        colorLine = getResources().getColor(R.color.common_divider);
        colorText = getResources().getColor(R.color.text_grey_light);
        colorMa5 = getResources().getColor(R.color.ma5);
        colorMa10 = getResources().getColor(R.color.ma10);
        colorMa20 = getResources().getColor(R.color.ma20);
        kLineGreen=getResources().getColor(R.color.k_line_green);
        kLineRed=getResources().getColor(R.color.k_line_red);

        stockBeans = Model.getData();
        mChart.setDescription("");
        mChart.setDrawGridBackground(true);
        mChart.setBackgroundColor(colorHomeBg);
        mChart.setGridBackgroundColor(colorHomeBg);
        mChart.setScaleYEnabled(false);
        mChart.setPinchZoom(true);
        mChart.setDrawValueAboveBar(false);
        mChart.setNoDataText("加载中...");
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE});



        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);//网格线的X显示
        xAxis.setGridColor(colorLine);
        xAxis.setTextColor(colorText);
        xAxis.setSpaceBetweenLabels(4);
        xAxis.setAvoidFirstLastClipping(true);//时间分隔靠右不至于压缩不可见时间

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setDrawGridLines(true);//网格线的Y显示
        leftAxis.setDrawAxisLine(false);
        leftAxis.setGridColor(colorLine);
        leftAxis.setTextColor(colorText);
//        leftAxis.setEnabled(false);

        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setLabelCount(8, false);
//        rightAxis.setDrawGridLines(true);//网格线的Y显示
//        rightAxis.setDrawAxisLine(false);
//        rightAxis.setGridColor(colorLine);
//        rightAxis.setTextColor(colorText);
        rightAxis.setEnabled(false);

        int[] colors = {colorMa5, colorMa10, colorMa20};
        String[] labels = {"MA5   "+stockBeans.get(stockBeans.size()-1).getMa5(), "MA10   "+stockBeans.get(stockBeans.size()-1).getMa10(), "MA20   "+stockBeans.get(stockBeans.size()-1).getMa20()};
         legend = mChart.getLegend();
        legend.setCustom(colors, labels);
        legend.setXEntrySpace(70f);
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
        legend.setTextColor(Color.BLACK);
        legend.setForm(Legend.LegendForm.CIRCLE);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                CandleEntry candleEntry = (CandleEntry) entry;
                float change = (candleEntry.getClose() - candleEntry.getOpen()) / candleEntry.getOpen();
                NumberFormat nf = NumberFormat.getPercentInstance();
                nf.setMaximumFractionDigits(2);
                StockListBean.StockBean  stockBean = stockBeans.get(candleEntry.getXIndex());
                String changePercentage = nf.format(Double.valueOf(String.valueOf(change)));
                Log.d("qqq","时间：" + stockBean.getDate() + "最高" + candleEntry.getHigh() + " 最低" + candleEntry.getLow() +
                        " 开盘" + candleEntry.getOpen() + " 收盘" + candleEntry.getClose() +
                        " 涨跌幅" + changePercentage+"  MA5:"+stockBean.getMa5()+ "  MA10:"+stockBean.getMa10()+ "   MA20:"+stockBean.getMa20());


                int[] colors = {colorMa5, colorMa10, colorMa20};
                String[] labels =new String[]{"MA5   "+stockBean.getMa5(), "MA10   "+stockBean.getMa10(), "MA20   "+stockBean.getMa20()};
                legend.setCustom(colors, labels);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }
    List<StockListBean.StockBean> stockBeans;
    private void loadChartData() {
        mChart.resetTracking();

        candleEntries = Model.getCandleEntries();

        itemcount = candleEntries.size();

        xVals = new ArrayList<>();
        for (int i = 0; i < itemcount; i++) {
            xVals.add(stockBeans.get(i).getDate());
        }

        combinedData = new CombinedData(xVals);

        /*k line*/
        candleData = generateCandleData();
        combinedData.setData(candleData);

        /*ma5*/
        ArrayList<Entry> ma5Entries = new ArrayList<Entry>();
        for (int index = 0; index < itemcount; index++) {
            ma5Entries.add(new Entry(stockBeans.get(index).getMa5(), index));
        }
        /*ma10*/
        ArrayList<Entry> ma10Entries = new ArrayList<Entry>();
        for (int index = 0; index < itemcount; index++) {
            ma10Entries.add(new Entry(stockBeans.get(index).getMa10(), index));
        }
        /*ma20*/
        ArrayList<Entry> ma20Entries = new ArrayList<Entry>();
        for (int index = 0; index < itemcount; index++) {
            ma20Entries.add(new Entry(stockBeans.get(index).getMa20(), index));
        }

        lineData = generateMultiLineData(
                generateLineDataSet(ma5Entries, colorMa5, "ma5"),
                generateLineDataSet(ma10Entries, colorMa10, "ma10"),
                generateLineDataSet(ma20Entries, colorMa20, "ma20"));

        combinedData.setData(lineData);
        mChart.setData(combinedData);//当前屏幕会显示所有的数据
        mChart.animateX(2000);
        mChart.invalidate();
    }


    private CandleData generateCandleData() {

        CandleDataSet set = new CandleDataSet(candleEntries, "");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setShadowWidth(1f);
        set.setDecreasingColor(kLineRed);//下降颜色
        set.setDecreasingPaintStyle(Paint.Style.FILL);
        set.setIncreasingColor(kLineGreen);//增加，增长颜色
        set.setIncreasingPaintStyle(Paint.Style.FILL);
        set.setNeutralColor(kLineRed);//中立颜色
        set.setShadowColorSameAsCandle(true);
        set.setHighlightLineWidth(0.7f);
        set.setHighLightColor(Color.BLACK);
        set.setDrawHorizontalHighlightIndicator(true);//拖动线是否有水平线
        set.setDrawHighlightIndicators(true);//拖动有线否
        set.setDrawValues(false);

        CandleData candleData = new CandleData(xVals);
        candleData.addDataSet(set);

        return candleData;
    }
    private LineData generateMultiLineData(LineDataSet... lineDataSets) {
        List<ILineDataSet> dataSets = new ArrayList<>();
        for (int i = 0; i < lineDataSets.length; i++) {
            dataSets.add(lineDataSets[i]);
        }

        List<String> xVals = new ArrayList<String>();
        for (int i = 0; i < itemcount; i++) {
            xVals.add("" + (1990 + i));
        }

        LineData data = new LineData(xVals, dataSets);

        return data;
    }
    private LineDataSet generateLineDataSet(List<Entry> entries, int color, String label) {
        LineDataSet set = new LineDataSet(entries, label);
        set.setColor(color);
        set.setLineWidth(1f);
        set.setDrawCubic(true);//圆滑曲线
        set.setDrawCircles(false);
        set.setDrawCircleHole(false);
        set.setDrawValues(false);
        set.setHighlightEnabled(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        return set;
    }

}
