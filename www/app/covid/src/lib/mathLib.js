Math.slope = function (xVals, yVals) {
    if (!xVals) {
        const mult = 1;
        xVals = [];
        Object.keys(yVals).forEach(idx => xVals.push((idx - 0 + 1) * mult));
    }
    return getSlope(xVals, yVals);
}

export function findLineByLeastSquares(xVals, yVals) {
    var sum_x = 0;
    var sum_y = 0;
    var sum_xy = 0;
    var sum_xx = 0;
    var count = 0;

    /*
     * We'll use those variables for faster read/write access.
     */
    var x = 0;
    var y = 0;
    var values_length = xVals.length;

    if (values_length != yVals.length) {
        throw new Error('The parameters xVals and yVals need to have same size!');
    }

    /*
     * Nothing to do.
     */
    if (values_length === 0) {
        return [[], []];
    }

    /*
     * Calculate the sum for each of the parts necessary.
     */
    for (var v = 0; v < values_length; v++) {
        x = xVals[v];
        y = yVals[v];
        sum_x += x - 0;
        sum_y += y - 0;
        sum_xx += x * x;
        sum_xy += x * y;
        count++;
    }

    /*
     * Calculate m and b for the formular:
     * y = x * m + b
     */
    var m = (count * sum_xy - sum_x * sum_y) / (count * sum_xx - sum_x * sum_x);
    var b = (sum_y / count) - (m * sum_x) / count;

    /*
     * We will make the x and y result line now
     */
    var result_values_x = [];
    var result_values_y = [];

    for (var v = 0; v < values_length; v++) {
        x = xVals[v];
        y = x * m + b;
        result_values_x.push(x);
        result_values_y.push(y);
    }

    return [result_values_x, result_values_y];
}

export function getSlope(xVals, yVals) {
    var sum_x = new Number(0);
    var sum_y = new Number(0);
    var sum_xy = 0;
    var sum_xx = 0;
    var count = 0;
    let mX = 0;   // Mean of x
    let mY = 0;  //Mean of y

    /*
     * We'll use those variables for faster read/write access.
     */
    var x = 0;
    var y = 0;
    var values_length = xVals.length;

    if (values_length != yVals.length) {
        throw new Error('The parameters xVals and yVals need to have same size!');
    }

    /*
     * Nothing to do.
     */
    if (values_length === 0) {
        return [[], []];
    }

    // Calc the means


    /*
     * Calculate the sum for each of the parts necessary.
     */
    for (var v = 0; v < values_length; v++) {
        x = xVals[v];
        y = yVals[v];
        sum_x += x - 0;
        sum_y += y - 0;
        count++;
    }
    mX = sum_x / values_length;
    mY = sum_y / values_length;
    for (var v = 0; v < values_length; v++) {
        x = xVals[v];
        y = yVals[v];
        sum_xx += (x - mX) * (x - mX);
        sum_xy += (x - mX) * (y - mY);
    }

    /*
     * Calculate m and b for the formular:
     * y = x * m + b
     */
    var m = sum_xy / sum_xx;
    // var m = (count*sum_xy - sum_x*sum_y) / (count*sum_xx - sum_x*sum_x);
    var b = (sum_y / count) - (m * sum_x) / count;

    return m;

}

export function niceNumber(x) {
    if (x == null) return null;
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

export function getCorrelation(xVals,yVals) {
    // from http://clymer.altervista.org/graphs/linear.html
    const m = Math;
    var x = new Array(),
        y = new Array();
    var xfield, yfield, ctY;
    y = yVals;
    if (!xVals) {
       // x vals are just integers 1..n
        Object.keys(y).forEach((val, idx) => {
            x[idx] = idx + 1;
        });
    }else x = xVals;
    //Make sure we are working with numbers
    for (var i = 0; i < y.length; i++) {
        y[i] = y[i] - 0;
    }

    ctY = y.length;
    var xmin = x[1],
        xmax = x[1];
    var ymin = y[1],
        ymax = y[1];
    var xs = 0,
        ys = 0,
        x2s = 0,
        y2s = 0,
        xys = 0;
    xmin = m.min(...x);
    xmax = m.max(...x);
    ymin = m.min(...y);
    ymax = m.max(...y);
    for (let i = 0; i < ctY; i++) {
        // xmin = m.min(x[i], xmin);
        // xmax = m.max(x[i], xmax);
        // ymin = m.min(y[i], ymin);
        // ymax = m.max(y[i], ymax);
        xs += x[i];
        ys += y[i];
        x2s += x[i] * x[i];
        y2s += y[i] * y[i];
        xys += x[i] * y[i];
    }

    var slope, intercept, correlation;

    intercept = (ys * x2s - xys * xs) / (ctY * x2s - xs * xs);
    slope = (ctY * xys - xs * ys) / (ctY * x2s - xs * xs);
    correlation =
        (ctY * xys - xs * ys) /
        m.sqrt((ctY * x2s - xs * xs) * (ctY * y2s - ys * ys));
    correlation = correlation * correlation;

    let inter, slo, cor; //rounded versions
    inter = m.round(10000 * intercept) / 10000;
    slo = m.round(10000 * slope) / 10000;
    cor = m.round(10000 * correlation) / 10000;
    let ret = {};
    let yValsCalc = [];
    for (var v = 0; v < ctY; v++) {
        let x = xVals[v];
        let y = slo*x  + intercept;
        yValsCalc.push(y);
    }
    ret.correlation = cor;
    ret.intercept = inter;
    ret.slope = slo;
    ret.xMax = xmax;
    ret.xMin = xmin;
    ret.yMax = Math.max(...yValsCalc);
    ret.yMin = Math.min(...yValsCalc);
    ret.yAvg = average(yValsCalc);
    ret.ctY = ctY;
    ret.yValsCalc = yValsCalc;
    return ret;
}

const average = arr => arr.reduce((sume, el) => sume + el, 0) / arr.length;

export function getSignificance(correlation, ctY) {
    const m = Math;
    const r = m.sqrt(correlation);
    const n = ctY;
    let t = r * m.sqrt((n - 2) / (1 - r * r));
    //compare to critical t value
    const critT = t95[n - 2];
    let significance = t;
    return { significance: significance, critT: critT }
}


