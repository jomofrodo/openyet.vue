import * as mathLib from './mathLib.js';

const M = mathLib;

let Trend = class {
    constructor(xVals,yVals,trendBL){
        this.xVals = xVals;
        this.yVals = yVals;
        let corrVals = getCorrelation(xVals,yVals);
        this.slope = fixPrec(corrVals.slope);
        this.intercept = fixPrec(corrVals.intercept);
        this.yMin = fixPrec(corrVals.yMin);
        this.yMax = fixPrec(corrVals.yMax);
        this.yAvg = fixPrec(corrVals.yAvg);
        let yValsCalc = corrVals.yValsCalc;
        let ctY = yValsCalc.length;
        this.yValsCalc = yValsCalc;
        this.correlation = corrVals.correlation;
        this.growthRate = calcDeltaPerX(this.yMax,this.yMin,ctY);
        this.growthPerc = calcPercentChange(yValsCalc[1],yValsCalc[0]);
        this.trend = this.growthRate; //alias
        if(trendBL){
            const bl = trendBL;
            this.slopeDelta = fixPrec(this.slope - bl.slope);
            this.yMaxDelta = fixPrec(this.yMax - bl.yMax);
            this.yAvgDelta = fixPrec(this.yAvg - bl.yAvg);
            this.yAvgDPerc = calcPercentChange(this.yAvg,bl.yAvg);
            this.growthRateDelta = this.growthRate - bl.growthRate;
            this.trendDelta = this.growthRateDelta;  //alias
            this.slopePerc = calcPercentChange(this.slope,bl.slope);
            this.yMaxPerc = calcPercentChange(this.yMaxDelta,bl.yMaxDelta);
            this.growthRatePerc = calcPercentChange(this.growthRate,bl.growthRate);
            this.trendPerc = calcPercentChange(this.trend,bl.trend);
        }

    }
    getCorrelation(xVals,yVals){
        return mathLib.getCorrelation(xVals,yVals);
    }
}

function fixPrec(num){
    num = Math.round(num*10)/10;
    return num;
}
function getCorrelation(xVals,yVals){
    return mathLib.getCorrelation(xVals,yVals);
}

function calcDeltaPerX(valA,valB,x){
    if(!x) x = 1;
    let delta = (valA-valB)/x;
    return Math.round(delta);
}
function calcPercentChange(valA,valB){
    let dPerc;
    if (valB == 0)  return NaN;
    dPerc = ((valA-valB)/valB)*100;
    dPerc = Math.round(dPerc*100)/100
    return dPerc;
}
export function calcTrend(key, recs, trendBL) {
        // Get the LR slope of recs[key]
        let yVals = [],
          yValsConv = [];
        let xVals = [];
        let sY = 0;
        recs.forEach((rec, idx) => {
          let val = rec[key];
          yVals.push(val);
          sY += val - 0;
          xVals.push(idx);
        });
        if (sY == 0) return 0;
        // [xVals, yValsConv] = mathLib.findLineByLeastSquares(xVals, yVals);
        // const perc1 = (yValsConv[1] - yValsConv[0]) / yValsConv[0];
        // let trend = perc1;
        // if (trend % 1 != 0) {
        //   // if(trend != 0 && (trend < 1 && trend > -1)){
        //   //decimal
        //   trend = Math.round(trend * 100, 0);
        // }
        let trend =new Trend(xVals,yVals,trendBL);
        trend.recs = recs;
        return trend;
      }